export type Usuario = {
  id: number;
  nome: string;
  cpf: string;
  projeto: string;
};

export type StatusEquipamento = "disponivel" | "alocado";

export type Equipamento = {
  id: number;
  nome: string;
  codigo: string;
  marca: string;
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
