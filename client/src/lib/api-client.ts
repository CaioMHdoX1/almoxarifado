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

  let corpo: ApiEnvelope<T> | null = null;
  try {
    corpo = await resposta.json();
  } catch {}

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
