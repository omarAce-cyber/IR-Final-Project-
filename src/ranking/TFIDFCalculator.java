package ranking;

import indexing.PositionalInvertedIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TFIDFCalculator {
    private final PositionalInvertedIndex index;
    private final List<String> documentIds;
    private final Map<String, Double> idf = new HashMap<>();
    private final Map<String, Map<String, Double>> documentVectors = new HashMap<>();

    public TFIDFCalculator(PositionalInvertedIndex index, List<String> documentIds) {
        this.index = index;
        this.documentIds = new ArrayList<>(documentIds);
        buildModel();
    }

    private void buildModel() {
        int totalDocs = Math.max(documentIds.size(), 1);

        for (String term : index.getVocabulary()) {
            int df = index.getPostings(term).size();
            double termIdf = Math.log((totalDocs + 1.0) / (df + 1.0)) + 1.0;
            idf.put(term, termIdf);
        }

        for (String docId : documentIds) {
            Map<String, Double> vector = new HashMap<>();
            for (String term : index.getVocabulary()) {
                List<Integer> positions = index.getPostings(term).get(docId);
                if (positions == null || positions.isEmpty()) {
                    continue;
                }
                double tf = 1.0 + Math.log(positions.size());
                vector.put(term, tf * idf.getOrDefault(term, 0.0));
            }
            documentVectors.put(docId, vector);
        }
    }

    public Map<String, Double> scoreQuery(List<String> queryTerms, Set<String> candidateDocs) {
        Map<String, Integer> tfCounts = new HashMap<>();
        for (String term : queryTerms) {
            tfCounts.put(term, tfCounts.getOrDefault(term, 0) + 1);
        }

        Map<String, Double> queryVector = new HashMap<>();
        for (Map.Entry<String, Integer> entry : tfCounts.entrySet()) {
            String term = entry.getKey();
            double tf = 1.0 + Math.log(entry.getValue());
            queryVector.put(term, tf * idf.getOrDefault(term, 1.0));
        }

        Map<String, Double> scores = new HashMap<>();
        for (String docId : documentIds) {
            if (candidateDocs != null && !candidateDocs.contains(docId)) {
                continue;
            }
            double score = CosineSimilarity.compute(queryVector, documentVectors.getOrDefault(docId, Map.of()));
            if (score > 0.0) {
                scores.put(docId, score);
            }
        }
        return scores;
    }
}
