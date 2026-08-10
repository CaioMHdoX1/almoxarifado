package com.almoxaf.api.exception;

import jakarta.servlet.http.HttpServletResponse;

public class RecursoNaoEncontradoException extends ApiException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem, HttpServletResponse.SC_NOT_FOUND);
    }
}
