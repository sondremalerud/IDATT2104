package Oving3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketTraad extends Thread {

    private Socket forbindelse;
    private BufferedReader input;
    final static int PORTNR = 1250;



    public SocketTraad(Socket s) throws IOException {
        this.forbindelse = s;
        this.input = new BufferedReader( new InputStreamReader(forbindelse.getInputStream()));
    }

    @Override
    public void run() {
        try {

            int tall1, tall2;
            String operator; //operatør

            //ServerSocket tjener = new ServerSocket(PORTNR);
            //System.out.println("Logg for tjenersiden. Nå venter vi...");
            //this.forbindelse = tjener.accept();  // venter inntil noen tar kontakt

            /* Åpner strømmer for kommunikasjon med klientprogrammet */
            InputStreamReader leseforbindelse = new InputStreamReader(forbindelse.getInputStream());
            BufferedReader leseren;
            PrintWriter skriveren = new PrintWriter(forbindelse.getOutputStream(), true);

            /* Sender innledning til klienten */
            skriveren.println("Hei, du har kontakt med tjenersiden!");

            while (true) {

                skriveren.println("Skriv inn et regnestykke på formen 'tall1 +/- tall2' eller skriv 'exit' for å avslutte");
                leseren = new BufferedReader(leseforbindelse);

                String input = leseren.readLine();
                if (input.equalsIgnoreCase("exit")) break;

                String[] tallene = input.split(" ");
                if (tallene.length != 3) {
                    skriveren.println("Formatet ble feil! Prøv igjen. Eksempel: '10 + 20'");
                    continue; // Starter while loopen fra begynnelsen igjen
                }
                tall1 = Integer.parseInt(tallene[0]);
                operator = tallene[1];
                tall2 = Integer.parseInt(tallene[2]);

                if (operator.equals("+")) skriveren.println(tall1 + " + " + tall2 + " = " + (tall1 + tall2));
                else if (operator.equals("-")) skriveren.println(tall1 + " - " + tall2 + " = " + (tall1 - tall2));
                else skriveren.println("Kalkulatoren har ikke støtte for dette regnestykket enda, prøv igjen.");

            }
                /* Lukker forbindelsen */
                leseren.close();
                skriveren.close();
                forbindelse.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
    }


    }

