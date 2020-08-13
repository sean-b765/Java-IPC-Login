package client;

import client.controller.Controller;

import java.io.*;
import java.net.SocketException;

public class IncomingData extends Thread {
    BufferedReader serverReader;
    Controller controller;
    String username;

    public IncomingData(InputStream socketInStream, Controller controller) {
        serverReader = new BufferedReader(new InputStreamReader(socketInStream));
        this.controller = controller;
    }

    public void run() {
        String line;
        while(true) {
            try {
                line = serverReader.readLine();
                if (line.startsWith("[@" + username + "]")) {
                    String messageContent = line.replace("[@" + username + "] ", "");
                    //controller.setLoginStatus(messageContent);
                    if (messageContent.contains("You have successfully logged in")) {
                        // detect if Client is admin or regular user.
                        if (messageContent.contains("as admin."))
                            controller.loginSuccess(true);
                        else if (messageContent.contains("as user."))
                            controller.loginSuccess(false);
                    } else if (messageContent.contains("User does not exist.")) {
                        controller.loginFailed();
                    }
                    System.out.println("SERVER > " + messageContent);
                }
            } catch (SocketException e) {
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
