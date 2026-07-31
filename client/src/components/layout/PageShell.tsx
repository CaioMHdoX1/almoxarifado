import type { ReactNode } from "react";
import { Header } from "@/components/layout/Header";
import { type PaginaId, Sidebar } from "@/components/layout/Sidebar";

type PageShellProps = {
  paginaAtual: PaginaId;
  aoNavegar: (pagina: PaginaId) => void;
  titulo: string;
  descricao?: string;
  children: ReactNode;
};

export function PageShell({ paginaAtual, aoNavegar, titulo, descricao, children }: PageShellProps) {
  return (
    <div className="flex h-screen bg-background">
      <Sidebar paginaAtual={paginaAtual} aoNavegar={aoNavegar} />
      <div className="flex flex-1 flex-col overflow-hidden">
        <Header titulo={titulo} descricao={descricao} />
        <main className="flex-1 overflow-y-auto px-8 py-6">{children}</main>
      </div>
    </div>
  );
}
