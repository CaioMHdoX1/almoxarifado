import { Pencil } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { useEditarEquipamento, useEquipamentos } from "@/features/equipamentos/api";
import { EquipamentoForm } from "@/features/equipamentos/components/EquipamentoForm";
import { EquipamentoLista } from "@/features/equipamentos/components/EquipamentoLista";
import type { EquipamentoFormValues } from "@/features/equipamentos/types";
import type { Equipamento } from "@/lib/mock-data";

export function EquipamentoEditar() {
  const { data: equipamentos, isLoading } = useEquipamentos();
  const editarEquipamento = useEditarEquipamento();
  const [emEdicaoId, setEmEdicaoId] = useState<number | null>(null);

  if (isLoading) return <p className="text-sm text-muted-foreground">Carregando...</p>;

  const equipamentoEmEdicao = equipamentos?.find((e) => e.id === emEdicaoId) ?? null;

  async function aoSubmeter(dados: EquipamentoFormValues) {
    if (!equipamentoEmEdicao) return;
    try {
      await editarEquipamento.mutateAsync({ id: equipamentoEmEdicao.id, dados });
      toast.success("Equipamento atualizado.");
      setEmEdicaoId(null);
    } catch (erro) {
      const mensagem = erro instanceof Error ? erro.message : "Não foi possível salvar.";
      toast.error(mensagem);
    }
  }

  if (equipamentoEmEdicao) {
    const valoresIniciais: EquipamentoFormValues = {
      nome: equipamentoEmEdicao.nome,
      codigo: equipamentoEmEdicao.codigo,
      marca: equipamentoEmEdicao.marca,
    };
    return (
      <Card className="max-w-lg">
        <h3 className="mb-4 font-serif text-lg text-foreground">
          Editando: {equipamentoEmEdicao.nome}
        </h3>
        <EquipamentoForm
          valoresIniciais={valoresIniciais}
          rotuloBotao="Salvar alterações"
          aoSubmeter={aoSubmeter}
          aoCancelar={() => setEmEdicaoId(null)}
        />
      </Card>
    );
  }

  return (
    <EquipamentoLista
      equipamentos={equipamentos ?? []}
      renderAcoes={(equipamento: Equipamento) => (
        <Button
          variante="secundaria"
          icone={<Pencil className="h-4 w-4" />}
          onClick={() => setEmEdicaoId(equipamento.id)}
        >
          Editar
        </Button>
      )}
    />
  );
}
