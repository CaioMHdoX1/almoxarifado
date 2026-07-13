package com.almoxaf.api.config;
 
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
 
import java.sql.Connection;
import java.sql.SQLException;
 

public final class DataSourceProvider {
 
    private static volatile HikariDataSource dataSource;
 
    private DataSourceProvider() {
    }
 
    public static synchronized void init() {
        if (dataSource != null) {
            return;
        }
 
        AppConfig config = AppConfig.getInstance();
 
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.get("db.url"));
        hikariConfig.setUsername(config.get("db.user"));
        hikariConfig.setPassword(config.get("db.password"));
        hikariConfig.setMaximumPoolSize(config.getInt("db.pool.maxSize"));
        hikariConfig.setMinimumIdle(config.getInt("db.pool.minIdle"));
        hikariConfig.setConnectionTimeout(config.getLong("db.pool.connectionTimeoutMs"));
        hikariConfig.setPoolName("almoxaf-pool");
 
        dataSource = new HikariDataSource(hikariConfig);
    }
 
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException(
                    "DataSourceProvider não foi inicializado. " +
                    "Verifique se AppContextListener rodou no startup da aplicação.");
        }
        return dataSource.getConnection();
    }
 
    public static synchronized void close() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }
}