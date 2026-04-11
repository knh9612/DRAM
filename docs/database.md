# Dram — Database

## 기술 스택

| 구분 | 서비스 | 용도 |
|---|---|---|
| RDBMS | PostgreSQL 16 + pgvector | 위스키, 사용자, 위시리스트, 노트 + 벡터 검색 |
| NoSQL | DynamoDB | AI 채팅 (세션/메시지) |

---

# PostgreSQL

## ENUM 타입

```sql
CREATE TYPE flavor_tag AS ENUM (
    'HONEY', 'VANILLA', 'CARAMEL', 'CHOCOLATE',
    'APPLE_PEAR', 'CITRUS', 'BERRY', 'TROPICAL', 'DRIED_FRUIT',
    'MALT', 'COFFEE',
    'CINNAMON', 'PEPPER', 'GINGER', 'CHILI',
    'SMOKY', 'MEDICINAL', 'EARTHY',
    'FLORAL', 'HERBAL',
    'OAK', 'CREAMY', 'NUTTY', 'MARITIME'
);

CREATE TYPE drinking_method AS ENUM ('NEAT', 'ON_THE_ROCKS', 'HIGHBALL', 'WATER');
CREATE TYPE body_type AS ENUM ('LIGHT', 'MEDIUM', 'FULL');
CREATE TYPE aftertaste_type AS ENUM ('SHORT', 'MEDIUM', 'LONG');
```

Java 매핑 시 각 ENUM에 `displayName` 필드로 한글 표시값 저장.

| flavor_tag | displayName |
|---|---|
| HONEY, VANILLA, CARAMEL, CHOCOLATE | 꿀, 바닐라, 캐러멜, 초콜릿 |
| APPLE_PEAR, CITRUS, BERRY, TROPICAL, DRIED_FRUIT | 사과/배, 감귤/레몬, 베리, 열대과일, 건과일 |
| MALT, COFFEE | 몰트/보리, 커피 |
| CINNAMON, PEPPER, GINGER, CHILI | 시나몬, 후추, 진저, 고추 |
| SMOKY, MEDICINAL, EARTHY | 스모키, 약품, 흙 |
| FLORAL, HERBAL | 꽃향, 허브 |
| OAK | 오크 |
| CREAMY | 크림/버터 |
| NUTTY | 견과류 |
| MARITIME | 바다/소금 |

| 기타 ENUM | 값 → displayName |
|---|---|
| drinking_method | NEAT → 니트, ON_THE_ROCKS → 온더락, HIGHBALL → 하이볼, WATER → 물 약간 |
| body_type | LIGHT → 라이트, MEDIUM → 미디엄, FULL → 풀바디 |
| aftertaste_type | SHORT → 짧다, MEDIUM → 중간, LONG → 길다 |

---

## 테이블

### whiskies

