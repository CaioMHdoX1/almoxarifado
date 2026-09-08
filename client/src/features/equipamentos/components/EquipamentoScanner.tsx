import { type IDetectedBarcode, Scanner } from "@yudiel/react-qr-scanner";
import { CameraOff, RotateCcw } from "lucide-react";
import { useState } from "react";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { StatusBadge } from "@/components/ui/StatusBadge";
import { useEquipamentoPorCodigo } from "@/features/equipamentos/api";

export function EquipamentoScanner() {
  const [codigoLido, setCodigoLido] = useState<string | null>(null);
  const [erroCamera, setErroCamera] = useState<string | null>(null);

  const { data: equipamento, isFetching, isError } = useEquipamentoPorCodigo(codigoLido ?? "");

  function aoDetectar(resultados: IDetectedBarcode[]) {
    const primeiro = resultados[0];
    if (primeiro && primeiro.rawValue !== codigoLido) {
      setCodigoLido(primeiro.rawValue);
    }
  }

  function escanearNovamente() {
    setCodigoLido(null);
  }

  // Já leu um código — mostra o resultado em vez da câmera (evita ficar
  // escaneando em loop o mesmo QR parado na frente da lente).
  if (codigoLido) {
    return (
      <div className="flex max-w-lg flex-col gap-4">
        <Card>
          <p className="text-xs uppercase tracking-wide text-muted-foreground">Código lido</p>
          <p className="mb-4 font-mono text-sm text-foreground">{codigoLido}</p>

          {isFetching && <p className="text-sm text-muted-foreground">Buscando...</p>}

          {isError && (
            <p className="text-sm text-destructive">
              Nenhum equipamento encontrado com esse código.
            </p>
          )}

          {equipamento && (
            <div className="flex flex-col gap-2 border-t border-border pt-4">
              <div className="flex flex-wrap items-center justify-between gap-2">
                <h3 className="font-serif text-lg text-foreground">{equipamento.nome}</h3>
                <StatusBadge status={equipamento.status} />
              </div>
              <p className="text-sm text-muted-foreground">Marca: {equipamento.marca}</p>
              <p className="text-sm text-muted-foreground">
                Com quem está: {equipamento.usuarioAtual?.nome ?? "Disponível — ninguém no momento"}
              </p>
            </div>
          )}
        </Card>

        <Button
          variante="secundaria"
          icone={<RotateCcw className="h-4 w-4" />}
          onClick={escanearNovamente}
          className="self-start"
        >
          Ler outro QR code
        </Button>
      </div>
    );
  }

  return (
    <div className="flex max-w-lg flex-col gap-4">
      <p className="text-sm text-muted-foreground">
        Aponte a câmera para o QR code colado no equipamento.
      </p>

      <div className="overflow-hidden rounded-lg border border-border">
        <Scanner
          onScan={aoDetectar}
          onError={(erro) => setErroCamera(erro instanceof Error ? erro.message : String(erro))}
          formats={["qr_code"]}
          constraints={{ facingMode: "environment" }}
          components={{ finder: true }}
        />
      </div>

      {erroCamera && (
        <Card className="flex items-start gap-3">
          <CameraOff className="mt-0.5 h-5 w-5 shrink-0 text-destructive" />
          <div>
            <p className="text-sm font-medium text-foreground">Não foi possível acessar a câmera</p>
            <p className="mt-1 text-sm text-muted-foreground">
              Verifique se você deu permissão de câmera pro navegador, e se está acessando por HTTPS
              ou "localhost" — navegadores bloqueiam a câmera em conexões HTTP comuns por segurança.
            </p>
          </div>
        </Card>
      )}
    </div>
  );
}
