package query;

import java.util.List;

public class ParsedQuery {
    public enum Type {
        TERM,
        PHRASE,
        PROXIMITY,
        WILDCARD
    }

    private final Type type;
    private final List<String> terms;
    private final String wildcardPattern;
    private final int proximityDistance;

    public ParsedQuery(Type type, List<String> terms, String wildcardPattern, int proximityDistance) {
        this.type = type;
        this.terms = terms;
        this.wildcardPattern = wildcardPattern;
        this.proximityDistance = proximityDistance;
    }

    public Type getType() {
        return type;
    }

    public List<String> getTerms() {
        return terms;
    }

    public String getWildcardPattern() {
        return wildcardPattern;
    }

    public int getProximityDistance() {
        return proximityDistance;
    }
}
