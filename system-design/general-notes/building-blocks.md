# System Design concepts
## Load Balancer
- Distributes high traffic across servers to prevent overload
- Scalability: add more servers to increase capacity
- Availability: system remains online even if a server fails
- Performance: distribution algorithm can route requests to the servers with the lowest loads
- LB Services:
    - Health Checks (heartbeat protocol)
    - TLS termination
    - Predictive analytics & automated failure handling
    - Mitigates DoS at Network, Transport, and Application layers
- LB Algorithms:
    - Static: fixed configuration (i.e. Round Robin), no overhead & very simple
    - Dynamic: uses state to determine routing (i.e. Lowest Response Time, Least Connections), communication overhead b/w LBs but performs better in practice

## Databases
- ACID
    - Atomicity: Transactions happen all at once or are rolled back together
    - Consistency: State is consistent before/after transactions
    - Isolation: Concurrent transactions have no effect on each other
    - Durability: Committed tx survives forever
- Relational DBs
    - Default for structured data storage
    - Abstracts ACID transactions
    - SQL allows schema modification even while DB server is up and running
    - RDBMS manage concurrency control
    - Consistent states for backup and quick recovery
- Non-Relational DBs
    - Designed for diverse data models
    - Excel in apps requiring large volumes of semi-structured/unstructured data
    - Simple design: Can store data in one document rather than splitting across multiple tables
    - Automatically shard data across multiple nodes, easy for horizontal scaling
    - High Availability (due to data replication)
    - Flexible schema
- Replication
    - Synchronous: primary node waits for ack from secondary nodes confirming data is updated
    - Asynchronous: primary node reports success after updating itself, does not wait for secondary nodes acks
    - Models:

        Single Leader (primary-secondary)
        - One node is the leader, processes writes and propagates updates to secondary nodes in the following possible ways:
            1. Statement-based replication (executes SQL statements and writes them to a log file, secondary nodes read the log file and execute)
            2. Write-ahead log shipping (Txs are written to a log file on disk, records low-level byte changes rather than SQL statements)
            3. Logical (row-based) replication (captures changes a row level - logical data changes like specific col in specific row)

        Multi Leader
        - Multiple nodes can accept writes and replicate to others, solves bottleneck of single leader but introduces possibility of write conflicts (i.e. two clients modify same data at two different clients). Can overcome via different methods, like last-write-wins or routing all writes for given record to single leader.
        
        Peer-to-peer (leaderless)
        - All nodes have equal weight and accept both reads/writes. 
- Partioning/Sharding
    Splits large dataset into smaller chunks
    - Vertical Sharding
        - Moving specific tables/columns to different DB instances
        - Manual & static, for example Employee has some metadata and a picture. Split into two tables, lighter metadata table and blob data picture table.
    - Horizontal Sharding
        - Divides table row-wise into smaller tables
        - Primary strategies are key-range-based (e.g. specific range of IDs 1-100, 101-200) and hash-based (uses hash function to determine partition like hash_function(key) % (# of partitions))
        - key-range-based is better for range queries, hash-based is better for avoiding hotspots

## Key-Value Stores
- Distributed hashmap
```
Functional Reqs:
- GET/PUT
- Configurable service (users can choose how consistent/available data is)
- Ability to always write (prioritizes Availability over Consistency)
- Hardware heterogeneity
```