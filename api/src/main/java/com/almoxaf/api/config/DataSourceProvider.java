package com.almoxaf.api.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class DataSourceProvider {

    private static volatile HikariDataSource ds;

    private DataSourceProvider() {
    }

    public static synchronized void init() {
        if (ds != null) {
            return;
        }

        AppConfig cfg = AppConfig.getInstance();

        HikariConfig hc = new HikariConfig();
        hc.setJdbcUrl(cfg.get("db.url"));
        hc.setUsername(cfg.get("db.user"));
        hc.setPassword(cfg.get("db.password"));
        hc.setDriverClassName("org.postgresql.Driver");
        hc.setMaximumPoolSize(cfg.getInt("db.pool.maxSize"));
        hc.setMinimumIdle(cfg.getInt("db.pool.minIdle"));
        hc.setConnectionTimeout(cfg.getLong("db.pool.connectionTimeoutMs"));
        hc.setPoolName("almoxaf-pool");

        ds = new HikariDataSource(hc);
    }

    public static Connection getConnection() throws SQLException {
        if (ds == null) {
            throw new IllegalStateException(
                    "DataSourceProvider não foi inicializado. " +
                    "Verifique se AppContextListener rodou no startup da aplicação.");
        }
        return ds.getConnection();
    }

    public static synchronized void close() {
        if (ds != null) {
            ds.close();
            ds = null;
        }
    }
}