package pgp.keys;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.bcpg.sig.KeyFlags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.bc.BcPGPObjectFactory;
import org.bouncycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.*;

import java.io.*;
import java.security.*;
import java.util.*;

public class KeyManagement {

    private static final String PUBLIC_RING_PATH = "src/pgp/keys/rings/publicRing.gpg";
    private static final String SECRET_RING_PATH = "src/pgp/keys/rings/secretRing.gpg";

    private static PGPPublicKeyRingCollection PUBLIC_RING;
    private static PGPSecretKeyRingCollection SECRET_RING;

    private static final ObservableList<KeyInfo> publicKeysList;
    private static final ObservableList<KeyInfo> secretKeysList;

    static {
        Security.addProvider(new BouncyCastleProvider());
        initRings();
        publicKeysList = FXCollections.observableArrayList();
        publicKeysList.addAll(KeyManagement.getPublicKeyInfoCollection());
        secretKeysList = FXCollections.observableArrayList();
        secretKeysList.addAll(KeyManagement.getSecretKeyInfoCollection());
    }

    private static void initRings() {
        try {
            File publicRing = new File(PUBLIC_RING_PATH);
            if (publicRing.exists()) {
                ArmoredInputStream armoredInputStream = new ArmoredInputStream(new FileInputStream(publicRing));
                BcKeyFingerprintCalculator keyFingerprintCalculator = new BcKeyFingerprintCalculator();
                PUBLIC_RING = new PGPPublicKeyRingCollection(armoredInputStream, keyFingerprintCalculator);
                armoredInputStream.close();
            } else
                PUBLIC_RING = new PGPPublicKeyRingCollection(Collections.emptyList());

            File secretRing = new File(SECRET_RING_PATH);
            if (secretRing.exists()) {
                ArmoredInputStream armoredInputStream = new ArmoredInputStream(new FileInputStream(secretRing));
                BcKeyFingerprintCalculator keyFingerprintCalculator = new BcKeyFingerprintCalculator();
                SECRET_RING = new PGPSecretKeyRingCollection(armoredInputStream, keyFingerprintCalculator);
                armoredInputStream.close();
            } else
                SECRET_RING = new PGPSecretKeyRingCollection(Collections.emptyList());

        } catch (IOException | PGPException e) {
            e.printStackTrace();
        }
    }


    public static void createNewKeyPair(UserInfo userInfo, String signAlgorithm, int signKeySize, String encryptionAlgorithm, int encryptionKeySize) throws NoSuchAlgorithmException, NoSuchProviderException, PGPException {

        KeyPairGenerator signKeyPairGenerator = KeyPairGenerator.getInstance(signAlgorithm, "BC");
        signKeyPairGenerator.initialize(signKeySize, new SecureRandom());
        KeyPair signKeyPair = signKeyPairGenerator.generateKeyPair();

        KeyPairGenerator encryptKeyPairGenerator = KeyPairGenerator.getInstance(encryptionAlgorithm, "BC");
        encryptKeyPairGenerator.initialize(encryptionKeySize, new SecureRandom());
        KeyPair encryptKeyPair = encryptKeyPairGenerator.generateKeyPair();

        PGPKeyRingGenerator pgpKeyRingGenerator = createKeyRingGenerator(userInfo, signKeyPair, encryptKeyPair, signAlgorithm, encryptionAlgorithm);

        PGPPublicKeyRing publicKeyRing = pgpKeyRingGenerator.generatePublicKeyRing();
        PUBLIC_RING = PGPPublicKeyRingCollection.addPublicKeyRing(PUBLIC_RING, publicKeyRing);
        exportPublicRing();

        PGPSecretKeyRing secretKeyRing = pgpKeyRingGenerator.generateSecretKeyRing();
        SECRET_RING = PGPSecretKeyRingCollection.addSecretKeyRing(SECRET_RING, secretKeyRing);
        exportSecretRing();

        KeyManagement.getPublicKeysList().add(new KeyInfo(publicKeyRing.getPublicKey().getKeyID(), publicKeyRing.getPublicKey().getUserIDs().next(), true));
        KeyManagement.getSecretKeysList().add(new KeyInfo(secretKeyRing.getSecretKey().getKeyID(), secretKeyRing.getSecretKey().getUserIDs().next(), false));
    }


