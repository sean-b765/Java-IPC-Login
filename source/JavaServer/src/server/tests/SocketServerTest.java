package server.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.SocketServer;
import server.controller.Controller;

import static org.junit.jupiter.api.Assertions.*;

class SocketServerTest {

    SocketServer socketServer = null;

    @BeforeAll
    void setUp() {
        // instantiate socketServer with port 1235
        //  Integer parsing occurs in constructor
        socketServer = new SocketServer("1235");
    }

    @Test
    void doesClientExist() {
        assertTrue(socketServer.doesClientExist("admin"));
    }
}