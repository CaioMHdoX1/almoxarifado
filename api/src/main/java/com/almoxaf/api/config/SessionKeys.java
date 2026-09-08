package com.almoxaf.api.config;

/** Nomes de atributos guardados na {@link jakarta.servlet.http.HttpSession}. */
public final class SessionKeys {

    /** Guarda o id (Long) do administrador autenticado na sessão atual. */
    public static final String ADMINISTRADOR_ID = "administradorId";

    private SessionKeys() {
    }
}
