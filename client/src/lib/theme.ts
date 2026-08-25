const CHAVE_STORAGE = "almoxaf-theme";

export type Tema = "light" | "dark";

export function lerTemaSalvo(): Tema {
  const salvo = localStorage.getItem(CHAVE_STORAGE);
  if (salvo === "light" || salvo === "dark") return salvo;

  // Sem preferência salva ainda — respeita o tema do sistema operacional
  const prefereEscuro = window.matchMedia("(prefers-color-scheme: dark)").matches;
  return prefereEscuro ? "dark" : "light";
}

export function aplicarTema(tema: Tema) {
  document.documentElement.classList.toggle("dark", tema === "dark");
  localStorage.setItem(CHAVE_STORAGE, tema);
}
