package evaluation;

import query.SearchResult;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        if (results == null || results.isEmpty() || relevantDocs == null) {
            return 0.0;
        }

        Set<String> retrievedDocs = results.stream()
                .map(SearchResult::getDocumentId)
                .map(EvaluationNormalizer::normalizeDocId)
                .collect(Collectors.toSet());
        Set<String> normalizedRelevant = relevantDocs.stream()
                .map(EvaluationNormalizer::normalizeDocId)
                .collect(Collectors.toSet());
        long relevantRetrieved = EvaluationNormalizer.countRelevantRetrieved(retrievedDocs, normalizedRelevant);
        return (double) relevantRetrieved / retrievedDocs.size();
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
        if (results == null || relevantDocs == null || relevantDocs.isEmpty()) {
            return 0.0;
        }
        Set<String> retrievedDocs = results.stream()
                .map(SearchResult::getDocumentId)
                .map(EvaluationNormalizer::normalizeDocId)
                .collect(Collectors.toSet());
        Set<String> normalizedRelevant = relevantDocs.stream()
                .map(EvaluationNormalizer::normalizeDocId)
                .collect(Collectors.toSet());
        long relevantRetrieved = EvaluationNormalizer.countRelevantRetrieved(retrievedDocs, normalizedRelevant);
        return (double) relevantRetrieved / normalizedRelevant.size();
    }
}
