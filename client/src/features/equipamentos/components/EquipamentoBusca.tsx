import { Search } from "lucide-react";
import { useState } from "react";
import { Card } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { StatusBadge } from "@/components/ui/StatusBadge";
import { useBuscaEquipamentoPorNome } from "@/features/equipamentos/api";

export function EquipamentoBusca() {
  const [termo, setTermo] = useState("");
  const { data: resultados, isFetching } = useBuscaEquipamentoPorNome(termo);

  return (
    <div className="flex flex-col gap-6">
      <div className="relative max-w-lg">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          placeholder="Buscar por nome do equipamento..."
          value={termo}
          onChange={(e) => setTermo(e.target.value)}
          className="pl-9"
        />
      </div>

      {termo.trim() === "" && (
        <p className="text-sm text-muted-foreground">
          Digite um nome para ver quantos existem, disponíveis e alocados.
        </p>
      )}

      {isFetching && <p className="text-sm text-muted-foreground">Buscando...</p>}

      {!isFetching && termo.trim() !== "" && resultados?.length === 0 && (
        <p className="text-sm text-muted-foreground">
          Nenhum equipamento encontrado com esse nome.
        </p>
      )}

      <div className="flex flex-col gap-4">
        {resultados?.map((grupo) => (
          <Card key={grupo.nome}>
            <div className="flex flex-wrap items-center justify-between gap-3">
              <h3 className="font-serif text-lg text-foreground">{grupo.nome}</h3>
              <div className="flex gap-2 text-sm">
                <span className="rounded-full bg-secondary px-3 py-1 font-medium text-secondary-foreground">
                  Total: {grupo.total}
                </span>
                <span className="rounded-full bg-emerald-100 px-3 py-1 font-medium text-emerald-800">
                  Disponíveis: {grupo.disponiveis}
                </span>
                <span className="rounded-full bg-[var(--accent)]/10 px-3 py-1 font-medium text-[var(--accent)]">
                  Alocados: {grupo.alocados}
                </span>
              </div>
            </div>

            <table className="mt-4 w-full border-t border-border pt-2 text-sm">
              <thead>
                <tr className="text-left text-xs uppercase tracking-wide text-muted-foreground">
                  <th className="py-2 pr-4 font-medium">Código</th>
                  <th className="py-2 pr-4 font-medium">Marca</th>
                  <th className="py-2 pr-4 font-medium">Status</th>
                  <th className="py-2 font-medium">Com quem está</th>
                </tr>
              </thead>
              <tbody>
                {grupo.itens.map((item) => (
                  <tr key={item.id} className="border-t border-border/60">
                    <td className="py-2 pr-4 font-mono text-xs text-foreground">{item.codigo}</td>
                    <td className="py-2 pr-4 text-foreground">{item.marca}</td>
                    <td className="py-2 pr-4">
                      <StatusBadge status={item.status} />
                    </td>
                    <td className="py-2 text-foreground">{item.usuarioAtual?.nome ?? "—"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </Card>
        ))}
      </div>
    </div>
  );
}
