package server;

import java.io.Serializable;

public interface IAccount extends Serializable {
    String getUser();
    byte[] getHash();
    byte[] getSalt();
    boolean matches(String user, String pass);
}
