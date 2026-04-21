package evaluation;

import query.SearchEngine;
import query.SearchResponse;
import query.SearchResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Evaluator {
    public void evaluateAndSave(
            SearchEngine engine,
            Map<String, Set<String>> relevanceJudgments,
            Path evaluationOutputPath
    ) throws IOException {
        List<String> lines = new ArrayList<>();
        double precisionSum = 0.0;
        double recallSum = 0.0;
        long timeSumNanos = 0L;
        int count = 0;

        for (Map.Entry<String, Set<String>> entry : relevanceJudgments.entrySet()) {
            String query = entry.getKey();
            Set<String> relevant = entry.getValue();

            long start = System.nanoTime();
            SearchResponse response = engine.search(query);
            long elapsed = System.nanoTime() - start;

            Set<String> retrieved = new HashSet<>();
            for (SearchResult result : response.getResults()) {
                retrieved.add(result.getDocumentId());
            }

            int relevantRetrieved = 0;
            for (String doc : retrieved) {
                if (relevant.contains(doc)) {
                    relevantRetrieved++;
                }
            }

            double precision = retrieved.isEmpty() ? 0.0 : (double) relevantRetrieved / retrieved.size();
            double recall = relevant.isEmpty() ? 0.0 : (double) relevantRetrieved / relevant.size();

            precisionSum += precision;
            recallSum += recall;
            timeSumNanos += elapsed;
            count++;

            lines.add(String.format("Query: %s", query));
            lines.add(String.format("Precision: %.4f", precision));
            lines.add(String.format("Recall: %.4f", recall));
            lines.add(String.format("Execution Time (ms): %.3f", elapsed / 1_000_000.0));
            lines.add("");
        }

        if (count > 0) {
            lines.add(String.format("Average Precision: %.4f", precisionSum / count));
            lines.add(String.format("Average Recall: %.4f", recallSum / count));
            lines.add(String.format("Average Execution Time (ms): %.3f", (timeSumNanos / (double) count) / 1_000_000.0));
        }

        Files.createDirectories(evaluationOutputPath.getParent());
        Files.write(evaluationOutputPath, lines);
    }

    public void saveSampleQueryOutputs(SearchEngine engine, List<String> queries, Path sampleOutputPath) throws IOException {
        List<String> lines = new ArrayList<>();
        for (String query : queries) {
            SearchResponse response = engine.search(query);
            lines.add("Query: " + query);
            if (response.getSuggestion() != null) {
                lines.add("Did you mean: " + response.getSuggestion());
            }
            if (response.getResults().isEmpty()) {
                lines.add("No results found.");
            } else {
                for (SearchResult result : response.getResults()) {
                    lines.add(String.format("- %s (score=%.4f)", result.getDocumentId(), result.getScore()));
                }
            }
            lines.add("");
        }

        Files.createDirectories(sampleOutputPath.getParent());
        Files.write(sampleOutputPath, lines);
    }
}
