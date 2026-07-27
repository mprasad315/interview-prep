```
1. Functional Requirements (FRs)
Ingest Weather Data: Accept time-series weather metrics (temperature, humidity, wind, pressure) from millions of global IoT sensors and external third-party APIs.
Query Latest Weather: Provide real-time fetch of the current weather for a specific location with sub-second latency.
Query Historical Trends: Allow users to fetch historical aggregations (e.g., daily average temperature) for a location over a specified date range.
Weather Alerts: Trigger immediate notifications if metrics cross dangerous thresholds (e.g., wind > 60 mph).

2. Non-Functional Requirements (NFRs)
High Scale/Throughput: Support 100,000 writes per second from global sensors.
Low Latency Reads: Current weather queries must return in under 50ms (P₉₉).
Fault Tolerance & Durability: No data loss if a processing node or region goes down. Zero single points of failure (SPOF).Eventual Consistency: Reads can be slightly stale (up to a few seconds behind real-time), but must reflect accurate timelines.

High Level Design:

                                   [ CLIENT & SENSOR EDGE ]
                      IoT Sensors / Web & Mobile Apps / Weather Stations
                                   │                   ▲
                     (1) HTTPS /   │                   │ (7) Persistent
                         gRPC/HTTP2│                   │     WebSockets
                                   ▼                   │
                ┌──────────────────────────────────────┴───────────────────┐
                │             [ EDGE ROUTING & PROTECTION LAYER ]          │
                │                                                          │
                │     ┌──────────────────────────────────────────────┐     │
                │     │     Layer 4 Network Load Balancer (NLB)      │     │
                │     └──────────────────────┬───────────────────────┘     │
                │                            │ (Pure TCP Routing)          │
                │                            ▼                             │
                │     ┌──────────────────────────────────────────────┐     │
                │     │      Envoy Proxy Layer (API Gateway)         │     │
                │     │  • TLS Termination     • Token Rate Limiting │     │
                │     └──────────────────────┬───────────────────────┘     │
                └────────────────────────────┼─────────────────────────────┘
                                             │ (Internal HTTP/2 / gRPC)
                                             ▼
                ┌──────────────────────────────────────────────────────────┐
                │              [ INGESTION WORKER LAYER ]                  │
                │                                                          │
                │     ┌──────────────────────────────────────────────┐     │
                │     │        Vanilla Java + Netty Cluster          │     │
                │     │  • Direct ByteBuf    • Avro Serialization    │     │
                │     └──────────────────────┬───────────────────────┘     │
                └────────────────────────────┼─────────────────────────────┘
                                             │ (Produce Compressed Avro)
                                             ▼
                ┌──────────────────────────────────────────────────────────┐
                │                [ EVENT STREAMING SPINE ]                 │
                │                                                          │
                │     ┌──────────────────────────────────────────────┐     │
                │     │    Apache Kafka Cluster (Partitioned by ID)  │     │
                │     └──────────────────────┬───────────────────────┘     │
                └────────────────────────────┼─────────────────────────────┘
                                             │ (High-Throughput Consume)
                                             ▼
                ┌──────────────────────────────────────────────────────────┐
                │             [ STREAM PROCESSING ENGINE ]                 │
                │                                                          │
                │     ┌──────────────────────────────────────────────┐     │
                │     │     Apache Flink Cluster (Java DataStream)    │    │
                │     │  • Tumbling/Sliding Event-Time Windowing     │     │
                │     └──────────────┬───────────────────────┬───────┘     │
                └────────────────────┼───────────────────────┼──────────────────────┐
                                     │                       │                      │
                  (5a) Trigger Alert │                       │(5b) Write Hot Metrics│ (5c) Micro-batch Cold Data
                                     ▼                       ▼                      ▼
  ┌──────────────────────────────────┴───┐       ┌───────────┴──────────┐       ┌───┴──────────────────┐
  │         [ REAL-TIME ALERTS ]         │       │    [ HOT STORAGE ]   │       │   [ COLD STORAGE ]   │
  │                                      │       │                      │       │                      │
  │ ┌──────────────────────────────────┐ │       │ ┌──────────────────┐ │       │ ┌──────────────────┐ │
  │ │  Kafka Topic: `alerts-channel`   │ │       │ │ Apache Cassandra │ │       │ │  Cloud Object    │ │
  │ └────────────────┬─────────────────┘ │       │ │ (Time-Series DB) │ │       │ │  Store (S3)      │ │
  │                  │                   │       │ └─────────▲────────┘ │       │ │ (.parquet)     │ │
  │                  ▼                   │       └───────────┼──────────┘       │ └────────▲─────────┘ │
  │ ┌──────────────────────────────────┐ │                   │                  └──────────┼───────────┘
  │ │   Java-Netty WebSocket Cluster   │ │                   │ (6b) Fetch Query            │ (6c) Heavy SQL Analytics
  │ │ • Manages Millions of Live Sockets││                   │                             │
  │ └──────────────────────────────────┘ │       ┌───────────┴──────────┐       ┌──────────┴───────────┐
  │                                      │       │    Query Service     │       │    Trino / Presto    │
  │                                      │       │  (Vanilla Java Netty)│       │  (Distributed Engine)│
  └──────────────────────────────────────┘       └──────────────────────┘       └──────────────────────┘

1. Client & Sensor Edge
Millions of telemetry devices (barometers, anemometers, thermal sensors) and end-user client applications initiate communication. Sensors package raw state arrays (e.g., coordinates, velocity, humidity) and stream them towards the network boundary.

2. Edge Routing & Protection Layer
Layer 4 NLB: Raw incoming TCP/IP payloads strike the Network Load Balancer first. Because it targets the transport layer, it skips inspecting HTTP payload headers entirely, maintaining a memory footprint lightweight enough to withstand massive connection spikes without exhaustion.
Envoy Proxy Gateway: Packets pass to Envoy, which performs CPU-intensive tasks at the edge to protect internal computing boundaries. It executes asymmetric cryptographic math to terminate TLS/SSL, decrypting payloads into raw bytes. Simultaneously, it connects to a distributed Redis mesh to validate access tokens and apply strict token-bucket rate limiting to isolate malicious or broken telemetry modules.

3. Ingestion Worker Layer
Validated requests feed into a lightweight Vanilla Java + Netty Cluster via internal HTTP/2 or gRPC multiplexing.
To preserve low latencies, Netty allocates system memory off-heap via native ByteBuf mechanisms.
Worker threads serialize raw incoming strings into space-optimized Apache Avro binaries.
To bypass context switching, synchronized blocks, and mutual exclusion lock overhead, data pushes through a lock-free ring-buffer structure (LMAX Disruptor Pattern) before being handed to the broker client.

4. Event Streaming Spine
The pipeline dumps binary Avro footprints into an Apache Kafka Cluster. To scale consumption out across downstream compute nodes, topics utilize precise partitioning schemes bound directly to structural Station_ID variables or string-based Geohash prefixes. Because Kafka preserves chronological order within any isolated partition, sequential integrity is maintained automatically for every distinct sensor matrix.

5. Stream Processing Engine
An Apache Flink Cluster running the native Java DataStream API reads from Kafka's partitions. Flink processes streams in real-time, executing an asynchronous dual-routing paradigm:
(5a) Real-Time Alerts: Flink applies strict logical boundaries to incoming streams using a stateful KeyedProcessFunction. If wind velocity or temperatures cross pre-configured safety values, Flink generates a high-priority structural alert footprint and routes it back into a dedicated alerts-channel Kafka topic instantly.
(5b) Hot Storage Pipelines: Sliding and tumbling event-time windows process raw observations into unified multi-minute aggregates (e.g., calculating running metric averages over 5-minute periods).
(5c) Cold Storage Compression: Raw historical entries are batched, converted into highly structured data configurations, and systematically exported to downstream data archives.

ALTERNATIVE: 5 -> stream processor pushes results to a processed-weather-events kafka stream, then we have 3 microservices consume from it (real-time alerting which pushes to websockets, latest-updater which writes hot data to Redis, historical-storage which writes cold data to time-series DB)

```