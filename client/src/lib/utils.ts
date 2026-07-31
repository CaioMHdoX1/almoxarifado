export function cn(...classes: Array<string | false | null | undefined>): string {
  return classes.filter(Boolean).join(" ");
}

export function somenteDigitos(valor: string): string {
  return valor.replace(/\D/g, "");
}

export function formatCpf(cpf: string): string {
  const digitos = somenteDigitos(cpf).padEnd(11, " ").slice(0, 11);
  if (somenteDigitos(cpf).length !== 11) return cpf;
  return digitos.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
}

export function formatData(iso: string): string {
  return new Date(iso).toLocaleDateString("pt-BR", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
}
