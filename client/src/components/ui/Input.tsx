import { Eye, EyeOff } from "lucide-react";
import {
  forwardRef,
  type InputHTMLAttributes,
  type ReactNode,
  type SelectHTMLAttributes,
  type TextareaHTMLAttributes,
  useState,
} from "react";
import { cn } from "@/lib/utils";

export function Input({ className, ...props }: InputHTMLAttributes<HTMLInputElement>) {
  return (
    <input
      className={cn(
        "w-full rounded-md border border-border bg-[var(--input-background)] px-3 py-2.5 text-sm",
        "placeholder:text-muted-foreground",
        "focus:outline focus:outline-2 focus:outline-primary focus:outline-offset-1",
        "disabled:cursor-not-allowed disabled:opacity-50",
        className,
      )}
      {...props}
    />
  );
}

export const Select = forwardRef<HTMLSelectElement, SelectHTMLAttributes<HTMLSelectElement>>(
  function Select({ className, children, ...props }, ref) {
    return (
      <select
        ref={ref}
        className={cn(
          "w-full rounded-md border border-border bg-[var(--input-background)] px-3 py-2.5 text-sm",
          "focus:outline focus:outline-2 focus:outline-primary focus:outline-offset-1",
          "disabled:cursor-not-allowed disabled:opacity-50",
          className,
        )}
        {...props}
      >
        {children}
      </select>
    );
  },
);

export const Textarea = forwardRef<
  HTMLTextAreaElement,
  TextareaHTMLAttributes<HTMLTextAreaElement>
>(function Textarea({ className, ...props }, ref) {
  return (
    <textarea
      ref={ref}
      rows={3}
      className={cn(
        "w-full resize-y rounded-md border border-border bg-[var(--input-background)] px-3 py-2.5 text-sm",
        "placeholder:text-muted-foreground",
        "focus:outline focus:outline-2 focus:outline-primary focus:outline-offset-1",
        "disabled:cursor-not-allowed disabled:opacity-50",
        className,
      )}
      {...props}
    />
  );
});

/**
 * Campo de senha com botão de olho pra mostrar/ocultar o texto digitado.
 * `forwardRef` é necessário porque o react-hook-form (`register(...)`)
 * precisa de acesso direto ao elemento `<input>`.
 */
export const PasswordInput = forwardRef<HTMLInputElement, InputHTMLAttributes<HTMLInputElement>>(
  function PasswordInput({ className, ...props }, ref) {
    const [visivel, setVisivel] = useState(false);

    return (
      <div className="relative">
        <input
          ref={ref}
          type={visivel ? "text" : "password"}
          className={cn(
            "w-full rounded-md border border-border bg-[var(--input-background)] px-3 py-2.5 pr-10 text-sm",
            "placeholder:text-muted-foreground",
            "focus:outline focus:outline-2 focus:outline-primary focus:outline-offset-1",
            "disabled:cursor-not-allowed disabled:opacity-50",
            className,
          )}
          {...props}
        />
        <button
          type="button"
          onClick={() => setVisivel((v) => !v)}
          tabIndex={-1}
          aria-label={visivel ? "Ocultar senha" : "Mostrar senha"}
          className="absolute right-2 top-1/2 -translate-y-1/2 rounded p-1.5 text-muted-foreground hover:text-foreground"
        >
          {visivel ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
        </button>
      </div>
    );
  },
);

type CampoProps = {
  label: string;
  erro?: string;
  dica?: string;
  children: ReactNode;
  htmlFor?: string;
};

/** Envolve um input com label + mensagem de erro, no padrão visual do app. */
export function Campo({ label, erro, dica, children, htmlFor }: CampoProps) {
  return (
    <div className="flex flex-col gap-1.5">
      <label htmlFor={htmlFor} className="text-sm font-medium text-foreground">
        {label}
      </label>
      {children}
      {dica && !erro && <p className="text-xs text-muted-foreground">{dica}</p>}
      {erro && <p className="text-xs text-destructive">{erro}</p>}
    </div>
  );
}
