-- ============================================================================
-- Almoxaf — Schema do banco de dados (PostgreSQL 15)
-- ============================================================================
-- Convenções:
--   - Todas as PKs são BIGSERIAL (id numérico auto-incremento)
--   - Timestamps em UTC (TIMESTAMPTZ)
--   - Status de alocação de um equipamento é DERIVADO: existe uma linha em
--     "alocacoes" com data_fim IS NULL para aquele equipamento => "alocado".
--     Não guardamos um campo "status" redundante para evitar inconsistência.
-- ============================================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto"; -- para gen_random_uuid(), caso precise no futuro

-- ----------------------------------------------------------------------------
-- Tabela: usuarios
-- ----------------------------------------------------------------------------
CREATE TABLE usuarios (
    id              BIGSERIAL PRIMARY KEY,
    nome            VARCHAR(150) NOT NULL,
    cpf             VARCHAR(11)  NOT NULL UNIQUE, -- armazenado só com dígitos (sem pontuação)
    projeto         VARCHAR(150),
    email           VARCHAR(150),
    senha_hash      VARCHAR(255),                  -- usado se/quando autenticação for ativada
    criado_em       TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em   TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT chk_cpf_formato CHECK (cpf ~ '^[0-9]{11}$')
);

CREATE INDEX idx_usuarios_nome ON usuarios (LOWER(nome));
CREATE INDEX idx_usuarios_projeto ON usuarios (projeto);

-- ----------------------------------------------------------------------------
-- Tabela: equipamentos
-- ----------------------------------------------------------------------------
-- categoria fica como texto livre por enquanto (placeholder). Quando o
-- sistema de categorização for definido, trocamos por categoria_id FK.
-- ----------------------------------------------------------------------------
CREATE TABLE equipamentos (
    id              BIGSERIAL PRIMARY KEY,
    nome            VARCHAR(150) NOT NULL,
    codigo          VARCHAR(50)  NOT NULL UNIQUE,  -- patrimônio / código único do item físico
    marca           VARCHAR(100),
    categoria       VARCHAR(100),                  -- placeholder simples (texto livre)
    criado_em       TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_equipamentos_nome ON equipamentos (LOWER(nome));
CREATE INDEX idx_equipamentos_codigo ON equipamentos (codigo);

-- ----------------------------------------------------------------------------
-- Tabela: alocacoes
-- ----------------------------------------------------------------------------
-- Histórico de posse de cada equipamento. Uma alocação "ativa" é aquela com
-- data_fim IS NULL. Regra de negócio (garantida na camada de serviço, e
-- reforçada aqui por índice único parcial): um equipamento não pode ter mais
-- de uma alocação ativa ao mesmo tempo.
-- ----------------------------------------------------------------------------
CREATE TABLE alocacoes (
    id              BIGSERIAL PRIMARY KEY,
    equipamento_id  BIGINT NOT NULL REFERENCES equipamentos(id) ON DELETE CASCADE,
    usuario_id      BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE RESTRICT,
    data_inicio     TIMESTAMPTZ NOT NULL DEFAULT now(),
    data_fim        TIMESTAMPTZ,                   -- NULL = ainda está com o usuário
    observacao      TEXT,

    CONSTRAINT chk_data_fim_apos_inicio CHECK (data_fim IS NULL OR data_fim >= data_inicio)
);

-- Garante no banco que um equipamento não tenha 2 alocações ativas simultâneas
CREATE UNIQUE INDEX idx_alocacao_ativa_unica
    ON alocacoes (equipamento_id)
    WHERE data_fim IS NULL;

CREATE INDEX idx_alocacoes_usuario_ativa
    ON alocacoes (usuario_id)
    WHERE data_fim IS NULL;

CREATE INDEX idx_alocacoes_equipamento ON alocacoes (equipamento_id);

-- ----------------------------------------------------------------------------
-- View auxiliar: equipamento com status derivado + usuário atual
-- ----------------------------------------------------------------------------
-- Facilita muito as duas buscas principais do sistema:
--   1) "quais equipamentos estão com o usuário X"
--   2) "quantos equipamentos com nome Y existem / disponíveis / alocados"
-- ----------------------------------------------------------------------------
CREATE VIEW vw_equipamentos_status AS
SELECT
    e.id,
    e.nome,
    e.codigo,
    e.marca,
    e.categoria,
    a.usuario_id                                   AS usuario_atual_id,
    u.nome                                          AS usuario_atual_nome,
    a.data_inicio                                   AS alocado_desde,
    CASE WHEN a.id IS NULL THEN 'disponivel' ELSE 'alocado' END AS status
FROM equipamentos e
LEFT JOIN alocacoes a
    ON a.equipamento_id = e.id AND a.data_fim IS NULL
LEFT JOIN usuarios u
    ON u.id = a.usuario_id;

-- ----------------------------------------------------------------------------
-- Trigger genérico para manter atualizado_em em dia
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION trg_set_atualizado_em()
RETURNS TRIGGER AS $$
BEGIN
    NEW.atualizado_em := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_atualizado_em_usuarios
    BEFORE UPDATE ON usuarios
    FOR EACH ROW EXECUTE FUNCTION trg_set_atualizado_em();

CREATE TRIGGER set_atualizado_em_equipamentos
    BEFORE UPDATE ON equipamentos
    FOR EACH ROW EXECUTE FUNCTION trg_set_atualizado_em();
