# MiniCache

A lightweight in-memory key-value store built from scratch in Java, inspired by how Redis works under the hood. MiniCache accepts real TCP connections, parses commands over a custom protocol, stores data in RAM, and serves it back at high speed — no HTTP framework, no ORM, no database engine.

Built as a portfolio project to demonstrate systems-level engineering thinking that goes one layer below what most developers ever touch.

![Java](https://img.shields.io/badge/Java-17-orange)
![React](https://img.shields.io/badge/React-18-blue)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)
![CI](https://img.shields.io/badge/CI-GitHub_Actions-green)

---

## What It Does

Most backend projects sit *on top* of infrastructure like Redis without understanding how it works. MiniCache flips that — it *is* the infrastructure.

A client connects over raw TCP, sends a command like `SET name john`, and gets `OK` back in milliseconds. The data lives in memory for instant access, auto-expires via a min-heap if a TTL was set, and survives server restarts via an append-only log replayed at startup.

---

## Features

### TCP Server
- Accepts multiple simultaneous client connections over raw TCP on port `6379`
- Thread pool via `ExecutorService` handles concurrent clients safely
- No HTTP — pure socket I/O using `java.net.ServerSocket`

### Custom Protocol Parser
- RESP-inspired text protocol: one command per line, space-separated
- `ProtocolParser` validates command names and argument counts
- Returns structured `Command` objects; rejects malformed input gracefully

### Core Command Set
```
SET key value          → OK
GET key                → value or NULL
DEL key                → 1 (deleted) or 0 (not found)
EXISTS key             → 1 or 0
KEYS                   → comma-separated list of all keys
SET key value EX 30    → store with 30-second TTL
PING                   → PONG
```

### TTL Expiry
- Keys auto-delete after N seconds via `SET key value EX <seconds>`
- Implemented with a `PriorityQueue` min-heap — soonest expiry always at the top
- Background daemon thread sweeps expired keys every 100ms without scanning all keys

### AOF Persistence
- Every write command is appended to `minicache.aof` on disk immediately
- On restart, the log is replayed top-to-bottom to rebuild exact in-memory state
- Data survives crashes and container restarts via Docker volume mount

### HTTP Stats Layer
- Thin HTTP server on port `8081` using Java's built-in `com.sun.net.httpserver`
- `GET /stats` → key count and uptime as JSON
- `GET /keys` → all keys with remaining TTL as JSON
- Read-only — the core TCP server is completely untouched

### Live React Dashboard
- Polls `/stats` and `/keys` every 2 seconds
- Shows server status (online/offline), total key count, uptime, and live key table
- Keys display TTL countdown and persistent/expiring status badge

---

## Architecture

```
┌─────────────────────────────────────────────┐
│                  Client                      │
│         (TestClient / Netcat / App)          │
└──────────────────┬──────────────────────────┘
                   │ TCP (port 6379)
┌──────────────────▼──────────────────────────┐
│              TcpServer                       │
│    ServerSocket + ExecutorService            │
│              ClientHandler                   │
│           (one thread per client)            │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│            CommandHandler                    │
│    ProtocolParser → Command → Store          │
│         AOFWriter logs all writes            │
└──────────┬───────────────────────────────────┘
           │
┌──────────▼───────────────────────────────────┐
│                  Store                        │
│          ConcurrentHashMap<K,V>               │
│          TTLManager (PriorityQueue)           │
│          Background sweep thread              │
└───────────────────────────────────────────────┘

┌───────────────────────────────────────────────┐
│           MiniCacheHttpServer (port 8081)      │
│    /stats   /keys  (read-only window)          │
│              ↑                                 │
│       React Dashboard (port 80)                │
└───────────────────────────────────────────────┘
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Server core | Java 17 (standard library only) |
| Concurrency | `java.util.concurrent` — `ExecutorService`, `ConcurrentHashMap` |
| TTL expiry | `java.util.PriorityQueue` (min-heap) |
| Persistence | File I/O — `BufferedWriter` / `BufferedReader` |
| HTTP layer | `com.sun.net.httpserver` (built-in, no Spring Boot) |
| Frontend | React 18 + Vite + Tailwind CSS |
| Containerization | Docker + Docker Compose |
| CI | GitHub Actions |
| Unit testing | JUnit 5 |

---

## Getting Started

### Prerequisites
- Docker Desktop installed and running
- That's it — everything else runs inside containers

### Run with Docker

```bash
git clone https://github.com/Yasiru-Silva/minicache.git
cd minicache
docker-compose up --build
```

- Dashboard: [http://localhost](http://localhost)
- TCP server: `localhost:6379`
- HTTP API: [http://localhost:8081/stats](http://localhost:8081/stats)

### Run Locally (without Docker)

**Server:**
```bash
cd server
mvn compile exec:java -Dexec.mainClass="com.minicache.Main"
```

**Dashboard:**
```bash
cd dashboard
npm install
npm run dev
```

---

## Testing the Server

With the server running, use the built-in test client:

```bash
cd server
mvn compile exec:java -Dexec.mainClass="com.minicache.TestClient"
```

Or connect manually with Netcat:

```bash
nc localhost 6379
SET foo bar
GET foo
SET session abc123 EX 5
EXISTS session
KEYS
DEL foo
```

Run unit tests:

```bash
cd server
mvn test
```

---

## Project Structure

```
minicache/
├── server/
│   └── src/main/java/com/minicache/
│       ├── Main.java
│       ├── server/         # TCP server, connection handling, command routing
│       ├── protocol/       # RESP-inspired parser, Command object
│       ├── store/          # ConcurrentHashMap store, TTL min-heap
│       ├── persistence/    # AOF writer and replay loader
│       └── http/           # Thin HTTP layer for dashboard
├── dashboard/
│   └── src/
│       ├── components/     # StatsCard, KeysTable, StatusBadge
│       ├── services/       # API fetch functions
│       └── App.jsx         # Polling logic, layout
├── docker-compose.yml
└── .github/workflows/      # Server CI + Dashboard CI
```

---

## What I Learned

**TCP and raw socket programming** — Understanding what actually happens beneath HTTP: how a connection is established, how bytes are read and written, and how to manage multiple simultaneous clients with a thread pool.

**Protocol design** — Designing a simple but complete wire protocol from scratch: how to frame messages, validate inputs, and handle malformed commands without crashing.

**Concurrent data structures** — Why a regular `HashMap` breaks under concurrent writes and how `ConcurrentHashMap` solves it internally. How to reason about thread safety in a multi-client server.

**TTL with a min-heap** — Why scanning all keys for expiry is O(n) and wasteful, and how a `PriorityQueue` reduces the sweep to O(log n) insertions with O(1) peek at the next expiry.

**Persistence trade-offs** — The durability vs speed trade-off in append-only logging: why `flush()` after every write matters, and how replaying a log rebuilds state identically after a crash.

**Docker multi-stage builds** — How to separate the build environment from the runtime environment to keep production images lean, and how Docker Compose wires multiple services together with shared networking and volumes.

---

## Future Enhancements

- LRU eviction policy when a memory limit is reached
- Sorted sets using a skip list implementation
- Basic primary-replica replication
- Pub/Sub messaging channels
- Benchmarking mode — requests per second under load
- AOF compaction — rewrite the log periodically to remove redundant commands
