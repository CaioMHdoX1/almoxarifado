package com.almoxaf.api.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Adiciona headers HTTP de segurança em toda resposta da API. É o
 * PRIMEIRO filtro da cadeia (roda antes até do CORS) para garantir que os
 * headers saiam mesmo em respostas de erro/bloqueio geradas por outros
 * filtros.
 *
 * <p>Cada header existe para mitigar uma classe de ataque específica:</p>
 * <ul>
 *   <li>{@code X-Content-Type-Options: nosniff} — impede o navegador de
 *       tentar "adivinhar" o tipo de conteúdo e executar algo que não é
 *       JSON como se fosse (MIME sniffing).</li>
 *   <li>{@code X-Frame-Options: DENY} — impede que a API seja embutida em
 *       um {@code <iframe>} de outro site (clickjacking).</li>
 *   <li>{@code Referrer-Policy: no-referrer} — não vaza a URL completa
 *       (que pode conter dados sensíveis em query params) para terceiros.</li>
 *   <li>{@code Cache-Control: no-store} — respostas da API (que podem
 *       conter dados de usuário) nunca devem ficar em cache do navegador
 *       ou de proxies intermediários.</li>
 *   <li>{@code Content-Security-Policy: default-src 'none'} — a API só
 *       serve JSON, nunca HTML/JS; essa política deixa isso explícito e
 *       neutraliza XSS caso, por algum bug, HTML acabe sendo refletido.</li>
 * </ul>
 *
 * <p>{@code Strict-Transport-Security} (HSTS) fica de fora de propósito:
 * só faz sentido quando a API já está atrás de HTTPS de verdade (ver nota
 * de produção no README) — declarar HSTS sem HTTPS ativo não tem efeito e
 * pode confundir quem for configurar o domínio depois.</p>
 */
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;

        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("X-Frame-Options", "DENY");
        resp.setHeader("Referrer-Policy", "no-referrer");
        resp.setHeader("Cache-Control", "no-store");
        resp.setHeader("Content-Security-Policy", "default-src 'none'");

        chain.doFilter(request, response);
    }
}
