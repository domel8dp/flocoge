package pl.dpawlak.flocoge.diagram;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ModelNamesUtils {

    private static final Set<String> RESERVED_WORDS = new HashSet<>(Arrays.asList("true", "false", "null", "abstract",
        "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do",
        "if", "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import",
        "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short",
        "try", "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile",
        "const", "float", "native", "super", "while"));

    private ModelNamesUtils() { }

    public static boolean validateElementLabel(String label) {
        return label != null && label.replaceAll("<br>", " ").matches("[^a-zA-Z]*[a-zA-Z].*");
    }

    public static boolean isReservedWord(String label) {
        return RESERVED_WORDS.contains(label);
    }

    public static String convertElementLabel(String label) {
        String[] parts = label.replaceAll("<br>|\\W", " ").trim().split(" ");
        StringBuilder nameBuilder = new StringBuilder();
        for (String part : parts) {
            if (part.length() > 0) {
                if (nameBuilder.length() == 0) {
                    nameBuilder.append(Character.toLowerCase(part.charAt(0))).append(part.substring(1));
                } else {
                    nameBuilder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
                }
            }
        }
        String name = nameBuilder.toString();
        if (name.matches("\\d+.*")) {
            Matcher matcher = Pattern.compile("\\d+").matcher(name);
            matcher.find();
            String digits = matcher.group();
            int position = matcher.end();
            nameBuilder = new StringBuilder();
            nameBuilder.append(Character.toLowerCase(name.charAt(position))).append(name.substring(position + 1))
                .append('_').append(digits);
            return nameBuilder.toString();
        } else {
            return name;
        }
    }

    public static String convertConnectionLabel(String label) {
        String[] parts = label.replaceAll("<br>|\\W", " ").trim().split(" ");
        StringBuilder nameBuilder = new StringBuilder();
        boolean first = true;
        for (String part : parts) {
            if (part.length() > 0) {
                if (first) {
                    first = false;
                } else {
                    nameBuilder.append('_');
                }
                nameBuilder.append(part.toUpperCase());
            }
        }
        String name = nameBuilder.toString();
        if (name.matches("[\\d_]+.*")) {
            Matcher matcher = Pattern.compile("[\\d_]+").matcher(name);
            matcher.find();
            String digits = matcher.group();
            if (digits.endsWith("_")) {
                digits = digits.substring(0, digits.length() - 1);
            }
            int position = matcher.end();
            nameBuilder = new StringBuilder();
            nameBuilder.append(name.substring(position)).append('_').append(digits);
            return nameBuilder.toString();
        } else {
            return name;
        }
    }

    public static String createEnumName(String methodName) {
        return new StringBuilder().append(Character.toUpperCase(methodName.charAt(0))).append(methodName.substring(1))
            .append("Result").toString();
    }
}
