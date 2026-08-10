import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { UsuarioFormValues } from "@/features/usuarios/types";
import { apiClient } from "@/lib/api-client";
import type { Usuario, UsuarioComEquipamentos } from "@/lib/types";

const CHAVE_BASE = ["usuarios"] as const;

export function useBuscaUsuarioPorNome(termo: string) {
  return useQuery({
    queryKey: [...CHAVE_BASE, "busca", termo],
    queryFn: () =>
      apiClient.get<UsuarioComEquipamentos[]>(`/api/usuarios?nome=${encodeURIComponent(termo)}`),
    enabled: termo.trim().length > 0,
  });
}

export function useCriarUsuario() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (dados: UsuarioFormValues) => apiClient.post<Usuario>("/api/usuarios", dados),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: CHAVE_BASE });
    },
  });
}
