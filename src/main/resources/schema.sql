-- pgvector 확장 활성화
CREATE EXTENSION IF NOT EXISTS vector;

-- Hibernate가 vector 타입을 모르므로, embedding 컬럼은 수동 추가
ALTER TABLE whiskies ADD COLUMN IF NOT EXISTS embedding vector(1536);
ALTER TABLE tasting_notes ADD COLUMN IF NOT EXISTS embedding vector(1536);