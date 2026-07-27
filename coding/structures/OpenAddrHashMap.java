package coding.structures;

class Node<K, V> {
    final K key;
    V value;

    Node(K key, V value) {
        this.key = key;
        this.value = value;
    }
}

public class OpenAddrHashMap<K, V> {
    private static final Node ABSENT = new Node<>(null, null);
    private Node<K, V>[] addresses;
    private int capacity = 16;
    private int size = 0;
    private final float loadFactor = 0.75f;

    private int hash(K key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    public void put(K key, V value) {
        if (size >= loadFactor * capacity) {
            resize();
        }

        int index = hash(key);
        int firstAbsent = -1;
        while (addresses[index] != null) {
            if (addresses[index] != ABSENT && addresses[index].key.equals(key)) {
                addresses[index].value = value;
                return;
            } else if (firstAbsent == -1 && addresses[index] == ABSENT) {
                firstAbsent = index;
            }
            index = (index + 1) % capacity;
        }
        if (firstAbsent != -1) {
            addresses[firstAbsent] = new Node<>(key, value);
        } else {
            addresses[index] = new Node<>(key, value);
        }
        size++;
    }

    public V get(K key) {
        int index = hash(key);
        int start = index;
        while (addresses[index] != null) {
            if(addresses[index] != ABSENT && addresses[index].key.equals(key)) {
                return addresses[index].value;
            }
            index = (index + 1) % capacity;
            if (start == index) break;
        }
        return null;
    }

    public boolean remove(K key) {
        int index = hash(key);
        int start = index;

        while (addresses[index] != null) {
            if (addresses[index] != ABSENT && addresses[index].key.equals(key)) {
                addresses[index] = (Node<K,V>) ABSENT;
                size--;
                return true;
            }

            index = (index + 1) % capacity;

            if (index == start) break;
        }

        return false;
    }

    private void resize() {
        Node<K, V>[] oldAddrs = addresses;
        capacity = capacity * 2;
        addresses = new Node[capacity];
        size = 0;

        for (Node<K, V> node : oldAddrs) {
            if (node != null && node != ABSENT) {
                put(node.key, node.value);
            }
        }
    }

    public static void main(String[] args) {
        OpenAddrHashMap<String, Integer> hm = new OpenAddrHashMap<>();
        hm.put("k1", 31);
        System.out.println(hm.get("k1"));
    }
}