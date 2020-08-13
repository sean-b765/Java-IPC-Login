package server;

import server.controller.Controller;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class SocketServer extends Thread {
    int port;

    ServerSocket serverSocket = null;
    Socket socket = null;
    // controller to access log() method
    Controller controller;

    List<IAccount> accounts = new LinkedList<>();

    String serializationFile = "server_clients.bin";

    // pass the Controller as reference so we can access public methods
    public SocketServer(String port, Controller controller) {
        try {
            this.port = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            this.port = 0;
        }
        this.controller = controller;
        loadSavedUsers();
    }

    public SocketServer(String port) {
        try {
            this.port = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            this.port = 0;
        }
        loadSavedUsers();
    }

    // thread start()
    public void run() {
        if (initialize()) {
            controller.log("Server started.");
            // enter the thread loop. Accept incoming socket connections, and send them to a new thread,
            //  to allow multiple-clients
            while (true) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                var client = new ConnectedClient(socket, controller, this);
                client.start();
            }
        } else {
            controller.log("Unable to start server. Check the port is not in use.");
        }
    }

    // initialize server method. Returns true if the server was successfully created,
    //  false if not.
    boolean initialize() {
        if (port == 0)
            return false;

        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IllegalArgumentException e) {
            return false;
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    // deserialization method
    //  load the accounts list object,
    //  and also check if there is a default admin account
    void loadSavedUsers() {
        FileInputStream fileIn;
        ObjectInputStream objectInputStream;

        try {
            fileIn = new FileInputStream(serializationFile);
            objectInputStream = new ObjectInputStream(fileIn);

            try {
                // get the saved object and set the users list
                accounts = (List<IAccount>) objectInputStream.readObject();
            } catch (Exception e) { }

            fileIn.close();
            objectInputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("Save file not found (" + serializationFile + "). Creating new admin account.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // check for default admin account
        if (accounts.size() == 0) {
            // if the accounts list is 0, we need to create a default admin acc
            AdminAccount a = new AdminAccount("admin", "admin");
            accounts.add(a);
        } else {
            boolean createAdmin = true;
            // loop through the accounts list to check if there is an admin instance
            for (int i = 0; i < accounts.size(); i++) {
                if (accounts.get(i) instanceof AdminAccount)
                    createAdmin = false;
            }
            if (createAdmin) {
                AdminAccount a = new AdminAccount("admin", "admin");
                accounts.add(a);
            }
        }
    }

    // serialize the List<IAccount> object into file
    public void saveUsers() {
        ObjectOutputStream objectOutputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            // File out
            fileOutputStream = new FileOutputStream(serializationFile);
            // obj out
            objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(accounts);

            // close resources
            fileOutputStream.close();
            objectOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // check if the client exists by username String
    //  returns true if client exists,
    //  false otherwise
    public boolean doesClientExist(String user) {
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getUser().equals(user)) {
                return true;
            }
        }

        return false;
    }

    public List<IAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<IAccount> list) {
        this.accounts = list;
    }
}
