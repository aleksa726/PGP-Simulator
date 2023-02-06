package pgp.algorithm;

import org.bouncycastle.bcpg.*;
import pgp.keys.KeyInfo;
import pgp.keys.KeyManagement;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

import java.io.*;
import java.security.SecureRandom;
import java.util.Date;
import java.util.NoSuchElementException;

public class PGPEncrypt {

    private String message;

    private File file;
    private String outputFile;

    private boolean compressionEnabled;
    private boolean radix64Enabled;

    private boolean encryptEnabled;
    private int symmetricAlgorithmId;
    private KeyInfo publicKeyInfo;

    private boolean signEnabled;
    private String passphrase;

    private KeyInfo secretKeyInfo;

    private String signedData;
    private String compressedData;
    private String encryptedData;
    private String radix64Data;

    public String outputData;

    public PGPEncrypt(String message, File file, boolean encryptEnabled, boolean signEnabled, boolean compressionEnabled, boolean radix64Enabled, int symmetricAlgorithmId, KeyInfo publicKeyInfo, KeyInfo secretKeyInfo, String passphrase) throws PGPException {

        this.message = message;
        this.file = file;
        this.outputFile = "encrypted_message.gpg";

        this.compressionEnabled = compressionEnabled;
        this.radix64Enabled = radix64Enabled;

        if (this.encryptEnabled = encryptEnabled) {
            this.symmetricAlgorithmId = symmetricAlgorithmId;
            this.publicKeyInfo = publicKeyInfo;
        }
        if (this.signEnabled = signEnabled) {
            this.passphrase = passphrase;
            this.secretKeyInfo = secretKeyInfo;
        }

        try {
            byte[] inputData = readFromFile();
            encrypt(inputData);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public byte[] readFromFile() throws IOException {
        FileInputStream fileInputStream = new FileInputStream(this.file.getName());
        byte[] bytes = fileInputStream.readAllBytes();
        fileInputStream.close();
        return bytes;
    }

    public void encrypt(byte[] data) throws PGPException, IOException {
        if (signEnabled) {
            data = sign(data, outputFile);
        } else {
            data = generateLiteralData(data, outputFile);
        }
        if (compressionEnabled) {
            data = compress(data);
        }
        if (encryptEnabled) {
            data = encrypt(data, this.publicKeyInfo, this.symmetricAlgorithmId);
        }
        if (radix64Enabled) {
            data = encodeRadix64(data);
        }
    }

    public byte[] sign(byte[] data, String filename) throws PGPException, IOException {

        if (this.secretKeyInfo == null)
            throw new RuntimeException("Secret key not provided.");

        PGPSecretKey secretKey = KeyManagement.getSecretKey(this.secretKeyInfo.getKeyIdLong());
        PGPPublicKey publicKey = secretKey.getPublicKey();
        PGPPrivateKey privateKey = null;

        PGPSignatureGenerator signatureGenerator = new PGPSignatureGenerator(new JcaPGPContentSignerBuilder(publicKey.getAlgorithm(), PGPUtil.SHA1).setProvider("BC"));

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        BCPGOutputStream helperStream = new BCPGOutputStream(byteStream);

        try {
            privateKey = KeyManagement.extractPrivateKey(secretKey, this.passphrase);
        }
        catch (PGPException e){
            throw new PGPException("Passphrase isn\'t correct!");
        }

        signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, privateKey);
        signatureGenerator.generateOnePassVersion(false).encode(helperStream);

        PGPLiteralDataGenerator lGen = new PGPLiteralDataGenerator();
        OutputStream os = lGen.open(helperStream, PGPLiteralData.BINARY, filename, data.length, new Date());
        InputStream is = new ByteArrayInputStream(data);

        int ch;

        while ((ch = is.read()) >= 0) {
            signatureGenerator.update((byte) ch);
            os.write(ch);
        }

        lGen.close();

        signatureGenerator.generate().encode(helperStream);

        byte[] signed = byteStream.toByteArray();


        StringBuilder sb = new StringBuilder();
        for (byte b : signed) {
            String str = String.format("%02X", b);
            sb.append(str);
        }

        this.signedData = sb.toString();
        outputData = signedData;

        byteStream.close();
        helperStream.close();
        return signed;

    }

    public static byte[] generateLiteralData(byte[] data, String filename) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        BCPGOutputStream helperStream = new BCPGOutputStream(byteStream);

        PGPLiteralDataGenerator lGen = new PGPLiteralDataGenerator();
        OutputStream os = lGen.open(helperStream, PGPLiteralData.BINARY, filename, data.length, new Date());
        InputStream is = new ByteArrayInputStream(data);

        int ch;

        while ((ch = is.read()) >= 0) {
            os.write((byte) ch);
        }

        lGen.close();

        byte[] literalData = byteStream.toByteArray();

        byteStream.close();
        helperStream.close();
        return literalData;

    }

    public byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        PGPCompressedDataGenerator compressionGenerator = new PGPCompressedDataGenerator(CompressionAlgorithmTags.ZIP);
        OutputStream compressedStream = compressionGenerator.open(byteOutputStream);
        compressedStream.write(data);
        compressedStream.close();
        byte[] compressedData = byteOutputStream.toByteArray();

        StringBuilder sb = new StringBuilder();
        for (byte b : compressedData) {
            String str = String.format("%02X", b);
            sb.append(str);
        }

        this.compressedData = sb.toString();
        outputData = this.compressedData;
        byteOutputStream.close();
        return compressedData;
    }

    public byte[] encrypt(byte[] data, KeyInfo publicKeyInfo, int algorithm) throws IOException, PGPException {

        OutputStream outputStream = new ByteArrayOutputStream();

        JcePGPDataEncryptorBuilder jcePGPDataEncryptorBuilder = new JcePGPDataEncryptorBuilder(algorithm).setWithIntegrityPacket(true).setSecureRandom(new SecureRandom()).setProvider("BC");


        PGPEncryptedDataGenerator encryptionGenerator = new PGPEncryptedDataGenerator(jcePGPDataEncryptorBuilder);

        try {
            encryptionGenerator.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(KeyManagement.getPublicSubkey(publicKeyInfo.getKeyIdLong())).setProvider("BC"));
        }
        catch (NoSuchElementException e){
            throw new PGPException("This key can\'t sign messages");
        }


        OutputStream encryptedOutputStream = encryptionGenerator.open(outputStream, data.length);

        encryptedOutputStream.write(data);
        encryptedOutputStream.close();

        byte[] encryptedData = ((ByteArrayOutputStream) outputStream).toByteArray();


        StringBuilder sb = new StringBuilder();
        for (byte b : encryptedData) {
            String str = String.format("%02X", b);
            sb.append(str);
        }

        this.encryptedData = sb.toString();
        outputData = this.encryptedData;

        return encryptedData;
    }

    public byte[] encodeRadix64(byte[] data) throws IOException {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ArmoredOutputStream armoredOutputStream = new ArmoredOutputStream(byteOutputStream);
        armoredOutputStream.write(data);
        armoredOutputStream.close();
        byte[] encodedData = byteOutputStream.toByteArray();

        this.radix64Data = new String(encodedData);
        outputData = this.radix64Data;
        byteOutputStream.close();
        return encodedData;
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

    public String getPassphrase() {
        return passphrase;
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

}
