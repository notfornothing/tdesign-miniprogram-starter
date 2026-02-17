# Rust Server Query System Backend

A Java backend service for querying Rust game server information using the Steam A2S protocol.

## Tech Stack

- Java 17
- Spring Boot 3.2.x
- MySQL 8.0
- MyBatis-Plus 3.5.x
- Steam A2S Protocol (Source Server Query)

## Project Structure

```
rust-server-backend/
├── pom.xml
├── src/main/java/com/rustserver/
│   ├── RustServerApplication.java
│   ├── config/
│   │   ├── MybatisPlusConfig.java
│   │   ├── WebConfig.java
│   │   └── CorsConfig.java
│   ├── controller/
│   │   ├── ServerController.java
│   │   ├── PlayerController.java
│   │   └── MapController.java
│   ├── service/
│   │   ├── ServerService.java
│   │   ├── PlayerService.java
│   │   └── impl/
│   ├── mapper/
│   │   ├── ServerMapper.java
│   │   └── PlayerMapper.java
│   ├── entity/
│   │   ├── ServerInfo.java
│   │   └── PlayerInfo.java
│   ├── dto/
│   │   ├── ServerListDTO.java
│   │   └── ServerDetailDTO.java
│   ├── a2s/
│   │   ├── A2SClient.java
│   │   ├── A2SQuery.java
│   │   └── model/
│   ├── common/
│   │   ├── Result.java
│   │   └── ErrorCode.java
│   └── util/
│       └── SteamIdUtil.java
└── src/main/resources/
    ├── application.yml
    ├── application-dev.yml
    └── db/
        └── schema.sql
```

## API Endpoints

### Servers

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/servers | Server list (paginated with filters) |
| GET | /api/servers/{id} | Server details |
| GET | /api/servers/{id}/players | Online players |
| GET | /api/servers/{id}/stats | Server statistics |
| GET | /api/servers/{id}/map | Map data |
| POST | /api/servers | Add server |
| DELETE | /api/servers/{id} | Delete server |
| POST | /api/servers/query | Real-time A2S query |
| POST | /api/servers/query/players | Query players via A2S |
| GET | /api/servers/filter-options | Get filter options |

### Players

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/players/{steamId} | Player details |
| GET | /api/players/{steamId}/history | Player server history |

## Quick Start

### 1. Database Setup

```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

### 2. Configure Database

Edit `application-dev.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/rust_server
    username: your_username
    password: your_password
```

### 3. Build & Run

```bash
cd rust-server-backend
mvn clean install
mvn spring-boot:run
```

### 4. Test API

```bash
# Get server list
curl http://localhost:8080/api/servers

# Query a server via A2S
curl -X POST http://localhost:8080/api/servers/query \
  -H "Content-Type: application/json" \
  -d '{"ip": "47.115.230.101", "port": 28015}'
```

## A2S Protocol

The Steam A2S (Source Server Query) protocol uses UDP:

- Query port: Game port + 1 (e.g., 28015 game port → 28016 query port)
- A2S_INFO: Request server information
- A2S_PLAYER: Request player list (requires challenge number)

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `a2s.timeout` | 5000 | A2S query timeout (ms) |
| `a2s.buffer-size` | 8192 | UDP buffer size |

## Database Tables

- `rs_server` - Server information
- `rs_server_status` - Real-time server status records
- `rs_wipe_history` - Server wipe history
- `rs_player` - Player information
- `rs_player_server` - Player-Server relationship
- `rs_search_history` - Search history for trending searches
