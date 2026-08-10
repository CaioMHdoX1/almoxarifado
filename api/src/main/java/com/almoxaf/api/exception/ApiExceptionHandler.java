package com.almoxaf.api.exception;

import com.almoxaf.api.util.JsonResponseWriter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ApiExceptionHandler {

    private static final Logger LOG = Logger.getLogger(ApiExceptionHandler.class.getName());

    private ApiExceptionHandler() {
    }

    public static void responder(HttpServletResponse resp, Exception e) throws IOException {
        if (e instanceof ApiException apiException) {
            JsonResponseWriter.writeError(resp, apiException.getMessage(), apiException.getHttpStatus());
            return;
        }

        LOG.log(Level.SEVERE, "Erro não tratado ao processar requisição", e);
        JsonResponseWriter.writeError(
                resp,
                "Ocorreu um erro interno. Tente novamente mais tarde.",
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
