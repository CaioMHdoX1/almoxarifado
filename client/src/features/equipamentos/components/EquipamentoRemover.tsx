import { Trash2 } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/Button";
import { ConfirmDialog } from "@/components/ui/ConfirmDialog";
import { useEquipamentos, useRemoverEquipamento } from "@/features/equipamentos/api";
import { EquipamentoLista } from "@/features/equipamentos/components/EquipamentoLista";

export function EquipamentoRemover() {
  const { data: equipamentos, isLoading } = useEquipamentos();
  const removerEquipamento = useRemoverEquipamento();

  if (isLoading) return <p className="text-sm text-muted-foreground">Carregando...</p>;

  return (
    <EquipamentoLista
      equipamentos={equipamentos ?? []}
      renderAcoes={(equipamento) => (
        <ConfirmDialog
          titulo="Remover equipamento"
          descricao={`Tem certeza que deseja remover "${equipamento.nome}" (${equipamento.codigo})? Essa ação não pode ser desfeita.`}
          textoConfirmar="Remover"
          onConfirmar={async () => {
            try {
              await removerEquipamento.mutateAsync(equipamento.id);
              toast.success("Equipamento removido.");
            } catch (erro) {
              const mensagem = erro instanceof Error ? erro.message : "Não foi possível remover.";
              toast.error(mensagem);
            }
          }}
          trigger={
            <Button variante="perigo" icone={<Trash2 className="h-4 w-4" />}>
              Remover
            </Button>
          }
        />
      )}
    />
  );
}
