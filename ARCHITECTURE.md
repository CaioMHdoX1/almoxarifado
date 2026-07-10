# Arquitetura do Almoxaf

Este documento descreve a hierarquia de pastas definida para o projeto e o
papel de cada uma. Nenhuma lógica foi implementada ainda (isso acontece nas
próximas etapas) — este é o "esqueleto" que vai comportar tudo.

---

## `api/` — Backend (Java 17, Jakarta EE, sem framework, Maven)

Arquitetura em camadas clássica: **Controller → Service → Repository → Model**,
com DTO/Mapper para nunca expor entidades de banco diretamente na API.

```
api/
└── src/
    ├── main/
    │   ├── java/com/almoxaf/api/
    │   │   ├── config/        Configuração de infraestrutura: DataSource/pool
    │   │   │                  de conexão com o Postgres, leitura de variáveis
    │   │   │                  de ambiente, inicialização (ServletContextListener).
    │   │   │
    │   │   ├── controller/    Servlets Jakarta EE (@WebServlet). Cada rota REST
    │   │   │                  vira uma classe aqui (ex: UsuarioServlet,
    │   │   │                  EquipamentoServlet, AuthServlet). Responsáveis
    │   │   │                  só por: ler request, chamar o Service, escrever
    │   │   │                  a resposta { data: ... }. Sem regra de negócio.
    │   │   │
    │   │   ├── service/       Regras de negócio (ex: UsuarioService,
    │   │   │                  EquipamentoService, AlocacaoService, AuthService).
    │   │   │                  Ex.: "um equipamento não pode ter 2 alocações
    │   │   │                  ativas", "cpf deve ser único", "gerar contagem
    │   │   │                  disponível/alocado ao buscar por nome".
    │   │   │
    │   │   ├── repository/    Acesso a dados via JDBC puro (DAO). Uma classe
    │   │   │                  por agregado (UsuarioRepository,
    │   │   │                  EquipamentoRepository, AlocacaoRepository).
    │   │   │                  Só SQL + mapeamento ResultSet → Model. Sem
    │   │   │                  regra de negócio.
    │   │   │
    │   │   ├── model/         Classes de domínio (POJOs) que espelham as
    │   │   │                  tabelas: Usuario, Equipamento, Alocacao.
    │   │   │                  Usadas internamente entre repository e service.
    │   │   │
    │   │   ├── dto/           Objetos de entrada/saída da API (o que trafega
    │   │   │                  em JSON): UsuarioRequestDTO, UsuarioResponseDTO,
    │   │   │                  EquipamentoResponseDTO,
    │   │   │                  EquipamentoBuscaResponseDTO (total/disponível/
    │   │   │                  alocado + lista), LoginRequestDTO, etc.
    │   │   │
    │   │   ├── mapper/        Conversão Model <-> DTO (ex: UsuarioMapper,
    │   │   │                  EquipamentoMapper). Mantém o Service limpo.
    │   │   │
    │   │   ├── filter/        Jakarta Filters: AuthFilter (bloqueia rotas sem
    │   │   │                  sessão válida), CorsFilter (libera o client em
    │   │   │                  dev), talvez LoggingFilter.
    │   │   │
    │   │   ├── exception/     Exceções customizadas (ex:
    │   │   │                  RecursoNaoEncontradoException,
    │   │   │                  RegraDeNegocioException,
    │   │   │                  CpfDuplicadoException) + um handler central que
    │   │   │                  as traduz em respostas HTTP com status/JSON
    │   │   │                  padronizados.
    │   │   │
    │   │   └── util/          Utilidades transversais: JsonResponseWriter
    │   │                      (padroniza { data: ... } / { error: ... }),
    │   │                      PasswordHasher (hash de senha), validações
    │   │                      genéricas.
    │   │
    │   ├── resources/         application.properties / db.properties
    │   │                      (config de conexão, lidas via classpath).
    │   │
    │   └── webapp/
    │       └── WEB-INF/       web.xml (se necessário além de anotações) e
    │                          descritores do WAR.
    │
    └── test/
        └── java/com/almoxaf/api/
            ├── service/        Testes unitários de regra de negócio.
            └── repository/     Testes de integração de queries (contra um
                                 Postgres de teste/testcontainers).
```

**Fluxo de uma requisição**, ex. "buscar equipamento por nome":
`EquipamentoServlet` (controller) → `EquipamentoService` (aplica regra:
agrupar por nome, contar disponível/alocado) → `EquipamentoRepository`
(consulta `vw_equipamentos_status` no Postgres) → `EquipamentoMapper`
(monta `EquipamentoBuscaResponseDTO`) → `JsonResponseWriter` devolve
`{ data: { total, disponiveis, alocados, itens: [...] } }`.

---

## `client/` — Frontend (React 19, Vite via `vp`, TanStack Router/Query)

Organização **híbrida**: `routes/` cuida só de roteamento (TanStack Router
file-based), e a lógica de cada domínio fica isolada em `features/`.

