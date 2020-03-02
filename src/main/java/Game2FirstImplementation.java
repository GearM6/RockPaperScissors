import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Game2FirstImplementation {
    public static AtomicInteger ActiveThreads = new AtomicInteger(0);
    static LinkedList<PlayerThread> playerList = new LinkedList<>();

    public static class MatchThread implements Runnable{
        PlayerThread player1;
        PlayerThread player2;

        public MatchThread(PlayerThread player1, PlayerThread player2){
            this.player1 = player1;
            this.player2 = player2;
        }

        @Override
        public void run(){
            synchronized (playerList){
                System.out.println(player1.name + " vs. " + player2.name);
                PlayerThread winner = player1.fight(player2);
                playerList.add(winner);
            }
        }
    }

    public static class PlayerThread extends Thread {
        private String[] Hands = {"rock", "paper", "scissors"};
        public String hand;
        public String name;
        private boolean isAlive;

        public PlayerThread(String name){
            this.name = name;
            this.hand = Hands[(new Random().nextInt(3))];
            this.isAlive = true;
        }

        @Override
        public void run(){
            while(isAlive) {

            }
            System.out.println(this.name + " lost. Terminating now.");
        }

        public PlayerThread fight(PlayerThread opponent){
            if(this.hand.equals("rock")){
                if(opponent.hand.equals("paper")){
                    this.isAlive=false;
                    ActiveThreads.decrementAndGet();
                    return opponent;
                }
            }
            else if(this.hand.equals("paper")){
                if(opponent.hand.equals("scissors")){
                    this.isAlive=false;
                    ActiveThreads.decrementAndGet();
                    return opponent;

                }
            }
            else { //this == scissors
                if(opponent.hand.equals("rock")){
                    this.isAlive=false;
                    ActiveThreads.decrementAndGet();
                    return opponent;
                }
            }
            return this;
        }
        }

    public static void main(String[] args) {
        System.out.print("Enter the number of players: ");
        Scanner in = new Scanner(System.in);
        int playerCount = in.nextInt();
        ActiveThreads.set(playerCount);
        for(int i = 0; i < playerCount; i++){
            PlayerThread player = new PlayerThread(("Player " + (i+1)));
            player.setDaemon(true);
            player.start();
            playerList.add(player);
        }
        ExecutorService threadPool = Executors.newFixedThreadPool(4);
        while(ActiveThreads.intValue() > 1){
            try {
                threadPool.submit(new MatchThread(playerList.remove(), playerList.remove()));
            }catch(Exception e) {

            }
        }
        System.out.println("The winner is " + playerList.remove());
    }
}
