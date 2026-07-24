import type { ButtonHTMLAttributes, ReactNode } from "react";
import { cn } from "@/lib/utils";

type Variante = "primaria" | "secundaria" | "perigo" | "fantasma";

type BotaoProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  variante?: Variante;
  icone?: ReactNode;
};

const estilosPorVariante: Record<Variante, string> = {
  primaria: "bg-primary text-primary-foreground hover:opacity-90 focus-visible:outline-primary",
  secundaria: "bg-white text-primary border border-primary/30 hover:bg-secondary",
  perigo:
    "bg-white text-destructive border border-destructive/30 hover:bg-destructive/10 focus-visible:outline-destructive",
  fantasma: "bg-transparent text-muted-foreground hover:bg-black/5",
};

export function Button({
  variante = "primaria",
  icone,
  className,
  children,
  ...props
}: BotaoProps) {
  return (
    <button
      className={cn(
        "inline-flex items-center justify-center gap-2 rounded-md px-4 py-2.5 text-sm font-medium transition-colors",
        "disabled:cursor-not-allowed disabled:opacity-50",
        "focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2",
        estilosPorVariante[variante],
        className,
      )}
      {...props}
    >
      {icone}
      {children}
    </button>
  );
}
