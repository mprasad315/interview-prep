# Consistency
- Eventual consistency (eventually all replicas have the same data after writes conclude)
- Causal consistency (dependent data is consistent – a writes 5 to x, y = x + 5 -> y is dependent on x)
    - Useful for things like commenting systems, order of all comments doesn't matter but replies to comments do matter
- Sequential consistency (order of ops for each client's program is preserved)
    - Useful for things like social media feeds, timeline of a single friend's posts matters but not relative order of posts from different friends
- Strict consistency (a read request always returns the most recent write)
    - Useful for things like passwords, old passwords can't be accepted for logging in

# Failure Models
- Fail Stop (node halts permanently, other nodes communicating can easily detect)
- Crash (node halts silently)
- Omission Failures (node fails to send or receive messages)
- Temporal (node delivers results too late to be useful)
- Byzantine (node exhibits arbitrary behavior)

# Non-Functional Characteristics
## Availability
- Percentage of time a system is accessible and functional

> A (in percent) = (Total Time - Amount Of Time Service Was Down) / Total Time x 10

## Reliability
- Measures consistent performance over time
- Strive for high MTBF (mean time between failures) and low MTTR (median time to repair)
> MTBF = (Total Elapsed Time - Sum of Downtime) / Total Number of Failures
> MTTR = Total Maintenance Time / Total Number of Repairs

## Scalability
- Ability to handle increasing workload
- System dependent – request workloads for # of reqs, data workloads for amount of data
- Vertical Scaling
    - Upgrade hardware resources of existing server, works best for predictable workloads needing an immediate boost
- Horizontal Scaling
    - Adding more machines to distribute workloads, works best for systems expecting rapid growth or fluctuating workloads
- Scalability Techniques
    - Load Balancing: distribute user traffic across multiple servers
    - Caching and CDNs: caching frequently accessed data to reduce db load, CDNs distribute static content from geographically relevant servers
    - Data replication and Sharding: replication duplicates data for fault tolerance, sharding partitions data across multiple DBs to improve performance
    - Microservices: scale services independently per demand

## Fault Tolerance
- Ability of a system to continue operating even if one or more components fail
- Mitigated by replication, fail-over strategies, etc.


# Non-Functional Requirements
## Performance
- Ability to respond to requests and process data efficiently
- Caching, algorithm/data-structure selection, load balancing
## Availability
- Redundancy – replicate key components and data across multiple servers
- Fault tolerance
- Rate limiting
- CDNs
- Stress testing & monitoring
## Scalability
- Vertical/Horizontal and Automatic scaling
- Sharding – split DBs to distribute data load across servers
- Modular design (microservices)
- Caches and CDNs


# Interview Tips
- Proactively clarify NFRs during the interview. Ask about:
    - Expected user traffic
    - Expected data load
    - Expected downtime tolerance
- Evaluate trade-offs between techniques, considering complexity, cost, and maintainability
- Prepare solutions for common patterns:
    - Transactions: Choose ACID-compliant relational databases
    - Large-scale data: Use NoSQL databases (MongoDB, Cassandra) for scalability
    - Real-time data: Use streaming platforms like Apache Kafka or Amazon Kinesis


# BOTEC (Back of the Envelope Calculations)
Single Server Capacity (64 cores) = 64,000 RPS

Pareto Principle (80/20 rule) – 80% of traffic occurs within 20% of the time

Sample Resource Estimation for Twitter-like service:
```
Server Requirements: 

Assumptions: 500M daily active users (DAU)
20 req/user/day
64K req/s (single server 64 cores)

Servers needed @ peak = 500M * 20 / 64K = 157K servers (10B req/s is upper bound, assuming all reqs come in the same second)

Using Pareto Principle -> (.8 * (500M * 20) reqs / .2 * 24 * 60 * 60) / 64K = 8 servers


Storage Requirements:

Assumptions: 500M DAU
3 tweets/user/day
10% contain images, 5% contain videos
Avg size: tweet 3b, img 200kb, video 3mb

500M * 3 = 1.5B req/day
1.5B * 0.1 (# of image tweets) * 200 * 1000B = 30 TB for images
1.5B * 0.05 (# of video tweets) * 3 * 1000000B = 225 TB for videos
Daily storage = 30 + 225 = 255 TB


Bandwidth Requirements:

Incoming: 255 TB/day / 86400 (s/day) = 24 Gbps

Outgoing: 
Assumptions: each user sees 50 tweets/day

500M * 50 tweets = 2.5B tweets
2.5B tweets/day / 86400 s/day = 289000 tweets/s
289000 * .1 (# of image tweets viewed) * 200 * 1000B * 8 = 46.24 Gbps
289000 * .05 (# of video tweets) * 3 * 1000000B * 8 = 346.8 Gbps
Outgoing = ~393 Gbps

Total = 393 + 24 = 417 Gbps
```