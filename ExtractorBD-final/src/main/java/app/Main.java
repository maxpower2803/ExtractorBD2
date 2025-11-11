package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/views/grillas.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("ExtractorBD - Servidores -> Bases -> Tablas");
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/app/css/application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
