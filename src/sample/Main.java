package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {
    public static URL patern;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("views/sample.fxml"));
        patern = getClass().getResource("views/upt_device.fxml");
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1200, 740));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
