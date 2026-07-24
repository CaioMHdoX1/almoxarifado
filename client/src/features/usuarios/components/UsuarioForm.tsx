import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { Campo, Input } from "@/components/ui/Input";
import { useCriarUsuario } from "@/features/usuarios/api";
import { type UsuarioFormValues, usuarioSchema } from "@/features/usuarios/types";

export function UsuarioForm() {
  const criarUsuario = useCriarUsuario();
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<UsuarioFormValues>({
    resolver: zodResolver(usuarioSchema),
  });

  async function aoSubmeter(dados: UsuarioFormValues) {
    try {
      await criarUsuario.mutateAsync(dados);
      toast.success("Usuário adicionado com sucesso.");
      reset();
    } catch (erro) {
      const mensagem =
        erro instanceof Error ? erro.message : "Não foi possível adicionar o usuário.";
      toast.error(mensagem);
    }
  }

  return (
    <Card className="max-w-lg">
      <form onSubmit={handleSubmit(aoSubmeter)} className="flex flex-col gap-4">
        <Campo label="Nome completo" htmlFor="nome" erro={errors.nome?.message}>
          <Input id="nome" placeholder="Ex.: Ana Beatriz Costa" {...register("nome")} />
        </Campo>

        <Campo
          label="CPF"
          htmlFor="cpf"
          erro={errors.cpf?.message}
          dica="Somente números, com ou sem pontuação"
        >
          <Input id="cpf" placeholder="000.000.000-00" {...register("cpf")} />
        </Campo>

        <Campo label="Projeto associado" htmlFor="projeto" erro={errors.projeto?.message}>
          <Input id="projeto" placeholder="Ex.: GREat" {...register("projeto")} />
        </Campo>

        <Button type="submit" disabled={isSubmitting} className="mt-2 self-start">
          {isSubmitting ? "Salvando..." : "Adicionar usuário"}
        </Button>
      </form>
    </Card>
  );
}
