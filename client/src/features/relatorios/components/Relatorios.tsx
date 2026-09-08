import { useState } from "react";
import { RelatorioDetalheView } from "@/features/relatorios/components/RelatorioDetalheView";
import { RelatorioLista } from "@/features/relatorios/components/RelatorioLista";

export function Relatorios() {
  const [idSelecionado, setIdSelecionado] = useState<number | null>(null);

  if (idSelecionado !== null) {
    return <RelatorioDetalheView id={idSelecionado} aoVoltar={() => setIdSelecionado(null)} />;
  }

  return <RelatorioLista aoSelecionar={setIdSelecionado} />;
}
