import { z } from "zod";

export const equipamentoSchema = z.object({
  nome: z.string().trim().min(2, "Informe o nome do equipamento").max(150),
  codigo: z.string().trim().min(1, "Informe o código/patrimônio").max(50, "Código muito longo"),
  marca: z.string().trim().min(1, "Informe a marca").max(100),
});

export type EquipamentoFormValues = z.infer<typeof equipamentoSchema>;
