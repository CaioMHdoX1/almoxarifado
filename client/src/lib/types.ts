export type Administrador = {
  id: number;
  nome: string;
  email: string;
};

export type Usuario = {
  id: number;
  nome: string;
  cpf: string;
  projeto: string;
};

export type StatusEquipamento = "disponivel" | "alocado";
export type TipoEquipamento = "equipamento" | "almoxarifado";

export type Equipamento = {
  id: number;
  nome: string;
  codigo: string;
  marca: string;
  descricao: string | null;
  tipo: TipoEquipamento;
  quantidade: number | null; // preenchido só quando tipo === "almoxarifado"
  status: StatusEquipamento;
  usuarioAtual: { id: number; nome: string } | null;
};

export type UsuarioComEquipamentos = {
  usuario: Usuario;
  equipamentos: Equipamento[];
};

export type BuscaEquipamentoResultado = {
  nome: string;
  total: number;
  disponiveis: number;
  alocados: number;
  itens: Equipamento[];
};

// --- Relatórios ------------------------------------------------------------

export type Movimentacao = {
  equipamentoNome: string;
  equipamentoCodigo: string;
  usuarioNome: string;
  usuarioCpf: string;
  data: string; // ISO 8601
};

export type PosseAtual = {
  equipamentoNome: string;
  equipamentoCodigo: string;
  usuarioNome: string;
};

export type RelatorioResumo = {
  id: number;
  periodoInicio: string;
  periodoFim: string;
  geradoEm: string;
  totalEntregas: number;
  totalDevolucoes: number;
};

export type RelatorioDetalhe = RelatorioResumo & {
  entregas: Movimentacao[];
  devolucoes: Movimentacao[];
  posseAtual: PosseAtual[];
};
