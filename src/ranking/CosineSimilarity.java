package ranking;

import java.util.Map;

public class CosineSimilarity {
    public static double compute(Map<String, Double> a, Map<String, Double> b) {
        if (a == null || b == null || a.isEmpty() || b.isEmpty()) {
            return 0.0;
        }

        double dot = 0.0;
        for (Map.Entry<String, Double> entry : a.entrySet()) {
            dot += entry.getValue() * b.getOrDefault(entry.getKey(), 0.0);
        }

        double normA = 0.0;
        for (double v : a.values()) {
            normA += v * v;
        }

        double normB = 0.0;
        for (double v : b.values()) {
            normB += v * v;
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