```
client/
├── public/                  Assets estáticos (favicon, imagens fixas).
│
└── src/
    ├── routes/               Definição de rotas (TanStack Router). Cada
    │   ├── __root.tsx         arquivo aqui vira uma URL. Componentes ficam
    │   ├── index.tsx          finos: só montam a página usando os
    │   ├── login.tsx          componentes de features/ e components/.
    │   ├── usuarios/
    │   │   ├── index.tsx      Rota "consultar usuário"
    │   │   └── novo.tsx       Rota "adicionar usuário"
    │   └── equipamentos/
    │       ├── index.tsx      Rota "consultar equipamento"
    │       ├── novo.tsx       Rota "adicionar equipamento"
    │       └── editar.$id.tsx Rota "editar equipamento" (parâmetro dinâmico)
    │   (routeTree.gen.ts é gerado automaticamente pelo TanStack Router,
    │    não é escrito manualmente — por isso está no .gitignore)
    │
    ├── features/             Um subpasta por domínio de negócio. Cada uma
    │   │                     concentra tudo que é específico daquele
    │   │                     domínio: chamadas de API, schemas de validação
    │   │                     e componentes que só fazem sentido ali.
    │   ├── usuarios/
    │   │   ├── api.ts         Hooks TanStack Query (useUsuarios,
    │   │   │                  useUsuarioPorNome, useCriarUsuario)
    │   │   ├── types.ts       Schemas Zod (validação) + tipos TS inferidos
    │   │   └── components/    UsuarioForm, UsuarioTabela,
    │   │                      EquipamentosDoUsuarioLista
    │   ├── equipamentos/
    │   │   ├── api.ts         useEquipamentos, useBuscaPorNome (retorna
    │   │   │                  total/disponível/alocado + lista),
    │   │   │                  useCriarEquipamento, useRemoverEquipamento
    │   │   ├── types.ts
    │   │   └── components/    EquipamentoForm, EquipamentoTabela,
    │   │                      ResumoDisponibilidade (cards com os totais)
    │   └── auth/
    │       ├── api.ts         useLogin, useLogout, useSessaoAtual
    │       └── types.ts       + AuthContext/useAuth (guarda de rotas)
    │
    ├── components/            Componentes genéricos, reutilizáveis entre
    │   ├── ui/                 features (não sabem nada de "usuário" ou
    │   │                       "equipamento").
    │   │                       ui/: wrappers em cima do Base UI (Button,
    │   │                       Input, Dialog, Table, Select...) — troca do
    │   │                       shadcn/Radix do export original do Figma.
    │   ├── layout/             Header, Sidebar, PageShell (vindos do
    │   │                       App.tsx original, "promovidos" a componentes
    │   │                       reutilizáveis).
    │   ├── forms/              Campos de formulário genéricos (FieldWrapper,
    │   │                       FormError) usados pelos forms de cada feature.
    │   └── feedback/           Wrappers de Sonner (toasts de sucesso/erro),
    │                           Skeletons/Spinners de loading.
    │
    ├── lib/                   Infraestrutura transversal do client.
    │   ├── api-client.ts       fetch wrapper: base URL da API, credentials
    │   │                       "include" (cookie de sessão), parse de
    │   │                       { data: ... } / { error: ... }.
    │   ├── query-client.ts     Instância única do QueryClient (TanStack
    │   │                       Query) + configs default (retry, staleTime).
    │   └── utils.ts             Helpers genéricos (formatCpf, formatData...).
    │
    ├── styles/                 tailwind.css, theme.css (variáveis oklch),
    │                           fonts.css — já existentes do export do Figma.
    │
    ├── test/                   Testes com Vitest (unitários de componentes/
    │                           hooks). Specs também podem ficar colocados
    │                           junto do arquivo testado (*.test.tsx).
    │
    └── main.tsx                Bootstrap: cria o root React, injeta
                                 QueryClientProvider + RouterProvider.
```

**Fluxo de uma tela**, ex. "consultar equipamento por nome":
rota `equipamentos/index.tsx` → usa `features/equipamentos/components/EquipamentoTabela`
+ `ResumoDisponibilidade` → que consomem `useBuscaPorNome` (de
`features/equipamentos/api.ts`, TanStack Query) → que chama `lib/api-client.ts`
→ que bate no endpoint do `EquipamentoServlet` na API.

---

## Por que essa separação

- **`routes/` vs `features/`**: rotas ficam "burras" (só composição), então
  trocar de roteador ou reorganizar URLs no futuro não obriga a mexer na
  lógica de negócio do frontend.
- **Camadas do backend espelham o fluxo de uma request**: cada camada só
  conhece a camada imediatamente abaixo (`controller` não conhece `repository`
  diretamente, por exemplo), o que facilita testar `service/` isoladamente
  com mocks de `repository/`.
- **DTO separado de Model**: a API nunca vaza detalhes de schema do banco
  (nomes de coluna, ids internos que não deveriam ser expostos, etc.) para o
  cliente.

## Próximas etapas que preenchem essa estrutura

- Etapa 2 → arquivos de `api/config`, `api/util`, `pom.xml`
- Etapa 3 → `api/filter`, `AuthServlet`, `AuthService`
- Etapa 4 → classes de `usuarios` em cada camada
- Etapa 5 → classes de `equipamentos`/`alocacoes` em cada camada
- Etapa 6 → configuração base do `client` (vp, Tailwind, Base UI, Router, Query)
- Etapa 7/8/9 → conteúdo de `features/usuarios`, `features/equipamentos`, `features/auth`
