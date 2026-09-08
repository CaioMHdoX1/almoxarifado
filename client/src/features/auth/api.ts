import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { LoginFormValues } from "@/features/auth/types";
import { ApiError, apiClient } from "@/lib/api-client";
import type { Administrador } from "@/lib/types";

const CHAVE_SESSAO = ["auth", "me"] as const;

/**
 * Sessão atual. Não é logado (401) é um estado normal, não um erro de
 * verdade — por isso a queryFn engole o ApiError 401 e devolve `null` em
 * vez de deixar o TanStack Query marcar a query como "isError".
 */
export function useSessaoAtual() {
  return useQuery({
    queryKey: CHAVE_SESSAO,
    queryFn: async (): Promise<Administrador | null> => {
      try {
        const resultado = await apiClient.get<{ usuario: Administrador }>("/api/auth/me");
        return resultado.usuario;
      } catch (erro) {
        if (erro instanceof ApiError && erro.status === 401) {
          return null;
        }
        throw erro;
      }
    },
    retry: false,
    staleTime: 5 * 60_000,
  });
}

export function useLogin() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (dados: LoginFormValues) =>
      apiClient.post<{ usuario: Administrador }>("/api/auth/login", dados),
    onSuccess: (resultado) => {
      queryClient.setQueryData(CHAVE_SESSAO, resultado.usuario);
    },
  });
}

export function useLogout() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => apiClient.post("/api/auth/logout"),
    onSuccess: () => {
      queryClient.setQueryData(CHAVE_SESSAO, null);
      // Limpa tudo que possa ter dado de outro usuário (usuários/equipamentos)
      queryClient.clear();
    },
  });
}
