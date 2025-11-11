package app.controllers;

import app.db.ConnectionPoolManager;
import app.db.DatabaseMetadataDao;
import app.db.ServerConfig;
import app.models.ServerInfo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;

import javax.sql.DataSource;
import java.net.URL;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class GrillasController implements Initializable {

    @FXML private ListView<ServerInfo> lvServers;
    @FXML private ListView<String> lvDatabases;
    @FXML private ListView<String> lvTables;
    @FXML private ListView<String> lvColumns;
    @FXML private ProgressIndicator progress;

    private final DatabaseMetadataDao dao = new DatabaseMetadataDao();
    private DataSource currentDataSource;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CompletableFuture.runAsync(() -> {
            try {
                List<ServerInfo> servers = ServerConfig.loadFromPath(Path.of("servers.properties"));
                Platform.runLater(() -> lvServers.getItems().setAll(servers));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        lvServers.setOnMouseClicked(this::onServerClicked);
        lvDatabases.setOnMouseClicked(this::onDatabaseClicked);
        lvTables.setOnMouseClicked(this::onTableClicked);
    }

    private void showProgress(boolean show) {
        Platform.runLater(() -> progress.setVisible(show));
    }

    private void onServerClicked(MouseEvent event) {
        ServerInfo selected = lvServers.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        lvDatabases.getItems().clear();
        lvTables.getItems().clear();
        lvColumns.getItems().clear();
        showProgress(true);

        CompletableFuture.supplyAsync(() -> {
            try {
                currentDataSource = ConnectionPoolManager.getDataSourceFor(selected);
                return dao.listDatabases(currentDataSource);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }).thenAccept(dbs -> Platform.runLater(() -> {
            lvDatabases.getItems().setAll(dbs);
            showProgress(false);
        })).exceptionally(ex -> {
            ex.printStackTrace();
            showProgress(false);
            return null;
        });
    }

    private void onDatabaseClicked(MouseEvent event) {
        String selectedDb = lvDatabases.getSelectionModel().getSelectedItem();
        if (selectedDb == null || currentDataSource == null) return;

        lvTables.getItems().clear();
        lvColumns.getItems().clear();
        showProgress(true);

        CompletableFuture.supplyAsync(() -> {
            try {
                return dao.listTables(currentDataSource, selectedDb);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }).thenAccept(tables -> Platform.runLater(() -> {
            lvTables.getItems().setAll(tables);
            showProgress(false);
        })).exceptionally(ex -> {
            ex.printStackTrace();
            showProgress(false);
            return null;
        });
    }

    private void onTableClicked(MouseEvent event) {
        String selectedDb = lvDatabases.getSelectionModel().getSelectedItem();
        String fullTableName = lvTables.getSelectionModel().getSelectedItem();
        if (selectedDb == null || fullTableName == null || currentDataSource == null) return;

        lvColumns.getItems().clear();
        showProgress(true);

        CompletableFuture.supplyAsync(() -> {
            try {
                String[] parts = fullTableName.split("\\.");
                if (parts.length != 2) return List.of();
                String schema = parts[0];
                String table = parts[1];
                return dao.listColumns(currentDataSource, selectedDb, schema, table);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }).thenAccept(columns -> Platform.runLater(() -> {
        	lvColumns.getItems().setAll(columns.toArray(new String[0]));
            showProgress(false);
        })).exceptionally(ex -> {
            ex.printStackTrace();
            showProgress(false);
            return null;
        });
    }

    @FXML
    private void onCloseApp() {
        ConnectionPoolManager.closePool();
        Platform.exit();
    }
}