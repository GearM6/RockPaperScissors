import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeclarationBackwards {
    static final Stack<String> wordStack = new Stack<>();

    public static LinkedList<String[]> strings = new LinkedList<>();
    public static class ReversingThread implements Runnable {
        @Override
        public void run() {
            String[] line = strings.remove();
            synchronized (wordStack){
                wordStack.addAll(Arrays.asList(line));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        File file = new File("./doi.txt");
        File output = new File("./backwards.txt");

        System.out.println("Enter the desired number of threads.");
        Scanner in = new Scanner(System.in);
        int threadCount = in.nextInt();
            ExecutorService pool = Executors.newFixedThreadPool(threadCount);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            long startTime = System.nanoTime();

            String line;
            while((line = reader.readLine()) != null){
                String[] wordArray = line.split(" ");
                wordStack.addAll(Arrays.asList(wordArray));
            }
            reader.close();

            for(int i = 0; i  < threadCount; i++){
                pool.submit(new ReversingThread());
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(output));

            while(!wordStack.isEmpty()){
                writer.write(wordStack.pop() + " ");
            }
            long endTime = System.nanoTime();
            pool.shutdown();
            System.out.println("Time elapsed: " + (endTime - startTime) + "ns");
            writer.close();

    }
}
