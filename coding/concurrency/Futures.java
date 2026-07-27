package coding.concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Futures {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        /*
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<Integer> future = executor.submit(() -> {
            Thread.sleep(2000);
            return 1;
        });
        System.out.println(future.isDone());
        Integer result = future.get();
        System.out.println(result);

        // CompleteableFuture as a Future
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
        executor.submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            completableFuture.complete(42);
        });
        */
    

        // run async (without returning anything from task)
        CompletableFuture<Void> runAsyncFuture = CompletableFuture.runAsync(() -> {
            System.out.println("Future thread " + Thread.currentThread().getName());
        });

        // supply async (return some result from background task) & () -> 3 is equivalent to () -> { 3; }
        CompletableFuture<Void> supplyAsyncFuture = CompletableFuture.supplyAsync(() -> 3).thenApply(Futures::SomeMethod).thenAccept(System.out::println);

        // thenCompose (flatten chained completable futures)
        CompletableFuture<Integer> composeAsyncFuture = getBankAccount(6).thenCompose(accountID -> getBankAccountBalance(accountID));
        composeAsyncFuture.thenAccept((accountBalance) -> {
            System.out.println(accountBalance);
        });
    }
    
    public static Integer SomeMethod(Integer a) {
        return a * 35;
    }

    public static CompletableFuture<Integer> getBankAccountBalance(String accId) {
        return CompletableFuture.supplyAsync(() -> {
            if ("something".equals(accId)) {
                return 5000;
            }
            return 6000;
        });
    }

    public static CompletableFuture<String> getBankAccount(Integer routingNumber) {
        return CompletableFuture.supplyAsync(() -> {
            if (routingNumber.equals(6)) {
                return "something";
            }
            return "nothing";
        });
    }
}
