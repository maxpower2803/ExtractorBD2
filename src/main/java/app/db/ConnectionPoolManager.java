package app.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import app.models.ServerInfo;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * Manages a single HikariDataSource instance at a time.
 * If getDataSourceFor(null) is called, returns the existing pool (or throws if none).
 */
public final class ConnectionPoolManager {

    private static HikariDataSource dataSource;
    private static ServerInfo currentServer;

    private ConnectionPoolManager() { }

    public static synchronized DataSource getDataSourceFor(ServerInfo server) {
        if (server == null) {
            if (dataSource == null) throw new IllegalStateException("No pool initialized. Select a server first.");
            return dataSource;
        }
        Objects.requireNonNull(server, "server");
        if (dataSource != null && server.equals(currentServer)) {
            return dataSource;
        }
        if (dataSource != null) {
            try { dataSource.close(); } catch (Exception ignored) {}
            dataSource = null;
            currentServer = null;
        }
        HikariConfig cfg = new HikariConfig();
        cfg.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String jdbcUrl = String.format("jdbc:sqlserver://%s:%d;databaseName=%s;encrypt=false;", server.getHost(), server.getPort(), server.getDatabase());
        cfg.setJdbcUrl(jdbcUrl);
        cfg.setUsername(server.getUser());
        cfg.setPassword(server.getPassword());
        cfg.setMaximumPoolSize(5);
        cfg.setMinimumIdle(1);
        cfg.setPoolName("ExtractorBDPool-" + server.getId());
        cfg.setConnectionTimeout(10_000);
        cfg.setIdleTimeout(60_000);
        cfg.setMaxLifetime(600_000);
        dataSource = new HikariDataSource(cfg);
        currentServer = server;
        return dataSource;
    }

    public static synchronized void closePool() {
        if (dataSource != null) {
            try { dataSource.close(); } catch (Exception ignored) {}
            dataSource = null;
            currentServer = null;
        }
    }
}
