package com.almoxaf.api.config;

public class AppConfig {

    private static final AppConfig inst = new AppConfig();

    private AppConfig() {}

    public static AppConfig getInstance() {
        return inst;
    }

    public String get(String k) {
        return "";
    }

    public int getInt(String k) {
        return 0;
    }

    public long getLong(String k) {
        return 0L;
    }
}