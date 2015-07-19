package com.googlecode.lanterna.input;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by martin on 19/07/15.
 */
public class MouseCharacterPattern implements CharacterPattern {

    private static final Pattern MOUSE_PATTERN
            = Pattern.compile(KeyDecodingProfile.ESC_CODE + "\\[M(.)(.)(.)");

    @Override
    public KeyStroke getResult(List<Character> matching) {
        return new KeyStroke(KeyType.MouseClickPressed);
    }

    @Override
    public boolean isCompleteMatch(List<Character> currentMatching) {
        String asString = "";
        for (int i = 0; i < currentMatching.size(); i++) {
            asString += currentMatching.get(i);
        }
        return MOUSE_PATTERN.matcher(asString).matches();
    }

    @Override
    public boolean matches(List<Character> currentMatching) {
        if (currentMatching.isEmpty()) {
            return false;
        }
        if (currentMatching.get(0) != KeyDecodingProfile.ESC_CODE) {
            return false;
        }
        if (currentMatching.size() == 1) {
            return true;
        }
        if (currentMatching.get(1) != '[') {
            return false;
        }
        if (currentMatching.size() == 2) {
            return true;
        }
        if (currentMatching.get(2) != 'M') {
            return false;
        }
        if (currentMatching.size() == 3) {
            return true;
        }
        return true;
    }
}
