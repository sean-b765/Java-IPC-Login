package server;

import server.controller.Controller;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;

public class ConnectedClient extends Thread {
    Socket socket = null;
    Controller controller;
    SocketServer serverObj;

    boolean connected = true;

    DataOutputStream out = null;

    String clientName = "";

    public ConnectedClient(Socket s, Controller controller, SocketServer ss) {
        socket = s;
        this.controller = controller;
        serverObj = ss;
        controller.log("+ New client connected.");
    }

    public void run() {
        InputStream in      = null;
        BufferedReader br   = null;

        // initialize socket input and output streams
        try {
            in = socket.getInputStream();
            br = new BufferedReader(new InputStreamReader(in));
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            return;
        }

        String line;
        while (connected) {
            // while the client is connected,
            //  receive input
            try {
                line = br.readLine();
                String user = "", pass = "";

                try {
                    if (line.split(",").length == 2) {
                        user = line.split(",")[0];
                        pass = line.split(",")[1];
                    } else
                        continue;
                } catch (Exception e) {
                    // the string was not a login attempt.
                    //  currently the only received messages can be login attempts anyways
                }

                if (serverObj.doesClientExist(user)) {
                    LinkedList<IAccount> accounts = (LinkedList<IAccount>) serverObj.getAccounts();
                    // loop through the registered accounts in the server
                    for (int i = 0; i < accounts.size(); i++) {
                        // check if one matches
                        if (accounts.get(i).matches(user, pass)) {
                            // send the validation code
                            if (accounts.get(i) instanceof AdminAccount) {
                                send("[@" + user + "] You have successfully logged in as admin.");
                            } else {
                                send("[@" + user + "] You have successfully logged in as user.");
                            }
                            controller.log(user + " has successfully logged in.");
                            clientName = user;
                            break;
                        }
                    }
                } else {
                    boolean success = send("[@" + user + "] User does not exist.");
                    this.clientName = user;
                    if (success) {
                        controller.log("[@" + clientName + "] - failed login attempt.");
                    }
                }

            } catch (SocketException e) {
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // throw a socket exception to the run() method, where it will be caught in the try/catch
    //  and will log the client being disconnected
    // will return boolean based on if message was sent or not
    boolean send(String s) throws SocketException {
        try {
            out.writeBytes(s);
            out.write(13); // \r
            out.write(10); // \n
            out.flush();
        } catch (SocketException e) {
            this.connected = false;
            controller.log("- Client disconnected.");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
