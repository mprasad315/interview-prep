package coding.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.security.auth.callback.Callback;

import coding.concurrency.PracticeProblems.ProblemTwo.User;

public class PracticeProblems {
    public class ProblemOne {
        public class Price {
            int amount;
            String symbol;
        }

        public interface PricingClient {
            CompletableFuture<Price> fetchPrice(String symbol);
        }
        public class PricingService {

            private final PricingClient client;

            public PricingService(PricingClient client) {
                this.client = client;
            }

            public Price getPrice(String symbol) {
                try {
                    return client.fetchPrice(symbol).get();
                } catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                } catch(ExecutionException e) {
                    throw new RuntimeException(e.getCause());
                }
            }
        }
    }

    public class ProblemTwo {
        public class User {
            String userName;
        }
        public interface UserClient {
            CompletableFuture<User> fetchUser(long userId);
        }
        public class UserService {

            private final UserClient client;

            public UserService(UserClient client) {
                this.client = client;
            }

            /**
             * Returns the user.
             *
             * Requirements:
             *
             * - Block until the asynchronous operation completes.
             * - If the operation fails, throw a RuntimeException whose cause is the
             *   original failure.
             * - If the waiting thread is interrupted, preserve the interrupt status.
             * - Do not modify UserClient.
             */
            public User getUser(long userId) {
                try {
                    return client.fetchUser(userId).get(500, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e.getCause());
                } catch (TimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public class ProblemThree {
        public interface UserRepository {
            User loadUser(long userId);
        }
        public class AsyncUserRepository {
            private final UserRepository repository;
            private final ExecutorService executor;

            public AsyncUserRepository(
                    UserRepository repository,
                    ExecutorService executor) {

                this.repository = repository;
                this.executor = executor;
            }

            /**
             * Requirements:
             *
             * - Execute loadUser() asynchronously.
             * - Use ONLY the provided ExecutorService.
             * - Return immediately.
             * - If loadUser throws, the CompletableFuture must complete exceptionally.
             * - Do not create additional threads.
             */
            public CompletableFuture<User> loadUserAsync(long userId) {
                return CompletableFuture.supplyAsync(() -> repository.loadUser(userId), executor);
            }
        }
    }
    public class ProblemFour {
        private final ReportService reportService;
        private final ExecutorService executor;

        public ProblemFour(
                ReportService reportService,
                ExecutorService executor) {

            this.reportService = reportService;
            this.executor = executor;
        }
        public class Report {
            String report;
        }
        public interface ReportService {
            Report generate(long accountId);
        }
        public List<Report> generateReports(List<Long> accountIds) {

            List<Future<Report>> tasks = new ArrayList<>(accountIds.size());
            for(Long accountId : accountIds) {
                tasks.add(executor.submit(() -> reportService.generate(accountId)));
            }
            List<Report> sol = new ArrayList<>(accountIds.size());
            try {
                for (Future<Report> future : tasks) {
                    sol.add(future.get());
                }
            } catch (InterruptedException e) {
                for (Future<?> future : tasks) {
                    future.cancel(true);
                }
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                for (Future<?> future : tasks) {
                    future.cancel(true);
                }
                throw new RuntimeException(e.getCause());
            }
            return sol;
        }
    }

    /*
    make the execution synchronous without changing the original classes (imagine, you are given the binaries and not the source code) 
    so that main thread waits till asynchronous execution is complete. In other words, the highlighted line#8 only executes once 
    the asynchronous task is complete.
     */
    public static void main( String args[] ) throws Exception{
        SynchronousExecutor executor = new SynchronousExecutor();
        executor.asynchronousExecution(() -> {
            System.out.println("I am done");
        });
        System.out.println("main thread exiting...");
    }
}

class Executor {
    public void asynchronousExecution(Callback callback) throws Exception {
        Thread t = new Thread(() -> {
            // Do some useful work
            try {
            // Simulate useful work by sleeping for 5 seconds
                Thread.sleep(5000);
            } catch (InterruptedException ie) {
            }
            callback.done();
        });
        t.start();
    }
    public interface Callback {
        public void done();
    }
}

class SynchronousExecutor extends Executor {
    public void asynchronousExecution(Callback callback) throws Exception {
        // pass something that the base class can notify on
        Object signal = new Object();
        final boolean[] isDone = new boolean[1];
        Callback cb = new Callback() {
            @Override
            public void done() {
                callback.done();
                synchronized (signal) {
                    isDone[0] = true;
                    signal.notify();
                }
            }
        };
        super.asynchronousExecution(cb);
        
        // wait on something the base class's async method notifies for
        synchronized(signal) {
            while (!isDone[0]) {
                signal.wait();
            }
        }
    }
}

class SynchronousExecutorTwo extends Executor {
    public void asynchronousExecution(Callback callback) throws Exception {
        CountDownLatch cdl = new CountDownLatch(1);
        Callback wrapped = new Callback() {
            @Override
            public void done() {
                callback.done();
                cdl.countDown();
            }
        };
        super.asynchronousExecution(wrapped);
        cdl.await();
    }
}