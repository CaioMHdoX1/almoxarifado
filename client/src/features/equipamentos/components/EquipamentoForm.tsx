import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/Button";
import { Campo, Input, Select, Textarea } from "@/components/ui/Input";
import {
  type EquipamentoFormValues,
  equipamentoSchema,
  TIPOS_EQUIPAMENTO,
} from "@/features/equipamentos/types";

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
    watch,
    formState: { errors, isSubmitting },
  } = useForm<EquipamentoFormValues>({
    resolver: zodResolver(equipamentoSchema),
    defaultValues: valoresIniciais ?? { tipo: "equipamento" },
  });

  const tipoSelecionado = watch("tipo");

  async function onValid(dados: EquipamentoFormValues) {
    await aoSubmeter(dados);
    if (!valoresIniciais) reset({ tipo: "equipamento" }); // só limpa no modo "adicionar"
  }

  return (
    <form onSubmit={handleSubmit(onValid)} className="flex flex-col gap-4">
      <Campo label="Tipo" htmlFor="tipo" erro={errors.tipo?.message}>
        <Select id="tipo" {...register("tipo")}>
          {TIPOS_EQUIPAMENTO.map((opcao) => (
            <option key={opcao.valor} value={opcao.valor}>
              {opcao.label}
            </option>
          ))}
        </Select>
      </Campo>

      <Campo label="Nome do equipamento" htmlFor="nome" erro={errors.nome?.message}>
        <Input id="nome" placeholder="Ex.: Notebook Dell Latitude 5520" {...register("nome")} />
      </Campo>

      <Campo label="Código / patrimônio" htmlFor="codigo" erro={errors.codigo?.message}>
        <Input id="codigo" placeholder="Ex.: PAT-00812" {...register("codigo")} />
      </Campo>

      <Campo label="Marca" htmlFor="marca" erro={errors.marca?.message}>
        <Input id="marca" placeholder="Ex.: Dell" {...register("marca")} />
      </Campo>

      {/* "sempre questiona a quantidade" — só aparece pra item de almoxarifado */}
      {tipoSelecionado === "almoxarifado" && (
        <Campo label="Quantidade em estoque" htmlFor="quantidade" erro={errors.quantidade?.message}>
          <Input
            id="quantidade"
            type="number"
            min={0}
            step={1}
            placeholder="Ex.: 42"
            {...register("quantidade")}
          />
        </Campo>
      )}

      <Campo
        label="Descrição"
        htmlFor="descricao"
        erro={errors.descricao?.message}
        dica="Opcional — detalhes extras sobre o item"
      >
        <Textarea
          id="descricao"
          placeholder="Ex.: i7 11ª geração, 16GB RAM, SSD 512GB"
          {...register("descricao")}
        />
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
