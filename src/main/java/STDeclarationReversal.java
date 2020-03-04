import java.io.*;
import java.util.Stack;

public class STDeclarationReversal {
    private static void singleThreadReverse(File output, BufferedReader reader, Stack wordStack) throws IOException {
        String line;
        while((line = reader.readLine()) != null){
            String[] wordArray = line.split(" ");
            for (String s: wordArray) {wordStack.add(s);}
        }
        reader.close();
        BufferedWriter writer = new BufferedWriter(new FileWriter(output));
        while(!wordStack.isEmpty()){
            writer.write(wordStack.pop() + " ");
        }
    }

    public static void main(String[] args) throws IOException {
        File file = new File("./doi.txt");
        File output = new File("./backwards.txt");
        Stack<String> wordStack = new Stack<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        long startTime = System.nanoTime();
        singleThreadReverse(output, reader, wordStack);
        long endTime = System.nanoTime();
        System.out.println("Time elapsed: " + (endTime - startTime) + "ns");
        reader.close();
    }
}
