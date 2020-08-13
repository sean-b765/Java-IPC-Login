package client.controller;

import client.IncomingData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    Button login = new Button();
    @FXML
    Button connect = new Button();
    @FXML
    TextField user = new TextField();
    @FXML
    TextField pass = new TextField();
    @FXML
    Label serverStatus = new Label();
    @FXML
    Label loginStatus = new Label();
    @FXML
    Pane accessPane = new Pane();
    @FXML
    Button addUser = new Button();
    @FXML
    Button openPlayer = new Button();
    @FXML
    Label admin = new Label();
    @FXML
    Label all = new Label();

    Socket socket;
    DataOutputStream out;
    public DataInputStream in;
    boolean connected;
    boolean loggedIn;
    boolean isAdmin;
    int PORT = 1235;

    // server messages Thread
    IncomingData incomingData;

    String name = "";
    String passw = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        connect.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> connect(PORT));
        login.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> attemptLogin());

        // openPlayer click event will open the Player
        openPlayer.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            // run the Player static main method
            try {
                URL playView = getClass().getClassLoader().getResource("client/player/view/View.fxml");

                FXMLLoader loader = new FXMLLoader(playView);

                Parent root = loader.load();
                Scene scene = new Scene(root);

                Stage stage = new Stage();

                stage.setScene(scene);
                stage.setTitle("Media Player X");
                stage.setMinHeight(530);
                stage.setMinWidth(687);
                stage.show();

                client.player.controller.Controller playController = loader.getController();

                stage.setOnCloseRequest(event -> {
                    // call shutdown() method, which will call serialization
                    playController.shutdown();
                });

            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });

        updateUI();

    }

    // connect method,
    //  takes port integer as argument
    void connect(int port) {
        try {
            // initialize relevant socket objects
            socket = new Socket("127.0.0.1", port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            connected = true;
        } catch (UnknownHostException e) {
            serverStatus.setText("Unknown host.");
        } catch (ConnectException e) {
            serverStatus.setText("The server is not currently operating.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // start receiving messages from server on separate thread, if we are connected
        if (connected) {
            incomingData = new IncomingData(in, this);
            incomingData.start();
            serverStatus.setText("You are connected. Please login.");
        } else {
            serverStatus.setText("Server is not running.");
        }
        updateUI();
    }

    // public loginFailed method, called by IncomingData thread,
    //  if the server has sent a 'failed login' message
    int failedTries = 0;
    public void loginFailed() {
        failedTries++;
        loggedIn = false;
        // increase failedTries counter. if > 5 disconnect the socket
        synchronized (incomingData) {
            Platform.runLater(() -> {
                setLoginStatus("Failed login attempt " + failedTries + " of 5.");
            });
        }
        if (failedTries == 5) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connected = false;
        }
        updateUI();
    }

    // successful login method, called by IncomingData thread
    //  takes boolean -> true if this client is an admin,
    //                   false if regular user.
    public void loginSuccess(boolean admin) {
        loggedIn = true;
        Platform.runLater(() -> {
            setLoginStatus("You have logged in!");
            serverStatus.setText("");
        });
        this.isAdmin = admin;
        updateUI();
    }

    // single updateUI method which relies on the proper setting of global booleans
    void updateUI() {
        if (connected) {
            login.setDisable(false);
            user.setDisable(false);
            pass.setDisable(false);
            connect.setDisable(true);
        } else {
            login.setDisable(true);
            user.setDisable(true);
            pass.setDisable(true);
            connect.setDisable(false);
        }

        if (loggedIn) {
            // loop through the Pane container and set all children visibility
            for (int i = 0; i < accessPane.getChildren().size(); i++) {
                accessPane.getChildren().get(i).setVisible(true);
            }
            accessPane.setVisible(true);

            // if the user is not an admin, certain controls should not be visible
            if (!isAdmin) {
                admin.setVisible(false);
                addUser.setVisible(false);
            }
        } else {
            // no controls should be visible if the user is not logged in
            for (int i = 0; i < accessPane.getChildren().size(); i++) {
                accessPane.getChildren().get(i).setVisible(false);
            }
            accessPane.setVisible(false);
        }
    }

    void attemptLogin() {
        if (!user.getText().isEmpty() && !pass.getText().isEmpty()) {
            String info = user.getText() + "," + pass.getText();

            incomingData.setUsername(user.getText());
            user.setText("");
            pass.setText("");

            send(info);
        }
    }

    void send(String info) {
        try {
            out.writeBytes(info);
            out.write(13); // \r
            out.write(10); // \n
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLoginStatus(String text) {
        loginStatus.setText(text);
    }
}
