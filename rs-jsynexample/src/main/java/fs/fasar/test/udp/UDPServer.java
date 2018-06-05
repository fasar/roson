package fs.fasar.test.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

class UDPServer {

    static boolean run = true;

    public static void main(String args[]) throws Exception {
        try (DatagramSocket serverSocket = new DatagramSocket(9876)) {
            byte[] receiveData = new byte[50000];
            while (run) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                System.out.println("RECEIVED: " + receivePacket.getLength()); }
        }
    }
}