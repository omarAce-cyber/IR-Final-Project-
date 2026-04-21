package query;

import java.util.List;

public class SearchResponse {
    private final List<SearchResult> results;
    private final String suggestion;

    public SearchResponse(List<SearchResult> results, String suggestion) {
        this.results = results;
        this.suggestion = suggestion;
    }

    public List<SearchResult> getResults() {
        return results;
    }

    public String getSuggestion() {
        return suggestion;
    }
}
