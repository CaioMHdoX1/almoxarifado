import { type ReactNode, useState } from "react";
import { Header } from "@/components/layout/Header";
import { type PaginaId, Sidebar } from "@/components/layout/Sidebar";
import type { Usuario } from "@/lib/types";

type PageShellProps = {
  paginaAtual: PaginaId;
  aoNavegar: (pagina: PaginaId) => void;
  titulo: string;
  descricao?: string;
  usuario: Usuario;
  aoSair: () => void;
  children: ReactNode;
};

export function PageShell({
  paginaAtual,
  aoNavegar,
  titulo,
  descricao,
  usuario,
  aoSair,
  children,
}: PageShellProps) {
  const [menuAberto, setMenuAberto] = useState(false);

  function navegarEFechar(pagina: PaginaId) {
    aoNavegar(pagina);
    setMenuAberto(false);
  }

  return (
    <div className="flex h-screen bg-background">
      <Sidebar
        paginaAtual={paginaAtual}
        aoNavegar={navegarEFechar}
        usuario={usuario}
        aoSair={aoSair}
        aberta={menuAberto}
        aoFechar={() => setMenuAberto(false)}
      />
      <div className="flex flex-1 flex-col overflow-hidden">
        <Header titulo={titulo} descricao={descricao} aoAbrirMenu={() => setMenuAberto(true)} />
        <main className="flex-1 overflow-y-auto px-4 py-4 sm:px-8 sm:py-6">{children}</main>
      </div>
    </div>
  );
}
