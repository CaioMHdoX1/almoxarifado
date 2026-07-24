type HeaderProps = {
  titulo: string;
  descricao?: string;
};

export function Header({ titulo, descricao }: HeaderProps) {
  return (
    <header className="flex h-16 shrink-0 items-center border-b border-border bg-card px-8">
      <div>
        <h1 className="font-serif text-xl leading-none text-foreground">{titulo}</h1>
        {descricao && <p className="mt-1 text-sm text-muted-foreground">{descricao}</p>}
      </div>
    </header>
  );
}
