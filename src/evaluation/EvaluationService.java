package evaluation;

import query.SearchResult;

import java.util.List;
import java.util.Set;

/**
 * Stateless service that computes Precision and Recall for a single query.
 *
 * <p>This class is deliberately decoupled from the search engine and indexing
 * layers; it only operates on the result list produced by a search and the
 * relevant-document set provided by {@link GroundTruth}.</p>
 *
 * <ul>
 *   <li>Precision = relevant_retrieved / retrieved_total</li>
 *   <li>Recall    = relevant_retrieved / relevant_total</li>
 * </ul>
 */
public class EvaluationService {

    /**
     * Computes precision for a list of search results against a set of
     * known-relevant document IDs.
     *
     * @param results      the ranked list returned by the search engine
     * @param relevantDocs the ground-truth set of relevant document IDs
     * @return precision in [0.0, 1.0]; returns 0.0 when no results were retrieved
     */
    public double precision(List<SearchResult> results, Set<String> relevantDocs) {
        if (results == null || results.isEmpty()) {
            return 0.0;
        }
        long relevantRetrieved = results.stream()
                .filter(r -> relevantDocs.contains(r.getDocumentId()))
                .count();
        return (double) relevantRetrieved / results.size();
    }

    /**
     * Computes recall for a list of search results against a set of
     * known-relevant document IDs.
     *
     * @param results      the ranked list returned by the search engine
     * @param relevantDocs the ground-truth set of relevant document IDs
     * @return recall in [0.0, 1.0]; returns 0.0 when the relevant set is empty
     */
    public double recall(List<SearchResult> results, Set<String> relevantDocs) {
        if (relevantDocs == null || relevantDocs.isEmpty()) {
            return 0.0;
        }
        long relevantRetrieved = results.stream()
                .filter(r -> relevantDocs.contains(r.getDocumentId()))
                .count();
        return (double) relevantRetrieved / relevantDocs.size();
    }
}
