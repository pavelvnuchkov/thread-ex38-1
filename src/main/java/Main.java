import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Future<Integer>> futureList = new ArrayList<>();

        long startThread = System.currentTimeMillis(); // start time
        for (String text : texts) {
            futureList.add(executorService.submit(addCallable(text)));
        }
        int maxThread = 0;
        for (Future<Integer> future : futureList) {
            maxThread = Math.max(maxThread, future.get());
        }
        System.out.println("Максимальное значение при многопоточности - " + maxThread);
        executorService.close();
        long endThread = System.currentTimeMillis(); // end time

        long startTs = System.currentTimeMillis(); // start time
        int max = 0;
        for (String text : texts) {

            int maxSize = 0;
            for (int i = 0; i < text.length(); i++) {
                for (int j = 0; j < text.length(); j++) {
                    if (i >= j) {
                        continue;
                    }
                    boolean bFound = false;
                    for (int k = i; k < j; k++) {
                        if (text.charAt(k) == 'b') {
                            bFound = true;
                            break;
                        }
                    }
                    if (!bFound && maxSize < j - i) {
                        maxSize = j - i;
                    }
                }
            }
            System.out.println(text.substring(0, 100) + " -> " + maxSize);
            max = Math.max(max, maxSize);
        }
        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Максимальное значение при одном потоке - " + max);
        System.out.println("Time: " + (endTs - startTs) + "ms");
        System.out.println("Time multipotok: " + (endThread - startThread) + "ms");

    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static Callable<Integer> addCallable(String text) {
        return () -> {
            int maxSize = 0;
            for (int i = 0; i < text.length(); i++) {
                for (int j = 0; j < text.length(); j++) {
                    if (i >= j) {
                        continue;
                    }
                    boolean bFound = false;
                    for (int k = i; k < j; k++) {
                        if (text.charAt(k) == 'b') {
                            bFound = true;
                            break;
                        }
                    }
                    if (!bFound && maxSize < j - i) {
                        maxSize = j - i;
                    }
                }
            }
            System.out.println(text.substring(0, 100) + " -> " + maxSize);
            return maxSize;
        };
    }
}
