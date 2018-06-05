package fr.fasar.roson;

import java.time.Instant;

public class Wrapper {
    public final int buffPos;
    public final int read;
    public final Instant ts;

    public Wrapper(int buffPos, int read, Instant ts) {
        this.buffPos = buffPos;
        this.read = read;
        this.ts = ts;
    }
}