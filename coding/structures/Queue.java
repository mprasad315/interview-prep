package coding.structures;

public class Queue {
    private int capacity;
    private int[] queue;
    private int head;
    private int tail;
    private int size;

    public Queue(int capacity) {
        this.capacity = capacity;
        this.queue = new int[capacity];
        this.head = 0;
        this.tail = -1;
        this.size = 0;
    }

    public void enqueue(int val) {
        if (size == capacity) {
            System.out.println("Queue is at capacity");
            return;
        }
        tail = (tail + 1) % capacity;
        queue[tail] = val;
        size++;
    }

    public int dequeue() {
        if (size == 0) {
            System.out.println("Queue is empty");
            return -1;
        }
        int val = queue[head++];
        head = head % capacity;
        size--;
        return val;
    }

    public int peek() {
        if (size == 0) {
            System.out.println("Queue is empty");
            return -1;
        }
        return queue[head];
    }
}
