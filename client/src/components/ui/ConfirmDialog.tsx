import type { ReactNode } from "react";
import { AlertDialog } from "@base-ui-components/react/alert-dialog";
import { Button } from "@/components/ui/Button";

type ConfirmDialogProps = {
	trigger: ReactNode;
	titulo: string;
	descricao: string;
	textoConfirmar?: string;
	onConfirmar: () => void;
};

export function ConfirmDialog({
	trigger,
	titulo,
	descricao,
	textoConfirmar = "Confirmar",
	onConfirmar,
}: ConfirmDialogProps) {
	return (
		<AlertDialog.Root>
			<AlertDialog.Trigger render={(props) => <span {...props}>{trigger}</span>} />
			<AlertDialog.Portal>
				<AlertDialog.Backdrop className="fixed inset-0 bg-black/40 data-[state=open]:animate-in data-[state=open]:fade-in" />
				<AlertDialog.Popup className="fixed left-1/2 top-1/2 w-full max-w-sm -translate-x-1/2 -translate-y-1/2 rounded-lg bg-card p-6 shadow-xl">
					<AlertDialog.Title className="text-base font-medium text-foreground">
						{titulo}
					</AlertDialog.Title>
					<AlertDialog.Description className="mt-2 text-sm text-muted-foreground">
						{descricao}
					</AlertDialog.Description>
					<div className="mt-6 flex justify-end gap-2">
						<AlertDialog.Close
							render={(props) => (
								<Button {...props} variante="fantasma">
									Cancelar
								</Button>
							)}
						/>
						<AlertDialog.Close
							render={(props) => (
								<Button {...props} variante="perigo" onClick={onConfirmar}>
									{textoConfirmar}
								</Button>
							)}
						/>
					</div>
				</AlertDialog.Popup>
			</AlertDialog.Portal>
		</AlertDialog.Root>
	);
}
