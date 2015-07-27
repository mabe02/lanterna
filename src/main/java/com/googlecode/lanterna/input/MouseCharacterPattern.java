package com.googlecode.lanterna.input;

import com.googlecode.lanterna.TerminalPosition;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Pattern used to detect Xterm-protocol mouse events coming in on the standard input channel
 * Created by martin on 19/07/15.
 */
public class MouseCharacterPattern implements CharacterPattern {

    private static final Pattern MOUSE_PATTERN
            = Pattern.compile(KeyDecodingProfile.ESC_CODE + "\\[M(.)(.)(.)");

    @Override
    public KeyStroke getResult(List<Character> matching) {
        MouseActionType actionType = null;
        int button = (matching.get(3) & 0x3) + 1;
        if(button == 4) {
            //If last two bits are both set, it means button click release
            button = 0;
        }
        int actionCode = (matching.get(3) & 0x60) >> 5;
        switch(actionCode) {
            case(1):
                if(button > 0) {
                    actionType = MouseActionType.CLICK_DOWN;
                }
                else {
                    actionType = MouseActionType.CLICK_RELEASE;
                }
                break;
            case(2):
                if(button == 0) {
                    actionType = MouseActionType.MOVE;
                }
                else {
                    actionType = MouseActionType.DRAG;
                }
                break;
            case(3):
                if(button == 1) {
                    actionType = MouseActionType.SCROLL_UP;
                    button = 4;
                }
                else {
                    actionType = MouseActionType.SCROLL_DOWN;
                    button = 5;
                }
                break;
        }
        int x = matching.get(4) - 33;
        int y = matching.get(5) - 33;

        return new MouseAction(actionType, button, new TerminalPosition(x, y));
    }

    @Override
    public boolean isCompleteMatch(List<Character> currentMatching) {
        String asString = "";
        for(Character aCurrentMatching : currentMatching) {
            asString += aCurrentMatching;
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
