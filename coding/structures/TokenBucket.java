package coding.structures;

public class TokenBucket {
    private int MAX_TOKENS;
    private long lastRequestTime = System.currentTimeMillis();
    long availTokens = 0;

    public TokenBucket(int maxTokens) {
        MAX_TOKENS = maxTokens;
    }

    synchronized void getToken() throws InterruptedException {
        availTokens += (System.currentTimeMillis() - lastRequestTime) / 1000;
        if (availTokens > MAX_TOKENS) {
            availTokens = MAX_TOKENS;
        }

        if (availTokens == 0) {
            Thread.sleep(1000);
        } else {
            availTokens--;
        }

        lastRequestTime = System.currentTimeMillis();
    }
}

class ThreadedTokenBucket {
    private long availableTokens = 0;
    private final int MAX_TOKENS;
    private final int ONE_SECOND = 1000;
    
    public ThreadedTokenBucket(int maxTokens) {
        MAX_TOKENS = maxTokens;
    }

    private void initialize() {
        Thread t = new Thread(() -> daemonThread());
        t.setDaemon(true);
        t.start();
    }

    private void daemonThread() {
        while(true) {
            synchronized(this) {
                if (availableTokens < MAX_TOKENS) {
                    availableTokens++;
                }
                this.notify();
            }
            try {
                Thread.sleep(ONE_SECOND);
            } catch (InterruptedException ie) {
                // swallow exception
            }
        }
    }

    void getToken() throws InterruptedException {
        synchronized (this) {
            while(availableTokens == 0) {
                this.wait();
            }
            availableTokens--;
        }
    }
}