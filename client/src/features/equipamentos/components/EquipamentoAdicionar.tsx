import { toast } from "sonner";
import { Card } from "@/components/ui/Card";
import { useCriarEquipamento } from "@/features/equipamentos/api";
import { EquipamentoForm } from "@/features/equipamentos/components/EquipamentoForm";
import type { EquipamentoFormValues } from "@/features/equipamentos/types";

export function EquipamentoAdicionar() {
  const criarEquipamento = useCriarEquipamento();

  async function aoSubmeter(dados: EquipamentoFormValues) {
    try {
      await criarEquipamento.mutateAsync(dados);
      toast.success("Equipamento adicionado com sucesso.");
    } catch (erro) {
      const mensagem =
        erro instanceof Error ? erro.message : "Não foi possível adicionar o equipamento.";
      toast.error(mensagem);
    }
  }

  return (
    <Card className="max-w-lg">
      <EquipamentoForm aoSubmeter={aoSubmeter} rotuloBotao="Adicionar equipamento" />
    </Card>
  );
}
