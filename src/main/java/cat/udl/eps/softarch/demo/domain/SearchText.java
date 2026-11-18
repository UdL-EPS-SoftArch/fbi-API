package cat.udl.eps.softarch.demo.domain;

import java.text.Normalizer;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Normalizes text for case- and accent-insensitive search (US-COM-04): "Àlgebra Lineal" and
 * "algebra lineal" become the same text. Done in Java, so it works the same on H2 and PostgreSQL.
 */
public final class SearchText {
    private static final Pattern MARKS = Pattern.compile("\\p{M}+");
    private static final Pattern SPACES = Pattern.compile("\\s+");

    private SearchText() {
    }

    /** Lowercase, without accents and with single spaces; "" for null. */
    public static String normalize(String text) {
        if (text == null) {
            return "";
        }
        String withoutAccents = MARKS.matcher(Normalizer.normalize(text, Normalizer.Form.NFD)).replaceAll("");
        return SPACES.matcher(withoutAccents.toLowerCase(Locale.ROOT)).replaceAll(" ").trim();
    }

    /** Tags as stored: trimmed, lowercase and without duplicates (accents are kept for display). */
    public static Set<String> normalizeTags(Collection<String> tags) {
        Set<String> normalized = new LinkedHashSet<>();
        if (tags != null) {
            tags.stream()
                    .filter(Objects::nonNull)
                    .map(tag -> SPACES.matcher(tag.toLowerCase(Locale.ROOT)).replaceAll(" ").trim())
                    .filter(tag -> !tag.isEmpty())
                    .forEach(normalized::add);
        }
        return normalized;
    }

    /**
     * Tags of a search filter, normalized like the stored ones. A query cannot use an empty IN
     * list, so "no tags" becomes a value no tag can have; the query ignores it.
     */
    public static List<String> tagFilter(Collection<String> tags) {
        Set<String> normalized = normalizeTags(tags);
        return normalized.isEmpty() ? List.of("") : List.copyOf(normalized);
    }

    public static boolean hasTags(Collection<String> tags) {
        return !normalizeTags(tags).isEmpty();
    }
}
