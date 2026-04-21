package query;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryParser {
    private static final Pattern PROXIMITY_PATTERN = Pattern.compile("^(.+)\\s+/([0-9]+)\\s+(.+)$");

    public ParsedQuery parse(String rawQuery) {
        String query = rawQuery == null ? "" : rawQuery.trim();

        if (query.startsWith("\"") && query.endsWith("\"") && query.length() >= 2) {
            String inner = query.substring(1, query.length() - 1).trim();
            return new ParsedQuery(ParsedQuery.Type.PHRASE, List.of(inner), null, -1);
        }

        Matcher matcher = PROXIMITY_PATTERN.matcher(query);
        if (matcher.matches()) {
            String term1 = matcher.group(1).trim();
            int distance = Integer.parseInt(matcher.group(2));
            String term2 = matcher.group(3).trim();
            return new ParsedQuery(ParsedQuery.Type.PROXIMITY, List.of(term1, term2), null, distance);
        }

        if (query.contains("*")) {
            return new ParsedQuery(ParsedQuery.Type.WILDCARD, List.of(), query, -1);
        }

        return new ParsedQuery(ParsedQuery.Type.TERM, List.of(query), null, -1);
    }
}
