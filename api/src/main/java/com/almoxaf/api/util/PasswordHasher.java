package com.almoxaf.api.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public final class PasswordHasher {

    private static final int ITERACOES = 210_000;
    private static final int TAMANHO_SALT_BYTES = 16;
    private static final int TAMANHO_HASH_BITS = 256;
    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";

    private PasswordHasher() {
    }

    public static String hash(String senhaPlana) {
        byte[] salt = new byte[TAMANHO_SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        byte[] hash = derivarHash(senhaPlana.toCharArray(), salt, ITERACOES);
        return ITERACOES + ":" + codificar(salt) + ":" + codificar(hash);
    }

    public static boolean verificar(String senhaPlana, String hashArmazenado) {
        String[] partes = hashArmazenado.split(":");
        if (partes.length != 3) {
            throw new IllegalArgumentException("Formato de hash inválido.");
        }
        int iteracoes = Integer.parseInt(partes[0]);
        byte[] salt = decodificar(partes[1]);
        byte[] hashEsperado = decodificar(partes[2]);

        byte[] hashCalculado = derivarHash(senhaPlana.toCharArray(), salt, iteracoes);
        return constantTimeEquals(hashEsperado, hashCalculado);
    }

    private static byte[] derivarHash(char[] senha, byte[] salt, int iteracoes) {
        try {
            PBEKeySpec spec = new PBEKeySpec(senha, salt, iteracoes, TAMANHO_HASH_BITS);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITMO);
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Falha ao gerar hash de senha.", e);
        }
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int resultado = 0;
        for (int i = 0; i < a.length; i++) {
            resultado |= a[i] ^ b[i];
        }
        return resultado == 0;
    }

    private static String codificar(byte[] valor) {
        return Base64.getEncoder().encodeToString(valor);
    }

    private static byte[] decodificar(String valor) {
        return Base64.getDecoder().decode(valor);
    }
}
