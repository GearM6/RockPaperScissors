import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Game1FirstImplementation  {
    private static String[] Hands = {"rock", "paper", "scissors"};
    private static HashMap<String, Integer> playerScores;
    volatile private static List<PlayerThread> playerThreadList;
    private static int playerCount;
    private static CyclicBarrier barrier;


    //TODO: put all winners in a new list and run until there is only 1 winner
    public static class PlayerThread extends Thread {
        String hand;
        public AtomicInteger score = new AtomicInteger(0);
        public String name;

        public PlayerThread(String name){
            this.name = name;
            this.hand = Hands[(new Random().nextInt(3))];
        }

        public PlayerThread(String name, AtomicInteger score){
            this.name = name;
            this.score = score;
            this.hand = Hands[(new Random().nextInt(3))];
        }

        @Override
        public void run() {
            playerThreadList.stream().forEach(this::fight);
        }

        public void fight(PlayerThread opponent){
            if(this.hand.equals("rock")){
                if(opponent.hand.equals("paper")){
                    this.score.decrementAndGet();
                }
                else if(opponent.hand.equals("scissors")){
                    this.score.incrementAndGet();
                }
            }
            else if(this.hand.equals("paper")){
                if(opponent.hand.equals("scissors")){
                    this.score.decrementAndGet();
                }
                else if(opponent.hand.equals("rock")){
                    this.score.incrementAndGet();                }
            }
            else { //this == scissors
                if(opponent.hand.equals("rock")){
                    this.score.decrementAndGet();
                }
                else if(opponent.hand.equals("paper")){
                    this.score.incrementAndGet();                }
            }
        }
    }

    public static class WinnerThread extends Thread {
        @Override
        public void run() {
            AtomicReference<PlayerThread> loser = new AtomicReference<>(new PlayerThread("", new AtomicInteger(Integer.MAX_VALUE)));
            List<PlayerThread> winnerList = new LinkedList<>();
            playerThreadList.stream().forEach(candidate -> {
                System.out.println(candidate.name + ":::" + candidate.score + "  hand:" + candidate.hand);
                if(candidate.score.intValue() < loser.get().score.intValue()){
                    loser.set(candidate);
                }
            });

            for(PlayerThread candidate : playerThreadList){
                if(candidate.getId() != loser.get().getId()){
                    PlayerThread thread = new PlayerThread(candidate.name, candidate.score);
                    winnerList.add(thread);
                    thread.start();
                }
            }
            playerThreadList = winnerList;
            System.out.println(Arrays.toString(playerThreadList.toArray()));
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Enter the number of players.");
        Scanner in = new Scanner(System.in);
        playerCount = in.nextInt();
        playerThreadList = new ArrayList<>();
        playerScores = new HashMap<>();
        barrier = new CyclicBarrier(playerCount);

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
        System.out.println("The winner is: " + playerThreadList.get(0).name + " -- Score: " + playerThreadList.get(0).score);
    }
}