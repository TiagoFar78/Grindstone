package io.github.tiagofar78.grindstone.util;

public class TextFormatting {

    public static String formatCentered(String message, int maxWidth) {
        if (message == null || message.isEmpty() || maxWidth <= 0) {
            return "";
        }

        String[] words = message.split("\\s+");
        StringBuilder result = new StringBuilder();
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (word.length() > maxWidth) {
                if (currentLine.length() > 0) {
                    result.append(centerLine(currentLine.toString(), maxWidth)).append("\n");
                    currentLine.setLength(0);
                }

                int index = 0;
                while (index < word.length()) {
                    int end = Math.min(index + maxWidth, word.length());
                    result.append(centerLine(word.substring(index, end), maxWidth)).append("\n");
                    index = end;
                }
                continue;
            }

            if (currentLine.length() == 0) {
                currentLine.append(word);
            } else if (currentLine.length() + 1 + word.length() <= maxWidth) {
                currentLine.append(" ").append(word);
            } else {
                result.append(centerLine(currentLine.toString(), maxWidth)).append("\n");
                currentLine.setLength(0);
                currentLine.append(word);
            }
        }

        if (currentLine.length() > 0) {
            result.append(centerLine(currentLine.toString(), maxWidth));
        }

        return result.toString();
    }

    private static String centerLine(String line, int maxWidth) {
        int padding = (maxWidth - line.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + line;
    }

}
