package fr.fasar.roson;

import java.io.IOException;
import java.net.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class RawUdpWriter implements Consumer<Wrapper> {

    private volatile boolean cmdOutput = false;
    private Path output = null;
    DatagramSocket clientSocket;
    InetAddress host;

    public RawUdpWriter() throws SocketException, UnknownHostException {
        clientSocket = new DatagramSocket();
        host = InetAddress.getByName("localhost");

    }

    @Override
    public void accept(Wrapper wrapper) {
        DatagramPacket sendPacket = new DatagramPacket(wrapper.buffer, wrapper.bufferOffset, wrapper.read, host, 9876);
        try {
            clientSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
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
