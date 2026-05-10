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
     * <p>Callers must pass normalized document IDs.
     * Exact normalized ID matching is used first.
     * If one side is unqualified (filename only), fallback filename matching is
     * allowed to tolerate ground-truth entries that omit the language/path prefix.</p>
     */
    static long countRelevantRetrieved(Set<String> retrievedDocs, Set<String> relevantDocs) {
        Set<String> unmatchedRetrieved = retrievedDocs.stream()
                .filter(id -> id != null && !id.isBlank())
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));

        long matches = 0L;
        for (String relevant : relevantDocs) {
            if (relevant == null || relevant.isBlank()) {
                continue;
            }
            if (unmatchedRetrieved.remove(relevant)) {
                matches++;
            }
        }

        for (String relevant : relevantDocs) {
            if (relevant == null || relevant.isBlank() || unmatchedRetrieved.isEmpty()) {
                continue;
            }
            if (retrievedDocs.contains(relevant)) {
                continue;
            }

            String relevantFileName = fileName(relevant);
            String matchedRetrieved = null;
            for (String retrieved : unmatchedRetrieved) {
                if (isFallbackMatch(relevant, retrieved, relevantFileName)) {
                    matchedRetrieved = retrieved;
                    break;
                }
            }

            if (matchedRetrieved != null) {
                unmatchedRetrieved.remove(matchedRetrieved);
                matches++;
            }
        }
        return matches;
    }

    private static boolean isFallbackMatch(String relevant, String retrieved, String relevantFileName) {
        if (retrieved == null || retrieved.isBlank()) {
            return false;
        }
        String retrievedFileName = fileName(retrieved);
        if (!relevantFileName.equals(retrievedFileName)) {
            return false;
        }

        boolean relevantQualified = relevant.contains("/");
        boolean retrievedQualified = retrieved.contains("/");
        return relevantQualified != retrievedQualified;
    }

    private static String fileName(String docId) {
        int idx = docId.lastIndexOf('/');
        return idx >= 0 ? docId.substring(idx + 1) : docId;
    }
}
