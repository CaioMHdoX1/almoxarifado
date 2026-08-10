import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { Campo, Input } from "@/components/ui/Input";
import { useLogin } from "@/features/auth/api";
import { type LoginFormValues, loginSchema } from "@/features/auth/types";

export function LoginForm() {
  const login = useLogin();
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
  });

  async function aoSubmeter(dados: LoginFormValues) {
    try {
      await login.mutateAsync(dados);
      toast.success("Login realizado com sucesso.");
    } catch {
      toast.error("Email ou senha inválidos.");
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-background px-4">
      <Card className="w-full max-w-sm">
        <div className="mb-6 text-center">
          <div className="mx-auto mb-3 flex h-10 w-10 items-center justify-center rounded-md bg-primary font-serif text-lg text-primary-foreground">
            A
          </div>
          <h1 className="font-serif text-xl text-foreground">Almoxaf</h1>
          <p className="mt-1 text-sm text-muted-foreground">Entre com seu email e senha</p>
        </div>

        <form onSubmit={handleSubmit(aoSubmeter)} className="flex flex-col gap-4">
          <Campo label="Email" htmlFor="email" erro={errors.email?.message}>
            <Input id="email" type="email" placeholder="voce@empresa.com" {...register("email")} />
          </Campo>

          <Campo label="Senha" htmlFor="senha" erro={errors.senha?.message}>
            <Input id="senha" type="password" placeholder="••••••••" {...register("senha")} />
          </Campo>

          <Button type="submit" disabled={isSubmitting} className="mt-2">
            {isSubmitting ? "Entrando..." : "Entrar"}
          </Button>
        </form>
      </Card>
    </div>
  );
}