```sql
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE whiskies (
    id                BIGSERIAL PRIMARY KEY,
    name_en           TEXT NOT NULL,
    name_ko           TEXT NOT NULL,
    image_file        TEXT,
    alcohol_by_volume DOUBLE PRECISION,
    style             VARCHAR(50),    -- Single Malt, Blended, Bourbon 등
    country           VARCHAR(50),
    region            VARCHAR(50),
    price             INTEGER,

    nose_tags         flavor_tag[],
    palate_tags       flavor_tag[],
    body              body_type,
    aftertaste        aftertaste_type,
    description       TEXT,

    embedding_text    TEXT,
    embedding         vector(1536),   -- 검색은 네이티브 쿼리 (<=> 연산자)

    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### users

`user`는 PostgreSQL 예약어라 `users` 사용.

```sql
CREATE TABLE users (
    id                BIGSERIAL PRIMARY KEY,
    nickname          VARCHAR(50) NOT NULL,
    email             VARCHAR(255),
    profile_image     TEXT DEFAULT 'default.png',
    oauth_provider    VARCHAR(20) NOT NULL,   -- KAKAO, NAVER, GOOGLE
    oauth_id          VARCHAR(255) NOT NULL,
    refresh_token     TEXT,
    reminder_enabled  BOOLEAN DEFAULT FALSE,
    fcm_token         TEXT,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_users_oauth UNIQUE (oauth_provider, oauth_id)
);
```

### wishlists

```sql
CREATE TABLE wishlists (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    whisky_id   BIGINT NOT NULL REFERENCES whiskies(id) ON DELETE CASCADE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_wishlists UNIQUE (user_id, whisky_id)
);
```

### tasting_notes

```sql
CREATE TABLE tasting_notes (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    whisky_id        BIGINT NOT NULL REFERENCES whiskies(id) ON DELETE CASCADE,

    drinking_method  drinking_method,
    selected_tags    flavor_tag[],
    body             body_type,
    aftertaste       aftertaste_type,
    rating           DOUBLE PRECISION NOT NULL,
    memo             TEXT,

    ai_note          TEXT,   -- [NOSE]...[PALATE]...[FINISH]...[OVERALL]...
    embedding        vector(1536),   -- ai_note 임베딩

    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### ERD

```
users (1) ──── (N) wishlists (N) ──── (1) whiskies
  │                                         │
  └──── (N) tasting_notes (N) ──────────────┘
```

---

## 벡터 검색 (pgvector)

### 임베딩 대상

| 테이블 | 컬럼 | 내용 |
|---|---|---|
| `whiskies.embedding` | 위스키 공식 정보 | 메타 + 원본 테이스팅 노트 + 한글 태그 조합 (`embedding_text`)을 임베딩 |
| `tasting_notes.embedding` | 사용자 경험 | `ai_note`를 임베딩 (LLM이 태그/바디/여운/메모를 정리한 자연어) |

둘 다 같은 모델(OpenAI `text-embedding-3-small`)로 1536차원 벡터 생성. 같은 공간에서 비교 가능.

### 검색 방식

| 용도 | 비교 대상 | 설명 |
|---|---|---|
| 자연어 검색 (검색 모드) | 쿼리 벡터 ↔ `whiskies.embedding` | 취향 미반영, 객관적 검색 |
| AI 추천 (AI 모드) | (쿼리 벡터 + 취향 벡터) ↔ `whiskies.embedding` | 취향 벡터 = `AVG(tasting_notes.embedding)`, 가중치는 튜닝 대상 |

유사도 연산자는 `<=>` (코사인 거리).

---

# DynamoDB

## chat 테이블 (싱글 테이블)

세션과 메시지를 한 테이블에 Item 타입으로 구분.

```
세션 Item:
  PK        "USER#1"                    (S)
  SK        "SESSION#sess_a1b2c3d4"     (S)
  type      "session"                    (S)
  title     "스모키 위스키 추천"           (S)
  created_at "2026-04-02T14:00:00"       (S)
  updated_at "2026-04-02T14:05:30"       (S)   -- 정렬용, 대화 시 갱신
  ttl       1743696000                   (N)   -- created_at + 7일

메시지 Item:
  PK              "SESSION#sess_a1b2c3d4"     (S)
  SK              "MSG#2026-04-02T14:00:01"   (S)
  type            "message"                    (S)
  role            "user" | "assistant"         (S)
  content         "5만원대 스모키한..."         (S)
  recommended_ids [10, 15, 22]                (L)
  ttl             1743696001                   (N)
```

## 조회 패턴

```
세션 목록:  Query PK = "USER#1", SK begins_with "SESSION#"
세션 메시지: Query PK = "SESSION#sess_abc", SK begins_with "MSG#"
```

## 세션 관리 정책

- 최대 5세션 / 사용자
- TTL 7일 (created_at 기준, 갱신 안 함)
- AI 모드 진입 시 항상 새 세션 생성
- 5개 초과 시 가장 오래된 세션 + 해당 메시지 삭제

## 데이터 예시

// 세션 Item
```json
{
  "PK": "USER#1",
  "SK": "SESSION#sess_a1b2c3d4",
  "type": "session",
  "title": "스모키 위스키 추천",
  "created_at": "2026-04-02T14:00:00",
  "updated_at": "2026-04-02T14:05:30",
  "ttl": 1743696000
}
```
// 메시지 Item (AI 응답)
```json
{
  "PK": "SESSION#sess_a1b2c3d4",
  "SK": "MSG#2026-04-02T14:00:03",
  "type": "message",
  "role": "assistant",
  "content": "스모키한 위스키 중에서 5만원대로 즐길 수 있는 3가지를 골라봤어요",
  "recommended_ids": [10, 15, 22],
  "ttl": 1743696003
}
```
