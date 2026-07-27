# Data Structures and Usage
## HashMap
- Implements Map interface
- Allows one null key, uses key-based retrieval/insertion/deletion with O(1) average time
- No order

Usage:
``` 
import java.util.HashMap;
import java.util.Map;

HashMap<String, Integer> hashMap = new HashMap<>();
hashMap.put("X", 3);
```
- Supports get(), put(), remove(), containsKey(), containsValue(), putAll()

## LinkedHashMap
- Extends HashMap
- Allows one null key, O(1) average time for all operations
- Maintains insertion order via doubly linked list

Usage:
```
import java.util.LinkedHashMap

// Default insertion order LHM
LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
// Access order LHM
LinkedHashMap<Integer, String> aoMap = new LinkedHashMap<>(16, .75f, true);
// .75f is the float factor (?) meaning once it reaches 75% capacity it will expand the map, true is ordering mode
```

- Pretty similar to LRU cache, can override removeEldestEntry method to enforce policy for removing stale mappings (maintain fixed # of mappings)

## TreeMap
- Implements Map interface using self-balancing Red/Black tree architecture
- No null keys, continuously maintained in sorted sequence, O(log n) average time

Usage:
```
import java.util.TreeMap;
import java.util.Comparator;

// Default ascending order
TreeMap<Integer, String> naturalMap = new TreeMap<>();
// Custom sorting using Comparator
TreeMap<Integer, String> customMap = new TreeMap<>(Comparator.reverseOrder());
```
- Most useful when you need sorted order like a game scoreboard or BST

## ArrayList
- Implements List interface to represent resizable, dynamic array
- Allows O(1) lookup through positional indices, as well as null and duplicate values, O(n) addition/removal of items due to shifting all the items
- Supports add, get, set, remove, size, contains, clear

## LinkedList
- Implements List and Deque interfaces
- Allows O(n) lookup due to needing to traverse from either head/tail, but does not require shifting elements for addition/removal of items
- Additional deque methods: addFirst, addLast, removeFirst, removeLast, getFirst, getLast

## BigDecimal
- Immutable (operations return a brand new object) & size limited only by system's available memory
- Provides mathematical exactness due to floating-point precision issues with double/float
- Initialize with Strings or static factory methods (i.e. valueOf()) to preserve precision