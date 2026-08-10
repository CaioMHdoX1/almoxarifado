-- ============================================================================
-- Almoxaf — Dados de exemplo (seed) para desenvolvimento/testes
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Usuários
-- ----------------------------------------------------------------------------
-- senha_hash abaixo corresponde à senha "123456" para todos (só para
-- desenvolvimento/teste!). Gerado com PBKDF2WithHmacSHA256, 210.000
-- iterações — mesmo algoritmo do util/PasswordHasher.java da API.
-- Formato: {iterações}:{salt base64}:{hash base64}
-- ----------------------------------------------------------------------------
INSERT INTO usuarios (nome, cpf, projeto, email, senha_hash) VALUES
    ('Ana Beatriz Costa',   '11122233344', 'GREat',        'ana.costa@empresa.com',
        '210000:IIG7FDMbq3bZY7k26awr7g==:tbWMz7/KlqOIxf+5fG3XrETWZv2jSIVc1xER73KnzuM='),
    ('Carlos Mendes',       '22233344455', 'GREat',        'carlos.mendes@empresa.com',
        '210000:IIG7FDMbq3bZY7k26awr7g==:tbWMz7/KlqOIxf+5fG3XrETWZv2jSIVc1xER73KnzuM='),
    ('Marina Ferreira',     '33344455566', 'Projeto Atlas', 'marina.ferreira@empresa.com',
        '210000:IIG7FDMbq3bZY7k26awr7g==:tbWMz7/KlqOIxf+5fG3XrETWZv2jSIVc1xER73KnzuM='),
    ('João Silva',          '44455566677', 'Projeto Atlas', 'joao.silva@empresa.com',
        '210000:IIG7FDMbq3bZY7k26awr7g==:tbWMz7/KlqOIxf+5fG3XrETWZv2jSIVc1xER73KnzuM='),
    ('Beatriz Nunes',       '55566677788', 'GREat',        'beatriz.nunes@empresa.com',
        '210000:IIG7FDMbq3bZY7k26awr7g==:tbWMz7/KlqOIxf+5fG3XrETWZv2jSIVc1xER73KnzuM=');

-- ----------------------------------------------------------------------------
-- Equipamentos
-- ----------------------------------------------------------------------------
INSERT INTO equipamentos (nome, codigo, marca, categoria) VALUES
    ('Notebook Dell Latitude 5520', 'PAT-00812', 'Dell',   'Notebook'),
    ('Notebook Dell Latitude 5520', 'PAT-00813', 'Dell',   'Notebook'),
    ('Notebook Dell Latitude 5520', 'PAT-00814', 'Dell',   'Notebook'),
    ('Switch Cisco SG350-10',       'PAT-00734', 'Cisco',  'Rede'),
    ('Monitor LG 27" IPS',          'PAT-00691', 'LG',     'Monitor'),
    ('Monitor LG 27" IPS',          'PAT-00692', 'LG',     'Monitor'),
    ('Teclado Logitech K120',       'PAT-00501', 'Logitech','Periférico');

-- ----------------------------------------------------------------------------
-- Alocações
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
