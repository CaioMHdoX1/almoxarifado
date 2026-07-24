import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/Button";
import { Campo, Input } from "@/components/ui/Input";
import { type EquipamentoFormValues, equipamentoSchema } from "@/features/equipamentos/types";

type EquipamentoFormProps = {
  valoresIniciais?: EquipamentoFormValues;
  rotuloBotao?: string;
  aoSubmeter: (dados: EquipamentoFormValues) => Promise<void>;
  aoCancelar?: () => void;
};

export function EquipamentoForm({
  valoresIniciais,
  rotuloBotao = "Adicionar equipamento",
  aoSubmeter,
  aoCancelar,
}: EquipamentoFormProps) {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<EquipamentoFormValues>({
    resolver: zodResolver(equipamentoSchema),
    defaultValues: valoresIniciais,
  });

  async function onValid(dados: EquipamentoFormValues) {
    await aoSubmeter(dados);
    if (!valoresIniciais) reset(); // só limpa no modo "adicionar"
  }

  return (
    <form onSubmit={handleSubmit(onValid)} className="flex flex-col gap-4">
      <Campo label="Nome do equipamento" htmlFor="nome" erro={errors.nome?.message}>
        <Input id="nome" placeholder="Ex.: Notebook Dell Latitude 5520" {...register("nome")} />
      </Campo>

      <Campo label="Código / patrimônio" htmlFor="codigo" erro={errors.codigo?.message}>
        <Input id="codigo" placeholder="Ex.: PAT-00812" {...register("codigo")} />
      </Campo>

      <Campo label="Marca" htmlFor="marca" erro={errors.marca?.message}>
        <Input id="marca" placeholder="Ex.: Dell" {...register("marca")} />
      </Campo>

      <div className="mt-2 flex gap-2">
        <Button type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Salvando..." : rotuloBotao}
        </Button>
        {aoCancelar && (
          <Button type="button" variante="fantasma" onClick={aoCancelar}>
            Cancelar
          </Button>
        )}
      </div>
    </form>
  );
}
