package com.almoxaf.api.exception;

public class RegraDeNegocioException extends ApiException {

    private static final int UNPROCESSABLE_ENTITY = 422;

    public RegraDeNegocioException(String mensagem) {
        super(mensagem, UNPROCESSABLE_ENTITY);
    }
}
