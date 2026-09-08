-- ============================================================================
-- Almoxaf — Dados de exemplo (seed) para desenvolvimento/testes
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Administrador (único usuário que consegue logar)
-- ----------------------------------------------------------------------------
-- senha: "123456" (só para desenvolvimento/teste!). Gerado com
-- PBKDF2WithHmacSHA256, 210.000 iterações — mesmo algoritmo do
-- util/PasswordHasher.java da API. Formato: {iterações}:{salt}:{hash}
-- ----------------------------------------------------------------------------
INSERT INTO administradores (nome, email, senha_hash) VALUES
    ('Administrador', 'admin@almoxaf.local',
        '210000:IIG7FDMbq3bZY7k26awr7g==:tbWMz7/KlqOIxf+5fG3XrETWZv2jSIVc1xER73KnzuM=');

-- ----------------------------------------------------------------------------
-- Usuários (pessoas que podem estar de posse de um equipamento — não logam)
-- ----------------------------------------------------------------------------
INSERT INTO usuarios (nome, cpf, projeto) VALUES
    ('Ana Beatriz Costa',   '11122233344', 'GREat'),
    ('Carlos Mendes',       '22233344455', 'GREat'),
    ('Marina Ferreira',     '33344455566', 'Projeto Atlas'),
    ('João Silva',          '44455566677', 'Projeto Atlas'),
    ('Beatriz Nunes',       '55566677788', 'GREat');

-- ----------------------------------------------------------------------------
-- Equipamentos (tipo 'equipamento' — itens únicos, controlados por código)
-- ----------------------------------------------------------------------------
INSERT INTO equipamentos (nome, codigo, marca, categoria, descricao, tipo) VALUES
    ('Notebook Dell Latitude 5520', 'PAT-00812', 'Dell', 'Notebook',
        'i7 11ª geração, 16GB RAM, SSD 512GB', 'equipamento'),
    ('Notebook Dell Latitude 5520', 'PAT-00813', 'Dell', 'Notebook',
        'i7 11ª geração, 16GB RAM, SSD 512GB', 'equipamento'),
    ('Notebook Dell Latitude 5520', 'PAT-00814', 'Dell', 'Notebook',
        'i7 11ª geração, 16GB RAM, SSD 512GB', 'equipamento'),
    ('Switch Cisco SG350-10',       'PAT-00734', 'Cisco', 'Rede',
        '10 portas gigabit gerenciável', 'equipamento'),
    ('Monitor LG 27" IPS',          'PAT-00691', 'LG', 'Monitor',
        'Full HD, entrada HDMI e DisplayPort', 'equipamento'),
    ('Monitor LG 27" IPS',          'PAT-00692', 'LG', 'Monitor',
        'Full HD, entrada HDMI e DisplayPort', 'equipamento'),
    ('Teclado Logitech K120',       'PAT-00501', 'Logitech', 'Periférico',
        'ABNT2, com fio USB', 'equipamento');

-- ----------------------------------------------------------------------------
-- Itens de almoxarifado (tipo 'almoxarifado' — controlados por quantidade)
-- ----------------------------------------------------------------------------
INSERT INTO equipamentos (nome, codigo, marca, categoria, descricao, tipo, quantidade) VALUES
    ('Cabo de rede Cat6 (2m)',   'ALM-00001', 'Furukawa', 'Cabo',
        'Cabo de rede Cat6 azul, conectorizado', 'almoxarifado', 42),
    ('Parafuso M3 para rack',    'ALM-00002', 'Genérico', 'Fixação',
        'Pacote com 100 unidades cada', 'almoxarifado', 8),
    ('Mouse óptico USB',         'ALM-00003', 'Logitech', 'Periférico',
        'Mouse básico com fio', 'almoxarifado', 15);

-- ----------------------------------------------------------------------------
-- Alocações (só para itens tipo 'equipamento')
-- (algumas ativas — data_fim NULL — e uma já finalizada, para ter histórico)
-- ----------------------------------------------------------------------------

-- PAT-00812 -> Ana Beatriz Costa (ativa)
INSERT INTO alocacoes (equipamento_id, usuario_id, data_inicio, data_fim)
SELECT e.id, u.id, now() - interval '30 days', NULL
FROM equipamentos e, usuarios u
WHERE e.codigo = 'PAT-00812' AND u.cpf = '11122233344';

-- PAT-00734 -> Carlos Mendes (ativa)
INSERT INTO alocacoes (equipamento_id, usuario_id, data_inicio, data_fim)
SELECT e.id, u.id, now() - interval '15 days', NULL
FROM equipamentos e, usuarios u
WHERE e.codigo = 'PAT-00734' AND u.cpf = '22233344455';

-- PAT-00691 -> Marina Ferreira (ativa)
INSERT INTO alocacoes (equipamento_id, usuario_id, data_inicio, data_fim)
SELECT e.id, u.id, now() - interval '7 days', NULL
FROM equipamentos e, usuarios u
WHERE e.codigo = 'PAT-00691' AND u.cpf = '33344455566';

-- PAT-00813 -> João Silva (ativa)
INSERT INTO alocacoes (equipamento_id, usuario_id, data_inicio, data_fim)
SELECT e.id, u.id, now() - interval '3 days', NULL
FROM equipamentos e, usuarios u
WHERE e.codigo = 'PAT-00813' AND u.cpf = '44455566677';

-- PAT-00501 -> devolvido (histórico, já finalizado) -> hoje está disponível
INSERT INTO alocacoes (equipamento_id, usuario_id, data_inicio, data_fim)
SELECT e.id, u.id, now() - interval '60 days', now() - interval '45 days'
FROM equipamentos e, usuarios u
WHERE e.codigo = 'PAT-00501' AND u.cpf = '55566677788';

-- PAT-00814 e PAT-00692 ficam disponíveis (sem nenhuma alocação)
