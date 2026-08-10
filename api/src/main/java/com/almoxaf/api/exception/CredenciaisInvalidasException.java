package com.almoxaf.api.exception;

import jakarta.servlet.http.HttpServletResponse;

public class CredenciaisInvalidasException extends ApiException {

    public CredenciaisInvalidasException(String mensagem) {
        super(mensagem, HttpServletResponse.SC_UNAUTHORIZED);
    }
}
