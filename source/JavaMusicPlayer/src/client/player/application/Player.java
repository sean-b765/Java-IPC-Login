package client.player.application;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import client.player.controller.*;
import java.net.URL;

public class Player extends Application {

    Controller controller;

    @Override
    public void start(Stage stage) throws Exception {

        try {
            URL url = getClass().getClassLoader().getResource("client/player/view/View.fxml");

            FXMLLoader loader = new FXMLLoader(url);

            Parent root = loader.load();
            Scene scene = new Scene(root);

            controller = loader.getController();

            stage.setScene(scene);
            stage.setTitle("Media Player X");
            stage.setMinHeight(530);
            stage.setMinWidth(687);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        launch(args);

    }

    // Override Application stop() method.
    // Call client.player.controller.shutdown() to serialize the Program objects
    @Override
    public void stop() throws InterruptedException {
        controller.shutdown();
    }

}
