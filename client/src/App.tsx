import { useState } from "react";
import { Inicio } from "@/components/layout/Inicio";
import { PageShell } from "@/components/layout/PageShell";
import type { PaginaId } from "@/components/layout/Sidebar";
import { EquipamentoAdicionar } from "@/features/equipamentos/components/EquipamentoAdicionar";
import { EquipamentoBusca } from "@/features/equipamentos/components/EquipamentoBusca";
import { EquipamentoEditar } from "@/features/equipamentos/components/EquipamentoEditar";
import { EquipamentoRemover } from "@/features/equipamentos/components/EquipamentoRemover";
import { UsuarioBusca } from "@/features/usuarios/components/UsuarioBusca";
import { UsuarioForm } from "@/features/usuarios/components/UsuarioForm";

const TITULOS: Record<PaginaId, { titulo: string; descricao?: string }> = {
  inicio: { titulo: "Início" },
  "usuarios-adicionar": { titulo: "Adicionar usuário", descricao: "Nome, CPF e projeto associado" },
  "usuarios-consultar": {
    titulo: "Consultar usuário",
    descricao: "Veja todos os equipamentos alocados a um usuário",
  },
  "equipamentos-adicionar": {
    titulo: "Adicionar equipamento",
    descricao: "Nome, código e marca",
  },
  "equipamentos-consultar": {
    titulo: "Consultar equipamento",
    descricao: "Totais de disponíveis/alocados por nome",
  },
  "equipamentos-editar": { titulo: "Editar equipamento" },
  "equipamentos-remover": { titulo: "Remover equipamento" },
};

export default function App() {
  const [pagina, setPagina] = useState<PaginaId>("inicio");
  const { titulo, descricao } = TITULOS[pagina];

  return (
    <PageShell paginaAtual={pagina} aoNavegar={setPagina} titulo={titulo} descricao={descricao}>
      {pagina === "inicio" && <Inicio aoNavegar={setPagina} />}
      {pagina === "usuarios-adicionar" && <UsuarioForm />}
      {pagina === "usuarios-consultar" && <UsuarioBusca />}
      {pagina === "equipamentos-adicionar" && <EquipamentoAdicionar />}
      {pagina === "equipamentos-consultar" && <EquipamentoBusca />}
      {pagina === "equipamentos-editar" && <EquipamentoEditar />}
      {pagina === "equipamentos-remover" && <EquipamentoRemover />}
    </PageShell>
  );
}
