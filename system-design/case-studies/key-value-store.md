```
Functional Reqs:
- GET/PUT
- Configurable service (users can choose how consistent/available data is)
- Ability to always write (prioritizes Availability over Consistency)
- Hardware heterogeneity (must integrate new servers without upgrading existing ones)

NFRs:
- Scalability, must support tens of thousands of servers globally
- Fault tolerance, must operate uninterrupted despite server failures

Essentially just a hashmap

get(key)

put(key, value)

Data integrity checks: Compress -> Hash -> Encrypt

Scalability:
We need to distribute data across multiple storage nodes. We can do this by hash-based partioning – typically a modular function if servers are not expected to scale. But in this case, key-value store we expect lots of scaling so we should use consistent hashing (minimizes data movement during scaling) – visualize it as a ring. We hash nodes and place them along the ring, then we hash keys and map them to the node with the closest hash going clockwise along the ring. We can make distribution even more uniform by using virtual nodes, we use multiple different hash functions on the nodes and place them multiple times along the ring

Data Replication:
Peer-to-peer replication allows for writes from all nodes, which satisfies our requirement as opposed to primary-secondary replication. The tradeoff here is that we may have more conflicts with data between nodes. We also should understand whether use synchronous or asynchronous replication, where synchronous would ensure data consistency but might result in decreased availability if some replicas are unreachable and async would increase availability of writes but risk temporary inconsistencies in data.

Data Integrity:
To maintain data integrity we should use versioning and checksums. We should also update the APIs
get(key) now returns context along with the value

put(key, context, value) where context holds the object metadata like vector clock (pair of node, counter)
```