import { Menu } from "lucide-react";

type HeaderProps = {
  titulo: string;
  descricao?: string;
  aoAbrirMenu: () => void;
};

export function Header({ titulo, descricao, aoAbrirMenu }: HeaderProps) {
  return (
    <header className="flex h-16 shrink-0 items-center gap-3 border-b border-border bg-card px-4 sm:px-8">
      <button
        type="button"
        onClick={aoAbrirMenu}
        aria-label="Abrir menu"
        className="-ml-1 rounded-md p-2 text-foreground hover:bg-secondary md:hidden"
      >
        <Menu className="h-5 w-5" />
      </button>

      <div className="min-w-0">
        <h1 className="truncate font-serif text-lg leading-none text-foreground sm:text-xl">
          {titulo}
        </h1>
        {descricao && (
          <p className="mt-1 hidden truncate text-sm text-muted-foreground sm:block">{descricao}</p>
        )}
      </div>
    </header>
  );
}
