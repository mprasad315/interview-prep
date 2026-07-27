package coding.structures;

class Node<K, V> {
    final K key;
    V value;
    Node<K, V> next;
    Node(K key, V value) {
        this.key = key;
        this.value = value;
    }
}

public class HashMap<K, V> {

    private Node<K, V>[] buckets;
    private static int capacity = 16;
    private int size;
    private final float loadFactor = 0.75f;

    private int hash(K key) {
        return Math.abs(key.hashCode()) % capacity;
    }
    public void put(K key, V value) {
        int hashVal = hash(key);
        Node<K, V> head = buckets[hashVal];

        if (head == null) {
            buckets[hashVal] = new Node<>(key, value);
            size++;
            return;
        }

        Node<K, V> current = head;

        while(true) {
            if(current.key == key || (current.key != null && current.key.equals(key))) {
                current.value = value;
                return;
            }
            if(current.next == null) {
                break;
            }
            current = current.next;
        }
        current.next = new Node<K, V>(key, value);
        size++;

        if (size / buckets.length >= loadFactor) {
            resize();
        }
    }

    public V get(K key) {
        int hashVal = hash(key);
        Node<K, V> current = buckets[hashVal];

        if (current == null) {
            throw new IllegalArgumentException();
        }
        while (current != null) {
            if (current.key == key || (current.key != null && current.key.equals(key))) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public V remove(K key) {
        int hashVal = hash(key);
        Node<K, V> current = buckets[hashVal];
        Node<K, V> prev = null;
        while(current != null) {
            if (current.key == key || (current.key != null && current.key.equals(key))) {
                if(prev == null) {
                    buckets[hashVal] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return current.value;
            }

            prev = current;
            current = current.next;
        }
        return null;
    }

    private void resize() {
        Node<K, V>[] oldBuckets = buckets;
        capacity = capacity * 2;
        buckets = new Node[capacity];
        size = 0;

        for (Node<K, V> head: oldBuckets) {
            Node<K, V> current = head;
            while (current != null) {
                put(current.key, current.value);
                current = current.next;
            }
        }

    }
}
