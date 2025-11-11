package app.db;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * Retrieves metadata: databases, tables, and columns.
 */
public final class DatabaseMetadataDao {

    /**
     * Lists all databases in the SQL Server instance.
     */
    public List<String> listDatabases(DataSource ds) throws SQLException {
        List<String> databases = new ArrayList<>();
        String sql = "SELECT name FROM sys.databases ORDER BY name";

        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                databases.add(rs.getString("name"));
            }
        }

        return databases;
    }

    /**
     * Lists all base tables in the specified database.
     */
    public List<String> listTables(DataSource ds, String database) throws SQLException {
        List<String> tables = new ArrayList<>();
        String sql = String.format(
            "SELECT TABLE_SCHEMA, TABLE_NAME FROM %s.INFORMATION_SCHEMA.TABLES " +
            "WHERE TABLE_TYPE = 'BASE TABLE' ORDER BY TABLE_SCHEMA, TABLE_NAME",
            database
        );

        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String schema = rs.getString("TABLE_SCHEMA");
                String table = rs.getString("TABLE_NAME");
                tables.add(schema + "." + table);
            }
        }

        return tables;
    }

    /**
     * Lists all columns for a given table in the specified database and schema.
     */
    public List<String> listColumns(DataSource ds, String database, String schema, String table) throws SQLException {
        List<String> columns = new ArrayList<>();
        String sql = String.format(
            "SELECT COLUMN_NAME FROM %s.INFORMATION_SCHEMA.COLUMNS " +
            "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? ORDER BY ORDINAL_POSITION",
            database
        );

        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, schema);
            stmt.setString(2, table);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    columns.add(rs.getString("COLUMN_NAME"));
                }
            }
        }

        return columns;
    }

    /**
     * Lists all columns for all tables in the specified database.
     * Returns a map: table name → list of column names.
     */
    public Map<String, List<String>> listAllTableColumns(DataSource ds, String database) throws SQLException {
        Map<String, List<String>> tableColumns = new LinkedHashMap<>();
        List<String> tables = listTables(ds, database);

        for (String fullTableName : tables) {
            String[] parts = fullTableName.split("\\.");
            if (parts.length != 2) continue;

            String schema = parts[0];
            String table = parts[1];
            List<String> columns = listColumns(ds, database, schema, table);
            tableColumns.put(fullTableName, columns);
        }

        return tableColumns;
    }
}