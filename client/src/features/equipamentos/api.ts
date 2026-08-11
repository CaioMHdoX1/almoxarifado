import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { EquipamentoFormValues } from "@/features/equipamentos/types";
import { apiClient } from "@/lib/api-client";
import type { BuscaEquipamentoResultado, Equipamento } from "@/lib/types";

const CHAVE_BASE = ["equipamentos"] as const;

export function useEquipamentos() {
  return useQuery({
    queryKey: CHAVE_BASE,
    queryFn: () => apiClient.get<Equipamento[]>("/api/equipamentos"),
  });
}

export function useBuscaEquipamentoPorNome(termo: string) {
  return useQuery({
    queryKey: [...CHAVE_BASE, "busca", termo],
    queryFn: () =>
      apiClient.get<BuscaEquipamentoResultado[]>(
        `/api/equipamentos?nome=${encodeURIComponent(termo)}`,
      ),
    enabled: termo.trim().length > 0,
  });
}

export function useEquipamentoPorCodigo(codigo: string) {
  return useQuery({
    queryKey: [...CHAVE_BASE, "codigo", codigo],
    queryFn: () =>
      apiClient.get<Equipamento>(`/api/equipamentos/codigo/${encodeURIComponent(codigo)}`),
    enabled: codigo.trim().length > 0,
    retry: false,
  });
}

export function useCriarEquipamento() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (dados: EquipamentoFormValues) =>
      apiClient.post<Equipamento>("/api/equipamentos", dados),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: CHAVE_BASE }),
  });
}

export function useEditarEquipamento() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, dados }: { id: number; dados: EquipamentoFormValues }) =>
      apiClient.put<Equipamento>(`/api/equipamentos/${id}`, dados),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: CHAVE_BASE }),
  });
}

export function useRemoverEquipamento() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => apiClient.delete(`/api/equipamentos/${id}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: CHAVE_BASE }),
  });
}
