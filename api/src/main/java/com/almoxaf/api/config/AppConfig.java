package com.almoxaf.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AppConfig {

    private static final Pattern PLACEHOLDER =
            Pattern.compile("\\$\\{([A-Z0-9_]+)(?::([^}]*))?}");

    private static final AppConfig INSTANCE = new AppConfig();

    private final Properties properties = new Properties();

    private AppConfig() {
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (in == null) {
                throw new IllegalStateException(
                        "application.properties não encontrado no classpath");
            }
            properties.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao carregar application.properties", e);
        }
    }

    public static AppConfig getInstance() {
        return INSTANCE;
    }

    /** Retorna o valor já resolvido (variável de ambiente ou valor padrão do arquivo). */
    public String get(String key) {
        String raw = properties.getProperty(key);
        if (raw == null) {
            throw new IllegalArgumentException("Propriedade não encontrada: " + key);
        }
        return resolve(raw);
    }

    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public long getLong(String key) {
        return Long.parseLong(get(key));
    }

    private String resolve(String rawValue) {
        Matcher matcher = PLACEHOLDER.matcher(rawValue);
        if (!matcher.matches()) {
            // Valor literal, sem placeholder ${VAR:default}
            return rawValue;
        }
        String envVarName = matcher.group(1);
        String defaultValue = matcher.group(2);
        String envValue = System.getenv(envVarName);
        return (envValue != null && !envValue.isBlank()) ? envValue : defaultValue;
    }
}
