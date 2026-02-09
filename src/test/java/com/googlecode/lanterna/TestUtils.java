/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
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
 * Copyright (C) 2010-2026 Martin Berglund
 */
package com.googlecode.lanterna;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

public class TestUtils {
    private TestUtils() {}

    private static final String someText =
          "\n" + " This file is part of lanterna (https://github.com/mabe02/lanterna)."
        + "\n" + ""
        + "\n" + " lanterna is free software: you can redistribute it and/or modify"
        + "\n" + " it under the terms of the GNU Lesser General Public License as published by"
        + "\n" + " the Free Software Foundation, either version 3 of the License, or"
        + "\n" + " (at your option) any later version."
        + "\n" + ""
        + "\n" + " This program is distributed in the hope that it will be useful,"
        + "\n" + " but WITHOUT ANY WARRANTY; without even the implied warranty of"
        + "\n" + " MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the"
        + "\n" + " GNU Lesser General Public License for more details."
        + "\n" + ""
        + "\n" + " You should have received a copy of the GNU Lesser General Public License"
        + "\n" + " along with this program.  If not, see <http://www.gnu.org/licenses/>."
        + "\n" + ""
        + "\n" + " Copyright (C) 2010-2026 Martin Berglund"
        .trim();

    public static String someText() {
        return someText;
    }

    public static String downloadGPL() {
        try {
            URL url = new URL("http://www.gnu.org/licenses/gpl.txt");
            try (InputStream inputStream = url.openStream()) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[32 * 1024];
                int readBytes = 0;
                while (readBytes != -1) {
                    readBytes = inputStream.read(buffer);
                    if (readBytes > 0) {
                        byteArrayOutputStream.write(buffer, 0, readBytes);
                    }
                }
                return new String(byteArrayOutputStream.toByteArray());
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
