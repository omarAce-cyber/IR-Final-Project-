package query;

import indexing.KGramIndex;

import java.util.HashSet;
import java.util.Set;

public class SpellingCorrector {
    private static final int MAX_EDIT_DISTANCE_FLOOR = 3;

    private final Set<String> vocabulary;
    private final KGramIndex kGramIndex;

    public SpellingCorrector(Set<String> vocabulary, KGramIndex kGramIndex) {
        this.vocabulary = new HashSet<>(vocabulary);
        this.kGramIndex = kGramIndex;
    }

    public String suggest(String term) {
        if (term == null || term.isBlank() || vocabulary.contains(term)) {
            return null;
        }

        Set<String> candidates = kGramIndex.getCandidateTerms(term);
        String best = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (String candidate : candidates) {
            double score = jaccard(term, candidate) - (0.1 * editDistance(term, candidate));
            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }

        if (best == null || editDistance(term, best) > computeMaxEditDistance(term)) {
            return null;
        }
        return best;
    }

    private int computeMaxEditDistance(String term) {
        return Math.max(MAX_EDIT_DISTANCE_FLOOR, term.length() / 2);
    }

    private double jaccard(String a, String b) {
        Set<Character> setA = toCharSet(a);
        Set<Character> setB = toCharSet(b);
        Set<Character> inter = new HashSet<>(setA);
        inter.retainAll(setB);
        Set<Character> union = new HashSet<>(setA);
        union.addAll(setB);
        if (union.isEmpty()) {
            return 0.0;
        }
        return inter.size() / (double) union.size();
    }

    private Set<Character> toCharSet(String s) {
        Set<Character> set = new HashSet<>();
        for (char ch : s.toCharArray()) {
            set.add(ch);
        }
        return set;
    }

    private int editDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= b.length(); j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[a.length()][b.length()];
    }
}
