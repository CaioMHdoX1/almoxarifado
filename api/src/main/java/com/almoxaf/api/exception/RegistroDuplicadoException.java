package com.almoxaf.api.exception;

import jakarta.servlet.http.HttpServletResponse;

public class RegistroDuplicadoException extends ApiException {

    public RegistroDuplicadoException(String mensagem) {
        super(mensagem, HttpServletResponse.SC_CONFLICT);
    }
}
