package fs.fasar.test;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.data.FloatSample;
import com.jsyn.devices.AudioDeviceManager;
import com.jsyn.unitgen.FixedRateStereoReader;
import com.jsyn.unitgen.LineOut;
import com.jsyn.util.SampleLoader;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SampleTester {

    private static final int NUM_FRAMES = 1000;

    public static void main(String[] args) throws InterruptedException, IOException {
        Synthesizer synth = JSyn.createSynthesizer();
        int numInputChannels = 2;
        int numOutputChannels = 2;
        synth.start(44100, AudioDeviceManager.USE_DEFAULT_DEVICE, numInputChannels, AudioDeviceManager.USE_DEFAULT_DEVICE,
                numOutputChannels);

        // Create a float array to contain audio data.
        FloatSample mySample = getASample();

        final URL resource = Runtime.getRuntime().getClass().getResource("/clack.wav");
        File file = new File(resource.getFile());
        mySample = SampleLoader.loadFloatSample(file);

        FixedRateStereoReader samplePlayer = new FixedRateStereoReader();
        synth.add(samplePlayer);
        samplePlayer.dataQueue.queue(mySample, 0, mySample.getNumFrames());


        LineOut lineOut = new LineOut();
        synth.add(lineOut);
        samplePlayer.output.connect(0, lineOut.input, 0);
        samplePlayer.output.connect(1, lineOut.input, 1);


        lineOut.start();
        Thread.sleep(100);
        samplePlayer.dataQueue.queue(mySample, 0, mySample.getNumFrames());
        Thread.sleep(100);
        samplePlayer.dataQueue.queue(mySample, 0, mySample.getNumFrames());


        Thread.sleep(1000);
    }

    private static FloatSample getASample() {
        float[] data = new float[NUM_FRAMES];
        // Fill it with sawtooth data. */
        float value = 0.0F;
        for (int i = 0; i < data.length; i++) {
            data[i] = value;
            value += 0.001;
            if (value >= 1.0) {
                value -= 2.0;
            }
        }
        FloatSample mySample = new FloatSample(data);
        return mySample;
    }
}
