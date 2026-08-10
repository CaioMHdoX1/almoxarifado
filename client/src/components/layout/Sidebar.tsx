import {
  LayoutDashboard,
  LogOut,
  PackageCheck,
  PackageMinus,
  PackagePlus,
  PackageSearch,
  QrCode,
  UserPlus,
  UserSearch,
  X,
} from "lucide-react";
import type { Usuario } from "@/lib/types";
import { cn } from "@/lib/utils";

export type PaginaId =
  | "inicio"
  | "usuarios-adicionar"
  | "usuarios-consultar"
  | "equipamentos-adicionar"
  | "equipamentos-consultar"
  | "equipamentos-remover"
  | "equipamentos-editar"
  | "equipamentos-ler-qr";

type ItemMenu = {
  id: PaginaId;
  label: string;
  icone: typeof LayoutDashboard;
};

type SecaoMenu = {
  titulo: string;
  itens: ItemMenu[];
};

const SECOES: SecaoMenu[] = [
  {
    titulo: "Geral",
    itens: [{ id: "inicio", label: "Início", icone: LayoutDashboard }],
  },
  {
    titulo: "Usuários",
    itens: [
      { id: "usuarios-adicionar", label: "Adicionar usuário", icone: UserPlus },
      { id: "usuarios-consultar", label: "Consultar usuário", icone: UserSearch },
    ],
  },
  {
    titulo: "Equipamentos",
    itens: [
      { id: "equipamentos-adicionar", label: "Adicionar equipamento", icone: PackagePlus },
      { id: "equipamentos-consultar", label: "Consultar equipamento", icone: PackageSearch },
      { id: "equipamentos-editar", label: "Editar equipamento", icone: PackageCheck },
      { id: "equipamentos-remover", label: "Remover equipamento", icone: PackageMinus },
      { id: "equipamentos-ler-qr", label: "Ler QR code", icone: QrCode },
    ],
  },
];

type SidebarProps = {
  paginaAtual: PaginaId;
  aoNavegar: (pagina: PaginaId) => void;
  usuario: Usuario;
  aoSair: () => void;
  aberta: boolean;
  aoFechar: () => void;
};

export function Sidebar({
  paginaAtual,
  aoNavegar,
  usuario,
  aoSair,
  aberta,
  aoFechar,
}: SidebarProps) {
  return (
    <>
      {aberta && (
        <button
          type="button"
          aria-label="Fechar menu"
          onClick={aoFechar}
          className="fixed inset-0 z-30 bg-black/40 md:hidden"
        />
      )}

      <aside
        className={cn(
          "fixed inset-y-0 left-0 z-40 flex h-screen w-72 shrink-0 flex-col border-r border-sidebar-border bg-sidebar transition-transform duration-200 ease-out",
          "md:static md:z-auto md:w-64 md:translate-x-0",
          aberta ? "translate-x-0" : "-translate-x-full",
        )}
      >
        <div className="flex h-16 items-center justify-between gap-2 border-b border-sidebar-border px-5">
          <div className="flex items-center gap-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-md bg-sidebar-primary font-serif text-sm font-medium text-sidebar-primary-foreground">
              A
            </div>
            <span className="font-serif text-lg text-sidebar-foreground">Almoxaf</span>
          </div>
          <button
            type="button"
            onClick={aoFechar}
            aria-label="Fechar menu"
            className="rounded-md p-1.5 text-muted-foreground hover:bg-sidebar-accent/60 md:hidden"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <nav className="flex-1 overflow-y-auto px-3 py-4">
          {SECOES.map((secao) => (
            <div key={secao.titulo} className="mb-6">
              <p className="mb-2 px-2 text-xs font-medium uppercase tracking-wide text-muted-foreground">
                {secao.titulo}
              </p>
              <ul className="flex flex-col gap-0.5">
                {secao.itens.map((item) => {
                  const Icone = item.icone;
                  const ativo = item.id === paginaAtual;
                  return (
                    <li key={item.id}>
                      <button
                        type="button"
                        onClick={() => aoNavegar(item.id)}
                        className={cn(
                          "flex w-full items-center gap-2.5 rounded-md px-3 py-2.5 text-left text-sm transition-colors",
                          ativo
                            ? "bg-sidebar-accent text-sidebar-accent-foreground font-medium"
                            : "text-sidebar-foreground hover:bg-sidebar-accent/60",
                        )}
                      >
                        <Icone className="h-4 w-4 shrink-0" />
                        {item.label}
                      </button>
                    </li>
                  );
                })}
              </ul>
            </div>
          ))}
        </nav>

        <div className="border-t border-sidebar-border p-3">
          <div className="flex items-center gap-2.5 rounded-md px-2 py-2">
            <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-sidebar-accent text-sm font-medium text-sidebar-accent-foreground">
              {usuario.nome.charAt(0).toUpperCase()}
            </div>
            <div className="min-w-0 flex-1">
              <p className="truncate text-sm font-medium text-sidebar-foreground">{usuario.nome}</p>
              <p className="truncate text-xs text-muted-foreground">{usuario.projeto}</p>
            </div>
            <button
              type="button"
              onClick={aoSair}
              title="Sair"
              className="shrink-0 rounded-md p-2 text-muted-foreground hover:bg-sidebar-accent/60 hover:text-sidebar-foreground"
            >
              <LogOut className="h-4 w-4" />
            </button>
          </div>
        </div>
      </aside>
    </>
  );
}
