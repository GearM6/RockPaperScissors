import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Game2FirstImplementation {
    public static AtomicInteger ActiveThreads = new AtomicInteger(0);
    static final LinkedList<PlayerThread> playerList = new LinkedList<>();
    static Lock listLock = new ReentrantLock();

    public static class MatchThread implements Runnable{
        PlayerThread player1;
        PlayerThread player2;

        public MatchThread(PlayerThread player1, PlayerThread player2){
            this.player1 = player1;
            this.player2 = player2;
        }

        @Override
        public void run(){
            System.out.println(player1.name + " vs. " + player2.name);
            PlayerThread winner = this.fight();
            while(winner == null){
                player1.reRoll();
                player2.reRoll();
                winner = this.fight();
            }
            listLock.tryLock();
                playerList.add(winner);
            listLock.unlock();
        //    System.out.println("Winner: " + winner.name + "   Players Remaining: " + ActiveThreads.intValue());
            return;
        }
        public synchronized PlayerThread fight(){
            if(this.player1.hand.equals("rock")){
                if(player2.hand.equals("paper")){
                    this.player1.isAlive.set(false);
                    ActiveThreads.decrementAndGet();
                    return this.player2;
                }
                else if(player2.hand.equals("scissors")){
                    this.player2.isAlive.set(false);
                    ActiveThreads.decrementAndGet();
                    return this.player1;
                }
            }
            else if(this.player1.equals("paper")){
                if(this.player2.hand.equals("scissors")){
                    this.player1.isAlive.set(false);
                    ActiveThreads.decrementAndGet();
                    return this.player2;
                }
                else if(this.player2.hand.equals("rock")){
                    this.player2.isAlive.set(false);
                    ActiveThreads.decrementAndGet();
                    return this.player1;
                }
            }
            else { //this == scissors
                if(this.player1.hand.equals("rock")){
                    this.player1.isAlive.set(false);
                    ActiveThreads.decrementAndGet();
                    return this.player2;
                }
                else if(this.player2.hand.equals("paper")){
                    this.player2.isAlive.set(false);
                    ActiveThreads.decrementAndGet();
                    return this.player1;
                }
            }
            return null;
        }
    }

    public static class PlayerThread extends Thread {
        private String[] Hands = {"rock", "paper", "scissors"};
        public String hand;
        public String name;
        public AtomicBoolean isAlive;

        public PlayerThread(String name){
            this.name = name;
            this.hand = Hands[(new Random().nextInt(3))];
            this.isAlive = new AtomicBoolean(true);
        }

        public void reRoll(){
            this.hand = Hands[(new Random().nextInt(3))];
        }

        @Override
        public void run(){
            while(isAlive.get()) {

            }
            System.out.println(this.name + " lost. Terminating now.");
            return;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.print("Enter the number of players: ");
        Scanner in = new Scanner(System.in);
        int playerCount = in.nextInt();
        ActiveThreads.set(playerCount);
        long startTime = System.nanoTime();
        for(int i = 0; i < playerCount; i++){
            PlayerThread player = new PlayerThread(("Player " + (i+1)));
            player.setDaemon(true);
            player.start();
            playerList.add(player);
        }
        ExecutorService threadPool = Executors.newFixedThreadPool(playerCount);

        while(ActiveThreads.intValue() > 1){
            try {
                listLock.tryLock();
                if(playerList.size() >= 2){
                    threadPool.submit(new MatchThread(playerList.remove(), playerList.remove()));
                }
                listLock.unlock();
            }catch(Exception e) {
        }}
        threadPool.shutdown();
        long endTime = System.nanoTime();
        System.out.println("The winner is " + playerList.remove().name);
        System.out.println("Time elapsed: " + (endTime-startTime) + "ns");
    }
}
