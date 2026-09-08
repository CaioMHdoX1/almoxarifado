import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { apiClient } from "@/lib/api-client";
import type { RelatorioDetalhe, RelatorioResumo } from "@/lib/types";

const CHAVE_BASE = ["relatorios"] as const;

export function useRelatorios() {
  return useQuery({
    queryKey: CHAVE_BASE,
    queryFn: () => apiClient.get<RelatorioResumo[]>("/api/relatorios"),
  });
}

export function useRelatorioDetalhe(id: number | null) {
  return useQuery({
    queryKey: [...CHAVE_BASE, id],
    queryFn: () => apiClient.get<RelatorioDetalhe>(`/api/relatorios/${id}`),
    enabled: id !== null,
  });
}

export function useGerarRelatorio() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => apiClient.post<RelatorioResumo>("/api/relatorios/gerar"),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: CHAVE_BASE }),
  });
}
