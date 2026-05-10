package evaluation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Stores the mapping of queries to their known-relevant document IDs.
 * Used exclusively for evaluation (Precision / Recall); has no coupling
 * to retrieval or ranking logic.
 */
public class GroundTruth {

    private final Map<String, Set<String>> relevanceMap = new HashMap<>();

    /**
     * Register the set of relevant document IDs for a given query.
     *
     * @param query       the raw query string
     * @param relevantDocs the document IDs considered relevant for this query
     */
    public void addRelevantDocs(String query, Set<String> relevantDocs) {
        String normalizedQuery = EvaluationNormalizer.normalizeQuery(query);
        Set<String> normalizedDocs = relevantDocs.stream()
                .map(EvaluationNormalizer::normalizeDocId)
                .collect(Collectors.toUnmodifiableSet());
        relevanceMap.put(normalizedQuery, normalizedDocs);
    }

    /**
     * Returns the set of relevant document IDs for the given query, or an
     * empty set if no judgment has been registered for it.
     */
    public Set<String> getRelevantDocs(String query) {
        String normalizedQuery = EvaluationNormalizer.normalizeQuery(query);
        return relevanceMap.getOrDefault(normalizedQuery, Set.of());
    }

    /**
     * Returns an unmodifiable view of all registered judgments.
     */
    public Map<String, Set<String>> getAllJudgments() {
        return Collections.unmodifiableMap(relevanceMap);
    }
}
