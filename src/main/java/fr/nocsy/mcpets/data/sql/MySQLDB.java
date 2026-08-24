package fr.nocsy.mcpets.data.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.GlobalConfig;

import java.sql.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class MySQLDB {

    private HikariDataSource dataSource;
    private String user;
    private String pass;
    private String ip;
    private String port;
    private String db;

    public MySQLDB(String user, String pass, String ip, String port, String db) {
        this.user = user;
        this.pass = pass;
        this.ip = ip;
        this.port = port;
        this.db = db;
    }

    public boolean init() {
        if (this.user == null || this.pass == null || this.ip == null || this.port == null || this.db == null) {
            MCPets.getInstance().getLogger().severe("Missing SQL parameter.");
            MCPets.getInstance().getLogger().severe("User : " + user);
            MCPets.getInstance().getLogger().severe("Pass : " + pass);
            MCPets.getInstance().getLogger().severe("Host : " + ip);
            MCPets.getInstance().getLogger().severe("Port : " + port);
            MCPets.getInstance().getLogger().severe("DB : " + db);
            return false;
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(urlBuilder());
            config.setUsername(this.user);
            config.setPassword(this.pass);

            // Pool configuration optimized for Folia's multi-threaded environment
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(30));
            config.setIdleTimeout(TimeUnit.MINUTES.toMillis(10));
            config.setMaxLifetime(TimeUnit.MINUTES.toMillis(30));

            // Connection validation
            config.setConnectionTestQuery("SELECT 1");
            config.setValidationTimeout(TimeUnit.SECONDS.toMillis(5));

            // Pool name for monitoring
            config.setPoolName("MCPets-MySQL-Pool");

            // Performance optimizations
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");

            this.dataSource = new HikariDataSource(config);
            MCPets.getInstance().getLogger().info("[Database] HikariCP connection pool initialized successfully.");
        }
        catch (Exception e) {
            MCPets.getInstance().getLogger().severe("Could not reach SQL database. Please configure your database parameters.");
            MCPets.getInstance().getLogger().log(Level.SEVERE, "Database initialization error", e);
            return false;
        }
        return true;
    }

    public void close() {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return;
        try {
            if (this.dataSource != null && !this.dataSource.isClosed()) {
                this.dataSource.close();
                MCPets.getInstance().getLogger().info("[Database] HikariCP connection pool closed.");
            }
        }
        catch (Exception e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "Failed to close SQL connection pool", e);
        }
    }

    public String urlBuilder() {
        return "jdbc:mysql://" + this.ip + ":" + this.port + "/" + this.db;
    }

    public ResultSet query(String s) {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return null;

        try (Connection conn = dataSource.getConnection()) {
            try (Statement stat = conn.createStatement()) {
                if (s.toLowerCase().startsWith("select")) {
                    // For SELECT queries, we need to return the ResultSet
                    // The caller is responsible for closing it
                    return stat.executeQuery(s);
                } else {
                    stat.executeUpdate(s);
                    return null;
                }
            }
        } catch (SQLException e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "SQL query failed: " + s, e);
        }
        return null;
    }

    public ResultSet preparedQuery(String sql, Object... params) {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return null;

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
                if (sql.trim().toLowerCase().startsWith("select")) {
                    // For SELECT queries, we need to return the ResultSet
                    // The caller is responsible for closing it
                    return pstmt.executeQuery();
                } else {
                    pstmt.executeUpdate();
                    return null;
                }
            }
        } catch (SQLException e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "SQL prepared query failed: " + sql, e);
        }
        return null;
    }
}
