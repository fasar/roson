package fs.fasar.test;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.devices.AudioDeviceManager;
import com.jsyn.unitgen.FilterStateVariable;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.WhiteNoise;

public class NoiseTester {

    public static void main(String[] args) throws InterruptedException {
        Synthesizer synth = JSyn.createSynthesizer();
        int numInputChannels = 2;
        int numOutputChannels = 2;
        synth.start( 44100, AudioDeviceManager.USE_DEFAULT_DEVICE, numInputChannels, AudioDeviceManager.USE_DEFAULT_DEVICE,
                numOutputChannels );

        LineOut myOut;
        WhiteNoise myNoise;
        FilterStateVariable myFilter;
        synth.add( myOut = new LineOut() );
        synth.add( myNoise = new WhiteNoise() );
        synth.add( myFilter = new FilterStateVariable() );

        myNoise.output.connect( myFilter.input );
        myFilter.output.connect( 0, myOut.input, 0 ); /* Left side */
        myFilter.output.connect( 0, myOut.input, 1 ); /* Right side */

        myOut.start();

        Thread.sleep(1000);
        synth.sleepUntil( 10000 );
        Thread.sleep(5000);
    }

}
