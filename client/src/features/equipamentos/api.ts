import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { EquipamentoFormValues } from "@/features/equipamentos/types";
import {
  buscarEquipamentoPorNome,
  criarEquipamento,
  type Equipamento,
  editarEquipamento,
  listarEquipamentos,
  removerEquipamento,
} from "@/lib/mock-data";

const CHAVE_BASE = ["equipamentos"] as const;

export function useEquipamentos() {
  return useQuery({
    queryKey: CHAVE_BASE,
    queryFn: listarEquipamentos,
  });
}

export function useBuscaEquipamentoPorNome(termo: string) {
  return useQuery({
    queryKey: [...CHAVE_BASE, "busca", termo],
    queryFn: () => buscarEquipamentoPorNome(termo),
    enabled: termo.trim().length > 0,
  });
}

export function useCriarEquipamento() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (dados: EquipamentoFormValues): Promise<Equipamento> => criarEquipamento(dados),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: CHAVE_BASE }),
  });
}

export function useEditarEquipamento() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, dados }: { id: number; dados: Partial<EquipamentoFormValues> }) =>
      editarEquipamento(id, dados),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: CHAVE_BASE }),
  });
}

export function useRemoverEquipamento() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => removerEquipamento(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: CHAVE_BASE }),
  });
}
