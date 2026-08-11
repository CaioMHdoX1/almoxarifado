import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { LoginFormValues } from "@/features/auth/types";
import { ApiError, apiClient } from "@/lib/api-client";
import type { Usuario } from "@/lib/types";

const CHAVE_SESSAO = ["auth", "me"] as const;

export function useSessaoAtual() {
  return useQuery({
    queryKey: CHAVE_SESSAO,
    queryFn: async (): Promise<Usuario | null> => {
      try {
        const resultado = await apiClient.get<{ usuario: Usuario }>("/api/auth/me");
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
      apiClient.post<{ usuario: Usuario }>("/api/auth/login", dados),
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
      queryClient.clear();
    },
  });
}
