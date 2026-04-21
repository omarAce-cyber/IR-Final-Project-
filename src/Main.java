import evaluation.Evaluator;
import gui.SearchGUI;
import indexing.KGramIndex;
import indexing.PositionalInvertedIndex;
import preprocessing.ArabicProcessor;
import preprocessing.EnglishProcessor;
import preprocessing.LanguageDetector;
import query.SearchEngine;
import query.SpellingCorrector;
import ranking.TFIDFCalculator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        try {
            Path projectRoot = Path.of("").toAbsolutePath();
            Path englishDocsPath = projectRoot.resolve("documents/english");
            Path arabicDocsPath = projectRoot.resolve("documents/arabic");

            LanguageDetector languageDetector = new LanguageDetector();
            EnglishProcessor englishProcessor = new EnglishProcessor(projectRoot.resolve("data/stopwords_en.txt"));
            ArabicProcessor arabicProcessor = new ArabicProcessor(projectRoot.resolve("data/stopwords_ar.txt"));

            PositionalInvertedIndex positionalIndex = new PositionalInvertedIndex();
            List<String> documentIds = new ArrayList<>();

            loadDocuments(englishDocsPath, "english", englishProcessor, arabicProcessor, positionalIndex, documentIds);
            loadDocuments(arabicDocsPath, "arabic", englishProcessor, arabicProcessor, positionalIndex, documentIds);

            KGramIndex kGramIndex = new KGramIndex(3);
            kGramIndex.build(positionalIndex.getVocabulary());

            TFIDFCalculator tfidfCalculator = new TFIDFCalculator(positionalIndex, documentIds);
            SpellingCorrector spellingCorrector = new SpellingCorrector(positionalIndex.getVocabulary(), kGramIndex);

            SearchEngine searchEngine = new SearchEngine(
                    languageDetector,
                    englishProcessor,
                    arabicProcessor,
                    positionalIndex,
                    kGramIndex,
                    tfidfCalculator,
                    spellingCorrector
            );

            Evaluator evaluator = new Evaluator();
            evaluator.evaluateAndSave(searchEngine, sampleRelevance(), projectRoot.resolve("results/evaluation_results.txt"));
            evaluator.saveSampleQueryOutputs(
                    searchEngine,
                    List.of("computer", "\"machine learning\"", "employment /3 place", "comput*", "\"الذكاء الاصطناعي\""),
                    projectRoot.resolve("results/sample_queries.txt")
            );

            SearchGUI gui = new SearchGUI(searchEngine);
            gui.launch();
        } catch (Exception e) {
            System.err.println("Failed to start the application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void loadDocuments(
            Path folder,
            String language,
            EnglishProcessor englishProcessor,
            ArabicProcessor arabicProcessor,
            PositionalInvertedIndex positionalIndex,
            List<String> documentIds
    ) throws IOException {
        if (!Files.exists(folder)) {
            return;
        }

        Files.list(folder)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".txt"))
                .sorted()
                .forEach(path -> {
                    try {
                        String content = Files.readString(path);
                        String docId = language + "/" + path.getFileName();
                        List<String> terms = "arabic".equals(language)
                                ? arabicProcessor.preprocess(content)
                                : englishProcessor.preprocess(content);

                        positionalIndex.addDocument(docId, terms);
                        documentIds.add(docId);
                    } catch (IOException ioException) {
                        throw new RuntimeException(ioException);
                    }
                });
    }

    private static Map<String, Set<String>> sampleRelevance() {
        Map<String, Set<String>> relevance = new HashMap<>();
        relevance.put("machine learning", Set.of("english/doc1.txt"));
        relevance.put("information retrieval", Set.of("english/doc2.txt", "arabic/doc2.txt"));
        relevance.put("\"الذكاء الاصطناعي\"", Set.of("arabic/doc1.txt"));
        relevance.put("employment /3 place", Set.of("english/doc3.txt"));
        return relevance;
    }
}
