import { ArrowLeft, PackageMinus, PackagePlus, Users } from "lucide-react";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { useRelatorioDetalhe } from "@/features/relatorios/api";
import { formatData } from "@/lib/utils";

type RelatorioDetalheViewProps = {
  id: number;
  aoVoltar: () => void;
};

export function RelatorioDetalheView({ id, aoVoltar }: RelatorioDetalheViewProps) {
  const { data: relatorio, isLoading, isError } = useRelatorioDetalhe(id);

  return (
    <div className="flex flex-col gap-4">
      <Button
        type="button" // Biome exige isso para evitar comportamentos inesperados de submit
        variante="fantasma"
        icone={<ArrowLeft className="h-4 w-4" />}
        onClick={aoVoltar}
        className="self-start"
      >
        Voltar para a lista
      </Button>

      {isLoading && <p className="text-sm text-muted-foreground">Carregando...</p>}
      {isError && (
        <p className="text-sm text-destructive">Não foi possível carregar esse relatório.</p>
      )}

      {relatorio && (
        <>
          <Card>
            <h3 className="font-serif text-lg text-foreground">
              Período: {formatData(relatorio.periodoInicio)} até {formatData(relatorio.periodoFim)}
            </h3>
            <p className="mt-1 text-sm text-muted-foreground">
              Gerado em {formatData(relatorio.geradoEm)}
            </p>
          </Card>

          <Card>
            <div className="mb-3 flex items-center gap-2">
              <PackagePlus className="h-4 w-4 text-emerald-700" />
              <h4 className="font-medium text-foreground">
                Entregas no período ({relatorio.entregas.length})
              </h4>
            </div>
            {relatorio.entregas.length === 0 ? (
              <p className="text-sm text-muted-foreground">Nenhuma entrega registrada.</p>
            ) : (
              <ul className="flex flex-col gap-2 text-sm">
                {/* Removido o 'i' e criada uma key com dados únicos (código + data) */}
                {relatorio.entregas.map((mov) => (
                  <li
                    key={`entrega-${mov.equipamentoCodigo}-${mov.data}`}
                    className="flex flex-wrap gap-x-2"
                  >
                    <span className="font-medium text-foreground">{mov.equipamentoNome}</span>
                    <span className="text-muted-foreground">
                      ({mov.equipamentoCodigo}) → {mov.usuarioNome} em {formatData(mov.data)}
                    </span>
                  </li>
                ))}
              </ul>
            )}
          </Card>

          <Card>
            <div className="mb-3 flex items-center gap-2">
              <PackageMinus className="h-4 w-4 text-[var(--accent)]" />
              <h4 className="font-medium text-foreground">
                Devoluções no período ({relatorio.devolucoes.length})
              </h4>
            </div>
            {relatorio.devolucoes.length === 0 ? (
              <p className="text-sm text-muted-foreground">Nenhuma devolução registrada.</p>
            ) : (
              <ul className="flex flex-col gap-2 text-sm">
                {/* Mesma lógica de key aplicada aqui */}
                {relatorio.devolucoes.map((mov) => (
                  <li
                    key={`devolucao-${mov.equipamentoCodigo}-${mov.data}`}
                    className="flex flex-wrap gap-x-2"
                  >
                    <span className="font-medium text-foreground">{mov.equipamentoNome}</span>
                    <span className="text-muted-foreground">
                      ({mov.equipamentoCodigo}) ← {mov.usuarioNome} em {formatData(mov.data)}
                    </span>
                  </li>
                ))}
              </ul>
            )}
          </Card>

          <Card>
            <div className="mb-3 flex items-center gap-2">
              <Users className="h-4 w-4 text-muted-foreground" />
              <h4 className="font-medium text-foreground">
                Quem estava com o quê nessa data ({relatorio.posseAtual.length})
              </h4>
            </div>
            {relatorio.posseAtual.length === 0 ? (
              <p className="text-sm text-muted-foreground">Nenhum equipamento alocado.</p>
            ) : (
              <ul className="flex flex-col gap-2 text-sm">
                {/* Key combinando código e usuário para garantir unicidade na posse */}
                {relatorio.posseAtual.map((item) => (
                  <li
                    key={`posse-${item.equipamentoCodigo}-${item.usuarioNome}`}
                    className="flex flex-wrap gap-x-2"
                  >
                    <span className="font-medium text-foreground">{item.equipamentoNome}</span>
                    <span className="text-muted-foreground">
                      ({item.equipamentoCodigo}) com {item.usuarioNome}
                    </span>
                  </li>
                ))}
              </ul>
            )}
          </Card>
        </>
      )}
    </div>
  );
}
