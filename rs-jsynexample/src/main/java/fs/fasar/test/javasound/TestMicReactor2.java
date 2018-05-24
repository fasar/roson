package fs.fasar.test.javasound;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.sound.sampled.*;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class TestMicReactor2 {

    private static final int NB_BUFFER = 100;

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
            int bufferSize = 4000;
            byte[] targetData = new byte[bufferSize * NB_BUFFER];

            Flux<Wrapper> flux = Flux.generate(
                    () -> NB_BUFFER-1,
                    (acc, sink) -> {
                        int currentIndex = acc + 1;
                        if(currentIndex % (NB_BUFFER) == 0) {
                            currentIndex = 0;
                        }
                        Instant start = Instant.now();
                        int read = targetLine.read(targetData, currentIndex * bufferSize, bufferSize);
                        sink.next(new Wrapper(currentIndex, read, start));
                        return currentIndex;
                    });
            flux = flux.subscribeOn(Schedulers.elastic());

            AtomicInteger readLoop = new AtomicInteger(0);
            flux
                    .publishOn(Schedulers.parallel())
                    .subscribe(wrapper -> {
                        sourceLine.write(targetData, wrapper.buffPos * bufferSize, wrapper.read);
                        int i1 = readLoop.incrementAndGet();
                        if ((i1 % 11) == 1) {
                            //System.out.println("Read SND on thread :" + Thread.currentThread().getName());
                            Instant stop = Instant.now();
                            Duration between = Duration.between(wrapper.ts, stop);
                            System.out.println("Read/Write sound takes : " + between.toString() + " " + between.getSeconds() + "s " + between.getNano() + "nano");
                        }
                    });

        } catch (Exception e) {
            System.err.println(e);
        }

        Thread.sleep(100_000);
    }

    public static class Wrapper {
        public final int buffPos;
        public final int read;
        public final Instant ts;

        public Wrapper(int buffPos, int read, Instant ts) {
            this.buffPos = buffPos;
            this.read = read;
            this.ts = ts;
        }
    }
}
