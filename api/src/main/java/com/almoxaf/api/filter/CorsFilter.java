package com.almoxaf.api.filter;

import com.almoxaf.api.config.AllowedOrigins;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Libera chamadas do frontend (rodando em outra porta/host) para a API.
 *
 * <p>{@code Access-Control-Allow-Credentials: true} é obrigatório porque a
 * autenticação usa cookie de sessão. Como a resposta leva credentials,
 * {@code Access-Control-Allow-Origin} não pode ser {@code *} — precisa ser
 * a origem exata que fez a requisição, refletida de volta só se estiver na
 * lista de {@link AllowedOrigins}.</p>
 *
 * <p>Registrado explicitamente no {@code web.xml} (não usa {@code @WebFilter})
 * — é o PRIMEIRO da cadeia: precisa responder o preflight (OPTIONS) antes
 * de qualquer outro filtro (headers de segurança, CSRF, sessão).</p>
 */
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String origem = req.getHeader("Origin");
        if (AllowedOrigins.contem(origem)) {
            resp.setHeader("Access-Control-Allow-Origin", origem);
            resp.setHeader("Access-Control-Allow-Credentials", "true");
            resp.setHeader("Vary", "Origin");
        }
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // Requisição de preflight — responde direto, sem passar pro resto da cadeia
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        chain.doFilter(request, response);
    }
}
