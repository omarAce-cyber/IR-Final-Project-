package preprocessing;

public class LanguageDetector {
    public enum Language {
        ENGLISH,
        ARABIC
    }

    public Language detectLanguage(String text) {
        if (text == null || text.isBlank()) {
            return Language.ENGLISH;
        }
        for (char ch : text.toCharArray()) {
            if (ch >= '\u0600' && ch <= '\u06FF') {
                return Language.ARABIC;
            }
        }
        return Language.ENGLISH;
    }
}
