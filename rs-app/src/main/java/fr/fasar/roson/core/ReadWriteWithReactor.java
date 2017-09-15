package fr.fasar.roson.core;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.devices.AudioDeviceManager;
import com.jsyn.unitgen.LineIn;
import com.jsyn.unitgen.LineOut;
import fr.fasar.roson.core.jsyn.LineBufferEntity;
import fr.fasar.roson.core.jsyn.LineBufferIn;
import fr.fasar.roson.core.jsyn.LineBufferOut;
import reactor.core.publisher.Flux;

public class ReadWriteWithReactor {

    public static void main(String[] args) throws InterruptedException {
        Synthesizer synth = JSyn.createSynthesizer();
        int numInputChannels = 2;
        int numOutputChannels = 2;
        synth.start(44100, AudioDeviceManager.USE_DEFAULT_DEVICE, numInputChannels, AudioDeviceManager.USE_DEFAULT_DEVICE,
                numOutputChannels);


        LineIn lineIn = new LineIn();
        LineBufferOut reactorOut = new LineBufferOut(2);
        synth.add(lineIn);
        synth.add(reactorOut);

        lineIn.output.connect(0, reactorOut.getInput(), 0);
        lineIn.output.connect(1, reactorOut.getInput(), 1);

        reactorOut.start();



        LineBufferIn reactorIn = new LineBufferIn(2);
        LineOut lineOut = new LineOut();
        synth.add(reactorIn);
        synth.add(lineOut);

        reactorIn.output.connect(0, lineOut.getInput(), 0);
        reactorIn.output.connect(1, lineOut.getInput(), 1);
        lineOut.start();

        final Flux<LineBufferEntity> flux = reactorOut.getFlux();
        reactorIn.registerOn(flux);
        flux.subscribe(e -> {
            System.out.println("Receive something : " + e.getInput(0)[0]);
        });

        Thread.sleep(1000);

    }

}
