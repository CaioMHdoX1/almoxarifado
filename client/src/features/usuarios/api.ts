import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { UsuarioFormValues } from "@/features/usuarios/types";
import { buscarUsuarioPorNome, criarUsuario, listarUsuarios, type Usuario } from "@/lib/mock-data";

export function useUsuarios() {
  return useQuery({
    queryKey: ["usuarios"],
    queryFn: listarUsuarios,
  });
}

export function useBuscaUsuarioPorNome(termo: string) {
  return useQuery({
    queryKey: ["usuarios", "busca", termo],
    queryFn: () => buscarUsuarioPorNome(termo),
    enabled: termo.trim().length > 0,
  });
}

export function useCriarUsuario() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (dados: UsuarioFormValues): Promise<Usuario> => criarUsuario(dados),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["usuarios"] });
    },
  });
}
