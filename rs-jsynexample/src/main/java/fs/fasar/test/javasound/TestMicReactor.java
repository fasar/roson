package fs.fasar.test.javasound;

import fr.fasar.roson.MyFormat;

import javax.sound.sampled.*;

public class TestMicReactor {
    public static void main(String[] args) {
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

            int numBytesRead;
            int bufferSize = targetLine.getBufferSize() / 5;
            byte[] targetData = new byte[bufferSize];
            System.out.println("Buffer size is : " + bufferSize);
            System.out.println("Buffer size is : " + bufferSize *1000.0 / 4.0 / 44100 + " ms");
            double acc = 0;
            int nbLoop = 0;
            long start = System.currentTimeMillis();
            while (10 > nbLoop++) {
                numBytesRead = targetLine.read(targetData, 0, targetData.length);
                if(acc==0) {
                    acc = numBytesRead;
                } else {
                    acc += numBytesRead;
                }
                if (numBytesRead == -1)	break;

                sourceLine.write(targetData, 0, numBytesRead);
            }
            long stop = System.currentTimeMillis();
            System.out.println("Average of " + acc / (nbLoop-1));
            System.out.println("Time : " + (stop - start));
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }

}
