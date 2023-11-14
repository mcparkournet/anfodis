package net.mcparkour.anfodis;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class TestOutputStream extends OutputStream {

    private final List<Byte> buffer;

    public TestOutputStream() {
        this.buffer = new ArrayList<>(64);
    }

    public String pop() {
        byte[] buffer = this.bufferToArray();
        var output = new String(buffer);
        this.buffer.clear();
        return output;
    }

    private byte[] bufferToArray() {
        int size = this.buffer.size();
        var array = new byte[size];
        for (int index = 0; index < size; index++) {
            array[index] = this.buffer.get(index);
        }
        return array;
    }

    @Override
    public void write(final int b) {
        this.buffer.add((byte) b);
    }
}
