package evaluation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        relevanceMap.put(query, Set.copyOf(relevantDocs));
    }

    /**
     * Returns the set of relevant document IDs for the given query, or an
     * empty set if no judgment has been registered for it.
     */
    public Set<String> getRelevantDocs(String query) {
        return relevanceMap.getOrDefault(query, Set.of());
    }

    /**
     * Returns an unmodifiable view of all registered judgments.
     */
    public Map<String, Set<String>> getAllJudgments() {
        return Collections.unmodifiableMap(relevanceMap);
    }
}
