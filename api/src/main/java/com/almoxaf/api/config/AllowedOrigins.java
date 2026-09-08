package com.almoxaf.api.config;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Lê e interpreta a variável de ambiente {@code CLIENT_ORIGIN} (lista de
 * origens separadas por vírgula). Usado tanto pelo {@code CorsFilter}
 * quanto pelo {@code CsrfOriginCheckFilter} — os dois precisam da mesma
 * lista, então ela é lida uma única vez aqui.
 */
public final class AllowedOrigins {

    private static final String PADRAO = "http://localhost:5173,http://localhost:3000";

    private static final Set<String> ORIGENS = carregar();

    private AllowedOrigins() {
    }

    public static boolean contem(String origem) {
        return origem != null && ORIGENS.contains(origem);
    }

    public static Set<String> todas() {
        return ORIGENS;
    }

    private static Set<String> carregar() {
        String fromEnv = System.getenv("CLIENT_ORIGIN");
        String valor = (fromEnv != null && !fromEnv.isBlank()) ? fromEnv : PADRAO;

        return Arrays.stream(valor.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }
}
