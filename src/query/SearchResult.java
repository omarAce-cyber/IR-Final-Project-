package query;

public class SearchResult implements Comparable<SearchResult> {
    private final String documentId;
    private final double score;

    public SearchResult(String documentId, double score) {
        this.documentId = documentId;
        this.score = score;
    }

    public String getDocumentId() {
        return documentId;
    }

    public double getScore() {
        return score;
    }

    @Override
    public int compareTo(SearchResult other) {
        return Double.compare(other.score, this.score);
    }
}
