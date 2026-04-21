package indexing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class KGramIndex {
    private final int k;
    private final Map<String, Set<String>> kgramMap = new HashMap<>();
    private final Set<String> vocabulary = new HashSet<>();

    public KGramIndex(int k) {
        this.k = k;
    }

    public void build(Set<String> terms) {
        for (String term : terms) {
            addTerm(term);
        }
    }

    public void addTerm(String term) {
        if (term == null || term.isBlank()) {
            return;
        }
        vocabulary.add(term);
        String wrapped = "$" + term + "$";
        if (wrapped.length() < k) {
            return;
        }
        for (int i = 0; i <= wrapped.length() - k; i++) {
            String gram = wrapped.substring(i, i + k);
            kgramMap.computeIfAbsent(gram, g -> new HashSet<>()).add(term);
        }
    }

    public Set<String> getCandidateTerms(String term) {
        Set<String> candidates = new HashSet<>();
        String wrapped = "$" + term + "$";
        if (wrapped.length() < k) {
            candidates.addAll(vocabulary);
            return candidates;
        }
        for (int i = 0; i <= wrapped.length() - k; i++) {
            String gram = wrapped.substring(i, i + k);
            candidates.addAll(kgramMap.getOrDefault(gram, Set.of()));
        }
        if (candidates.isEmpty()) {
            candidates.addAll(vocabulary);
        }
        return candidates;
    }

    public Set<String> expandWildcard(String wildcard) {
        if (wildcard == null || wildcard.isBlank()) {
            return Set.of();
        }

        String regex = "^" + Pattern.quote(wildcard).replace("*", "\\E.*\\Q") + "$";

        List<String> segments = new ArrayList<>();
        for (String part : wildcard.split("\\*")) {
            if (!part.isBlank()) {
                segments.add(part);
            }
        }

        Set<String> candidates = new HashSet<>(vocabulary);
        for (String segment : segments) {
            if (segment.length() < k) {
                continue;
            }
            Set<String> segmentCandidates = new HashSet<>();
            String wrapped = "$" + segment + "$";
            for (int i = 0; i <= wrapped.length() - k; i++) {
                String gram = wrapped.substring(i, i + k);
                segmentCandidates.addAll(kgramMap.getOrDefault(gram, Set.of()));
            }
            if (!segmentCandidates.isEmpty()) {
                candidates.retainAll(segmentCandidates);
            }
        }

        Set<String> expanded = new HashSet<>();
        for (String term : candidates) {
            if (term.matches(regex)) {
                expanded.add(term);
            }
        }
        return expanded;
    }
}
