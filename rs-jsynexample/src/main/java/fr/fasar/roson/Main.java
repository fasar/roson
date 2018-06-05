package fr.fasar.roson;

import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.scheduler.Schedulers;

import javax.sound.sampled.*;
import java.io.IOException;
import java.time.Instant;

public class Main {

    private static final int NB_PAGES_BUFFER = 100;
    private static final int BUFFER_TO_READ = 44100;

    static boolean run = true;

    public static void main(String[] args) throws InterruptedException, IOException {
        AudioFormat format = MyFormat.get();

        DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
        DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
        RawSoundFileWriter fileOutTask = new RawSoundFileWriter();
        RawUdpWriter udpOutput = new RawUdpWriter();

        try {
            TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
            targetLine.open(format);
            targetLine.start();

            SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
            sourceLine.open(format);
            sourceLine.start();

            int bufferToRead = BUFFER_TO_READ;
            byte[] targetData = new byte[bufferToRead * NB_PAGES_BUFFER];

            // Task to read the sound
            ReplayProcessor<Wrapper> emitter = ReplayProcessor.create(1, false);
            Runnable taskRunnable = () -> {
                int pageBuffer = 0;
                while (true) {
                    pageBuffer = pageBuffer + 1;
                    if (pageBuffer % (NB_PAGES_BUFFER) == 0) {
                        pageBuffer = 0;
                    }
                    Instant startTs = Instant.now();
                    int read = targetLine.read(targetData, pageBuffer * bufferToRead, bufferToRead);
                    emitter.onNext(new Wrapper(targetData, pageBuffer * bufferToRead, pageBuffer, read, startTs));
                }
            };
            Thread task = new Thread(taskRunnable);
            task.start();
            Flux<Wrapper> objectFlux = emitter;

            // Output the sound to the speakers
            objectFlux
                    .publishOn(Schedulers.parallel())
                    .subscribe(wrapper -> {
                        sourceLine.write(targetData, wrapper.pageBuffer * bufferToRead, wrapper.read);
                    });

            // Output the sound to a file
            objectFlux
                    .publishOn(Schedulers.parallel())
                    .subscribe(fileOutTask);

            // Output the sound to a file
            objectFlux
                    .publishOn(Schedulers.parallel())
                    .subscribe(udpOutput);

            // Do the RMS.
            objectFlux
                    .publishOn(Schedulers.parallel())
                    .subscribe(new SilenceDetector());

        } catch (Exception e) {
            System.err.println(e);
        }

        boolean isStart = false;
        while(run) {
            System.in.read();
            isStart = !isStart;
            fileOutTask.startOutput(isStart);
            while(System.in.available()>0) {
                System.in.read();
            }
        }
    }


}
