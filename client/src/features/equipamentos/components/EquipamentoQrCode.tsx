import QRCode from "react-qr-code";

type EquipamentoQrCodeProps = {
  codigo: string;
  nome: string;
};

/**
 * O QR code carrega só o texto puro do código (ex.: "PAT-00812") — o mesmo
 * valor que já é digitado manualmente na busca por código. Por isso ler o
 * QR e digitar o código dão exatamente o mesmo resultado.
 */
export function EquipamentoQrCode({ codigo, nome }: EquipamentoQrCodeProps) {
  return (
    <div className="flex flex-col items-center gap-3 rounded-lg border border-border bg-white p-5">
      <QRCode value={codigo} size={168} />
      <div className="text-center">
        <p className="font-mono text-sm font-medium text-foreground">{codigo}</p>
        <p className="text-xs text-muted-foreground">{nome}</p>
      </div>
    </div>
  );
}
