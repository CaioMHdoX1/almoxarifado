# Deploy em produção

Este guia parte do zero: um servidor limpo até o sistema rodando com HTTPS
de verdade num domínio.

## O que muda em relação ao desenvolvimento

| | Desenvolvimento | Produção |
|---|---|---|
| Acesso | `http://localhost:3000` / `:5173` | `https://seu-dominio.com.br` |
| HTTPS | Não tem | Automático, via Caddy + Let's Encrypt |
| Portas expostas | 3000, 8080, 5432, 5050 | Só 80/443 (Caddy) |
| Cookie de sessão | `Secure` desativado | `Secure` ativado |
| CORS entre front/API | Necessário (portas diferentes) | Não existe (mesmo domínio) |
| Senha do admin | `123456` (seed) | Trocada antes de ir ao ar |

---

## 1. Provisionar um servidor

Qualquer VPS serve (DigitalOcean, Hetzner, AWS Lightsail, etc.) — o mínimo
recomendado é **2 vCPUs / 4 GB RAM**, Ubuntu 22.04 ou 24.04.

Instale o Docker e o Docker Compose:
```bash
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER
# saia e entre de novo na sessão SSH pra aplicar o grupo
```

## 2. Apontar o domínio

No painel do seu provedor de DNS, crie um registro **A** apontando seu
domínio (ou subdomínio, ex.: `almoxaf.suaempresa.com`) para o **IP público
do servidor**. Espere propagar (minutos a poucas horas) antes de continuar
— o Caddy só consegue emitir o certificado HTTPS depois que o DNS já
resolve pro servidor.

## 3. Levar o projeto pro servidor

Duas opções:

**Opção A — clonar o repositório Git** (se você colocou o projeto num
repositório):
```bash
git clone <url-do-seu-repositorio> almoxaf-project
cd almoxaf-project
```

**Opção B — copiar via `scp`** (se ainda não tem repositório):
```bash
# na sua máquina, dentro da pasta do projeto:
scp -r . usuario@ip-do-servidor:~/almoxaf-project
```

## 4. Configurar as variáveis de produção

```bash
cp .env.production.example .env.production
nano .env.production   # ou vim, o editor que preferir
```

Preencha:
- `DOMINIO`: o domínio de verdade que você apontou no passo 2 (sem `https://`)
- `POSTGRES_PASSWORD`: uma senha forte de verdade. Pra gerar uma boa:
  ```bash
  openssl rand -base64 24
  ```

## 5. Trocar a senha do administrador

**Nunca vá pra produção com a senha `123456` do seed.** Gere um hash novo
localmente (na sua máquina, não precisa ser no servidor) — o script usa só
o Node.js, que você já tem instalado pra rodar o frontend:

```bash
node scripts/gerar-hash-senha.js "sua-senha-bem-forte-aqui"
```

Isso imprime um comando SQL pronto. Depois que o banco já estiver rodando
no servidor (próximo passo), conecte nele e rode esse UPDATE:

```bash
docker compose --env-file .env.production -f docker-compose.prod.yml exec db \
  psql -U almoxaf -d almoxaf -c "UPDATE administradores SET senha_hash = '...' WHERE email = 'admin@almoxaf.local';"
```

(cole o hash gerado no lugar de `...`)

Se quiser trocar também o **email** do admin (em vez de `admin@almoxaf.local`),
edite direto `db/init/002_seed.sql` antes de subir pela primeira vez, ou
rode um `UPDATE administradores SET email = '...'` depois.

## 6. Subir tudo

```bash
docker compose --env-file .env.production -f docker-compose.prod.yml up -d --build
```

A primeira subida demora um pouco (build das imagens + Caddy emitindo o
certificado). Acompanhe os logs do Caddy pra confirmar que o certificado
saiu certo:
```bash
docker compose -f docker-compose.prod.yml logs -f caddy
```
Procure por uma linha mencionando `certificate obtained successfully`.

## 7. Testar

Acesse `https://seu-dominio.com.br` no navegador — deve aparecer a tela de
login, com o cadeado de HTTPS válido. Entre com o admin (e a senha nova que
você configurou no passo 5).

## 8. Firewall

Libere só o necessário:
```bash
sudo ufw allow 22/tcp   # SSH
sudo ufw allow 80/tcp   # Caddy (HTTP, redireciona pra HTTPS)
sudo ufw allow 443/tcp  # Caddy (HTTPS)
sudo ufw enable
```
Note que `db`, `api` e `client` **não têm porta publicada** no
`docker-compose.prod.yml` — mesmo sem firewall nenhum, eles já não são
acessíveis de fora, só o Caddy é.

## 9. Backup do banco

O volume `almoxaf_db_data` é onde tudo mora. Backup simples (rode via
`cron` periodicamente):
```bash
docker compose -f docker-compose.prod.yml exec -T db \
  pg_dump -U almoxaf almoxaf > backup-$(date +%Y%m%d).sql
```
Guarde esses arquivos em outro lugar (outro servidor, S3, etc.) — um backup
que só existe na mesma máquina não protege contra a máquina falhar.

## 10. Atualizando o sistema depois

Sempre que houver mudança de código:
```bash
git pull   # ou copie os arquivos novos via scp de novo
docker compose --env-file .env.production -f docker-compose.prod.yml up -d --build
```

Se a mudança incluir alteração no `db/init/*.sql`: esses scripts só rodam
na **primeira vez** que o volume do Postgres é criado — mudanças em schema
depois disso exigem uma migração manual (`ALTER TABLE ...` via `psql`), já
que o projeto não tem uma ferramenta de migração (Flyway/Liquibase) — isso
é uma limitação conhecida, ver checklist de produção no README.

---

## Checklist final antes de anunciar que está no ar

- [ ] Testou login com a senha nova (não a `123456`)
- [ ] `https://` no navegador mostra cadeado válido, sem aviso de certificado
- [ ] `POSTGRES_PASSWORD` do `.env.production` é uma senha forte gerada de verdade
- [ ] Firewall configurado (passo 8)
- [ ] Backup do banco configurado e testado pelo menos uma vez (restaurar o
      backup num banco de teste, pra confirmar que o arquivo gerado
      realmente funciona)
- [ ] `.env.production` não foi commitado no git (confira `git status`)
