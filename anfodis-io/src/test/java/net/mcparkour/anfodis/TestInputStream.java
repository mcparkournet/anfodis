package net.mcparkour.anfodis;

import java.io.InputStream;

public class TestInputStream extends InputStream {

    private byte[] currentLine;
    private int index;

    public void pushLine(final String line) {
        this.currentLine = line.getBytes();
        this.index = 0;
    }

    @Override
    public int read() {
        if (this.currentLine == null || this.index == this.currentLine.length) {
            return -1;
        }

        byte b = this.currentLine[this.index];
        this.index++;
        return b & 0xFF;
    }
}
