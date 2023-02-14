package Oving4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class UDPClient {
    public static void main(String[] args) throws SocketException {
        final int PORTNR = 1250;
        byte[] buf;

        Scanner scan = new Scanner(System.in);
        System.out.print("Oppgi navnet på maskinen der tjenerprogrammet kjører: ");
        String serverName = scan.nextLine();

        DatagramSocket socket = new DatagramSocket(PORTNR);


        /* Leser tekst fra kommandovinduet (brukeren) */
        String line = scan.nextLine();


    }

}
