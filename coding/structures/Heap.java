package coding.structures;

import java.util.ArrayList;

public class Heap {
    /*
       1
     2   3
    4 6 5

    [1, 2, 3, 4, 6, 5]
     */
    class MinHeap {
        private ArrayList<Integer> heap;

        public MinHeap() {
            heap = new ArrayList<>();
        }
        
        private Integer parent(int i) {
            return (i - 1) / 2;
        }
        private Integer leftChild(int i) {
            return 2 * i + 2;
        }
        private Integer rightChild(int i) {
            return 2 * i + 1;
        }
        private void swap(int i, int j) {
            int temp = heap.get(i);
            heap.set(i, heap.get(j));
            heap.set(j, temp);
        }
        public void insert(int value) {
            heap.add(value);
            int currIndex = heap.size() - 1;

            while (currIndex > 0 && heap.get(currIndex) < heap.get(parent(currIndex))) {
                swap(currIndex, parent(currIndex));
                currIndex = parent(currIndex);
            }
        }
        public int extractMin() {
            if (heap.isEmpty()) {
                throw new RuntimeException("Heap is empty");
            }
            int min = heap.get(0);
            int last = heap.remove(heap.size() - 1);
            if (!heap.isEmpty()) {
                heap.set(0, last);
            }
            int currIndex = 0;
            while (true) {
                int left = leftChild(currIndex);
                int right = rightChild(currIndex);
                int smaller = currIndex;

                if (left < heap.size() && heap.get(left) < heap.get(currIndex)) {
                    smaller = left;
                } else if (right < heap.size() && heap.get(right) < heap.get(currIndex)) {
                    smaller = right;
                }

                if (smaller == currIndex) {
                    break;
                }
                swap(currIndex, smaller);
                currIndex = smaller;
            }
            return min;
        }
    }
    
    class MaxHeap {

    }
}
