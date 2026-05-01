# Jackpot Service

A backend service that manages jackpot pool contributions and rewards.
Built with Java 17, Spring Boot 3, Kafka, and H2 in-memory database.

---

## Architecture Overview

```
POST /api/jackpot/bets
        │
        ▼
 JackpotController
        │
        ▼
 BetKafkaProducer ──► Kafka (jackpot-bets topic)
                                │
                                ▼
                      BetKafkaConsumer
                                │
                                ▼
                  JackpotContributionService
                      (Strategy pattern)
                                │
                                ▼
                         H2 Database

POST /api/jackpot/bets/{betId}/evaluate
        │
        ▼
 JackpotRewardService
   (Strategy pattern)
        │
        ▼
   H2 Database
```

### Key Design Decisions

**Strategy Pattern** is used for both contribution and reward logic:
- `FixedContributionStrategy` — fixed % of bet amount regardless of pool size
- `VariableContributionStrategy` — decreasing % as pool grows
- `FixedRewardStrategy` — fixed % win probability
- `VariableRewardStrategy` — growing win probability, 100% at pool limit

New contribution or reward types can be added by implementing the
`ContributionStrategy` or `RewardStrategy` interfaces — no changes to
existing code required.

---

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker + Docker Compose

---

## How to Run

### 1. Start Kafka

```bash
docker-compose up -d
```

Wait ~10 seconds for Kafka to be ready.

### 2. Build and run the application

```bash
mvn spring-boot:run
```

The service starts on `http://localhost:8080`.

### 3. Run tests

```bash
mvn test
```

---

## Pre-seeded Jackpots

Two jackpots are available on startup:

| Jackpot ID | Contribution | Reward |
|------------|-------------|--------|
| `jackpot-fixed-001` | Fixed 5% of bet | 10% fixed win chance |
| `jackpot-variable-001` | Variable 10% → decays as pool grows | Variable: 100% at £50,000 pool |

---

## API Reference

### 1. Place a Bet

Publishes a bet to Kafka. The consumer processes it asynchronously
and contributes to the matching jackpot pool.

```
POST /api/jackpot/bets
Content-Type: application/json

{
  "betId": "bet-001",
  "userId": "user-123",
  "jackpotId": "jackpot-fixed-001",
  "betAmount": 100.00
}
```

**Response:** `202 Accepted`
```
Bet bet-001 submitted successfully.
```

---

### 2. Evaluate Jackpot Reward

Evaluates whether a contributing bet wins the jackpot.
The bet must have been processed (consumed from Kafka) before calling this.

```
POST /api/jackpot/bets/{betId}/evaluate
```

**Example:**
```
POST /api/jackpot/bets/bet-001/evaluate
```

**Response — Winner:**
```json
{
  "betId": "bet-001",
  "userId": "user-123",
  "jackpotId": "jackpot-fixed-001",
  "winner": true,
  "rewardAmount": 1005.00,
  "message": "Congratulations! You won the jackpot!"
}
```

**Response — No win:**
```json
{
  "betId": "bet-001",
  "userId": "user-123",
  "jackpotId": "jackpot-fixed-001",
  "winner": false,
  "rewardAmount": 0,
  "message": "Better luck next time."
}
```

---

## H2 Console

Available at `http://localhost:8080/h2-console`

- **JDBC URL:** `jdbc:h2:mem:jackpotdb`
- **Username:** `sa`
- **Password:** *(empty)*

---

## Example Flow with curl

```bash
# 1. Place a bet
curl -X POST http://localhost:8080/api/jackpot/bets \
  -H "Content-Type: application/json" \
  -d '{"betId":"bet-001","userId":"user-123","jackpotId":"jackpot-fixed-001","betAmount":100.00}'

# Wait 1-2 seconds for Kafka consumer to process the contribution

# 2. Evaluate if the bet wins
curl -X POST http://localhost:8080/api/jackpot/bets/bet-001/evaluate
```
