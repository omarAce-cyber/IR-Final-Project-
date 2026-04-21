package query;

import indexing.KGramIndex;
import indexing.PositionalInvertedIndex;
import preprocessing.ArabicProcessor;
import preprocessing.EnglishProcessor;
import preprocessing.LanguageDetector;
import ranking.TFIDFCalculator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SearchEngine {
    private final LanguageDetector languageDetector;
    private final EnglishProcessor englishProcessor;
    private final ArabicProcessor arabicProcessor;
    private final PositionalInvertedIndex positionalIndex;
    private final KGramIndex kGramIndex;
    private final TFIDFCalculator tfidfCalculator;
    private final QueryParser queryParser = new QueryParser();
    private final SpellingCorrector spellingCorrector;

    public SearchEngine(
            LanguageDetector languageDetector,
            EnglishProcessor englishProcessor,
            ArabicProcessor arabicProcessor,
            PositionalInvertedIndex positionalIndex,
            KGramIndex kGramIndex,
            TFIDFCalculator tfidfCalculator,
            SpellingCorrector spellingCorrector
    ) {
        this.languageDetector = languageDetector;
        this.englishProcessor = englishProcessor;
        this.arabicProcessor = arabicProcessor;
        this.positionalIndex = positionalIndex;
        this.kGramIndex = kGramIndex;
        this.tfidfCalculator = tfidfCalculator;
        this.spellingCorrector = spellingCorrector;
    }

    public SearchResponse search(String rawQuery) {
        ParsedQuery parsedQuery = queryParser.parse(rawQuery);

        Set<String> candidateDocs = null;
        List<String> rankingTerms = new ArrayList<>();
        String suggestion = null;

        switch (parsedQuery.getType()) {
            case PHRASE -> {
                String phrase = parsedQuery.getTerms().get(0);
                List<String> terms = preprocessText(phrase);
                rankingTerms.addAll(terms);
                candidateDocs = positionalIndex.phraseSearch(terms);
            }
            case PROXIMITY -> {
                String term1 = preprocessSingleToken(parsedQuery.getTerms().get(0));
                String term2 = preprocessSingleToken(parsedQuery.getTerms().get(1));
                if (!term1.isBlank() && !term2.isBlank()) {
                    rankingTerms.add(term1);
                    rankingTerms.add(term2);
                    candidateDocs = positionalIndex.proximitySearch(term1, term2, parsedQuery.getProximityDistance());
                } else {
                    candidateDocs = Set.of();
                }
            }
            case WILDCARD -> {
                String wildcardPattern = normalizeForLanguage(parsedQuery.getWildcardPattern());
                Set<String> expandedTerms = kGramIndex.expandWildcard(wildcardPattern);
                rankingTerms.addAll(expandedTerms);
                Set<String> docs = new HashSet<>();
                for (String term : expandedTerms) {
                    docs.addAll(positionalIndex.getPostings(term).keySet());
                }
                candidateDocs = docs;
            }
            case TERM -> {
                String termQuery = parsedQuery.getTerms().get(0);
                rankingTerms.addAll(preprocessText(termQuery));
                candidateDocs = null;

                if (!rankingTerms.isEmpty()) {
                    String suggestedTerm = spellingCorrector.suggest(rankingTerms.get(0));
                    if (suggestedTerm != null && !suggestedTerm.equals(rankingTerms.get(0))) {
                        suggestion = suggestedTerm;
                    }
                }
            }
        }

        Map<String, Double> scores = tfidfCalculator.scoreQuery(rankingTerms, candidateDocs);
        List<SearchResult> results = new ArrayList<>();
        for (Map.Entry<String, Double> entry : scores.entrySet()) {
            results.add(new SearchResult(entry.getKey(), entry.getValue()));
        }
        results.sort(SearchResult::compareTo);

        return new SearchResponse(results, suggestion);
    }

    private List<String> preprocessText(String text) {
        if (languageDetector.detectLanguage(text) == LanguageDetector.Language.ARABIC) {
            return arabicProcessor.preprocess(text);
        }
        return englishProcessor.preprocess(text.toLowerCase(Locale.ROOT));
    }

    private String preprocessSingleToken(String token) {
        List<String> terms = preprocessText(token);
        return terms.isEmpty() ? "" : terms.get(0);
    }

    private String normalizeForLanguage(String wildcard) {
        if (languageDetector.detectLanguage(wildcard) == LanguageDetector.Language.ARABIC) {
            return arabicProcessor.normalize(wildcard).replaceAll("\\s+", "");
        }
        return wildcard.toLowerCase(Locale.ROOT).replaceAll("\\s+", "");
    }
}
