import { z } from "zod";

export const equipamentoSchema = z
  .object({
    nome: z.string().trim().min(2, "Informe o nome do equipamento").max(150),
    codigo: z.string().trim().min(1, "Informe o código/patrimônio").max(50, "Código muito longo"),
    marca: z.string().trim().min(1, "Informe a marca").max(100),
    descricao: z.string().trim().max(2000, "Descrição muito longa").optional(),
    tipo: z.enum(["equipamento", "almoxarifado"]),
    // z.coerce porque o <input type="number"> do HTML devolve string —
    // aqui já convertido pra number antes de chegar no resto do app.
    quantidade: z.coerce.number().int().min(0, "Quantidade não pode ser negativa").optional(),
  })
  // "sempre questiona a quantidade" quando o item é de almoxarifado —
  // aqui é onde essa regra vira validação de verdade.
  .superRefine((dados, ctx) => {
    if (dados.tipo === "almoxarifado" && dados.quantidade === undefined) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        path: ["quantidade"],
        message: "Informe a quantidade em estoque",
      });
    }
  });

export type EquipamentoFormValues = z.infer<typeof equipamentoSchema>;

export const TIPOS_EQUIPAMENTO: Array<{ valor: "equipamento" | "almoxarifado"; label: string }> = [
  {
    valor: "equipamento",
    label: "Equipamento (notebook, monitor, fone, tablet, celular, mini PC...)",
  },
  {
    valor: "almoxarifado",
    label: "Almoxarifado (item de estoque/consumível, controlado por quantidade)",
  },
];
