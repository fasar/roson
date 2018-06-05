package fr.fasar.roson;

import java.time.Instant;
import java.util.function.Consumer;

public class SilenceDetector implements Consumer<Wrapper> {

    double threasholdSilence = 10.0;
    double threasholdWhisper = 300.0;
    Instant detection = null;
    int nbDetection = 0;


    @Override
    public void accept(Wrapper wrapper) {
        Double rms = 0.0;
        int indexOffset = wrapper.bufferOffset;
        for (int i = 0; i < wrapper.read / 4; i++) {
            int i2 = indexOffset + i * 4;
            int num1 = (wrapper.buffer[i2] << 8) + (wrapper.buffer[i2 + 1] & 0xFF);
            int num2 = (wrapper.buffer[i2 + 2] << 8) + (wrapper.buffer[i2 + 3] & 0xFF);
            double avg = (1.0 + num1 + num2) / 2;
            rms += Math.pow(avg, 2);
        }
        rms = rms / (wrapper.read / 4);
        if (rms < threasholdSilence) {
            System.out.println("Silence detected with RMS : " + rms);
        } else if (rms < threasholdWhisper) {
            System.out.println("Whisper detected with RMS : " + rms);
        } else {
            System.out.println("Voice   detected with RMS : " + rms);
        }

    }
}
