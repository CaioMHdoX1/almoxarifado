import { z } from "zod";
import { somenteDigitos } from "@/lib/utils";

export const usuarioSchema = z.object({
  nome: z.string().trim().min(3, "Informe o nome completo").max(150, "Nome muito longo"),
  cpf: z
    .string()
    .transform(somenteDigitos)
    .refine((valor) => valor.length === 11, "CPF deve ter 11 dígitos"),
  projeto: z.string().trim().min(1, "Informe o projeto associado"),
});

export type UsuarioFormValues = z.infer<typeof usuarioSchema>;
