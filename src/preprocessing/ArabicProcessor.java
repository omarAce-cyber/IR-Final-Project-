package preprocessing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArabicProcessor {
    private final Set<String> stopwords = new HashSet<>();

    public ArabicProcessor(Path stopwordsPath) throws IOException {
        if (Files.exists(stopwordsPath)) {
            for (String line : Files.readAllLines(stopwordsPath)) {
                String word = normalize(line.trim());
                if (!word.isEmpty()) {
                    stopwords.add(word);
                }
            }
        }
    }

    public String normalize(String text) {
        if (text == null) {
            return "";
        }
        String normalized = text
                .replace('أ', 'ا')
                .replace('إ', 'ا')
                .replace('آ', 'ا')
                .replace('ى', 'ي')
                .replace('ؤ', 'و')
                .replace('ئ', 'ي')
                .replace('ة', 'ه');

        normalized = normalized.replaceAll("[\\u064B-\\u0652]", "");
        normalized = normalized.replaceAll("[^\\u0600-\\u06FF\\s0-9]", " ");
        return normalized;
    }

    public List<String> preprocess(String text) {
        List<String> terms = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return terms;
        }

        String normalized = normalize(text);
        for (String token : normalized.split("\\s+")) {
            if (!token.isBlank() && !stopwords.contains(token)) {
                terms.add(token);
            }
        }
        return terms;
    }
}
