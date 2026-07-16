package com.almoxaf.api.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class DataSourceProvider {

    private static volatile HikariDataSource ds;

    private DataSourceProvider() {}

    public static synchronized void init() {
        if (ds != null) {
            return;
        }

        AppConfig cfg = AppConfig.getInstance();
        HikariConfig hCfg = new HikariConfig();

        hCfg.setJdbcUrl(cfg.get("db.url"));
        hCfg.setUsername(cfg.get("db.user"));
        hCfg.setPassword(cfg.get("db.password"));
        hCfg.setMaximumPoolSize(cfg.getInt("db.pool.maxSize"));
        hCfg.setMinimumIdle(cfg.getInt("db.pool.minIdle"));
        hCfg.setConnectionTimeout(cfg.getLong("db.pool.connectionTimeoutMs"));
        hCfg.setPoolName("almoxaf-pool");

        ds = new HikariDataSource(hCfg);
    }

    public static Connection getConnection() throws SQLException {
        if (ds == null) {
            throw new IllegalStateException("DataSourceProvider não inicializado.");
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