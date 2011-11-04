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
 * Copyright (C) 2010-2011 mabe02
 */

package org.lantern.input;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.lantern.terminal.TerminalPosition;

/**
 *
 * @author mberglun
 */
public class ScreenInfoCharacterPattern implements CharacterPattern
{
    private static final Pattern REPORT_CURSOR_PATTERN =
            Pattern.compile("\\[([0-9]+);([0-9]+)R");

    public ScreenInfoCharacterPattern()
    {
    }

    public Key getResult()
    {
        return new Key(Key.Kind.CursorLocation);
    }

    public boolean isCompleteMatch(List currentMatching)
    {
        if(currentMatching.isEmpty())
            return false;

        if(((Character)currentMatching.get(0)).charValue() != KeyMappingProfile.ESC_CODE)
            return false;

        String asString = "";
        for(int i = 1; i < currentMatching.size(); i++)
            asString += currentMatching.get(i);

        Matcher matcher = REPORT_CURSOR_PATTERN.matcher(asString);
        if(!matcher.matches())
            return false;

        return true;
    }

    public boolean matches(List currentMatching)
    {
        if(currentMatching.isEmpty())
            return true;

        if(((Character)currentMatching.get(0)).charValue() != KeyMappingProfile.ESC_CODE)
            return false;
        if(currentMatching.size() == 1)
            return true;

        if(((Character)currentMatching.get(1)).charValue() != '[')
            return false;
        if(currentMatching.size() == 2)
            return true;

        int i = 2;
        for(i = 2; i < currentMatching.size(); i++) {
            if(!Character.isDigit(((Character)currentMatching.get(i)).charValue()) && ';' != ((Character)currentMatching.get(i)).charValue())
                return false;
            
            if(';' == ((Character)currentMatching.get(i)).charValue())
                break;
        }

        if(i == currentMatching.size())
            return true;

        for(i = i+1; i < currentMatching.size(); i++) {
            if(!Character.isDigit(((Character)currentMatching.get(i)).charValue()) && 'R' != ((Character)currentMatching.get(i)).charValue())
                return false;

            if('R' == ((Character)currentMatching.get(i)).charValue())
                break;
        }

        return true;
    }

    public static TerminalPosition getCursorPosition(List currentMatching)
    {
        if(currentMatching.isEmpty())
            return null;

        if(((Character)currentMatching.get(0)).charValue() != KeyMappingProfile.ESC_CODE)
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
