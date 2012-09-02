/*
 * This file is part of lanterna (http://code.google.com/p/lanterna/).
 * 
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) 2010-2012 Martin
 */

package com.googlecode.lanterna.input;

import com.googlecode.lanterna.terminal.TerminalPosition;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class recognizes character combinations which are actually a cursor
 * position report. See 
 * <a href="http://en.wikipedia.org/wiki/ANSI_escape_code">Wikipedia</a>'s 
 * article on ANSI escape codes for more information about how cursor position
 * reporting works ("DSR – Device Status Report").
 * @author mberglun
 */
public class ScreenInfoCharacterPattern implements CharacterPattern
{
    private static final Pattern REPORT_CURSOR_PATTERN =
            Pattern.compile("\\[([0-9]+);([0-9]+)R");

    public ScreenInfoCharacterPattern()
    {
    }

    public Key getResult(List<Character> matching)
    {
        return new Key(Key.Kind.CursorLocation);
    }

    public boolean isCompleteMatch(List<Character> currentMatching)
    {
        if(currentMatching.isEmpty())
            return false;

        if(currentMatching.get(0) != KeyMappingProfile.ESC_CODE)
            return false;

        String asString = "";
        for(int i = 1; i < currentMatching.size(); i++)
            asString += currentMatching.get(i);

        Matcher matcher = REPORT_CURSOR_PATTERN.matcher(asString);
        if(!matcher.matches())
            return false;

        return true;
    }

    public boolean matches(List<Character> currentMatching)
    {
        if(currentMatching.isEmpty())
            return true;

        if(currentMatching.get(0) != KeyMappingProfile.ESC_CODE)
            return false;
        if(currentMatching.size() == 1)
            return true;

        if(currentMatching.get(1) != '[')
            return false;
        if(currentMatching.size() == 2)
            return true;

        int i = 2;
        for(i = 2; i < currentMatching.size(); i++) {
            if(!Character.isDigit(currentMatching.get(i)) && ';' != currentMatching.get(i))
                return false;
            
            if(';' == currentMatching.get(i))
                break;
        }

        if(i == currentMatching.size())
            return true;

        for(i = i+1; i < currentMatching.size(); i++) {
            if(!Character.isDigit(currentMatching.get(i)) && 'R' != currentMatching.get(i))
                return false;

            if('R' == currentMatching.get(i))
                break;
        }

        return true;
    }

    public static TerminalPosition getCursorPosition(List<Character> currentMatching)
    {
        if(currentMatching.isEmpty())
            return null;

        if(currentMatching.get(0) != KeyMappingProfile.ESC_CODE)
            return null;

        String asString = "";
        for(int i = 1; i < currentMatching.size(); i++)
            asString += currentMatching.get(i);

        Matcher matcher = REPORT_CURSOR_PATTERN.matcher(asString);
        if(!matcher.matches())
            return null;

        return new TerminalPosition(Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(1)));
    }
}
