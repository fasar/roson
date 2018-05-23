package fr.fasar.roson.core.jsyn;

import com.jsyn.ports.UnitInputPort;
import com.jsyn.unitgen.UnitGenerator;
import com.jsyn.unitgen.UnitSink;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;

import java.util.Arrays;

public class LineBufferOut extends UnitGenerator implements UnitSink {
    private DirectProcessor<LineBufferEntity> directProcessor;
    public UnitInputPort input;
    private final int numParts;


    public LineBufferOut(int numParts) {
        this.numParts = numParts;
        input = new UnitInputPort(numParts, "Input");
        addPort(input);
        directProcessor = DirectProcessor.create();
    }

    @Override
    public void generate(int start, int limit) {
        LineBufferEntity entity = new LineBufferEntity(numParts);
        for (int i = 0; i < numParts; i++) {
            final double[] values = Arrays.copyOfRange(input.getValues(i), start, start+limit);
            entity.setInput(i, values);
        }
        directProcessor.onNext(entity);
    }

    public Flux<LineBufferEntity> getFlux() {
        return directProcessor;
    }


    @Override
    public UnitInputPort getInput() {
        return input;
    }

    @Override
    public void stop() {
        super.stop();
        directProcessor.onComplete();
    }
}
