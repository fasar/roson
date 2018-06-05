package fr.fasar.roson;

import java.time.Instant;

public class Wrapper {
    public final byte[] buffer;
    public final int bufferOffset;
    public final int pageBuffer;
    public final int read;
    public final Instant ts;

    public Wrapper(byte[] buffer, int bufferOffset, int pageBuffer, int read, Instant ts) {
        this.buffer = buffer;
        this.bufferOffset = bufferOffset;
        this.pageBuffer = pageBuffer;
        this.read = read;
        this.ts = ts;
    }
}