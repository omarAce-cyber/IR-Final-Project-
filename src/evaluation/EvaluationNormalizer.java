package evaluation;

import java.util.Locale;

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

    static boolean documentIdsMatch(String leftDocId, String rightDocId) {
        String left = normalizeDocId(leftDocId);
        String right = normalizeDocId(rightDocId);
        if (left.isEmpty() || right.isEmpty()) {
            return false;
        }
        if (left.equals(right)) {
            return true;
        }
        if (!left.contains("/") || !right.contains("/")) {
            return fileName(left).equals(fileName(right));
        }
        return false;
    }

    private static String fileName(String docId) {
        int idx = docId.lastIndexOf('/');
        return idx >= 0 ? docId.substring(idx + 1) : docId;
    }
}
