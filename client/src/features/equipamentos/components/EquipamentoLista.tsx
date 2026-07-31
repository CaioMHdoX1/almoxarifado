import type { ReactNode } from "react";
import { Card } from "@/components/ui/Card";
import { StatusBadge } from "@/components/ui/StatusBadge";
import type { Equipamento } from "@/lib/mock-data";

type EquipamentoListaProps = {
  equipamentos: Equipamento[];
  renderAcoes: (equipamento: Equipamento) => ReactNode;
};

export function EquipamentoLista({ equipamentos, renderAcoes }: EquipamentoListaProps) {
  if (equipamentos.length === 0) {
    return <p className="text-sm text-muted-foreground">Nenhum equipamento cadastrado ainda.</p>;
  }

  return (
    <Card className="p-0">
      <table className="w-full text-sm">
        <thead>
          <tr className="border-b border-border text-left text-xs uppercase tracking-wide text-muted-foreground">
            <th className="px-4 py-3 font-medium">Nome</th>
            <th className="px-4 py-3 font-medium">Código</th>
            <th className="px-4 py-3 font-medium">Marca</th>
            <th className="px-4 py-3 font-medium">Status</th>
            <th className="px-4 py-3 font-medium">Com quem está</th>
            <th className="px-4 py-3 font-medium">Ações</th>
          </tr>
        </thead>
        <tbody>
          {equipamentos.map((equipamento) => (
            <tr key={equipamento.id} className="border-b border-border/60 last:border-0">
              <td className="px-4 py-3 text-foreground">{equipamento.nome}</td>
              <td className="px-4 py-3 font-mono text-xs text-foreground">{equipamento.codigo}</td>
              <td className="px-4 py-3 text-foreground">{equipamento.marca}</td>
              <td className="px-4 py-3">
                <StatusBadge status={equipamento.status} />
              </td>
              <td className="px-4 py-3 text-foreground">{equipamento.usuarioAtual?.nome ?? "—"}</td>
              <td className="px-4 py-3">{renderAcoes(equipamento)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </Card>
  );
}
