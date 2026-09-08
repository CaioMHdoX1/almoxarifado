package com.almoxaf.api.filter;

import com.almoxaf.api.util.JsonResponseWriter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Rejeita, antes de qualquer processamento, requisições cujo corpo declare
 * mais que {@link #LIMITE_BYTES}. Nenhum endpoint desta API precisa de
 * payloads grandes (o maior é um formulário de equipamento/usuário — bem
 * menor que 1 KB na prática); um limite de 1 MB já é folgado o bastante
 * para uso legítimo e barra tentativas de esgotar memória/CPU do servidor
 * mandando corpos enormes.
 *
 * <p>Baseado no header {@code Content-Length} — não é uma garantia
 * absoluta (um cliente pode mentir ou usar chunked encoding), mas é uma
 * primeira barreira barata antes mesmo de ler o corpo. O limite do próprio
 * Tomcat ({@code maxPostSize}, 2 MB por padrão) é a segunda camada.</p>
 */
public class RequestSizeLimitFilter implements Filter {

    private static final long LIMITE_BYTES = 1024L * 1024L; // 1 MB

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        long tamanho = req.getContentLengthLong();
        if (tamanho > LIMITE_BYTES) {
            JsonResponseWriter.writeError(resp, "Corpo da requisição excede o limite permitido.",
                    HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
            return;
        }

        chain.doFilter(request, response);
    }
}
