import {
  LayoutDashboard,
  PackageCheck,
  PackageMinus,
  PackagePlus,
  PackageSearch,
  UserPlus,
  UserSearch,
} from "lucide-react";
import { cn } from "@/lib/utils";

export type PaginaId =
  | "inicio"
  | "usuarios-adicionar"
  | "usuarios-consultar"
  | "equipamentos-adicionar"
  | "equipamentos-consultar"
  | "equipamentos-remover"
  | "equipamentos-editar";

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
    ],
  },
];

type SidebarProps = {
  paginaAtual: PaginaId;
  aoNavegar: (pagina: PaginaId) => void;
};

export function Sidebar({ paginaAtual, aoNavegar }: SidebarProps) {
  return (
    <aside className="flex h-screen w-64 shrink-0 flex-col border-r border-sidebar-border bg-sidebar">
      <div className="flex h-16 items-center gap-2 border-b border-sidebar-border px-5">
        <div className="flex h-8 w-8 items-center justify-center rounded-md bg-sidebar-primary font-serif text-sm font-medium text-sidebar-primary-foreground">
          A
        </div>
        <span className="font-serif text-lg text-sidebar-foreground">Almoxaf</span>
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
                        "flex w-full items-center gap-2.5 rounded-md px-3 py-2 text-left text-sm transition-colors",
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
    </aside>
  );
}
