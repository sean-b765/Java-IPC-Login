package server.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import server.SocketServer;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    ToggleButton startStop = new ToggleButton();
    @FXML
    TextField hostPort = new TextField();
    @FXML
    TextArea connection = new TextArea();

    SocketServer server;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hostPort.setText("1235");

        startStop.setText("Start");
        startStop.selectedProperty().addListener(((observableValue, oldVal, newVal) -> {
            if (newVal) {
                // start the server
                startStop.setText("Stop");
                String port = hostPort.getText();
                server = new SocketServer(port, this);
                server.start();
            } else
                startStop.setText("Start");
        }));
    }

    public void log(String message) {
        connection.setText(connection.getText() + message + "\n");
    }

    // serialize the list of registered users on shutdown
    public void shutdown() {
        server.saveUsers();
    }
}
