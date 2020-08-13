package server.tests;

import org.junit.jupiter.api.DisplayName;
import server.AdminAccount;
import server.IAccount;

import static org.junit.jupiter.api.Assertions.*;

class IAccountTest {

    IAccount defaultAdmin = null;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        defaultAdmin = new AdminAccount("admin", "admin");

    }

    @org.junit.jupiter.api.Test
    @DisplayName("IAccount matches() Test")
    void matches() {
        assertTrue(defaultAdmin.matches("admin", "admin"));
    }
}