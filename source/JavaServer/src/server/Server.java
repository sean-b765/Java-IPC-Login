package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.controller.Controller;

import java.net.URL;

public class Server extends Application {
    Controller controller;
    @Override
    public void start(Stage stage) throws Exception {
        try {
            URL url = getClass().getClassLoader().getResource("server/view/View.fxml");

            FXMLLoader loader = new FXMLLoader(url);

            Parent root = loader.load();
            Scene scene = new Scene(root);

            controller = loader.getController();

            stage.setScene(scene);
            stage.setTitle("Server");
            stage.setMinHeight(530);
            stage.setMinWidth(687);
            stage.show();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }


    // Override the Application stop() to call saveUsers();
    public void stop() {
        controller.shutdown();
    }
}
