package evaluation;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

final class EvaluationNormalizer {
    private EvaluationNormalizer() {
    }

    static String normalizeQuery(String query) {
        if (query == null) {
            return "";
        }
        String normalized = query.trim();
        if (normalized.length() >= 2 && normalized.startsWith("\"") && normalized.endsWith("\"")) {
            normalized = normalized.substring(1, normalized.length() - 1).trim();
        }
        normalized = normalized.replaceAll("\\s+", " ");
        return normalized.toLowerCase(Locale.ROOT);
    }

    static String normalizeDocId(String docId) {
        if (docId == null) {
            return "";
        }

        String normalized = docId.trim().replace('\\', '/');
        while (normalized.startsWith("./")) {
            normalized = normalized.substring(2);
        }

        String[] parts = normalized.split("/");
        if (parts.length >= 2) {
            normalized = parts[parts.length - 2] + "/" + parts[parts.length - 1];
        } else if (parts.length == 1) {
            normalized = parts[0];
        }

        return normalized.toLowerCase(Locale.ROOT);
    }

    /**
     * Counts how many retrieved documents are relevant.
     *
     * <p>Exact normalized ID matching is used first.
     * If one side is unqualified (filename only), fallback filename matching is
     * allowed to tolerate ground-truth entries that omit the language/path prefix.</p>
     */
    static long countRelevantRetrieved(Set<String> retrievedDocs, Set<String> relevantDocs) {
        Set<String> normalizedRetrieved = retrievedDocs.stream()
                .map(EvaluationNormalizer::normalizeDocId)
                .collect(Collectors.toSet());
        Set<String> normalizedRelevant = relevantDocs.stream()
                .map(EvaluationNormalizer::normalizeDocId)
                .collect(Collectors.toSet());

        Set<String> retrievedFileNames = normalizedRetrieved.stream()
                .map(EvaluationNormalizer::fileName)
                .collect(Collectors.toSet());
        Set<String> unqualifiedRetrievedFileNames = normalizedRetrieved.stream()
                .filter(id -> !id.contains("/"))
                .map(EvaluationNormalizer::fileName)
                .collect(Collectors.toSet());

        long matches = 0L;
        for (String relevant : normalizedRelevant) {
            if (normalizedRetrieved.contains(relevant)) {
                matches++;
                continue;
            }

            String relevantFileName = fileName(relevant);
            if (!relevant.contains("/")) {
                if (retrievedFileNames.contains(relevantFileName)) {
                    matches++;
                }
            } else if (unqualifiedRetrievedFileNames.contains(relevantFileName)) {
                matches++;
            }
        }
        return matches;
    }

    private static String fileName(String docId) {
        int idx = docId.lastIndexOf('/');
        return idx >= 0 ? docId.substring(idx + 1) : docId;
    }
}
