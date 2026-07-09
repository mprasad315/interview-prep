# Java Concurrency Interview Cheat Sheet

## Golden Rule

Always ask:

1. What state is shared?
2. Is it mutable?

If it isn't mutable, you often don't need synchronization.

> **Prefer immutability over synchronization whenever possible.**

---

# 1. Immutable Objects ⭐⭐⭐⭐⭐

```java
class Config {
    private final Map<Currency, Strategy> strategies;
}
```

### Use when

- Configuration
- Lookup tables
- DTOs
- Request objects

### Pros

- No locks
- Thread-safe by design
- Fast reads
- Easy reasoning

### Cons

- Must rebuild object to modify data

---

# 2. Atomic Swap / Immutable Snapshot ⭐⭐⭐⭐⭐

```java
private volatile Map<Currency, Strategy> strategies;
```

Update flow:

```
Old Map
      ↓
Create New Immutable Map
      ↓
strategies = newMap
```

Readers always see either the old snapshot or the new snapshot.

### Use when

- Configuration updates
- Feature flags
- Currency rules
- Routing tables

### Pros

- Lock-free reads
- Simple implementation
- Excellent for read-heavy workloads

### Cons

- Entire structure copied during updates

---

# 3. synchronized ⭐⭐⭐

```java
synchronized(lock) {
    ...
}
```

### Use when

Simple mutual exclusion.

### Pros

- Very simple
- Built into Java

### Cons

- Readers block readers
- No timeout
- No fairness
- One thread at a time

---

# 4. ReentrantLock ⭐⭐⭐⭐

```java
lock.lock();
try {
    ...
} finally {
    lock.unlock();
}
```

Extra features:

- tryLock()
- timeout
- fairness
- interruptible locking

### Use when

Need more control than synchronized.

### Pros

- Flexible
- Explicit locking

### Cons

- Still exclusive (one thread at a time)

---

# 5. ReentrantReadWriteLock ⭐⭐⭐⭐

```
Readers
✓
✓
✓

Writer
Exclusive
```

Many readers.
One writer.

### Use when

Read-heavy mutable data.

Examples:

- Cache
- Metadata
- Lookup tables

### Pros

- Readers don't block each other

### Cons

- Readers still acquire locks
- More overhead
- Writer starvation possible

---

# 6. StampedLock ⭐⭐⭐

Optimistic reads.

```
Read
 ↓
Validate
 ↓
Done

If validation fails:

Retry with read lock
```

### Use when

Extremely read-heavy workloads.

### Pros

- Very fast optimistic reads

### Cons

- Complex API
- Not reentrant
- Easy to misuse

---

# 7. volatile ⭐⭐⭐⭐

Guarantees **visibility**, NOT atomicity.

```java
volatile boolean running;
```

Good:

```java
running = false;
```

Bad:

```java
counter++;
```

because increment is:

```
Read
Add
Write
```

### Use when

- Shutdown flag
- Configuration reference
- Atomic swap

---

# 8. AtomicInteger ⭐⭐⭐⭐⭐

```java
AtomicInteger count = new AtomicInteger();

count.incrementAndGet();
```

Uses CAS (Compare-And-Swap).

### Use when

- Counters
- Metrics
- IDs
- Statistics

### Pros

- No locking
- Very fast

### Cons

- Only simple atomic operations

---

# 9. AtomicReference ⭐⭐⭐⭐

Thread-safe reference updates.

```java
AtomicReference<Config> config;
```

Example:

```java
config.set(newConfig);
```

### Use when

Atomic object replacement.

---

# 10. ConcurrentHashMap ⭐⭐⭐⭐⭐

Thread-safe map.

Prefer over:

```java
Collections.synchronizedMap(...)
```

### Use when

- Shared cache
- Registry
- Session store
- Mutable lookup table

### Pros

- Concurrent reads
- Concurrent writes
- Highly optimized

### Cons

Still mutable.

Use atomic methods:

```java
computeIfAbsent()
putIfAbsent()
merge()
compute()
```

Avoid:

```java
if (!map.containsKey(key)) {
    map.put(key, value);
}
```

Race condition.

---

# 11. CopyOnWriteArrayList ⭐⭐⭐

Every write copies the array.

