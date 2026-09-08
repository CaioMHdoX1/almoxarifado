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

## ⚠️ Mudanças importantes nesta leva (leia antes de rodar)

- **O banco mudou de estrutura** (administrador separado dos usuários, novos
  campos em equipamentos, tabela de relatórios). Se você já tinha rodado o
  projeto antes, **precisa resetar o volume do Postgres**:
  ```bash
  docker compose down -v
  docker compose up -d --build
  ```
- **Login agora é só com o administrador único**: `admin@almoxaf.local` /
  `123456`. Os emails antigos (`ana.costa@empresa.com` etc.) não fazem mais
  login — essas pessoas continuam existindo como "usuários" (quem recebe
  equipamento), só não têm mais conta.
- **Frontend de Relatórios** — pronto: menu lateral → Relatórios quinzenais.
  Lista os relatórios gerados, com um botão "Gerar agora", e ao clicar em
  um deles mostra entregas, devoluções e quem estava com o quê naquele
  período.
- Adicionado: mostrar/ocultar senha no login, modo noturno (botão no rodapé
  do menu lateral), campo "Descrição" no equipamento, e o campo "Tipo"
  (Equipamento / Almoxarifado) — item de almoxarifado pede quantidade em
  vez de ser alocado a alguém.

## Como testar os relatórios (backend, sem tela ainda)

```bash
# gerar um relatório agora mesmo (por padrão, últimos 15 dias)
curl -i -b cookies.txt -X POST http://localhost:8080/api/relatorios/gerar

# listar relatórios já gerados
curl -b cookies.txt http://localhost:8080/api/relatorios

# ver o detalhe de um relatório específico (troque {id})
curl -b cookies.txt http://localhost:8080/api/relatorios/{id}
```

## Status atual do projeto (progresso por etapas)

- [x] **Etapa 0** — Esqueleto do monorepo
- [x] **Etapa 1** — Banco de dados (schema + seed)
- [x] **Etapa 2** — Base do backend (Java/Jakarta)
- [x] **Etapa 3** — Autenticação por sessão
- [x] **Etapa 4** — Backend: módulo Usuários
- [x] **Etapa 5** — Backend: módulo Equipamentos
- [x] **Etapa 6** — Base do frontend (React + Base UI + TanStack Query; navegação por
      estado em vez de TanStack Router, decisão combinada — ver `ARCHITECTURE.md`)
- [x] **Etapa 7** — Frontend: telas de Usuários (conectadas na API real)
- [x] **Etapa 8** — Frontend: telas de Equipamentos (conectadas na API real)
- [x] **Etapa 9** — Frontend: Login (sessão real, guarda de rota, logout)
- [x] **Etapa 10** — Integração final (Docker Compose com os 4 serviços) + CI/CD

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

## Como testar a base do backend agora (Etapa 2)

```bash
cp .env.example .env
docker compose up -d --build db api
```

- **API** disponível em `http://localhost:8080`
- Teste rápido: `curl http://localhost:8080/api/health` deve retornar
  `{ "data": { "status": "ok", "database": "up" } }`

Ainda não há regra de negócio nenhuma implementada (usuários/equipamentos) —
isso é o alicerce: pool de conexão com o banco, resposta JSON padronizada
(`{ data: ... }` / `{ error: ... }`), CORS liberado para o futuro frontend, e
hierarquia de exceções pronta para as próximas etapas usarem.

## Segurança

Camadas de defesa implementadas no backend:

| Ameaça | Defesa |
|---|---|
| Força bruta no login | `RateLimiter` — 10 tentativas / 5 min por IP (`AuthServlet`) |
| CSRF | CORS com allowlist de origem + `CsrfOriginCheckFilter` (valida Origin/Referer em POST/PUT/DELETE) + cookie `SameSite=Strict` (`context.xml`) — três camadas independentes |
| Fixação de sessão | Sessão existente é invalidada e recriada do zero a cada login bem-sucedido |
| XSS refletido / clickjacking | `SecurityHeadersFilter` (`X-Content-Type-Options`, `X-Frame-Options`, `CSP`) |
| Vazamento de cookie de sessão | `HttpOnly` sempre ativo; `Secure` documentado no `web.xml` (ativar em produção com HTTPS) |
| SQL injection | Todas as queries usam `PreparedStatement` com parâmetros |
| Enumeração de usuários no login | Mensagem de erro genérica ("email ou senha inválidos"), nunca diz qual dos dois errou |
| Payload gigante (DoS) | `RequestSizeLimitFilter` — rejeita corpo > 1 MB antes de processar |
| Senha fraca (quando existir cadastro de senha pelo usuário) | `PasswordPolicy` já pronta, ver comentário na classe |
| Vazamento de stacktrace/erro interno | `ApiExceptionHandler` nunca expõe exceção crua ao cliente, só loga no servidor |

