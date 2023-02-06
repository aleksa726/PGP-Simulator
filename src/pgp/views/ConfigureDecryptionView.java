package pgp.views;


import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.PublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.jdesktop.application.FrameView;
import pgp.PgpApp;
import pgp.algorithm.PGPDecrypt;
import pgp.algorithm.PGPEncrypt;
import pgp.keys.KeyInfo;
import pgp.keys.KeyManagement;

import java.awt.*;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import static pgp.views.ConfigureEncryptionView.TEMP_MESSAGE_FILENAME;

public class ConfigureDecryptionView extends FrameView {

    private JPanel mainPanel;
    private JTextArea ciphertextJTextArea;
    private JCheckBox enableEncrypt;
    private JPasswordField passphraseField;
    
    private PgpApp app;

    private String passphrase;

    private ErrorView errorView;
    private PassphraseInputView passphraseInputView;
    private PGPSecretKey secretKey;

    private int algorithm;


    public ConfigureDecryptionView(PgpApp app) {
        super(app);
        getFrame().setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getFrame().setResizable(false);
        getFrame().pack();
        this.app = app;
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("RadioButton.focus", new Color(0, 0, 0, 0));
        UIManager.put("CheckBox.focus", new Color(0, 0, 0, 0));
        initComponents();
    }

