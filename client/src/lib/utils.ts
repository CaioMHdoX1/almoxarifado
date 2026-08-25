/** Combina classes condicionalmente, ignorando valores falsy. */
export function cn(...classes: Array<string | false | null | undefined>): string {
  return classes.filter(Boolean).join(" ");
}

/** Remove tudo que não for dígito (usado antes de salvar/comparar CPF). */
export function somenteDigitos(valor: string): string {
  return valor.replace(/\D/g, "");
}

/** Formata "11122233344" como "111.222.333-44" para exibição. */
export function formatCpf(cpf: string): string {
  const digitos = somenteDigitos(cpf).padEnd(11, " ").slice(0, 11);
  if (somenteDigitos(cpf).length !== 11) return cpf;
  return digitos.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
}

/** Formata uma data ISO como "13 jul 2026". */
export function formatData(iso: string): string {
  return new Date(iso).toLocaleDateString("pt-BR", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
}
