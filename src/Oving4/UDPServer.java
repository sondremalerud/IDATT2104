package Oving4;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {
    public static void main(String[] args) throws IOException {
        final int PORTNR = 1250;
        byte[] buf = new byte[256];
        int num1, num2;
        String operator;

        // will be used to send packets
        DatagramSocket socket = new DatagramSocket(PORTNR);
        String returnMsg = "";


        while (true) {
            // receives incoming message
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            // blocks until a message arrives, then stores it in the byte buffer
            socket.receive(packet);

            // address and port of the client, will be used to send response back
            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            String received = new String(packet.getData(), 0, packet.getLength());

            if (received.equalsIgnoreCase("exit")) break;

            try {
                String[] numbers = received.split(" ");
                num1 = Integer.parseInt(numbers[0]);
                operator = numbers[1];
                num2 = Integer.parseInt(numbers[2]);
                System.out.println(operator);
                if (operator.equals("+")) returnMsg = (num1 + " + " + num2 + " = " + (num1 + num2));
                else if (operator.equals("-")) returnMsg = (num1 + " - " + num2 + " = " + (num1 - num2));

            } catch (Exception e) {
                e.printStackTrace();
            }

            packet = new DatagramPacket(returnMsg.getBytes(), returnMsg.length(), address, port);
            socket.send(packet);
        }
        socket.close();

        System.out.println("Exiting...");
    }
}
