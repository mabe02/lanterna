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
 * Copyright (C) 2010-2016 Martin
 */
package com.googlecode.lanterna;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

/**
 * Created by martin on 28/07/15.
 */
public class TestUtils {
    private TestUtils() {}

    public static String downloadGPL() {
        try {
            URL url = new URL("http://www.gnu.org/licenses/gpl.txt");
            InputStream inputStream = url.openStream();
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[32 * 1024];
                int readBytes = 0;
                while(readBytes != -1) {
                    readBytes = inputStream.read(buffer);
                    if(readBytes > 0) {
                        byteArrayOutputStream.write(buffer, 0, readBytes);
                    }
                }
                return new String(byteArrayOutputStream.toByteArray());
            }
            finally {
                inputStream.close();
            }
        }
        catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            return stringWriter.toString();
        }
    }
}
