package preprocessing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class EnglishProcessor {
    private final Set<String> stopwords = new HashSet<>();

    public EnglishProcessor(Path stopwordsPath) throws IOException {
        if (Files.exists(stopwordsPath)) {
            for (String line : Files.readAllLines(stopwordsPath)) {
                String word = line.trim().toLowerCase(Locale.ROOT);
                if (!word.isEmpty()) {
                    stopwords.add(word);
                }
            }
        }
    }

    public List<String> preprocess(String text) {
        List<String> terms = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return terms;
        }

        String normalized = text.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9\\s]", " ");
        for (String token : normalized.split("\\s+")) {
            if (!token.isBlank() && !stopwords.contains(token)) {
                terms.add(token);
            }
        }
        return terms;
    }
}
