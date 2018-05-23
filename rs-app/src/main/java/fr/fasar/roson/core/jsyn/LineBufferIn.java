package fr.fasar.roson.core.jsyn;

import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.UnitGenerator;
import com.jsyn.unitgen.UnitSource;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

public class LineBufferIn extends UnitGenerator implements UnitSource {

    private final int numParts;
    public UnitOutputPort output;
    private double[][] buffers;

    public LineBufferIn(
            int numParts
    ) {
        this.numParts = numParts;
        buffers = new double[numParts][];
        for (int i = 0; i < numParts; i++) {
            buffers[i] = new double[8];
        }
        addPort(output = new UnitOutputPort(numParts, "Output"));
    }

    @Override
    public void generate(int start, int limit) {
        double[][] outBuffers = new double[numParts][];
        for (int i = 0; i < numParts; i++) {
            outBuffers[i] = output.getValues(i);
        }

        for (int i = start; i < limit; i++) {
            for (int j = 0; j < numParts; j++) {
                outBuffers[j][i] = buffers[j][i];
            }
        }
    }

    public Disposable registerOn(Flux<LineBufferEntity> flux) {
        Disposable subscribe = flux.subscribe(entity -> {
            for (int j = 0; j < numParts; j++) {
                buffers[j] = entity.inputs[j];
            }
        }, throwable -> {
            throwable.printStackTrace();
            output.disconnectAll();
        }, () -> {
            output.disconnectAll();
        });
        return subscribe;
    }

    @Override
    public UnitOutputPort getOutput() {
        return this.output;
    }
}
