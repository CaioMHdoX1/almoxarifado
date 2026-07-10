# Almoxaf

Sistema de controle de equipamentos e usuários (almoxarifado de TI).

## Estrutura do monorepo

```
almoxaf-project/
├── client/          # Frontend — React 19 + Vite (vp) + TanStack Router/Query
├── api/             # Backend — Java 17, Jakarta EE (Servlets), Maven, WAR/Tomcat
├── db/
│   └── init/        # Scripts SQL executados automaticamente na 1ª subida do container
├── .github/
│   └── workflows/   # CI/CD (GitHub Actions)
└── docker-compose.yml
```

## Status atual do projeto (progresso por etapas)

- [x] **Etapa 0** — Esqueleto do monorepo
- [x] **Etapa 1** — Banco de dados (schema + seed)
- [ ] Etapa 2 — Base do backend (Java/Jakarta)
- [ ] Etapa 3 — Autenticação por sessão
- [ ] Etapa 4 — Backend: módulo Usuários
- [ ] Etapa 5 — Backend: módulo Equipamentos
- [ ] Etapa 6 — Base do frontend
- [ ] Etapa 7 — Frontend: telas de Usuários
- [ ] Etapa 8 — Frontend: telas de Equipamentos
- [ ] Etapa 9 — Frontend: Login
- [ ] Etapa 10 — Integração final + CI/CD

## Como subir o banco de dados agora (Etapa 1)

Ainda não há `api` nem `client` rodando via Docker (isso chega nas próximas etapas),
mas o banco já funciona isoladamente:

```bash
cp .env.example .env
docker compose up -d db pgadmin
```

- **Postgres** disponível em `localhost:5432` (usuário/senha/banco: ver `.env`)
- **pgAdmin** disponível em [http://localhost:5050](http://localhost:5050)
  (login: `admin@almoxaf.local` / `admin`)

Os scripts em `db/init/` rodam automaticamente **apenas na primeira vez** que o
volume do Postgres é criado. Para resetar o banco do zero durante o desenvolvimento:

```bash
docker compose down -v
docker compose up -d db pgadmin
```

## Modelo de dados (resumo)

- **usuarios**: nome, cpf (único), projeto associado
- **equipamentos**: nome, código (único), marca, categoria (texto livre por ora)
- **alocacoes**: histórico de qual usuário está/esteve com qual equipamento
  (`data_fim IS NULL` = alocação ativa; um equipamento não pode ter 2 alocações
  ativas ao mesmo tempo — isso é garantido por índice único no banco)
- **vw_equipamentos_status**: view pronta que já cruza equipamento + usuário atual
  + status (`disponivel` / `alocado`) — pensada para alimentar diretamente as
  buscas por nome de usuário e por nome de equipamento descritas nas specs do projeto.
