package pgp.keys;

public class KeyInfo {

    private long keyId;
    private String username;
    private String email;
    private boolean publicKey;

    public KeyInfo(long keyId, String userId, boolean publicKey) {
        this.keyId = keyId;
        this.publicKey = publicKey;

        int startIndex = userId.indexOf('<');
        int endIndex = userId.indexOf('>');
        if(startIndex < 0 || endIndex < 0){
            this.username = userId;
            this.email = "";
        }
        else {
            this.username = userId.substring(0, startIndex - 1);
            this.email = userId.substring(startIndex + 1, endIndex);
        }
    }

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

    public String getKeyId() {
        return  String.format("%016X", keyId);
    }

    public long getKeyIdLong() {
        return this.keyId;
    }

    public boolean isPublicKey() { return publicKey; }

    @Override
    public String toString() {
        String id = String.format("%016X", keyId);
        return username + "[0x" + id + "]";
    }

}
