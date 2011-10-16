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

package org.lantern.terminal;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.lantern.LanternException;

/**
 *
 * @author mabe02
 */
public class TerminalProperties
{
    private final Map<String, String> environment;

    private TerminalProperties(Map<String, String> environment) {
        this.environment = new HashMap<String, String>(environment);
    }

    public boolean hasPrivateBufferMode()
    {
        return environment.containsKey("enter_ca_mode");
    }

    public String getClearScreenString()
    {
        return environment.get("clear_screen");
    }

    public String getCursorPositionString()
    {
        return environment.get("cursor_address");
    }

    public String getEnterPrivateModeString()
    {
        return environment.get("enter_ca_mode");
    }

    public String getExitPrivateModeString()
    {
        return environment.get("exit_ca_mode");
    }

    public String getEnterBoldModeString()
    {
        return environment.get("enter_bold_mode");
    }

    public String getEnterReverseModeString()
    {
        return environment.get("enter_reverse_mode");
    }

    public String getExitReverseModeString()
    {
        return environment.get("exit_standout_mode");
    }

    public String getEnterUnderlineModeString()
    {
        return environment.get("enter_underline_mode");
    }

    public String getExitUnderlineModeString()
    {
        return environment.get("exit_underline_mode");
    }

    public String getSetForegroundColorString()
    {
        return environment.get("set_a_foreground");
    }

    public String getSetBackgroundColorString()
    {
        return environment.get("set_a_background");
    }

    public String getKeyCursorUp()
    {
        return environment.get("cursor_up");
    }

    public String getKeyCursorDown()
    {
        return environment.get("cursor_down");
    }

    public String getKeyCursorRight()
    {
        return environment.get("cursor_right");
    }

    public String getKeyCursorLeft()
    {
        return environment.get("cursor_left");
    }

    public String getKeyReverseTab()
    {
        return environment.get("back_tab");
    }

    public String getKeyInsert()
    {
        return environment.get("key_ic");
    }

    public String getKeyDelete()
    {
        return environment.get("key_dc");
    }

    public String getKeyHome()
    {
        return environment.get("key_home");
    }

    public String getKeyEnd()
    {
        return environment.get("key_end");
    }

    public String getKeyPageUp()
    {
        return environment.get("key_ppage");
    }

    public String getKeyPageDown()
    {
        return environment.get("key_npage");
    }

    public static TerminalProperties query() throws LanternException
    {
        final Pattern keyValuePattern = Pattern.compile("([a-z_0-9]+)=(.*)");
        final Map<String, String> parameters = new HashMap<String, String>();

        String infocmpOutput = ShellCommand.exec("infocmp", "-L");
        String []entries = infocmpOutput.split(",");
        for(String entry: entries) {
            Matcher matcher = keyValuePattern.matcher(entry.trim());
            if(matcher.matches()) {
                parameters.put(matcher.group(1), matcher.group(2));
            }
        }
        return new TerminalProperties(parameters);
    }
}
