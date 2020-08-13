package server;

public class RegisteredAccount implements IAccount {
    String user;
    byte[] hash;
    byte[] salt;

    public RegisteredAccount(String user, String pass) {
        this.user = user;
        char[] password = pass.toCharArray();

        salt = Hash.genSalt();
        hash = Hash.genHash(password, salt);
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public byte[] getHash() {
        return hash;
    }

    @Override
    public byte[] getSalt() {
        return salt;
    }

    @Override
    public boolean matches(String user, String pass) {
        return Hash.match(pass.toCharArray(), salt, hash);
    }
}
