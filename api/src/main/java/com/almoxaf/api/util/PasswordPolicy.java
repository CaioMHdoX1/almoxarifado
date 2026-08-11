package com.almoxaf.api.util;

import com.almoxaf.api.exception.RegraDeNegocioException;

import java.util.Set;

public final class PasswordPolicy {

    private static final int TAMANHO_MINIMO = 8;

    private static final Set<String> SENHAS_FRACAS_COMUNS = Set.of(
            "12345678", "123456789", "password", "senha123", "qwerty123", "11111111"
    );

    private PasswordPolicy() {
    }

    public static void validar(String senha) {
        if (senha == null || senha.length() < TAMANHO_MINIMO) {
            throw new RegraDeNegocioException("A senha deve ter pelo menos 8 caracteres.");
        }
        if (SENHAS_FRACAS_COMUNS.contains(senha.toLowerCase())) {
            throw new RegraDeNegocioException("Essa senha é muito comum/fraca. Escolha outra.");
        }

        boolean temLetra = senha.chars().anyMatch(Character::isLetter);
        boolean temNumero = senha.chars().anyMatch(Character::isDigit);
        if (!temLetra || !temNumero) {
            throw new RegraDeNegocioException("A senha deve conter pelo menos uma letra e um número.");
        }
    }
}
