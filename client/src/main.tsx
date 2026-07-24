import { QueryClientProvider } from "@tanstack/react-query";
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { Toaster } from "sonner";
import App from "@/App";
import { queryClient } from "@/lib/query-client";
import "@/styles/index.css";

const rootElement = document.getElementById("root");
if (!rootElement) {
  throw new Error("Elemento #root não encontrado em index.html.");
}

createRoot(rootElement).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <App />
      <Toaster position="top-right" richColors closeButton />
    </QueryClientProvider>
  </StrictMode>,
);
