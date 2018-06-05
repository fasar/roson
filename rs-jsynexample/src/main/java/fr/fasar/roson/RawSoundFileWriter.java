package fr.fasar.roson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

/**
 * Raw sound can be translated with ffmepg : C:\Utils\ffmpeg\bin\ffmpeg.exe -f s16be -ar 44100 -ac 2  -i input_file.raw  output_file.wav
 *
 */
public class RawSoundFileWriter implements Consumer<Wrapper> {

    private volatile boolean cmdOutput = false;
    private Path output = null;

    @Override
    public void accept(Wrapper wrapper) {
        if(output == null && cmdOutput) {
            output = Paths.get(getFileName());
            try {
                Files.createFile(output);
                System.out.println("Start to record file " + output.getFileName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (cmdOutput) {
            byte[] buffer = new byte[wrapper.read];
            System.arraycopy(wrapper.targetData, wrapper.bufferOffset, buffer, 0, wrapper.read);
            try {
                Files.write(output, buffer, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(output != null) {
            System.out.println("Stop  to record file " + output.getFileName());
            output = null;
        }
    }

    private String getFileName() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-hhmmss");
        String format = dateTimeFormatter.format(now);
        return format + ".raw";
    }

    public void startOutput(boolean isStart) {
        this.cmdOutput = isStart;
    }
}
