import { Laptop, Search } from "lucide-react";
import { useState } from "react";
import { Card } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { useBuscaUsuarioPorNome } from "@/features/usuarios/api";
import { formatCpf } from "@/lib/utils";

export function UsuarioBusca() {
  const [termo, setTermo] = useState("");
  const { data: resultados, isFetching } = useBuscaUsuarioPorNome(termo);

  return (
    <div className="flex flex-col gap-6">
      <div className="relative max-w-lg">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          placeholder="Buscar por nome do usuário..."
          value={termo}
          onChange={(e) => setTermo(e.target.value)}
          className="pl-9"
        />
      </div>

      {termo.trim() === "" && (
        <p className="text-sm text-muted-foreground">
          Digite um nome para ver os equipamentos alocados a esse usuário.
        </p>
      )}

      {isFetching && <p className="text-sm text-muted-foreground">Buscando...</p>}

      {!isFetching && termo.trim() !== "" && resultados?.length === 0 && (
        <p className="text-sm text-muted-foreground">Nenhum usuário encontrado com esse nome.</p>
      )}

      <div className="flex flex-col gap-4">
        {resultados?.map(({ usuario, equipamentos }) => (
          <Card key={usuario.id}>
            <div className="flex items-start justify-between">
              <div>
                <h3 className="font-serif text-lg text-foreground">{usuario.nome}</h3>
                <p className="text-sm text-muted-foreground">
                  CPF {formatCpf(usuario.cpf)} · Projeto {usuario.projeto}
                </p>
              </div>
              <span className="rounded-full bg-secondary px-3 py-1 text-xs font-medium text-secondary-foreground">
                {equipamentos.length} equipamento{equipamentos.length !== 1 && "s"}
              </span>
            </div>

            {equipamentos.length > 0 ? (
              <ul className="mt-4 flex flex-col gap-2 border-t border-border pt-4">
                {equipamentos.map((equipamento) => (
                  <li key={equipamento.id} className="flex items-center gap-3 text-sm">
                    <Laptop className="h-4 w-4 shrink-0 text-muted-foreground" />
                    <span className="font-medium text-foreground">{equipamento.nome}</span>
                    <span className="text-muted-foreground">
                      · {equipamento.codigo} · {equipamento.marca}
                    </span>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="mt-4 border-t border-border pt-4 text-sm text-muted-foreground">
                Nenhum equipamento alocado no momento.
              </p>
            )}
          </Card>
        ))}
      </div>
    </div>
  );
}
