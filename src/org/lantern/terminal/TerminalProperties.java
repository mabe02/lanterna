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
    private final Map environment;

    private TerminalProperties(Map environment) {
        this.environment = new HashMap(environment);
    }

    public boolean hasPrivateBufferMode()
    {
        return environment.containsKey("enter_ca_mode");
    }

    public String getClearScreenString()
    {
        return (String)environment.get("clear_screen");
    }

    public String getCursorPositionString()
    {
        return (String)environment.get("cursor_address");
    }

    public String getEnterPrivateModeString()
    {
        return (String)environment.get("enter_ca_mode");
    }

    public String getExitPrivateModeString()
    {
        return (String)environment.get("exit_ca_mode");
    }

    public String getEnterBoldModeString()
    {
        return (String)environment.get("enter_bold_mode");
    }

    public String getEnterReverseModeString()
    {
        return (String)environment.get("enter_reverse_mode");
    }

    public String getExitReverseModeString()
    {
        return (String)environment.get("exit_standout_mode");
    }

    public String getEnterUnderlineModeString()
    {
        return (String)environment.get("enter_underline_mode");
    }
    
    public String getExitUnderlineModeString()
    {
        return (String)environment.get("exit_underline_mode");
    }
    
    public String getEnterBlinkMode()
    {
    	return (String)environment.get("enter_blink_mode");
    }
    
    public String getSetForegroundColorString()
    {
        return (String)environment.get("set_a_foreground");
    }

    public String getSetBackgroundColorString()
    {
        return (String)environment.get("set_a_background");
    }

    public String getKeyCursorUp()
    {
        return (String)environment.get("cursor_up");
    }

    public String getKeyCursorDown()
    {
        return (String)environment.get("cursor_down");
    }

    public String getKeyCursorRight()
    {
        return (String)environment.get("cursor_right");
    }

    public String getKeyCursorLeft()
    {
        return (String)environment.get("cursor_left");
    }

    public String getKeyReverseTab()
    {
        return (String)environment.get("back_tab");
    }

    public String getKeyInsert()
    {
        return (String)environment.get("key_ic");
    }

    public String getKeyDelete()
    {
        return (String)environment.get("key_dc");
    }

    public String getKeyHome()
    {
        return (String)environment.get("key_home");
    }

    public String getKeyEnd()
    {
        return (String)environment.get("key_end");
    }

    public String getKeyPageUp()
    {
        return (String)environment.get("key_ppage");
    }

    public String getKeyPageDown()
    {
        return (String)environment.get("key_npage");
    }

    public static TerminalProperties query() throws LanternException
    {
        final Pattern keyValuePattern = Pattern.compile("([a-z_0-9]+)=(.*)");
        final Map parameters = new HashMap();

        String infocmpOutput = ShellCommand.exec(new String[] {"infocmp", "-L"});
        String []entries = infocmpOutput.split(",");
        for(int i = 0; i < entries.length; i++) {
            String entry = entries[i];
            Matcher matcher = keyValuePattern.matcher(entry.trim());
            if(matcher.matches()) {
                parameters.put(matcher.group(1), matcher.group(2));
            }
        }
        return new TerminalProperties(parameters);
    }
}
