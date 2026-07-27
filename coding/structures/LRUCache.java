package coding.structures;

import java.util.Map;

public class LRUCache {

    class Node {
        int key;
        int val;
        Node prev;
        Node next;

        public Node(int key, int val){
            this.key = key;
            this.val = val;
        }
    }

    private int capacity;
    private Map<Integer, Node> map;
    private Node head;
    private Node tail;

    public LRUCache(int capacity) {
        this.capacity = capacity;
    }
    
    public int get(int key) {
        if (!map.containsKey(key)) {
            return -1;
        }
        Node n = map.get(key);
        remove(n);
        add(n);
        return n.val;
    }
    
    public void put(int key, int val) {
        if (map.containsKey(key)) {
            Node old = map.get(key);
            remove(old);
        }
        Node newNode = new Node(key, val);
        map.put(key, newNode);
        add(newNode);

        if (map.size() > capacity) {
            map.remove(head.next.key);
            remove(head.next);
        }
    }

    public void add(Node node) {
        Node prev = tail.prev;
        prev.next = node;
        node.prev = prev;
        node.next = tail;
        tail.prev = node;
    }

    public void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
}
