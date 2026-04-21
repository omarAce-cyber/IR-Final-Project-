package indexing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PositionalInvertedIndex {
    private final Map<String, Map<String, List<Integer>>> index = new HashMap<>();

    public void addDocument(String docId, List<String> terms) {
        for (int position = 0; position < terms.size(); position++) {
            String term = terms.get(position);
            index.computeIfAbsent(term, t -> new HashMap<>())
                    .computeIfAbsent(docId, d -> new ArrayList<>())
                    .add(position);
        }
    }

    public Map<String, List<Integer>> getPostings(String term) {
        return index.getOrDefault(term, Collections.emptyMap());
    }

    public Set<String> getVocabulary() {
        return index.keySet();
    }

    public Set<String> phraseSearch(List<String> phraseTerms) {
        Set<String> result = new HashSet<>();
        if (phraseTerms == null || phraseTerms.isEmpty()) {
            return result;
        }

        Map<String, List<Integer>> firstPostings = getPostings(phraseTerms.get(0));
        for (String docId : firstPostings.keySet()) {
            List<Integer> candidatePositions = new ArrayList<>(firstPostings.get(docId));
            boolean matches = true;

            for (int i = 1; i < phraseTerms.size(); i++) {
                Map<String, List<Integer>> termPostings = getPostings(phraseTerms.get(i));
                List<Integer> positions = termPostings.get(docId);
                if (positions == null || positions.isEmpty()) {
                    matches = false;
                    break;
                }

                Set<Integer> expected = new HashSet<>();
                for (int p : candidatePositions) {
                    expected.add(p + 1);
                }

                List<Integer> nextCandidates = new ArrayList<>();
                for (int p : positions) {
                    if (expected.contains(p)) {
                        nextCandidates.add(p);
                    }
                }

                if (nextCandidates.isEmpty()) {
                    matches = false;
                    break;
                }
                candidatePositions = nextCandidates;
            }

            if (matches) {
                result.add(docId);
            }
        }
        return result;
    }

    public Set<String> proximitySearch(String term1, String term2, int maxDistance) {
        Set<String> result = new HashSet<>();
        Map<String, List<Integer>> postings1 = getPostings(term1);
        Map<String, List<Integer>> postings2 = getPostings(term2);

        for (String docId : postings1.keySet()) {
            if (!postings2.containsKey(docId)) {
                continue;
            }
            List<Integer> positions1 = postings1.get(docId);
            List<Integer> positions2 = postings2.get(docId);

            int i = 0;
            int j = 0;
            while (i < positions1.size() && j < positions2.size()) {
                int p1 = positions1.get(i);
                int p2 = positions2.get(j);
                if (calculateWordGap(p1, p2) <= maxDistance) {
                    result.add(docId);
                    break;
                }
                if (p1 < p2) {
                    i++;
                } else {
                    j++;
                }
            }
        }
        return result;
    }

    private int calculateWordGap(int positionA, int positionB) {
        return Math.abs(positionA - positionB) - 1;
    }
}
