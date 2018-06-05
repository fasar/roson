package fs.fasar.test.javasound;

import fr.fasar.roson.MyFormat;

import javax.sound.sampled.*;

public class ReadSound {
    public static void main(String[] args) throws MyException {
        AudioFormat format = MyFormat.get();
        SourceDataLine line;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format); // format is an AudioFormat object
        if (!AudioSystem.isLineSupported(info)) {
            // Handle the error ...
            throw new MyException("format is not supported");
        }

        // Obtain and open the line.
        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
        } catch (LineUnavailableException e) {
            // Handle the error ...
            throw new MyException("Can't open line", e);
        }


    }

}
