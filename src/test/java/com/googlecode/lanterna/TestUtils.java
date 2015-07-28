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
