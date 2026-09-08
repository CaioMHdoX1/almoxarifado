import { useState } from "react";
import { toast } from "sonner";
import { Inicio } from "@/components/layout/Inicio";
import { PageShell } from "@/components/layout/PageShell";
import type { PaginaId } from "@/components/layout/Sidebar";
import { useLogout, useSessaoAtual } from "@/features/auth/api";
import { LoginForm } from "@/features/auth/components/LoginForm";
import { EquipamentoAdicionar } from "@/features/equipamentos/components/EquipamentoAdicionar";
import { EquipamentoBusca } from "@/features/equipamentos/components/EquipamentoBusca";
import { EquipamentoEditar } from "@/features/equipamentos/components/EquipamentoEditar";
import { EquipamentoRemover } from "@/features/equipamentos/components/EquipamentoRemover";
import { EquipamentoScanner } from "@/features/equipamentos/components/EquipamentoScanner";
import { Relatorios } from "@/features/relatorios/components/Relatorios";
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
  "equipamentos-ler-qr": {
    titulo: "Ler QR code",
    descricao: "Aponte a câmera para consultar um equipamento",
  },
  relatorios: {
    titulo: "Relatórios quinzenais",
    descricao: "Entregas, devoluções e quem está com o quê",
  },
};

export default function App() {
  const { data: usuario, isLoading } = useSessaoAtual();
  const logout = useLogout();
  const [pagina, setPagina] = useState<PaginaId>("inicio");

  // Ainda checando se existe uma sessão válida (cookie) — evita mostrar o
  // login por um instante mesmo quando o usuário já está autenticado.
  if (isLoading) {
    return (
      <div className="flex h-screen items-center justify-center bg-background">
        <p className="text-sm text-muted-foreground">Carregando...</p>
      </div>
    );
  }

  if (!usuario) {
    return <LoginForm />;
  }

  async function aoSair() {
    await logout.mutateAsync();
    toast.success("Sessão encerrada.");
  }

  const { titulo, descricao } = TITULOS[pagina];

  return (
    <PageShell
      paginaAtual={pagina}
      aoNavegar={setPagina}
      titulo={titulo}
      descricao={descricao}
      usuario={usuario}
      aoSair={aoSair}
    >
      {pagina === "inicio" && <Inicio aoNavegar={setPagina} />}
      {pagina === "usuarios-adicionar" && <UsuarioForm />}
      {pagina === "usuarios-consultar" && <UsuarioBusca />}
      {pagina === "equipamentos-adicionar" && <EquipamentoAdicionar />}
      {pagina === "equipamentos-consultar" && <EquipamentoBusca />}
      {pagina === "equipamentos-editar" && <EquipamentoEditar />}
      {pagina === "equipamentos-remover" && <EquipamentoRemover />}
      {pagina === "equipamentos-ler-qr" && <EquipamentoScanner />}
      {pagina === "relatorios" && <Relatorios />}
    </PageShell>
  );
}
