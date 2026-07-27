```
FRs:
1. Place Orders: Support price limit orders & instant execution orders
2. Cancel/Modify Orders
3. Provide real-time order books & history

NFRs:
1. Extremely low latency < 1 ms for matching orders, < 5 ms for APIs
2. High throughput 50-100k req/s
3. No duplication of transactions
4. Deterministic/Idempotent: Time priority on execution, same inputs yield same outputs

Ingestion:
Clients should hit a Load Balancer (within our infra) which will terminate TLS and forward HTTP over raw TCP socket to our infra (over virtual private cloud for security).
First stop is API gateway, where we can authenticate traffic & drop anything malicious. Also serialize our payload here into compact, binary payloads.
(Sequencer) acts as immutable, append-only ledger. Assigns a sequential ID to the order. Makes the system deterministic, on crash we can replay all logs from 0 to recreate the exact state.
From there, we go to our matching engine which reads sequenced binary events one by one. Performs logic of crossing bids and asks using O(1) data structures. Spits out a stream of execution events (Order 59 filled by Order 89 at $150, quantity 10)
Downstream 1: Account State & DB Persistence
Downstream 2: Market Data Broadcast (UDP Multicast for institutions, websockets for retail/web). For retail/web clients – cluster of fan-out servers consumes the market data stream via UDP multicast, converts back to JSON, and pushes over persistent web socket connections to thousands of retail screens or charts 
If UDP drops a packet, it can fetch the missed packets by sequence # from our data persisted in DB
Matching engine emits events to high-speed messaging ring buffer – single thread. Writes directly with no locks
                       +-------------------+

                       |  Matching Engine  | (Producer)
                       +-------------------+
                                 |
                                 v
                       +-------------------+

                       |    RING BUFFER    |
                       +-------------------+
                        /                 \
                       /                   \
                      v                     v
            +-------------------+ +-------------------+

            | Downstream 1:     | | Downstream 2:     | (Independent
            | Account State /DB | | Market Data Pub   |  Parallel Readers)
            +-------------------+ +-------------------+


Component Deep Dive: Deeply Understanding the LOB
To ace the data structure portion, draw this exact hybrid schema to achieve O(1) critical path operations:
The Bids/Asks Trees: Two Balanced Binary Search Trees (Red-Black Trees or B-Trees). Bids are sorted descending; Asks are sorted ascending. Finding the best price is O(1) (the tree root or extreme leaf).
The Price Buckets: Each node in the tree points to a Doubly Linked List of orders at that price. Inserting a new order or filling the oldest order is O(1) (FIFO).
The Order Lookup Map: A standard Hash Map (std::unordered_map or HashMap<OrderID, OrderNode*>). If a user cancels an order, the system looks up the memory pointer in O(1) and extracts it from the linked list in O(1), avoiding a slow tree search.

Dealing with Bottlenecks (Bottleneck Resolution)
Database Writes
Mistake: Writing every trade to a relational database inside the matching loop. This destroys performance.
Fix: Keep balances and the book entirely in-memory. Asynchronously stream trade results out of the engine to a database worker pool for persistence.

Garbage Collection (GC) Pauses
Mistake: Utilizing standard Java or Go objects that trigger runtime GC pauses, freezing the exchange.
Fix: Use object pooling (reusing memory blocks), flat primitive arrays, or off-heap memory allocations (like DirectByteBuffer in Java or writing the core engine in C++/Rust).

Multi-Threading Lock Contention
Mistake: Using Mutexes or synchronized blocks to let multiple CPU cores touch the order book.
Fix: The Matching Engine must run on a single thread pinned to a specific CPU core. A single modern CPU thread running pure in-memory pointer manipulation can effortlessly process over 200,000 matches per second without locking overhead.
```