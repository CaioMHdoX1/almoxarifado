package com.almoxaf.api.exception;

public class MuitasTentativasException extends ApiException {

    private static final int TOO_MANY_REQUESTS = 429;

    public MuitasTentativasException(String mensagem) {
        super(mensagem, TOO_MANY_REQUESTS);
    }
}
