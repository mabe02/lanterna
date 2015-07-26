package com.googlecode.lanterna.input;

import com.googlecode.lanterna.TerminalPosition;

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
        MouseActionType actionType = null;
        int button = 0;
        int lowerTwoBitsAndMouseWheel = matching.get(3).charValue() & 0x43;
        switch(lowerTwoBitsAndMouseWheel) {
            case(0):
            case(1):
            case(2):
                actionType = MouseActionType.CLICK_DOWN;
                button = lowerTwoBitsAndMouseWheel + 1;
                break;
            case(3):
                actionType = MouseActionType.CLICK_RELEASE;
                button = 0;
                break;
            case(64):
                actionType = MouseActionType.SCROLL_UP;
                button = 4;
                break;
            case(65):
                actionType = MouseActionType.SCROLL_DOWN;
                button = 5;
                break;
        }
        int x = matching.get(4).charValue() - 33;
        int y = matching.get(5).charValue() - 33;

        return new MouseAction(actionType, button, new TerminalPosition(x, y));
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
