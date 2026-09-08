import { FileText, RefreshCw } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { useGerarRelatorio, useRelatorios } from "@/features/relatorios/api";
import { formatData } from "@/lib/utils";

type RelatorioListaProps = {
  aoSelecionar: (id: number) => void;
};

export function RelatorioLista({ aoSelecionar }: RelatorioListaProps) {
  const { data: relatorios, isLoading } = useRelatorios();
  const gerarRelatorio = useGerarRelatorio();

  async function aoGerarAgora() {
    try {
      await gerarRelatorio.mutateAsync();
      toast.success("Relatório gerado com sucesso.");
    } catch (erro) {
      const mensagem = erro instanceof Error ? erro.message : "Não foi possível gerar o relatório.";
      toast.error(mensagem);
    }
  }

  return (
    <div className="flex flex-col gap-4">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <p className="text-sm text-muted-foreground">
          Um relatório é gerado automaticamente a cada 15 dias. Você também pode gerar um agora,
          cobrindo os últimos 15 dias a partir de hoje.
        </p>
        <Button
          icone={<RefreshCw className="h-4 w-4" />}
          onClick={aoGerarAgora}
          disabled={gerarRelatorio.isPending}
          className="shrink-0"
        >
          {gerarRelatorio.isPending ? "Gerando..." : "Gerar agora"}
        </Button>
      </div>

      {isLoading && <p className="text-sm text-muted-foreground">Carregando...</p>}

      {!isLoading && relatorios?.length === 0 && (
        <p className="text-sm text-muted-foreground">
          Nenhum relatório gerado ainda — clique em "Gerar agora" pra criar o primeiro.
        </p>
      )}

      <div className="flex flex-col gap-2">
        {relatorios?.map((relatorio) => (
          <button key={relatorio.id} type="button" onClick={() => aoSelecionar(relatorio.id)}>
            <Card className="flex flex-row items-center justify-between gap-3 text-left transition-shadow hover:shadow-md">
              <div className="flex items-center gap-3">
                <FileText className="h-5 w-5 shrink-0 text-primary" />
                <div>
                  <p className="font-medium text-foreground">
                    {formatData(relatorio.periodoInicio)} até {formatData(relatorio.periodoFim)}
                  </p>
                  <p className="text-xs text-muted-foreground">
                    Gerado em {formatData(relatorio.geradoEm)}
                  </p>
                </div>
              </div>
              <div className="flex shrink-0 gap-2 text-xs">
                <span className="rounded-full bg-emerald-100 px-2.5 py-1 font-medium text-emerald-800">
                  {relatorio.totalEntregas} entregas
                </span>
                <span className="rounded-full bg-secondary px-2.5 py-1 font-medium text-secondary-foreground">
                  {relatorio.totalDevolucoes} devoluções
                </span>
              </div>
            </Card>
          </button>
        ))}
      </div>
    </div>
  );
}
