package com.tiduswr;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

public class Buffer implements Closeable{
    
    private PushbackReader buffer;
    public static final int EOF = -1;

    public Buffer(String programPath) throws FileNotFoundException{
        buffer = new PushbackReader(new FileReader(programPath));
    }

    public int readNextChar() throws IOException{
        return buffer.read();
    }

    public void pushback(int symbol) throws IOException{
        buffer.unread(symbol);
    }

    @Override
    public void close() throws IOException {
        if(buffer != null) buffer.close();    
    }

}
