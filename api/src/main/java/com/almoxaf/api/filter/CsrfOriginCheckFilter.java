package com.almoxaf.api.filter;

import com.almoxaf.api.config.AllowedOrigins;
import com.almoxaf.api.util.JsonResponseWriter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

/**
 * Segunda camada de defesa contra CSRF, além do CORS.
 *
 * <p>Por que precisa de mais isso se já tem CORS? Porque CORS só impede o
 * NAVEGADOR de deixar o JavaScript de outro site LER a resposta — em
 * alguns casos (formulário HTML simples, sem JSON) o navegador ainda
 * ENVIA a requisição com o cookie de sessão, mesmo que depois seja
 * bloqueado de ler a resposta. Como as rotas aqui mudam estado (criar
 * usuário, remover equipamento, etc.), isso já seria um ataque bem
 * sucedido mesmo sem o atacante ver a resposta.</p>
 *
 * <p>Estratégia: para métodos que alteram estado (POST/PUT/DELETE/PATCH),
 * exige que o header {@code Origin} (ou, na ausência dele, o host do
 * {@code Referer}) esteja na lista de origens permitidas. Requisições sem
 * nenhum dos dois headers (ex.: chamadas de servidor-a-servidor, testes
 * via curl/Postman) são deixadas passar — CSRF é um ataque especificamente
 * de NAVEGADOR, então a ausência desses headers indica que não veio de
 * um navegador terceiro tentando o ataque.</p>
 *
 * <p>Registrado explicitamente no {@code web.xml}, depois do
 * {@link CorsFilter} e antes do {@link AuthFilter}.</p>
 */
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

        // Nenhum dos dois headers presente → não é uma requisição de
        // navegador cross-site; deixa passar (ex.: curl, testes, chamadas
        // internas de servidor).
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
            return null; // Referer malformado — trata como ausente
        }
    }
}
