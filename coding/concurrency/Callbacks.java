package coding.concurrency;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Callbacks {
    public static void main(String[] args) {
        EventListener listener = new SynchronousEventListenerImpl();
        SynchronousEventConsumer synchronousEventConsumer = new SynchronousEventConsumer(listener);
        String result = synchronousEventConsumer.doSynchronousOperation();
        System.out.println(result);

        EventListener asyncListener = new AsynchronousEventListenerImpl();
        AsynchronousEventConsumer asynchronousEventConsumer = new AsynchronousEventConsumer(asyncListener);
        Future<?> answer = asynchronousEventConsumer.doAsynchronousOperation();
        try {
            answer.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
        System.out.println(answer.isDone());
    }
}

interface EventListener {
    String onTrigger();

    void respondToTrigger();
}
class SynchronousEventListenerImpl implements EventListener {
    @Override
    public String onTrigger() {
        return "Synchronously running callback function";
    }

    @Override
    public void respondToTrigger() {
        // Nothing
    }
}
class SynchronousEventConsumer {
    private final EventListener eventListener;

    public SynchronousEventConsumer(EventListener e) {
        eventListener = e;
    }

    public String doSynchronousOperation() {
        System.out.println("Performing callback before synchronous task");
        return eventListener.onTrigger();
    }
}
class AsynchronousEventListenerImpl implements EventListener {
    @Override
    public String onTrigger() {
        respondToTrigger();
        return "Asynchronously running callback function";
    }

    @Override
    public void respondToTrigger() {
        System.out.println("Side effect of the asynchronous trigger");
    }
}

class AsynchronousEventConsumer {
    private final EventListener eventListener;
    private ExecutorService threadPool;

    public AsynchronousEventConsumer(EventListener e) {
        eventListener = e;
        threadPool = Executors.newFixedThreadPool(1);
    }

    public Future<?> doAsynchronousOperation() {
        System.out.println("Performing operation in Asynchronous task");
        return threadPool.submit(() -> {eventListener.onTrigger();});
    }
}