package pgp.keys;

public class UserInfo {

    private final String username;
    private final String email;
    private final String passphrase;

    public UserInfo(String username, String email, String passphrase) {
        this.username = username;
        this.email = email;
        this.passphrase = passphrase;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public String getId() {
        return this.username + "<" + this.email + ">";
    }
}
