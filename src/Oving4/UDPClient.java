package Oving4;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPClient {
    public static void main(String[] args) throws IOException {
        final int PORTNR = 1250;
        byte[] buf = new byte[256];
        InetAddress address;
        address = InetAddress.getByName("localhost");
        Scanner scan = new Scanner(System.in);

        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet, receivedPacket;

        String line = "";
        while(!line.equalsIgnoreCase("exit")) {
            System.out.println("Welcome to the calculator server, enter a simple calculation");
            line = scan.nextLine();
            packet = new DatagramPacket(line.getBytes(), line.length(), address, PORTNR);
            socket.send(packet);
            if (line.equalsIgnoreCase("exit")) break;

            receivedPacket = new DatagramPacket(buf, buf.length);
            socket.receive(receivedPacket);

            String received = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
            System.out.println(received);
        }
        socket.close();

        System.out.println("Exiting...");
    }

}
