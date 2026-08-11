package com.almoxaf.api.filter;

import com.almoxaf.api.config.AllowedOrigins;
import com.almoxaf.api.util.JsonResponseWriter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

public class CsrfOriginCheckFilter implements Filter {

    private static final Set<String> METODOS_QUE_ALTERAM_ESTADO =
            Set.of("POST", "PUT", "DELETE", "PATCH");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if (!METODOS_QUE_ALTERAM_ESTADO.contains(req.getMethod().toUpperCase())) {
            chain.doFilter(request, response);
            return;
        }

        String origem = req.getHeader("Origin");
        if (origem == null) {
            origem = origemDoReferer(req.getHeader("Referer"));
        }

        if (origem == null) {
            chain.doFilter(request, response);
            return;
        }

        if (!AllowedOrigins.contem(origem)) {
            JsonResponseWriter.writeError(resp,
                    "Requisição bloqueada: origem não permitida.",
                    HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }

    private String origemDoReferer(String referer) {
        if (referer == null || referer.isBlank()) return null;
        try {
            URI uri = URI.create(referer);
            return uri.getScheme() + "://" + uri.getAuthority();
        } catch (IllegalArgumentException e) {
            return null; 
        }
    }
}
