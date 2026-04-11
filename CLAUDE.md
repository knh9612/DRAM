# Dram

위스키 초보자를 위한 AI 기반 추천 + 테이스팅 노트 서비스.

## 서비스 개요

수천 종의 위스키 중에서 뭘 골라야 할지 모르는 입문자를 위한 앱. 자연어로 "5만원대 스모키한 입문용 추천해줘"라고 물으면 AI가 추천해주고, 마신 위스키는 태그 선택만으로 AI가 테이스팅 노트를 자동 생성해준다.

### 핵심 기능

1. **AI 모드 채팅** — RAG 기반 위스키 추천 대화 (SSE 스트리밍)
2. **AI 테이스팅 노트** — 태그 선택 → LLM이 노트 자동 생성
3. **자연어 검색** — 키워드가 아닌 문장으로 검색 ("달달하고 부드러운")
4. **위시리스트 / 노트 관리** — 마신 위스키 기록, 다시 마시고 싶은 것 저장
5. **OAuth 소셜 로그인** — 카카오 / 네이버 / 구글

## Important Note for AI Agent

As the AI Agent, you **must not** perform any actions or make any changes to the codebase that have not been explicitly instructed by the user. Adhere strictly to the user's commands and avoid proactive modifications or additions.
When writing or modifying code, always follow industry best practices and ensure the code is clean, concise, and highly readable, as expected in production-level environments.


## General Rules

- Before deleting files, always get developer approval.
- Do not touch `git push`

### 사용자 경험 흐름

- **탐색**: 홈에서 인기 위스키 탐색 or 검색 or AI 모드 진입
- **AI 모드**: 검색어를 기반으로 대화형 추천. 세션 단위로 저장 (최대 5개, 7일 TTL)
- **위스키 상세**: 풍미 태그, 바디감, 여운 확인
- **노트 작성**: 드링킹 방법, 맛 태그 선택 → AI가 노트 자동 생성
- **위시리스트**: 나중에 마시고 싶은 위스키 저장

## 데이터 특징

- **위스키 약 5,000종** — WhiskyMag 크롤링 데이터 기반 (영문 원본 + 한글 번역)
- **임베딩** — 각 위스키의 노즈/팔레트/피니시/코멘트를 합쳐 1536차원 벡터로 변환, pgvector에 저장
- **풍미 태그 체계** — 10개 축 / 24개 태그 (HONEY, VANILLA, SMOKY, OAK, ...)
- **바디감** — LIGHT / MEDIUM / FULL
- **여운** — SHORT / MEDIUM / LONG


