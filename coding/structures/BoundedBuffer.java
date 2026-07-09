package coding.structures;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * Acts a circular buffer, we will never resize the array – only mess with the indices
 */
public class BoundedBuffer<T> {
    int capacity;
    T[] queue;
    int head = 0;
    int tail = 0;
    int size = 0;

    @SuppressWarnings("unchecked")
    public BoundedBuffer(int capacity) {
        this.queue = (T[]) new Object[capacity];
        this.capacity = capacity;
    }

    public synchronized void enqueue(T item) throws InterruptedException {
        while(size == capacity) {
            wait();
        }

        if(tail == capacity) {
            tail = 0;
        }

        queue[tail++] = item;
        size++;
        notifyAll();
    }

    public synchronized T dequeue() throws InterruptedException {
        T item = null;

        while(size == 0) {
            wait();
        }
        if (head == capacity) {
            head = 0;
        }
        item = queue[head];
        queue[head++] = null;
        size--;
        notifyAll();
        return item;
    }
}

class LockingBoundedBuffer<T> {
    int capacity;
    T[] queue;
    int head = 0;
    int tail = 0;
    int size = 0;
    Lock lock = new ReentrantLock();

    @SuppressWarnings("unchecked")
    public LockingBoundedBuffer(int capacity) {
        this.queue = (T[]) new Object[capacity];
        this.capacity = capacity;
    }

    public synchronized void enqueue(T item) throws InterruptedException {
        lock.lock();
        while(size == capacity) {
            lock.unlock();
            // Attempt to acquire lock again before checking conditional
            lock.lock();
        }

        if(tail == capacity) {
            tail = 0;
        }

        queue[tail++] = item;
        size++;
        notifyAll();
        lock.unlock();
    }

    public synchronized T dequeue() throws InterruptedException {
        T item = null;

        lock.lock();
        while(size == 0) {
            lock.unlock();
            lock.lock();
        }
        if (head == capacity) {
            head = 0;
        }
        item = queue[head];
        queue[head++] = null;
        size--;
        notifyAll();
        lock.unlock();
        return item;
    }
}