/*
 * X10 mouse fallback for terminals that don't support SGR mode 1006
 * (e.g. ConPTY on WSL2). Lanterna 3.2+ only parses SGR format
 * (ESC [ < button ; col ; row M/m), so X10 events (ESC [ M Cb Cx Cy)
 * would be silently ignored. This InputStream wrapper converts X10
 * to SGR at the byte level, before UTF-8 decoding. SGR events
 * (ESC [ <) pass through unchanged.
 */
package com.googlecode.lanterna.input;

import java.io.IOException;
import java.io.InputStream;

public class MouseTranslatingInputStream extends InputStream {

    private final InputStream source;

    // Output buffer for converted SGR sequences
    private final byte[] outBuf = new byte[48];
    private int outPos = 0;
    private int outLen = 0;

    public MouseTranslatingInputStream(InputStream source) {
        this.source = source;
    }

    @Override
    public int available() throws IOException {
        return (outLen - outPos) + source.available();
    }

    @Override
    public void close() throws IOException {
        source.close();
    }

    private void bufferByte(int b) {
        outBuf[outLen++] = (byte) b;
    }

    private void bufferDecimal(int n) {
        String s = Integer.toString(n);
        for (int i = 0; i < s.length(); i++) {
            bufferByte(s.charAt(i));
        }
    }

    private int readOne() throws IOException {
        // Return from buffer if non-empty
        if (outPos < outLen) {
            int b = outBuf[outPos++] & 0xFF;
            if (outPos >= outLen) {
                outPos = 0;
                outLen = 0;
            }
            return b;
        }

        int b = source.read();
        if (b != 27) {  // not ESC, pass through
            return b;
        }

        // Saw ESC - look for [ M (X10 mouse)
        int b2 = source.read();
        if (b2 == -1) {
            return 27;
        }
        if (b2 != '[') {
            outPos = 0;
            outLen = 0;
            bufferByte(b2);
            return 27;
        }

        // Saw ESC [ - check for M
        int b3 = source.read();
        if (b3 == -1) {
            outPos = 0;
            outLen = 0;
            bufferByte('[');
            return 27;
        }
        if (b3 != 'M') {
            // Not X10 mouse, pass through ESC [ <b3>
            outPos = 0;
            outLen = 0;
            bufferByte('[');
            bufferByte(b3);
            return 27;
        }

        // X10 mouse event: ESC [ M Cb Cx Cy
        int cb = source.read();
        int cx = source.read();
        int cy = source.read();
        if (cb == -1 || cx == -1 || cy == -1) {
            return -1;
        }

        // Convert to SGR: ESC [ < button ; col ; row M/m
        int button = cb - 32;
        int col = cx - 32;
        int row = cy - 32;
        boolean isRelease = (button & 3) == 3 && (button & 64) == 0;

        outPos = 0;
        outLen = 0;
        bufferByte('[');
        bufferByte('<');
        bufferDecimal(button);
        bufferByte(';');
        bufferDecimal(col);
        bufferByte(';');
        bufferDecimal(row);
        bufferByte(isRelease ? 'm' : 'M');
        return 27;  // return ESC
    }

    @Override
    public int read() throws IOException {
        return readOne();
    }

    @Override
    public int read(byte[] buf, int off, int len) throws IOException {
        if (len <= 0) {
            return 0;
        }
        // First byte: blocking
        int first = readOne();
        if (first == -1) {
            return -1;
        }
        buf[off] = (byte) first;
        // Remaining bytes: only if immediately available
        int i = 1;
        while (i < len && available() > 0) {
            int b = readOne();
            if (b == -1) {
                break;
            }
            buf[off + i] = (byte) b;
            i++;
        }
        return i;
    }
}
