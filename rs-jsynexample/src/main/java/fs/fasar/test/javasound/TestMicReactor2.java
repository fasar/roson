package fs.fasar.test.javasound;

import fr.fasar.roson.MyFormat;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.scheduler.Schedulers;

import javax.sound.sampled.*;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestMicReactor2 {

    private static final int NB_BUFFER = 100;

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(8);
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
            int bufferToRead = 44100;
            byte[] targetData = new byte[bufferToRead * NB_BUFFER];

            // Task to read the sound
            ReplayProcessor<Wrapper> emitter = ReplayProcessor.create(1, false);
            Runnable taskRunnable = () -> {
                int currentIndex = 0;
                while (true) {
                    currentIndex = currentIndex + 1;
                    if (currentIndex % (NB_BUFFER) == 0) {
                        currentIndex = 0;
                    }
                    Instant start = Instant.now();
                    int read = targetLine.read(targetData, currentIndex * bufferToRead, bufferToRead);
                    emitter.onNext(new Wrapper(currentIndex, read, start));
                }
            };
            Thread task = new Thread(taskRunnable);
            task.run();
            Flux<Wrapper> objectFlux = emitter;

            // Output the sound to the speakers
            objectFlux
                    .publishOn(Schedulers.parallel())
                    .subscribe(wrapper -> {
                        sourceLine.write(targetData, wrapper.buffPos * bufferToRead, wrapper.read);
                    });

            // Output the sound to a file
            AtomicBoolean startOutput = new AtomicBoolean(false);
            objectFlux
                    .publishOn(Schedulers.parallel())
                    .subscribe(wrapper -> {
                        sourceLine.write(targetData, wrapper.buffPos * bufferToRead, wrapper.read);
                    });

            // Do the RMS.
            objectFlux
                    .publishOn(Schedulers.parallel())
                    .subscribe(wrapper -> {
                        Double rms = 0.0;
                        int indexOffset = wrapper.buffPos * bufferToRead;
                        for (int i = 0; i < bufferToRead / 4; i++) {
                            int i2 = indexOffset + i * 4;
                            byte high = targetData[i2];
                            byte low = targetData[i2 + 1];
                            int num = (high << 8) + (low & 0xFF);
                            rms += Math.pow(num, 2);
                        }
                        rms = rms / bufferToRead;
                        System.out.println("RMS is : " + rms);

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
