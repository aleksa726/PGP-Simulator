package pgp.algorithm;

import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import pgp.keys.KeyInfo;
import pgp.keys.KeyManagement;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class PGPDecrypt {

    private String message;

    private String passphrase;

    private File file;
    private String outputFile;

    private String signedData;
    private String compressedData;
    private String encryptedData;
    private String radix64Data;

    private boolean compressionEnabled = true;
    private boolean radix64Enabled = true;
    private boolean encryptEnabled = true;
    private boolean signEnabled = true;

    private KeyInfo publicKeyInfo;
    private KeyInfo secretKeyInfo;

    private byte[] data;

    private String decryptedMessage;

    public PGPDecrypt(String message, File file, String passphrase, byte[] data, boolean radix64Enabled, boolean encryptEnabled, byte[] radix64Data, byte[] encryptedData, PGPSecretKey secretKey) throws PGPException {
        this.message = message;
        this.file = file;
        this.passphrase = passphrase;
        this.outputFile = "encrypted_message.gpg";
        this.data = data;
        this.radix64Enabled = radix64Enabled;
        this.encryptEnabled = encryptEnabled;

        if(encryptEnabled)
            this.secretKeyInfo = new KeyInfo(secretKey.getKeyID(), secretKey.getUserIDs().next(), false);

        if(radix64Data != null) {
            StringBuilder sb = new StringBuilder();
            for (byte b : radix64Data) {
                String str = String.format("%02X", b);
                sb.append(str);
            }
            this.radix64Data = sb.toString();
        }

        if(encryptEnabled) {
            StringBuilder sb2 = new StringBuilder();
            for (byte b : encryptedData) {
                String str = String.format("%02X", b);
                sb2.append(str);
            }
            this.encryptedData = sb2.toString();
        }

        decrypt();

    }

    private void decrypt() {
        try {
            this.data = decompress(this.data);
        } catch (PGPException | IOException e) {
            compressionEnabled = false;
        }

        boolean verified = false;
        try {
            verified = verifySignature(this.data);
        } catch (Exception e) {
            signEnabled = false;
        }

        if(verified){
            String author = null;
            try {
                author = extractMessageAuthor(this.data);
            } catch (IOException | PGPException e) {
                e.printStackTrace();
            }
        }

        try {
            this.data = parseLiteralData(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.decryptedMessage = new String(data, StandardCharsets.UTF_8);

    }


    public byte[] decompress(byte[] data) throws PGPException, IOException {

        JcaPGPObjectFactory pgpFactory = new JcaPGPObjectFactory(data);
        Object object = pgpFactory.nextObject();
        if (!(object instanceof PGPCompressedData)) {
            throw new PGPException("Unable to decompress message");
        }
        byte[] decompressedData = ((PGPCompressedData) object).getDataStream().readAllBytes();

        StringBuilder sb = new StringBuilder();
        for (byte b : decompressedData) {
            String str = String.format("%02X", b);
            sb.append(str);
        }
        this.compressedData = sb.toString();

        return decompressedData;
    }

    public boolean verifySignature(byte[] data) throws Exception {
        JcaPGPObjectFactory objectFactory = new JcaPGPObjectFactory(data);
        Object object = objectFactory.nextObject();

        if (object instanceof PGPOnePassSignatureList) {
            PGPOnePassSignatureList onePassSignatureList = (PGPOnePassSignatureList) object;
            PGPOnePassSignature onePassSignature = onePassSignatureList.get(0);

            long keyId = onePassSignature.getKeyID();

            PGPPublicKey publicKey = KeyManagement.getPublicKey(keyId);
            this.publicKeyInfo = new KeyInfo(publicKey.getKeyID(), publicKey.getUserIDs().next(), true);
            if (publicKey == null) {
                throw new PGPException("Verification failed");
            }

            onePassSignature.init(new JcaPGPContentVerifierBuilderProvider().setProvider("BC"), publicKey);

            InputStream toVerify = ((PGPLiteralData) objectFactory.nextObject()).getInputStream();

            onePassSignature.update(toVerify.readAllBytes());

            PGPSignatureList signatureList = (PGPSignatureList) objectFactory.nextObject();
            PGPSignature signature = signatureList.get(0);

            if (onePassSignature.verify(signature)) {
                return true;
            } else {
                throw new PGPException("Verification failed");
            }
        }
        throw new PGPException("Verification failed");
    }

    public String extractMessageAuthor(byte[] data) throws IOException, PGPException {
        JcaPGPObjectFactory objectFactory = new JcaPGPObjectFactory(data);
        Object object = objectFactory.nextObject();

        if (object instanceof PGPOnePassSignatureList) {
            PGPOnePassSignatureList onePassSignatureList = (PGPOnePassSignatureList) object;
            PGPOnePassSignature onePassSignature = onePassSignatureList.get(0);

            long keyId = onePassSignature.getKeyID();
            PGPPublicKey publicKey = KeyManagement.getPublicKey(keyId);
            return (String) publicKey.getUserIDs().next();
        }
        return null;
    }

    public byte[] parseLiteralData(byte[] data) throws IOException {
        JcaPGPObjectFactory objectFactory = new JcaPGPObjectFactory(data);
        Object object = objectFactory.nextObject();
        while (object != null) {
            if (object instanceof PGPLiteralData) {

                PGPLiteralData literalData = (PGPLiteralData) object;
                InputStream inputStream = literalData.getInputStream();
                return inputStream.readAllBytes();

            }
            object = objectFactory.nextObject();
        }

        return data;
    }


    public String getMessage() {
        return message;
    }

    public String getSignedData() {
        return signedData;
    }

    public String getCompressedData() {
        return compressedData;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public String getRadix64Data() {
        return radix64Data;
    }

    public boolean isCompressionEnabled() {
        return compressionEnabled;
    }

    public boolean isRadix64Enabled() {
        return radix64Enabled;
    }

    public boolean isEncryptEnabled() {
        return encryptEnabled;
    }

    public boolean isSignEnabled() {
        return signEnabled;
    }

    public KeyInfo getPublicKeyInfo() {
        return publicKeyInfo;
    }

    public KeyInfo getSecretKeyInfo() {
        return secretKeyInfo;
    }

    public String getDecryptedMessage() {
        return decryptedMessage;
    }
}
