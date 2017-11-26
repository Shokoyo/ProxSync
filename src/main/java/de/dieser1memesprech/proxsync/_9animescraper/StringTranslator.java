package de.dieser1memesprech.proxsync._9animescraper;

import java.util.HashMap;
import java.util.Map;

public class StringTranslator {
    private Map<Character, Character> translationMap;

    public StringTranslator(String from, String to) {
        translationMap = new HashMap<Character, Character>();

        if (from.length() != to.length())
            throw new IllegalArgumentException("The from and to strings must be of the same length");

        for (int i = 0; i < from.length(); i++)
            translationMap.put(from.charAt(i), to.charAt(i));
    }

    public String translate(String str) {
        StringBuilder buffer = new StringBuilder(str);

        for (int i = 0; i < buffer.length(); i++) {
            Character ch = buffer.charAt(i);
            Character replacement = translationMap.get(ch);
            if (replacement != null)
                buffer.replace(i, i + 1, "" + replacement);
        }

        return buffer.toString();
    }

    public String translate(String str, String deleteChars) {
        StringBuilder buffer = new StringBuilder(str);
        char[] deletions = deleteChars.toCharArray();
        for (char ch : deletions) {
            int index;
            if ((index = buffer.indexOf("" + ch)) != -1)
                buffer.deleteCharAt(index);
        }

        return translate(buffer.toString());
    }
}