    private static PGPKeyRingGenerator createKeyRingGenerator(UserInfo userInfo, KeyPair keyPair, KeyPair subkeyPair, String signAlgorithm, String encryptionAlgorithm) throws PGPException {

        int signAlg = PGPPublicKey.RSA_SIGN;
        int encryptAlg = PGPPublicKey.RSA_ENCRYPT;

        switch (signAlgorithm){
            case "DSA":
                signAlg = PGPPublicKey.DSA;
                break;
            case "RSA":
                break;
        }

        switch (encryptionAlgorithm){
            case "RSA":
                break;
            case "ElGamal":
                encryptAlg = PGPPublicKey.ELGAMAL_ENCRYPT;
                break;
        }

        Date date = new Date();

        PGPKeyPair dsaKeyPair = new JcaPGPKeyPair(signAlg, keyPair, date);
        PGPKeyPair elGamalKeyPair = new JcaPGPKeyPair(encryptAlg, subkeyPair, date);

        PGPDigestCalculator sha1DigestCalculator = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);

        PBESecretKeyEncryptor secretKeyEncryptor = (userInfo.getPassphrase().length() == 0 ? null : new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.CAST5, sha1DigestCalculator).setProvider("BC").build(userInfo.getPassphrase().toCharArray()));

        PGPSignatureSubpacketGenerator signatureSubPacketGenerator = new PGPSignatureSubpacketGenerator();
        signatureSubPacketGenerator.setKeyFlags(false, KeyFlags.SIGN_DATA | KeyFlags.CERTIFY_OTHER);
        signatureSubPacketGenerator.setPreferredSymmetricAlgorithms(false, new int[]{SymmetricKeyAlgorithmTags.TRIPLE_DES, SymmetricKeyAlgorithmTags.CAST5});
        signatureSubPacketGenerator.setPreferredHashAlgorithms(false, new int[]{HashAlgorithmTags.SHA1});

        PGPSignatureSubpacketVector signatureSubpacketVector =  signatureSubPacketGenerator.generate();


        PGPSignatureSubpacketGenerator subkeySignatureSubPacketGenerator = new PGPSignatureSubpacketGenerator();
        signatureSubPacketGenerator.setKeyFlags(false, KeyFlags.ENCRYPT_COMMS | KeyFlags.ENCRYPT_STORAGE);

        PGPSignatureSubpacketVector subkeySignatureSubPacketVector = subkeySignatureSubPacketGenerator.generate();

        PGPKeyRingGenerator keyRingGenerator = new PGPKeyRingGenerator(
                PGPSignature.POSITIVE_CERTIFICATION,
                dsaKeyPair,
                userInfo.getId(),
                sha1DigestCalculator,
                signatureSubpacketVector,
                null,
                new JcaPGPContentSignerBuilder(dsaKeyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1),
                secretKeyEncryptor
        );
        keyRingGenerator.addSubKey(elGamalKeyPair, subkeySignatureSubPacketVector, null);

        return keyRingGenerator;
    }


    public static void exportKeyToFile(KeyInfo keyInfo, File file) throws IOException, PGPException {

        ArmoredOutputStream armoredOutputStream = new ArmoredOutputStream(new FileOutputStream(file));

        if (keyInfo.isPublicKey())
            PUBLIC_RING.getPublicKeyRing(keyInfo.getKeyIdLong()).encode(armoredOutputStream);
        else
            SECRET_RING.getSecretKeyRing(keyInfo.getKeyIdLong()).encode(armoredOutputStream);

        armoredOutputStream.close();
    }


    public static void importKey(File file) throws IOException, PGPException {
        List<KeyInfo> keyInfoList = new ArrayList<>();

        PGPObjectFactory objectFactory = new BcPGPObjectFactory(PGPUtil.getDecoderStream(new FileInputStream(file)));
        Iterator iterator = objectFactory.iterator();

        if(iterator.hasNext()) {
            Object object = iterator.next();

            if (object instanceof PGPPublicKeyRing) {
                try {
                    PGPPublicKeyRing keyRing = (PGPPublicKeyRing) object;
                    PUBLIC_RING = PGPPublicKeyRingCollection.addPublicKeyRing(PUBLIC_RING, keyRing);
                    keyInfoList.add(new KeyInfo(keyRing.getPublicKey().getKeyID(), keyRing.getPublicKey().getUserIDs().next(), true));
                } catch (IllegalArgumentException e) {
                    throw new PGPException("This key is already in key ring");
                }
                publicKeysList.addAll(keyInfoList);
                exportPublicRing();
            }
            else if (object instanceof PGPSecretKeyRing) {
                try {
                    PGPSecretKeyRing keyRing = (PGPSecretKeyRing) object;
                    SECRET_RING = PGPSecretKeyRingCollection.addSecretKeyRing(SECRET_RING, keyRing);
                    keyInfoList.add(new KeyInfo(keyRing.getSecretKey().getKeyID(), keyRing.getSecretKey().getUserIDs().next(), false));
                } catch (IllegalArgumentException e) {
                    throw new PGPException("This key is already in key ring");
                }
                secretKeysList.addAll(keyInfoList);
                exportSecretRing();
            }
        }
    }


    public static void deletePublicKey(KeyInfo keyInfo) throws PGPException {
        PGPPublicKeyRing publicKeyRing = PUBLIC_RING.getPublicKeyRing(keyInfo.getKeyIdLong());
        PUBLIC_RING = PGPPublicKeyRingCollection.removePublicKeyRing(PUBLIC_RING, publicKeyRing);
        publicKeysList.remove(keyInfo);
        exportPublicRing();
    }


    public static void deleteSecretKey(KeyInfo keyInfo, String passphrase) throws PGPException {
        PGPSecretKeyRing secretKeyRing = SECRET_RING.getSecretKeyRing(keyInfo.getKeyIdLong());
        secretKeyRing.getSecretKey().extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(passphrase.toCharArray()));
        SECRET_RING = PGPSecretKeyRingCollection.removeSecretKeyRing(SECRET_RING, secretKeyRing);
        secretKeysList.remove(keyInfo);
        exportSecretRing();
    }


    public static void deleteSecretKey(KeyInfo keyInfo) throws PGPException {
        PGPSecretKeyRing secretKeyRing = SECRET_RING.getSecretKeyRing(keyInfo.getKeyIdLong());
        secretKeyRing.getSecretKey().extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(null));
        SECRET_RING = PGPSecretKeyRingCollection.removeSecretKeyRing(SECRET_RING, secretKeyRing);
        secretKeysList.remove(keyInfo);
        exportSecretRing();
    }


    public static boolean isEncrypted(KeyInfo keyInfo) throws PGPException {
        return SECRET_RING.getSecretKey(keyInfo.getKeyIdLong()).getS2K() != null;
    }


    public static PGPPublicKey getPublicSubkey(long keyIdLong) throws PGPException, NoSuchElementException {
        PGPPublicKeyRing publicKeyRing = PUBLIC_RING.getPublicKeyRing(keyIdLong);
        Iterator<PGPPublicKey> it = publicKeyRing.getPublicKeys();
        it.next();
        return it.next();
    }


    private static void exportPublicRing() {
        try {
            ArmoredOutputStream armoredOutputStream = new ArmoredOutputStream(new FileOutputStream(PUBLIC_RING_PATH));

            for (PGPPublicKeyRing publicKeyRing : PUBLIC_RING)
                publicKeyRing.encode(armoredOutputStream);

            armoredOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void exportSecretRing() {
        try {
            ArmoredOutputStream armoredOutputStream = new ArmoredOutputStream(new FileOutputStream(SECRET_RING_PATH));

            for (PGPSecretKeyRing secretKeyRing : SECRET_RING)
                secretKeyRing.encode(armoredOutputStream);
            armoredOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static PGPPrivateKey extractPrivateKey(PGPSecretKey secretKey, String passphrase) throws PGPException {
        return secretKey.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(passphrase.toCharArray()));
    }

    public static Collection<KeyInfo> getPublicKeyInfoCollection() {
        Collection<KeyInfo> publicKeyInfoCollection = new ArrayList<>(PUBLIC_RING.size());
        PUBLIC_RING.forEach(keyRing -> publicKeyInfoCollection.add(new KeyInfo(keyRing.getPublicKey().getKeyID(), keyRing.getPublicKey().getUserIDs().next(), true)));
        return publicKeyInfoCollection;
    }

    public static Collection<KeyInfo> getSecretKeyInfoCollection() {
        Collection<KeyInfo> secretKeyInfoCollection = new ArrayList<>(SECRET_RING.size());
        SECRET_RING.forEach(keyRing -> secretKeyInfoCollection.add(new KeyInfo(keyRing.getSecretKey().getKeyID(), keyRing.getSecretKey().getUserIDs().next(), false)));
        return secretKeyInfoCollection;
    }

    public static ObservableList<KeyInfo> getPublicKeysList() {
        return publicKeysList;
    }

    public static ObservableList<KeyInfo> getSecretKeysList() {
        return secretKeysList;
    }

    public static PGPPublicKeyRingCollection getPublicRingCollection() {
        return PUBLIC_RING;
    }

    public static PGPSecretKeyRingCollection getSecretRingCollection() {
        return SECRET_RING;
    }

    public static PGPSecretKey getSecretKey(long keyId) throws PGPException {
        return SECRET_RING.getSecretKey(keyId);
    }

    public static PGPPublicKey getPublicKey(long keyId) throws PGPException {
        return PUBLIC_RING.getPublicKey(keyId);
    }

    public static PGPSecretKeyRing getSecretKeyRing(long keyID) throws PGPException {
        return SECRET_RING.getSecretKeyRing(keyID);
    }
}
