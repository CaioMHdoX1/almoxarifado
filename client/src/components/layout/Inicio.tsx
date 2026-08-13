import { PackagePlus, PackageSearch, QrCode, UserPlus, UserSearch } from "lucide-react";
import type { PaginaId } from "@/components/layout/Sidebar";
import { Card } from "@/components/ui/Card";

const ATALHOS: Array<{ id: PaginaId; label: string; icone: typeof UserPlus; descricao: string }> = [
  {
    id: "usuarios-adicionar",
    label: "Adicionar usuário",
    icone: UserPlus,
    descricao: "Cadastrar nome, CPF e projeto associado",
  },
  {
    id: "usuarios-consultar",
    label: "Consultar usuário",
    icone: UserSearch,
    descricao: "Ver todos os equipamentos alocados a alguém",
  },
  {
    id: "equipamentos-adicionar",
    label: "Adicionar equipamento",
    icone: PackagePlus,
    descricao: "Cadastrar um novo item — gera o QR code na hora",
  },
  {
    id: "equipamentos-consultar",
    label: "Consultar equipamento",
    icone: PackageSearch,
    descricao: "Ver totais de disponíveis e alocados por nome",
  },
  {
    id: "equipamentos-ler-qr",
    label: "Ler QR code",
    icone: QrCode,
    descricao: "Aponte a câmera e consulte o item na hora",
  },
];

export function Inicio({ aoNavegar }: { aoNavegar: (pagina: PaginaId) => void }) {
  return (
    <div className="flex flex-col gap-6">
      <p className="max-w-2xl text-sm text-muted-foreground">
        Bem-vindo ao Almoxaf. Use os atalhos abaixo ou o menu lateral para gerenciar usuários e
        equipamentos.
      </p>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        {ATALHOS.map((atalho) => {
          const Icone = atalho.icone;
          return (
            <button
              key={atalho.id}
              type="button"
              onClick={() => aoNavegar(atalho.id)}
              className="text-left"
            >
              <Card className="h-full transition-shadow hover:shadow-md">
                <div className="flex items-start gap-3">
                  <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-md bg-secondary text-primary">
                    <Icone className="h-5 w-5" />
                  </div>
                  <div>
                    <h3 className="font-medium text-foreground">{atalho.label}</h3>
                    <p className="mt-1 text-sm text-muted-foreground">{atalho.descricao}</p>
                  </div>
                </div>
              </Card>
            </button>
          );
        })}
      </div>
    </div>
  );
}
