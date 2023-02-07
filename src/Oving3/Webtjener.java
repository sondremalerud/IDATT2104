package Oving3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Webtjener {
    public static void main(String[] args) throws IOException {
        final int PORTNR = 1250;


        ServerSocket tjener = new ServerSocket(PORTNR);
        System.out.println("Logg for tjenersiden. Nå venter vi...");
        Socket forbindelse = tjener.accept();  // venter inntil noen tar kontakt

        /* Åpner strømmer for kommunikasjon med klientprogrammet */
        InputStreamReader leseforbindelse = new InputStreamReader(forbindelse.getInputStream());
        BufferedReader leseren = new BufferedReader(leseforbindelse);
        PrintWriter skriveren = new PrintWriter(forbindelse.getOutputStream(), true);

        skriveren.println("HTTP/1.0 200 OK");
        skriveren.println("Content-Type: text/html; charset=utf-8");
        skriveren.println("");
        skriveren.println("<HTML><BODY><H1>Velkommen til webtjeneren!</H1><UL>");

        String line;

        while ((line = leseren.readLine()) != "") {
            skriveren.println("<LI>" + line + "</LI>");
        }

        skriveren.println("</UL></BODY></HTML>");

        leseren.close();
        skriveren.close();
        forbindelse.close();
    }
}
