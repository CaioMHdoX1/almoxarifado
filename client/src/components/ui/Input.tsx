import type { InputHTMLAttributes, ReactNode } from "react";
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

type CampoProps = {
	label: string;
	erro?: string;
	dica?: string;
	children: ReactNode;
	htmlFor?: string;
};

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
