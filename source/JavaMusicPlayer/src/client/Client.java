package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Client extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        try {
            String pth = "client/view/View.fxml";
            URL url = getClass().getClassLoader().getResource(pth);

            System.out.println(url.getPath());
            FXMLLoader loader = new FXMLLoader(url);

            Parent root = loader.load();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Log In");
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
}
