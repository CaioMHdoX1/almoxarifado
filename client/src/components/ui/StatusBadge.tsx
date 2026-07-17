import type { StatusEquipamento } from "@/lib/mock-data";
import { cn } from "@/lib/utils";

export function StatusBadge({ status }: { status: StatusEquipamento }) {
	const disponivel = status === "disponivel";
	return (
		<span
			className={cn(
				"inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium",
				disponivel ? "bg-emerald-100 text-emerald-800" : "bg-secondary text-secondary-foreground",
			)}
		>
			{disponivel ? "Disponível" : "Alocado"}
		</span>
	);
}