Reads require no locking.

### Use when

- Event listeners
- Plugin registry
- Very few writes

### Pros

- Extremely fast reads

### Cons

- Writes are expensive

---

# 12. ThreadLocal ⭐⭐⭐

Each thread gets its own copy.

```java
ThreadLocal<DateFormat>
```

### Use when

Per-thread state.

Avoids synchronization.

---

# 13. ExecutorService ⭐⭐⭐⭐⭐

Never manually create threads.

```java
ExecutorService pool =
    Executors.newFixedThreadPool(8);

pool.submit(task);
```

### Use when

- Background work
- Parallel tasks
- Task scheduling

### Pros

- Thread reuse
- Limits concurrency
- Lifecycle management

### Cons

Must shutdown properly.

---

# 14. ForkJoinPool ⭐⭐⭐⭐

Divide-and-conquer framework.

```
Large Task
     ↓
 Split
     ↓
 Split
     ↓
Combine
```

Used by:

```java
parallelStream()
```

### Use when

CPU-heavy recursive work.

---

# 15. CompletableFuture ⭐⭐⭐⭐⭐

Modern async API.

```java
future
    .thenApply(...)
    .thenCompose(...)
```

Useful methods:

```java
allOf()
anyOf()
exceptionally()
```

### Use when

- Asynchronous workflows
- Calling multiple services
- Pipelining async work

---

# Common Interview Patterns

## Pattern 1

Configuration

↓

Immutable Snapshot

↓

Atomic Swap

---

## Pattern 2

Shared Counter

↓

AtomicInteger

---

## Pattern 3

Shared Mutable Map

↓

ConcurrentHashMap

---

## Pattern 4

Mostly Reads

↓

ReentrantReadWriteLock

OR

Immutable Snapshot

---

## Pattern 5

Independent CPU-bound Tasks

↓

ExecutorService

ForkJoinPool

parallelStream()

---

# Decision Tree

```
Shared State?

│

No
│
└── No synchronization needed

↓

Yes

↓

Mutable?

│

No
│
└── Immutable Object

↓

Yes

↓

Simple Counter?

│
└── AtomicInteger

↓

Single Shared Reference?

│
└── volatile
    or AtomicReference

↓

Shared Map?

│
└── ConcurrentHashMap

↓

Read-heavy?

│

├── Updates Rare
│      └── Immutable Snapshot + Atomic Swap
│
├── Updates Occasional
│      └── ReentrantReadWriteLock
│
└── Updates Frequent
       └── ConcurrentHashMap
       or ReentrantLock
```

---

# Interview Tradeoffs

| Primitive | Best For | Advantages | Tradeoffs |
|------------|----------|------------|-----------|
| Immutable Object | Read-only shared state | No synchronization | Must rebuild to modify |
| Atomic Snapshot | Rare configuration changes | Lock-free reads | Copies whole structure |
| synchronized | Simple locking | Easiest | Blocks everyone |
| ReentrantLock | General-purpose locking | Flexible API | One thread at a time |
| ReentrantReadWriteLock | Read-heavy mutable data | Parallel readers | Lock overhead |
| StampedLock | Extremely read-heavy | Optimistic reads | Complex, non-reentrant |
| volatile | Visibility only | Very lightweight | Not atomic |
| AtomicInteger | Counters | Lock-free | Limited operations |
| AtomicReference | Object replacement | Lock-free swap | Reference only |
| ConcurrentHashMap | Shared mutable maps | Highly concurrent | Still mutable |
| CopyOnWriteArrayList | Mostly reads | Lock-free reads | Expensive writes |
| ThreadLocal | Per-thread state | No sharing | Memory leaks if misused |
| ExecutorService | Task execution | Thread reuse | Pool management |
| ForkJoinPool | Divide-and-conquer | Great for CPU work | Shared common pool by default |
| CompletableFuture | Async workflows | Composable | Can become hard to debug |

---

# Five Principles to Remember

1. Prefer **immutability** over synchronization.
2. Use the **highest-level abstraction** that solves the problem.
3. Match the concurrency primitive to the **read/write workload**.
4. Avoid **shared mutable state** whenever possible.
5. **Measure performance** instead of assuming more threads or different locks will be faster.