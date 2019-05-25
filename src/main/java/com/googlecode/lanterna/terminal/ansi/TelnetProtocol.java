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
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.terminal.ansi;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains the telnet protocol commands, although not a complete set.
 * @author Martin
 */
class TelnetProtocol {
    public static final byte COMMAND_SUBNEGOTIATION_END = (byte)0xf0;  //SE
    public static final byte COMMAND_NO_OPERATION = (byte)0xf1;    //NOP
    public static final byte COMMAND_DATA_MARK = (byte)0xf2;   //DM
    public static final byte COMMAND_BREAK = (byte)0xf3;       //BRK
    public static final byte COMMAND_INTERRUPT_PROCESS = (byte)0xf4;   //IP
    public static final byte COMMAND_ABORT_OUTPUT = (byte)0xf5;    //AO
    public static final byte COMMAND_ARE_YOU_THERE = (byte)0xf6;   //AYT
    public static final byte COMMAND_ERASE_CHARACTER = (byte)0xf7; //EC
    public static final byte COMMAND_ERASE_LINE = (byte)0xf8;  //WL
    public static final byte COMMAND_GO_AHEAD = (byte)0xf9;    //GA
    public static final byte COMMAND_SUBNEGOTIATION = (byte)0xfa;  //SB
    public static final byte COMMAND_WILL = (byte)0xfb;
    public static final byte COMMAND_WONT = (byte)0xfc;
    public static final byte COMMAND_DO = (byte)0xfd;
    public static final byte COMMAND_DONT = (byte)0xfe;
    public static final byte COMMAND_IAC = (byte)0xff;

    public static final byte OPTION_TRANSMIT_BINARY = (byte)0x00;
    public static final byte OPTION_ECHO = (byte)0x01;
    public static final byte OPTION_SUPPRESS_GO_AHEAD = (byte)0x03;
    public static final byte OPTION_STATUS = (byte)0x05;
    public static final byte OPTION_TIMING_MARK = (byte)0x06;
    public static final byte OPTION_NAOCRD = (byte)0x0a;
    public static final byte OPTION_NAOHTS = (byte)0x0b;
    public static final byte OPTION_NAOHTD = (byte)0x0c;
    public static final byte OPTION_NAOFFD = (byte)0x0d;
    public static final byte OPTION_NAOVTS = (byte)0x0e;
    public static final byte OPTION_NAOVTD = (byte)0x0f;
    public static final byte OPTION_NAOLFD = (byte)0x10;
    public static final byte OPTION_EXTEND_ASCII = (byte)0x01;
    public static final byte OPTION_TERMINAL_TYPE = (byte)0x18;
    public static final byte OPTION_NAWS = (byte)0x1f;
    public static final byte OPTION_TERMINAL_SPEED = (byte)0x20;
    public static final byte OPTION_TOGGLE_FLOW_CONTROL = (byte)0x21;
    public static final byte OPTION_LINEMODE = (byte)0x22;
    public static final byte OPTION_AUTHENTICATION = (byte)0x25;

    public static final Map<String, Byte> NAME_TO_CODE = createName2CodeMap();
    public static final Map<Byte, String> CODE_TO_NAME = reverseMap(NAME_TO_CODE);
    
    private static Map<String, Byte> createName2CodeMap() {
        Map<String, Byte> result = new HashMap<String, Byte>();
        for(Field field: TelnetProtocol.class.getDeclaredFields()) {
            if(field.getType() != byte.class || (!field.getName().startsWith("COMMAND_") && !field.getName().startsWith("OPTION_"))) {
                continue;
            }
            try {
                String namePart = field.getName().substring(field.getName().indexOf("_") + 1);
                result.put(namePart, (Byte)field.get(null));
            }
            catch(IllegalAccessException ignored) {
            }
            catch(IllegalArgumentException ignored) {
            }
        }
        return Collections.unmodifiableMap(result);
    }

    private static <V,K> Map<V,K> reverseMap(Map<K,V> n2c) {
        Map<V, K> result = new HashMap<V,K>();
        for (Map.Entry<K, V> e : n2c.entrySet()) {
            result.put(e.getValue(), e.getKey());
        }
        return Collections.unmodifiableMap(result);
    }
    /** Cannot instantiate. */
    private TelnetProtocol() {}
}
