package org.test.loader.util;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

public class CharFilterReader extends FilterReader {

    public CharFilterReader(Reader in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        int c = super.read();
        if (c != -1) {
            return filter((char) c);
        } else {
            return -1;
        }
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int processed = super.read(cbuf, off, len);

        for (int i = 0; i < processed; i++) {
            cbuf[i] = filter(cbuf[i]);
        }

        return processed;
    }

    private char filter(char c) {
        if (c == 'ё') {
            return 'е';
        } else {
            return c;
        }
    }
}
