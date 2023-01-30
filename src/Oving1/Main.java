package Oving1;
import java.util.ArrayList;
import java.util.Collections;

public class Main {
    public static int from = 0;
    public static int to = 1000;
    public static int numberOfThreads = 5;
    public static ArrayList<Integer> results;

    public static void main(String[] args) throws InterruptedException {
        results = new ArrayList<>(); //resultatlista som består av alle primes mellom to gitte tall
        ArrayList<MyThread> threads = new ArrayList<>(numberOfThreads); // listen over threadsa
        ArrayList<Integer> numbers; // listen med tall en thread skal behandle
        ArrayList<ArrayList<Integer>> threadLists = new ArrayList<>(); // hver av listene med tall som threadsa skal behandle


        for (int threadId = 0; threadId < numberOfThreads; threadId++) {
            numbers = new ArrayList<>();

            for (int number = from + threadId; number <= to; number = number + numberOfThreads) {
                numbers.add(number);
            }
            threadLists.add(numbers);
            MyThread t = new MyThread(threadId,numbers);
            threads.add(t);
            t.start();
        }
        for (int i = 0; i < numberOfThreads; i++) {
            MyThread thread = threads.get(i);
            thread.join();
            results.addAll(thread.getPrimes());
        }
        Collections.sort(results);
        System.out.println(results);
    }
}

class MyThread extends Thread {

    private int threadNumber;
    private ArrayList<Integer> primes;
    private ArrayList<Integer> numbers;

    public MyThread (int threadNumber, ArrayList numbers) {
        this.threadNumber = threadNumber;
        this.numbers = numbers;
    }

    public int getThreadNumber() {
        return this.threadNumber;
    }

    public ArrayList<Integer> getPrimes(){
        return this.primes;
    }

    //sjekker om gitt tall er primtall
    public boolean isPrime(int number) {
        if (number <= 1) return false;
        if (number == 2 || number == 3) return true;
        if (number % 2 == 0 || number % 3 == 0) return false;
        for (int i = 5; i <= Math.sqrt(number); i = i + 6) if (number % i == 0 || number % (i + 2) == 0) return false;

        return true;
    }

    public void run(){
        this.primes = new ArrayList<>();
        System.out.println("hello from thread " + threadNumber);
        for (int i = 0; i < this.numbers.size(); i++) {
            if (isPrime(this.numbers.get(i))) this.primes.add(this.numbers.get(i));
        }

        System.out.println("bye 👋 from thread " + threadNumber);
    }
}