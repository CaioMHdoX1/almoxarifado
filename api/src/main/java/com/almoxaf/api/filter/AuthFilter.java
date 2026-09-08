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

/**
 * Bloqueia acesso a qualquer rota da API que não esteja na lista de rotas
 * públicas, a menos que exista uma sessão válida (usuário logado).
 *
 * <p>Registrado explicitamente no {@code web.xml} (não usa {@code @WebFilter})
 * para garantir que rode <b>depois</b> do {@link CorsFilter} — a ordem dos
 * filtros com anotação não é garantida entre containers, então a ordem real
 * é definida lá.</p>
 */
public class AuthFilter implements Filter {

    // Prefixos de rota que não exigem sessão. Comparação por "startsWith"
    // (ex.: "/api/auth" libera tanto /api/auth/login quanto /api/auth/logout).
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
        Object administradorId = sessao != null ? sessao.getAttribute(SessionKeys.ADMINISTRADOR_ID) : null;

        if (administradorId == null) {
            JsonResponseWriter.writeError(resp, "Sessão inválida ou expirada. Faça login novamente.",
                    HttpServletResponse.SC_UNAUTHORIZED);
            return; // não deixa a requisição seguir adiante
        }

        chain.doFilter(request, response);
    }
}