    private void initComponents(){

        passphraseInputView = new PassphraseInputView(getFrame(),"Enter sender passphrase", this, null, null);
        passphraseInputView.setVisible(false);

        errorView = new ErrorView(getFrame());
        errorView.setVisible(false);

        this.mainPanel = new JPanel();
        this.mainPanel.setPreferredSize(new Dimension(750, 650));

        JLabel headingLabel = new JLabel("PGP Decryption");
        headingLabel.setFont(PgpApp.headingFont);

        JLabel ciphertextLabel = new JLabel("Message");
        ciphertextLabel.setFont(PgpApp.labelFont);

        enableEncrypt = new JCheckBox("Enable encryption view");
        enableEncrypt.setFont(PgpApp.labelFont);

        passphraseField = new JPasswordField();
        passphraseField.setFont(PgpApp.textfieldFont);
        passphraseField.setColumns(10);
        passphraseField.setText("aleksa123");

        JLabel passphraseLabel = new JLabel("Passphrase");
        passphraseLabel.setFont(PgpApp.labelFont);

        ciphertextJTextArea = new JTextArea();
        ciphertextJTextArea.setFont(PgpApp.textfieldFont);
        ciphertextJTextArea.setColumns(30);
        ciphertextJTextArea.setRows(20);
        ciphertextJTextArea.setLineWrap(true);
        JScrollPane ciphertextScrollPane = new JScrollPane(ciphertextJTextArea);

        JButton beginButton = new JButton("BEGIN");
        beginButton.setFont(PgpApp.buttonFont);
        beginButton.addActionListener(e -> begin());

        GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
        this.mainPanel.setLayout(mainPanelLayout);

        JButton backButton = new JButton("<");
        backButton.setFont(PgpApp.buttonFont);
        backButton.addActionListener(e -> back());

        JLabel importMessageLabel = new JLabel("Import message");
        importMessageLabel.setFont(PgpApp.labelFont);

        JButton importMessage = new JButton("Choose file");
        importMessage.setFont(PgpApp.buttonFont);
        importMessage.addActionListener(e -> importMessage());

        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.LEADING)
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addGap(20)
                                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(Alignment.TRAILING, mainPanelLayout.createParallelGroup(Alignment.LEADING)
                                                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.TRAILING)
                                                                        .addComponent(ciphertextLabel)
                                                                        .addComponent(importMessageLabel)
                                                                        .addComponent(passphraseLabel)
                                                                )))
                                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                                        .addGap(32))
                                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                                .addGap(18)
                                                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.TRAILING, false)
                                                                        .addComponent(passphraseField, Alignment.LEADING, 250,250,250)
                                                                        .addComponent(enableEncrypt, Alignment.LEADING)
                                                                        .addComponent(importMessage, Alignment.LEADING)
                                                                        .addComponent(ciphertextScrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)))))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addComponent(backButton)
                                                .addGap(215)
                                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                                        .addGap(10))
                                                        .addComponent(headingLabel)))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addGap(335)
                                                .addComponent(beginButton, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
                                        )
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addGap(70)
                                                ))
                                .addContainerGap(52, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(backButton)
                                .addComponent(headingLabel)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGap(35)
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.BASELINE))
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(ciphertextLabel)
                                        .addComponent(ciphertextScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(importMessageLabel)
                                        .addComponent(importMessage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(passphraseLabel)
                                        .addComponent(passphraseField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addComponent(enableEncrypt)
                                .addPreferredGap(ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                                .addGap(10)
                                .addComponent(beginButton)
                                .addPreferredGap(ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                                .addGap(15))
        );
        setComponent(this.mainPanel);
    }

    private void importMessage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("C:\\Users\\Aleksa\\Desktop"));
        int response = fileChooser.showSaveDialog(null);
        if(response == JFileChooser.APPROVE_OPTION) {

            File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
            Scanner myReader = null;
            try {
                StringBuilder sb = new StringBuilder();
                myReader = new Scanner(file);
                while (myReader.hasNextLine()) {
                    sb.append(myReader.nextLine());
                    sb.append("\n");
                }
                myReader.close();
                this.ciphertextJTextArea.setText(sb.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void begin() {

        if(this.ciphertextJTextArea.getText().length() == 0) {
            errorView.setMessage("Ciphertext isn\'t provided!");
            errorView.setVisible(true);
            return;
        }

        File file = new File(TEMP_MESSAGE_FILENAME);
        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(TEMP_MESSAGE_FILENAME);
            fileWriter.write(ciphertextJTextArea.getText());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        FileInputStream fileInputStream = null;
        byte[] data = null;

        try {
            fileInputStream = new FileInputStream(file);

            byte[] bytes = Files.readAllBytes(Paths.get(TEMP_MESSAGE_FILENAME));
            String str = new String (bytes);


            if(!(str.charAt(0) == '-'))
                data = hexStringToByteArray(str);
            else
                data = fileInputStream.readAllBytes();

            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean radixEnabled = true;
        byte[] radix64Data = null;
        try {
            byte[] dataOld = data;
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            data  = PGPUtil.getDecoderStream(byteArrayInputStream).readAllBytes();
            radix64Data = data;
            if(Arrays.equals(dataOld, data)){
                radixEnabled = false;
            }
        } catch (IOException e) {
            radixEnabled = false;
        }

        Object object = null;

        try {
            PGPObjectFactory objectFactory = new PGPObjectFactory(data, new BcKeyFingerprintCalculator());
            object = objectFactory.nextObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean encryptionEnabled = false;
        byte[] encryptedData = null;
        if(object instanceof PGPEncryptedDataList) {
            encryptionEnabled = true;
            passphrase = "";
            try {
                data = checkPassphrase(data, new String(passphraseField.getPassword()));
                encryptedData = data;
            } catch (PGPException | IOException ex) {
                errorView.setMessage("Passphrase isn\'t correct!");
                errorView.setVisible(true);
                return;
            }
        }

        PGPDecrypt decrypt;
        try {
            decrypt = new PGPDecrypt(this.ciphertextJTextArea.getText(), file, new String(passphraseField.getPassword()), data, radixEnabled, encryptionEnabled, radix64Data, encryptedData, this.secretKey);

            if(enableEncrypt.isSelected()){

                File file1 = new File(TEMP_MESSAGE_FILENAME);
                try {
                    file1.createNewFile();
                    FileWriter fileWriter = new FileWriter(TEMP_MESSAGE_FILENAME);
                    fileWriter.write(decrypt.getDecryptedMessage());
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                PGPEncrypt encryption;
                try {
                    encryption = new PGPEncrypt(decrypt.getDecryptedMessage(), file1, decrypt.isEncryptEnabled(), decrypt.isSignEnabled(),
                            decrypt.isCompressionEnabled(), decrypt.isRadix64Enabled(), 2,
                            decrypt.getPublicKeyInfo(), decrypt.getSecretKeyInfo(), passphrase);

                    app.showEncryptionAndDecryptionView(this, encryption, decrypt);
                }
                catch (PGPException e) {
                    errorView.setMessage(e.getMessage());
                    errorView.setVisible(true);
                }
            }
            else
                app.showPgpDecryptView(this, decrypt);

        } catch (PGPException e) {
            errorView.setMessage("Error!");
            errorView.setVisible(true);
        }
    }

    private byte[] checkPassphrase(byte[] data, String passphrase) throws IOException, PGPException {
        JcaPGPObjectFactory objectFactory = new JcaPGPObjectFactory(data);
        Object object = objectFactory.nextObject();

        if (object instanceof PGPEncryptedDataList) {

            PGPEncryptedDataList pgpEncryptedDataList = (PGPEncryptedDataList) object;
            Iterator<PGPEncryptedData> iterator = pgpEncryptedDataList.getEncryptedDataObjects();

            PGPPublicKeyEncryptedData pgpPublicKeyEncryptedData;
            if (iterator.hasNext()) {

                pgpPublicKeyEncryptedData = (PGPPublicKeyEncryptedData) iterator.next();
                this.algorithm = pgpPublicKeyEncryptedData.getAlgorithm();
                PGPSecretKeyRing secretKeyRing = KeyManagement.getSecretKeyRing(pgpPublicKeyEncryptedData.getKeyID());

                if (secretKeyRing != null) {

                    Iterator<PGPSecretKey> secretKeyIterator = secretKeyRing.getSecretKeys();
                    PGPSecretKey masterKey = secretKeyIterator.next();
                    PGPSecretKey secretKey = secretKeyIterator.next();
                    this.secretKey = masterKey;

                    PGPPrivateKey privateKey = secretKey.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(passphrase.toCharArray()));
                    PublicKeyDataDecryptorFactory dataDecryptorFactory = new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC").build(privateKey);
                    InputStream inputStream = pgpPublicKeyEncryptedData.getDataStream(dataDecryptorFactory);

                    return inputStream.readAllBytes();
                } else {
                    throw new PGPException("");
                }
            }
        } else {
            throw new PGPException("");
        }
        return null;
    }

    private void getUserPassphrase() {
        passphraseInputView.setVisible(true);
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private void back(){
        app.showEncDecView(this);
    }
}
