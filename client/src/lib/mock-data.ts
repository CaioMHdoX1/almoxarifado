export type Usuario = {
  id: number;
  nome: string;
  cpf: string; // só dígitos
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

const USUARIOS: Usuario[] = [
  { id: 1, nome: "Ana Beatriz Costa", cpf: "11122233344", projeto: "GREat" },
  { id: 2, nome: "Carlos Mendes", cpf: "22233344455", projeto: "GREat" },
  { id: 3, nome: "Marina Ferreira", cpf: "33344455566", projeto: "Projeto Atlas" },
  { id: 4, nome: "João Silva", cpf: "44455566677", projeto: "Projeto Atlas" },
  { id: 5, nome: "Beatriz Nunes", cpf: "55566677788", projeto: "GREat" },
];

const EQUIPAMENTOS: Equipamento[] = [
  {
    id: 1,
    nome: "Notebook Dell Latitude 5520",
    codigo: "PAT-00812",
    marca: "Dell",
    status: "alocado",
    usuarioAtual: { id: 1, nome: "Ana Beatriz Costa" },
  },
  {
    id: 2,
    nome: "Notebook Dell Latitude 5520",
    codigo: "PAT-00813",
    marca: "Dell",
    status: "alocado",
    usuarioAtual: { id: 4, nome: "João Silva" },
  },
  {
    id: 3,
    nome: "Notebook Dell Latitude 5520",
    codigo: "PAT-00814",
    marca: "Dell",
    status: "disponivel",
    usuarioAtual: null,
  },
  {
    id: 4,
    nome: "Switch Cisco SG350-10",
    codigo: "PAT-00734",
    marca: "Cisco",
    status: "alocado",
    usuarioAtual: { id: 2, nome: "Carlos Mendes" },
  },
  {
    id: 5,
    nome: 'Monitor LG 27" IPS',
    codigo: "PAT-00691",
    marca: "LG",
    status: "alocado",
    usuarioAtual: { id: 3, nome: "Marina Ferreira" },
  },
  {
    id: 6,
    nome: 'Monitor LG 27" IPS',
    codigo: "PAT-00692",
    marca: "LG",
    status: "disponivel",
    usuarioAtual: null,
  },
  {
    id: 7,
    nome: "Teclado Logitech K120",
    codigo: "PAT-00501",
    marca: "Logitech",
    status: "disponivel",
    usuarioAtual: null,
  },
];

function delay<T>(valor: T, ms = 400): Promise<T> {
  return new Promise((resolve) => setTimeout(() => resolve(valor), ms));
}

export async function listarUsuarios(): Promise<Usuario[]> {
  return delay([...USUARIOS]);
}

export async function buscarUsuarioPorNome(
  termo: string,
): Promise<Array<{ usuario: Usuario; equipamentos: Equipamento[] }>> {
  const termoNormalizado = termo.trim().toLowerCase();
  const encontrados = USUARIOS.filter((u) => u.nome.toLowerCase().includes(termoNormalizado));
  const resultado = encontrados.map((usuario) => ({
    usuario,
    equipamentos: EQUIPAMENTOS.filter((e) => e.usuarioAtual?.id === usuario.id),
  }));
  return delay(resultado);
}

export async function criarUsuario(dados: Omit<Usuario, "id">): Promise<Usuario> {
  const jaExiste = USUARIOS.some((u) => u.cpf === dados.cpf);
  if (jaExiste) {
    throw new Error("Já existe um usuário cadastrado com esse CPF.");
  }
  const novo: Usuario = { id: Math.max(...USUARIOS.map((u) => u.id)) + 1, ...dados };
  USUARIOS.push(novo);
  return delay(novo);
}

export type BuscaEquipamentoResultado = {
  nome: string;
  total: number;
  disponiveis: number;
  alocados: number;
  itens: Equipamento[];
};

export async function buscarEquipamentoPorNome(
  termo: string,
): Promise<BuscaEquipamentoResultado[]> {
  const termoNormalizado = termo.trim().toLowerCase();
  const nomesUnicos = [...new Set(EQUIPAMENTOS.map((e) => e.nome))].filter((nome) =>
    nome.toLowerCase().includes(termoNormalizado),
  );

  const resultado = nomesUnicos.map((nome) => {
    const itens = EQUIPAMENTOS.filter((e) => e.nome === nome);
    return {
      nome,
      total: itens.length,
      disponiveis: itens.filter((e) => e.status === "disponivel").length,
      alocados: itens.filter((e) => e.status === "alocado").length,
      itens,
    };
  });

  return delay(resultado);
}

export async function listarEquipamentos(): Promise<Equipamento[]> {
  return delay([...EQUIPAMENTOS]);
}

export async function criarEquipamento(
  dados: Pick<Equipamento, "nome" | "codigo" | "marca">,
): Promise<Equipamento> {
  const jaExiste = EQUIPAMENTOS.some((e) => e.codigo === dados.codigo);
  if (jaExiste) {
    throw new Error("Já existe um equipamento cadastrado com esse código.");
  }
  const novo: Equipamento = {
    id: Math.max(...EQUIPAMENTOS.map((e) => e.id)) + 1,
    status: "disponivel",
    usuarioAtual: null,
    ...dados,
  };
  EQUIPAMENTOS.push(novo);
  return delay(novo);
}

export async function editarEquipamento(
  id: number,
  dados: Partial<Pick<Equipamento, "nome" | "codigo" | "marca">>,
): Promise<Equipamento> {
  const equipamento = EQUIPAMENTOS.find((e) => e.id === id);
  if (!equipamento) throw new Error("Equipamento não encontrado.");
  Object.assign(equipamento, dados);
  return delay(equipamento);
}

export async function removerEquipamento(id: number): Promise<void> {
  const index = EQUIPAMENTOS.findIndex((e) => e.id === id);
  if (index === -1) throw new Error("Equipamento não encontrado.");
  EQUIPAMENTOS.splice(index, 1);
  return delay(undefined);
}
