package Oving3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketTraadTjener {


    public static void main(String[] args) throws IOException {
        final int PORTNR = 1250;


        ServerSocket tjener = new ServerSocket(PORTNR);

        while (true) {
            try {
                System.out.println("Logg for tjenersiden. Nå venter vi...");
                Socket forbindelse = tjener.accept();  // venter inntil noen tar kontakt
                SocketTraad s = new SocketTraad(forbindelse);
                s.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
