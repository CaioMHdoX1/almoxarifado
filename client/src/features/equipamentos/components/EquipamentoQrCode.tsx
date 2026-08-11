import QRCode from "react-qr-code";

type EquipamentoQrCodeProps = {
  codigo: string;
  nome: string;
};

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
