import { Plus } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { useCriarEquipamento } from "@/features/equipamentos/api";
import { EquipamentoForm } from "@/features/equipamentos/components/EquipamentoForm";
import type { EquipamentoFormValues } from "@/features/equipamentos/types";
import type { Equipamento } from "@/lib/types";
import { EquipamentoQrCode } from "./EquipamentoQrCode.js";

export function EquipamentoAdicionar() {
  const criarEquipamento = useCriarEquipamento();
  const [equipamentoCriado, setEquipamentoCriado] = useState<Equipamento | null>(null);

  async function aoSubmeter(dados: EquipamentoFormValues) {
    try {
      const criado = await criarEquipamento.mutateAsync(dados);
      toast.success("Equipamento adicionado com sucesso.");
      setEquipamentoCriado(criado);
    } catch (erro) {
      const mensagem =
        erro instanceof Error ? erro.message : "Não foi possível adicionar o equipamento.";
      toast.error(mensagem);
    }
  }

  if (equipamentoCriado) {
    return (
      <div className="flex max-w-lg flex-col gap-4">
        <Card className="flex flex-col items-center gap-4">
          <p className="text-sm text-muted-foreground">
            Equipamento cadastrado. Aponte a câmera pra esse QR code (ou o do item físico depois de
            impresso) na tela de "Ler QR code" pra consultar rapidamente.
          </p>
          <EquipamentoQrCode codigo={equipamentoCriado.codigo} nome={equipamentoCriado.nome} />
        </Card>
        <Button
          variante="secundaria"
          icone={<Plus className="h-4 w-4" />}
          onClick={() => setEquipamentoCriado(null)}
          className="self-start"
        >
          Cadastrar outro equipamento
        </Button>
      </div>
    );
  }

  return (
    <Card className="max-w-lg">
      <EquipamentoForm aoSubmeter={aoSubmeter} rotuloBotao="Adicionar equipamento" />
    </Card>
  );
}
