import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicReference;

public class Game1 {
    private static String[] Hands = {"rock", "paper", "scissors"};
    private static HashMap<String, Integer> playerScores;
    volatile private static List<PlayerThread> playerThreadList;
    private static int playerCount;
    private static CyclicBarrier barrier;


    //TODO: put all winners in a new list and run until there is only 1 winner
    public static class PlayerThread extends Thread {
        String hand;
        volatile Integer score = 0;

        @Override
        public void run() {
            barrier = new CyclicBarrier(playerCount);
            this.hand = Hands[(new Random().nextInt(3))];
            playerThreadList.stream().forEach(this::fight);
            try {
                barrier.await();
                System.out.println("Waiting.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

        public void fight(PlayerThread opponent){
            if(this.hand.equals("rock")){
                if(opponent.hand.equals("paper")){
                    this.score--;
                }
                else if(opponent.hand.equals("scissors")){
                    this.score++;
                }
            }
            else if(this.hand.equals("paper")){
                if(opponent.hand.equals("scissors")){
                    this.score--;
                }
                else if(opponent.hand.equals("rock")){
                    this.score++;
                }
            }
            else { //this == scissors
                if(opponent.hand.equals("rock")){
                    this.score--;
                }
                else if(opponent.hand.equals("paper")){
                    this.score++;
                }
            }
        }

        public void reroll(){
            this.hand = Hands[(new Random().nextInt(3))];
        }
    }

    public static class WinnerThread extends Thread {
        private List<PlayerThread> winnerList;

        public WinnerThread(List<PlayerThread> winnerList){
            this.winnerList = winnerList;
        }

        @Override
        public void run() {
            AtomicReference<PlayerThread> winner = new AtomicReference<>(playerThreadList.get(0));

            playerThreadList.stream().forEach(candidate -> {
                System.out.println(candidate.getName() + ":::" + candidate.score + "  hand:" + candidate.hand);
                if(candidate.score > winner.get().score){
                    winner.set(candidate);
                }
            });

            for(Iterator<PlayerThread> iterator = playerThreadList.iterator(); iterator.hasNext();){
                if(iterator.next().score == winner.get().score){
                    winnerList.add(iterator.next());
                }
            }
            while(winnerList.size() > 1){
                List<PlayerThread> newList = new LinkedList<>();
                for(PlayerThread player : winnerList){
                    player.reroll();
                    player.start();
                }
                playerThreadList.stream().forEach(candidate -> {
                    System.out.println(candidate.getName() + ":::" + candidate.score + "  hand:" + candidate.hand);
                    if(candidate.score > winner.get().score){
                        winner.set(candidate);
                    }
                });
                for(PlayerThread candidate : playerThreadList){
                    if(candidate.score == winner.get().score){
                        newList.add(candidate);
                    }
                }
                winnerList = newList;
            }
        }
    }

    public static void main(String[] args){
        System.out.println("Enter the number of players.");
        Scanner in = new Scanner(System.in);
        playerCount = in.nextInt();
        playerThreadList = new ArrayList<>();
        playerScores = new HashMap<>();
        List<PlayerThread> winnerList = new LinkedList<>();
        barrier = new CyclicBarrier(playerCount);

        for(int i = 0; i < playerCount; i++){
            PlayerThread thread = new PlayerThread();
            playerThreadList.add(thread);
            thread.start();
        }
        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        System.out.println(Arrays.toString(playerThreadList.toArray()));
        //  new WinnerThread(playerThreadList).run();
    }
}
