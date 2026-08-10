package com.almoxaf.api.filter;

import com.almoxaf.api.config.SessionKeys;
import com.almoxaf.api.util.JsonResponseWriter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

public class AuthFilter implements Filter {

    private static final List<String> ROTAS_PUBLICAS = List.of(
            "/api/health",
            "/api/auth/login"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String caminho = req.getRequestURI().substring(req.getContextPath().length());

        boolean rotaPublica = ROTAS_PUBLICAS.stream().anyMatch(caminho::startsWith);
        if (rotaPublica) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession sessao = req.getSession(false);
        Object usuarioId = sessao != null ? sessao.getAttribute(SessionKeys.USUARIO_ID) : null;

        if (usuarioId == null) {
            JsonResponseWriter.writeError(resp, "Sessão inválida ou expirada. Faça login novamente.",
                    HttpServletResponse.SC_UNAUTHORIZED);
            return; 
        }

        chain.doFilter(request, response);
    }
}
