package com.almoxaf.api.filter;

import com.almoxaf.api.util.JsonResponseWriter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

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
