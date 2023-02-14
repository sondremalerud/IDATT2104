package Oving4;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {
    public static void main(String[] args) throws IOException {
        final int PORTNR = 1250;
        byte[] buf = new byte[256];

        // will be used to send packets
        DatagramSocket socket = new DatagramSocket(PORTNR);


        while (true) {
            // receives incoming message
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            // blocks until a message arrives, then stores it in the byte buffer
            socket.receive(packet);

            // address and port of the client, will be used to send response back
            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            packet = new DatagramPacket(buf, buf.length, address, port);
            String received = new String(packet.getData(), 0, packet.getLength());

            if (received.equalsIgnoreCase("exit")) {
                continue; //start loop over and wait for a new connection
            }


            //TODO: process packet before sending back


            socket.send(packet);



        }
        socket.close();


    }


}
