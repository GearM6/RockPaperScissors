import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Game1Memoization  {
    private static String[] Hands = {"rock", "paper", "scissors"};
    private volatile static HashMap<String, Integer> playerScores = new HashMap<>();
    private static LinkedList<PlayerThread> playerThreadList;
    private static int playerCount;


    public static class PlayerThread extends Thread {
        String hand;
        public String name;

        public PlayerThread(String name){
            this.name = name;
            this.hand = Hands[(new Random().nextInt(3))];
            playerScores.put(this.name, 0);
        }
        public PlayerThread(String name, int score){
            this.name = name;
            this.hand = Hands[(new Random().nextInt(3))];
            playerScores.put(this.name, score);
        }

        @Override
        public void run() {
            playerThreadList.stream().forEach(this::fight);
        }

        public void fight(PlayerThread opponent){
            synchronized (playerScores){
                if(this.hand.equals("rock")){
                    if(opponent.hand.equals("paper")){
                        playerScores.put(this.name, (playerScores.get(this.name)-1)    );
                    }
                    else if(opponent.hand.equals("scissors")){
                        playerScores.put(this.name, (playerScores.get(this.name)+1));
                    }
                }
                else if(this.hand.equals("paper")){
                    if(opponent.hand.equals("scissors")){
                        playerScores.put(this.name, (playerScores.get(this.name)-1)    );
                    }
                    else if(opponent.hand.equals("rock")){
                        playerScores.put(this.name, (playerScores.get(this.name)+1));
                    }
                }
                else { //this == scissors
                    if(opponent.hand.equals("rock")){
                        playerScores.put(this.name, (playerScores.get(this.name)-1)    );
                    }
                    else if(opponent.hand.equals("paper")){
                        playerScores.put(this.name, (playerScores.get(this.name)+1));
                    }
                }
            }
        }
    }

    public static class WinnerThread extends Thread {
        @Override
        public void run() {
            AtomicReference<PlayerThread> loser = new AtomicReference<>(new PlayerThread("", Integer.MAX_VALUE));
            LinkedList<PlayerThread> winnerList = new LinkedList<>();
            playerThreadList.stream().forEach(candidate -> {
                if(playerScores.get( candidate.name) < playerScores.get(loser.get().name)){
                    loser.set(candidate);
                }
            });

            for(PlayerThread candidate : playerThreadList){
                if(candidate.getId() != loser.get().getId()){
                    PlayerThread thread = new PlayerThread(candidate.name, playerScores.get(candidate.name));
                    winnerList.add(thread);
                    thread.start();
                }
            }
        //    System.out.println(Arrays.toString(playerThreadList.toArray()));
            playerThreadList = winnerList; }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Enter the number of players.");
        Scanner in = new Scanner(System.in);
        playerCount = in.nextInt();
        playerThreadList = new LinkedList<>();

        long startTime = System.nanoTime();
        for(int i = 0; i < playerCount; i++){
            PlayerThread thread = new PlayerThread(("Player " + (i+1)));
            playerThreadList.add(thread);
            thread.start();
        }
        for(PlayerThread player: playerThreadList) {
            try {
                player.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while(playerThreadList.size() > 1){
            for(PlayerThread player: playerThreadList){
                player.join();
            }
            WinnerThread winnerThread = new WinnerThread();
            winnerThread.start();
            winnerThread.join();
        }

        long endTime = System.nanoTime();
        System.out.println("The winner is: " + playerThreadList.get(0).name + " -- Score: " + playerScores.get(playerThreadList.get(0).name));
        System.out.println("Runtime: " + (endTime-startTime)+"ns");
    }
}