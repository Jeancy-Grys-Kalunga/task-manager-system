package jeancygrys.taskmanagerapp;

import database.Db;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {
    public static String currentTaskFilter;

    private static Scene scene;
    private Db db = new Db();

    @Override
    public void start(Stage stage) throws IOException {

        scene = new Scene(loadFXML("home"));
        stage.setScene(scene);

        stage.show();
        db.getConnection();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}