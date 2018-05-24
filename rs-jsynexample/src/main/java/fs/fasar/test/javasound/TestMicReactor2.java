package fs.fasar.test.javasound;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.sound.sampled.*;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class TestMicReactor2 {
    public static void main(String[] args) throws InterruptedException {
        AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
        format = MyFormat.get();

        DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
        DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);

        try {
            TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
            targetLine.open(format);
            targetLine.start();

            SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
            sourceLine.open(format);
            sourceLine.start();

            int bufferSizeOld = targetLine.getBufferSize() / 5;
            int bufferSize = 40;

            AtomicInteger micLoop = new AtomicInteger(0);
            Flux<Wrapper> flux = Flux.<Wrapper>generate((sink) -> {
                Instant start = Instant.now();
                byte[] targetData2 = new byte[bufferSize];
                int read = targetLine.read(targetData2, 0, bufferSize);
                if(read != bufferSize) {
                    System.out.println("Read only " + read);
                    return;
                }
                sink.next(new Wrapper(targetData2, start));
                int i1 = micLoop.incrementAndGet();
                if ((i1 % 11) == 1) {
                    //System.out.println("Read MIC on thread :" + Thread.currentThread().getName());
                }
            }).subscribeOn(Schedulers.elastic());

            AtomicInteger readLoop = new AtomicInteger(0);
            flux
                    .publishOn(Schedulers.parallel())
                    .subscribe(wrapper -> {
                        sourceLine.write(wrapper.buffer, 0, bufferSize);
                        int i1 = readLoop.incrementAndGet();
                        if ((i1 % 11) == 1) {
                            //System.out.println("Read SND on thread :" + Thread.currentThread().getName());
                            Instant stop = Instant.now();
                            Duration between = Duration.between(wrapper.ts, stop);
                            System.out.println("Read/Write sound takes : " + between.toString() + " " + between.getSeconds() + "s " + between.getNano() +"nano");
                        }
                    });

        } catch (Exception e) {
            System.err.println(e);
        }

        Thread.sleep(100_000);
    }

    public static class Wrapper {
        public final byte[] buffer;
        public final Instant ts;

        public Wrapper(byte[] buffer, Instant ts) {
            this.buffer = buffer;
            this.ts = ts;
        }
    }
}
