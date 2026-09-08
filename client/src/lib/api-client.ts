/**
 * Wrapper único sobre `fetch` para toda a API. Centraliza:
 * - a URL base (configurável por variável de ambiente)
 * - `credentials: "include"` (obrigatório para o cookie de sessão ir/vir)
 * - o parse do formato padrão do backend: { data: ... } ou { error: { message, status } }
 */

const BASE_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

export class ApiError extends Error {
  status: number;

  constructor(message: string, status: number) {
    super(message);
    this.name = "ApiError";
    this.status = status;
  }
}

type ApiEnvelope<T> = { data: T } | { error: { message: string; status: number } };

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const resposta = await fetch(`${BASE_URL}${path}`, {
    ...init,
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      ...init?.headers,
    },
  });

  // Corpo pode vir vazio em alguns casos (ex.: erro de rede tratado antes
  // de chegar no backend) — tenta parsear, mas não quebra se não der.
  let corpo: ApiEnvelope<T> | null = null;
  try {
    corpo = await resposta.json();
  } catch {
    // resposta sem corpo JSON — corpo continua null, tratado abaixo
  }

  if (!resposta.ok || !corpo || "error" in corpo) {
    const mensagem = corpo && "error" in corpo ? corpo.error.message : `Erro ${resposta.status}`;
    throw new ApiError(mensagem, resposta.status);
  }

  return corpo.data;
}

export const apiClient = {
  get: <T>(path: string) => request<T>(path, { method: "GET" }),
  post: <T>(path: string, body?: unknown) =>
    request<T>(path, { method: "POST", body: body ? JSON.stringify(body) : undefined }),
  put: <T>(path: string, body?: unknown) =>
    request<T>(path, { method: "PUT", body: body ? JSON.stringify(body) : undefined }),
  delete: <T>(path: string) => request<T>(path, { method: "DELETE" }),
};
