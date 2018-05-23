package fs.fasar.test.jsyn;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.devices.AudioDeviceManager;
import com.jsyn.unitgen.LineIn;
import com.jsyn.unitgen.LineOut;

public class MicTester {

    public static void main(String[] args) {
        Synthesizer synth = JSyn.createSynthesizer();
        int numInputChannels = 2;
        int numOutputChannels = 2;
        synth.start(44100, AudioDeviceManager.USE_DEFAULT_DEVICE, numInputChannels, AudioDeviceManager.USE_DEFAULT_DEVICE,
                numOutputChannels);

        LineIn lineIn = new LineIn();

        synth.add(lineIn);

        LineOut lineOut = new LineOut();
        synth.add(lineOut);
        lineIn.output.connect(0, lineOut.input, 0);
        lineIn.output.connect(1, lineOut.input, 1);
        lineOut.start();

    }

}
