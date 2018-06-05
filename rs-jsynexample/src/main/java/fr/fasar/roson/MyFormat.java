package fr.fasar.roson;

import javax.sound.sampled.AudioFormat;

public class MyFormat {

    public static AudioFormat get() {
        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        int frameRate = 44100;
        float sampleRate = frameRate;
        int sampleSize = 16;
        int nbChan = 2;

        return new AudioFormat(encoding, sampleRate, sampleSize, nbChan, nbChan * sampleSize / 8, frameRate, true);
    }

}