### Checklist de produção

- [x] HTTPS de verdade (Caddy + Let's Encrypt automático) — ver `DEPLOY.md`
- [x] Cookie de sessão com `Secure` ativado em produção (`SECURE_COOKIES=true` no build da API — automático via `docker-compose.prod.yml`)
- [x] Senha do administrador trocada antes de ir ao ar (`scripts/gerar-hash-senha.js`) — ver `DEPLOY.md`
- [x] Banco/API/frontend sem porta exposta à internet (só o Caddy é público) — ver `docker-compose.prod.yml`
- [ ] Criar um usuário de banco com permissões mínimas pra API (hoje ela usa o mesmo usuário `almoxaf` que tem acesso total ao schema — em produção, restrinja a `SELECT/INSERT/UPDATE/DELETE` nas tabelas específicas, sem `DROP`/`ALTER`)
- [ ] Se o `RateLimiter` precisar funcionar com múltiplas instâncias da API atrás de um load balancer, trocar a implementação em memória por uma compartilhada (Redis, por exemplo)
- [ ] Rodar `mvn dependency-check` ou similar periodicamente para checar CVEs nas dependências (Postgres driver, Jackson, HikariCP)
- [ ] Configurar backup automático do banco (ver passo 9 do `DEPLOY.md`)

**Guia completo passo a passo (servidor do zero até no ar): ver [`DEPLOY.md`](./DEPLOY.md).**

## Como testar o login agora (Etapa 3)

Todos os usuários do seed têm a senha `123456`:

```bash
docker compose up -d --build db api

# login — deve retornar { "data": { "usuario": {...} } } e um cookie de sessão
curl -i -c cookies.txt -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@almoxaf.local","senha":"123456"}'

# usando o cookie salvo, confirma quem está logado
curl -b cookies.txt http://localhost:8080/api/auth/me

# sem sessão, qualquer rota que não seja /api/health ou /api/auth/login
# deve responder 401
curl -i http://localhost:8080/api/equipamentos

# logout
curl -i -b cookies.txt -X POST http://localhost:8080/api/auth/logout
```

Ainda não existem rotas de usuários/equipamentos de verdade (`/api/equipamentos`
no exemplo acima só serve pra provar que o `AuthFilter` está bloqueando sem
sessão — vai dar 404 depois do 401 ser resolvido, porque a rota ainda não
existe). Isso é a Etapa 4/5.

## Como testar o módulo de Usuários agora (Etapa 4)

Precisa estar logado primeiro (ver seção de login acima — o cookie fica em
`cookies.txt`):

```bash
# criar um usuário novo
curl -i -b cookies.txt -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nome":"Pedro Alves","cpf":"99988877766","projeto":"GREat"}'

# buscar por nome — já vem com os equipamentos alocados a cada resultado
curl -b cookies.txt "http://localhost:8080/api/usuarios?nome=Ana"

# tentar cadastrar CPF repetido deve retornar 409
curl -i -b cookies.txt -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nome":"Outro Nome","cpf":"11122233344","projeto":"GREat"}'
```

## Como testar o módulo de Equipamentos agora (Etapa 5)

Também precisa estar logado (`cookies.txt`):

```bash
# criar equipamento
curl -i -b cookies.txt -X POST http://localhost:8080/api/equipamentos \
  -H "Content-Type: application/json" \
  -d '{"nome":"Monitor LG 27\" IPS","codigo":"PAT-00900","marca":"LG"}'

# buscar por nome — total / disponíveis / alocados + lista
curl -b cookies.txt "http://localhost:8080/api/equipamentos?nome=Monitor"

# listar todos (sem parâmetro nome) — usado pelas telas de editar/remover
curl -b cookies.txt http://localhost:8080/api/equipamentos

# editar (troque {id} pelo id retornado na criação)
curl -i -b cookies.txt -X PUT http://localhost:8080/api/equipamentos/{id} \
  -H "Content-Type: application/json" \
  -d '{"nome":"Monitor LG 27\" IPS","codigo":"PAT-00900","marca":"LG (revisado)"}'

# remover
curl -i -b cookies.txt -X DELETE http://localhost:8080/api/equipamentos/{id}

# buscar por código exato — mesma consulta que a leitura do QR code usa
curl -b cookies.txt http://localhost:8080/api/equipamentos/codigo/PAT-00900

# tentar remover um equipamento alocado deve dar 422
```

## QR code do equipamento

Ao cadastrar um equipamento (`POST /api/equipamentos`), a tela mostra na
hora um QR code contendo só o código/patrimônio do item (ex.: `PAT-00812`)
— o mesmo valor que já era usado na busca digitada. Dá pra tirar print,
ou imprimir e colar no equipamento físico.

Pra ler: menu lateral → **Equipamentos → Ler QR code**. Abre a câmera,
aponta pro QR, e assim que reconhece ele já busca e mostra as informações
do item — nome, marca, status e com quem está — exatamente como a busca
por código digitado (`GET /api/equipamentos/codigo/{codigo}`, endpoint
novo).

### ⚠️ Câmera exige conexão segura (HTTPS ou "localhost")

Isso não é um bug do app — é uma regra de segurança do próprio navegador:
**getUserMedia (acesso à câmera) só funciona em HTTPS, ou em `http://localhost`
exatamente.** Um endereço tipo `http://192.168.0.42:5173` (IP na rede local,
usado pra testar no celular) **não é considerado seguro**, e o navegador vai
bloquear a câmera mesmo com a permissão concedida.

O que funciona hoje sem configuração extra:
- **No navegador do computador**, acessando por `http://localhost:5173` — a
  câmera do notebook funciona normalmente (localhost é uma exceção da regra).
- **Em produção de verdade** (ver `DEPLOY.md`) — o Caddy já cuida do HTTPS
  automaticamente, então a câmera funciona no celular sem nenhum ajuste
  extra, inclusive fora da rede local.

O que **não** funciona sem configuração extra:
- Testar no celular acessando pelo IP da rede local em HTTP puro (o cenário
  descrito na seção "Usando pelo celular"). Isso é só uma limitação do
  ambiente de desenvolvimento local — não existe em produção. Se precisar
  mesmo assim testar a câmera nesse cenário específico antes de fazer o
  deploy, duas opções:
  1. Gerar um certificado local (ex.: [mkcert](https://github.com/FiloSottile/mkcert))
     e configurar o Vite pra servir com HTTPS — mais trabalho, mas fica
     igual à produção.
  2. Usar um túnel HTTPS temporário (ex.: `ngrok http 5173`) — mais rápido
     pra só testar uma vez, mas repare que a API (`VITE_API_URL`) também
     precisaria estar atrás de HTTPS nesse cenário, senão o navegador
     bloqueia a chamada por "conteúdo misto" (página HTTPS chamando API HTTP).

### Compatibilidade do navegador

A leitura usa a Barcode Detection API nativa do navegador (Chrome, Edge e
Safari recentes suportam; Firefox pode não suportar sem configuração
adicional). Se a câmera abrir mas nada for reconhecido, tente Chrome no
Android ou Safari no iPhone.

## Como rodar o projeto inteiro agora (Etapa 10 — final)

Tudo via Docker, os 4 serviços juntos:

```bash
cp .env.example .env
docker compose up -d --build
```

- **Frontend**: http://localhost:3000 (build de produção, servido por Nginx)
- **API**: http://localhost:8080
- **Postgres**: localhost:5432
- **pgAdmin**: http://localhost:5050

Login: `admin@almoxaf.local` / `123456` (administrador único do sistema — os "usuários" cadastrados no app são pessoas que recebem equipamento, não fazem login).

Pra derrubar tudo e resetar o banco do zero:
```bash
docker compose down -v
```

### Modo desenvolvimento (frontend com hot-reload)

Pra mexer no frontend com hot-reload em vez do build estático do Docker,
suba só `db` e `api` via Docker e rode o `client` com `npm run dev` (ver
seção específica mais abaixo).

### CI/CD

`.github/workflows/ci.yml` roda a cada push/PR na branch `main`:
- **client**: `biome check` (lint), `npm test` (Vitest), `npm run build`
- **api**: `mvn test`, `mvn package` (gera o `.war`)

## Usando pelo celular

O frontend agora é responsivo: em telas pequenas o menu lateral vira uma
gaveta (abre com o ícone ☰ no canto superior esquerdo), tabelas longas
ganham rolagem horizontal em vez de quebrar o layout, e os toques têm área
confortável.

Duas formas de acessar pelo celular:

### 1. Testando no próprio navegador do computador (simulado)
Chrome/Firefox DevTools → modo de dispositivo móvel (ícone de celular/tablet
no canto superior do DevTools, ou `Ctrl+Shift+M`). Não precisa de nenhuma
configuração extra — é só o CSS reagindo ao tamanho de tela.

### 2. Testando no celular de verdade, na mesma rede Wi-Fi
Isso já funciona hoje, mas por padrão a API só libera `localhost` — o
celular não é "localhost" da sua máquina, então precisa apontar tudo pro IP
do seu computador na rede local:

1. Descubra o IP local do seu computador:
   - Windows (PowerShell): `ipconfig` → procure "Endereço IPv4" (algo como `192.168.0.42`)
   - Mac/Linux: `ifconfig` ou `ip addr`

2. No `docker-compose.yml`, ajuste temporariamente (ou via `.env`) o
   `CLIENT_ORIGIN` da API para incluir o IP, ex.:
   ```
   CLIENT_ORIGIN: http://localhost:5173,http://localhost:3000,http://192.168.0.42:5173
   ```

3. Se estiver rodando o `client` com `npm run dev`, o Vite já escuta em
   todas as interfaces de rede (`host: true` no `vite.config.ts`) — no
   celular, acesse `http://192.168.0.42:5173`.

4. O `client/.env` precisa apontar `VITE_API_URL` pro mesmo IP (não
   `localhost`, porque isso rodaria no navegador do celular, que não tem
   nada rodando em "localhost" dele):
   ```
   VITE_API_URL=http://192.168.0.42:8080
   ```

5. Reinicie `docker compose up -d --build api` e `npm run dev` depois de
   qualquer mudança nessas variáveis.

Celular e computador precisam estar na mesma rede Wi-Fi para isso funcionar
(rede do escritório/casa — não funciona se o celular estiver na rede de
dados móvel, sem VPN até a sua máquina).

## Modo desenvolvimento — backend via Docker + frontend com hot-reload

```bash
# 1. banco + api
cp .env.example .env
docker compose up -d --build db api

# 2. frontend
cd client
cp .env.example .env
npm install
npm run dev
```

Acesse `http://localhost:5173` e entre com qualquer usuário do seed —
o administrador único do seed: `admin@almoxaf.local` / senha `123456`.

O frontend não usa mais dados mockados: todas as telas (Usuários,
Equipamentos, Login) chamam a API de verdade. Ainda não tem roteamento por
URL (combinamos manter a navegação por estado interno por enquanto — ver
`ARCHITECTURE.md`).

## Modelo de dados (resumo)

- **usuarios**: nome, cpf (único), projeto associado
- **equipamentos**: nome, código (único), marca, categoria (texto livre por ora)
- **alocacoes**: histórico de qual usuário está/esteve com qual equipamento
  (`data_fim IS NULL` = alocação ativa; um equipamento não pode ter 2 alocações
  ativas ao mesmo tempo — isso é garantido por índice único no banco)
- **vw_equipamentos_status**: view pronta que já cruza equipamento + usuário atual
  + status (`disponivel` / `alocado`) — pensada para alimentar diretamente as
  buscas por nome de usuário e por nome de equipamento descritas nas specs do projeto.
