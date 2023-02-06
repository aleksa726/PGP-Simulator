package pgp.views;

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
import java.util.Scanner;

import static pgp.views.ConfigureDecryptionView.hexStringToByteArray;

public class ConfigureEncryptionView extends FrameView {

    public static final String TEMP_MESSAGE_FILENAME = "message.txt";

    private JTextArea message;
    private JComboBox<KeyInfo> publicKey;
    private JComboBox<KeyInfo> secretKey;
    private JPasswordField passphraseField;

    private PgpApp app;

    private JPanel mainPanel;

    private JCheckBox encrypt;
    private JCheckBox sign;
    private JCheckBox compress;
    private JCheckBox radix;

    private JRadioButton cast;
    private JRadioButton des;
    private JRadioButton idea;
    private ButtonGroup buttonGroup;

    private JCheckBox enableDecrypt;

    private ErrorView errorView;

    private PGPSecretKey privateKey;

    private PassphraseInputView passphraseInputView;

    private String passphrase;

    public ConfigureEncryptionView(PgpApp app) {
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
        passphraseInputView = new PassphraseInputView(getFrame(),"Enter receiver passphrase", null, null, this);
        passphraseInputView.setVisible(false);

        this.mainPanel = new JPanel();
        this.mainPanel.setPreferredSize(new Dimension(580, 670));

        errorView = new ErrorView(getFrame());
        errorView.setVisible(false);

        JLabel headingLabel = new JLabel("PGP Encryption");
        headingLabel.setFont(PgpApp.headingFont);

        JLabel subheadingLabel = new JLabel("Choose algorithm parameters:");
        subheadingLabel.setFont(PgpApp.subheadingFont);

        encrypt = new JCheckBox("Encrypt");
        encrypt.setFont(PgpApp.checkboxFont);
        sign = new JCheckBox("Sign");
        sign.setFont(PgpApp.checkboxFont);
        compress = new JCheckBox("Compress");
        compress.setFont(PgpApp.checkboxFont);
        radix = new JCheckBox("Radix-64");
        radix.setFont(PgpApp.checkboxFont);

        enableDecrypt = new JCheckBox("Enable decryption view");
        enableDecrypt.setFont(PgpApp.labelFont);

        cast = new JRadioButton("CAST");
        cast.setFont(PgpApp.checkboxFont);
        des = new JRadioButton("3DES");
        des.setFont(PgpApp.checkboxFont);
        idea = new JRadioButton("IDEA");
        idea.setFont(PgpApp.checkboxFont);
        des.setSelected(true);

        JLabel plaintextLabel = new JLabel("Message");
        plaintextLabel.setFont(PgpApp.labelFont);

        message = new JTextArea();
        message.setFont(PgpApp.textfieldFont);
        message.setColumns(25);
        message.setRows(10);
        JScrollPane plaintextScrollPane = new JScrollPane(message);

        passphraseField = new JPasswordField();
        passphraseField.setFont(PgpApp.textfieldFont);
        passphraseField.setColumns(10);

        JLabel keyLabel = new JLabel("Public key");
        keyLabel.setFont(PgpApp.labelFont);

        JLabel secretKeyLabel = new JLabel("Secret key");
        secretKeyLabel.setFont(PgpApp.labelFont);

        JLabel passphraseLabel = new JLabel("Passphrase");
        passphraseLabel.setFont(PgpApp.labelFont);

        publicKey = new JComboBox<>();
        publicKey.setFont(PgpApp.textfieldFont);
        publicKey.addItem(null);
        for(KeyInfo k: KeyManagement.getPublicKeysList()) {
            publicKey.addItem(k);
        }

        secretKey = new JComboBox<>();
        secretKey.setFont(PgpApp.textfieldFont);
        secretKey.addItem(null);
        for(KeyInfo k: KeyManagement.getSecretKeysList()) {
            secretKey.addItem(k);
        }

        buttonGroup = new ButtonGroup();
        buttonGroup.add(cast);
        buttonGroup.add(des);
        buttonGroup.add(idea);

        this.encrypt.setSelected(true);
        this.sign.setSelected(true);
        this.compress.setSelected(true);
        this.radix.setSelected(true);

        this.encrypt.addActionListener(e -> {
            des.setEnabled(encrypt.isSelected());
            idea.setEnabled(encrypt.isSelected());
            cast.setEnabled(encrypt.isSelected());
            publicKey.setEnabled(encrypt.isSelected());
        });

        this.sign.addActionListener(e -> {
            secretKey.setEnabled(sign.isSelected());
            passphraseField.setEnabled(sign.isSelected());
        });

        JButton beginButton = new JButton("BEGIN");
        beginButton.setFont(PgpApp.buttonFont);
        beginButton.addActionListener(e -> begin());

        JButton backButton = new JButton("<");
        backButton.setFont(PgpApp.buttonFont);
        backButton.addActionListener(e -> back());

        JLabel importMessageLabel = new JLabel("Import message");
        importMessageLabel.setFont(PgpApp.labelFont);

        JButton importMessage = new JButton("Choose file");
        importMessage.setFont(PgpApp.buttonFont);
        importMessage.addActionListener(e -> importMessage());

        GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
        this.mainPanel.setLayout(mainPanelLayout);

        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.LEADING)
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addGap(60)
                                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(encrypt)
                                                        .addComponent(cast, Alignment.CENTER)
                                                        .addComponent(des, Alignment.CENTER)
                                                        .addComponent(idea, Alignment.CENTER)
                                                        .addGroup(Alignment.TRAILING, mainPanelLayout.createParallelGroup(Alignment.LEADING)
                                                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.TRAILING)
                                                                        .addComponent(passphraseLabel)
                                                                        .addComponent(secretKeyLabel)
                                                                        .addComponent(keyLabel)
                                                                        .addComponent(plaintextLabel)
                                                                        .addComponent(importMessageLabel)
                                                                )))
                                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                                .addGap(20)
                                                                .addComponent(sign)
                                                                .addGap(42)
                                                                .addComponent(compress)
                                                                .addGap(42)
                                                                .addComponent(radix))
                                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                                .addGap(18)
                                                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.TRAILING, false)
                                                                        .addComponent(passphraseField, Alignment.LEADING)
                                                                        .addComponent(secretKey, Alignment.LEADING)
                                                                        .addComponent(publicKey, Alignment.LEADING)
                                                                        .addComponent(importMessage, Alignment.LEADING)
                                                                        .addComponent(enableDecrypt, Alignment.LEADING)
                                                                        .addComponent(plaintextScrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)))))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addComponent(backButton)
                                                .addGap(130)
                                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                                .addGap(8)
                                                                .addComponent(subheadingLabel))
                                                        .addComponent(headingLabel)
                                                        ))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addGap(250)
                                                .addComponent(beginButton, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
                                                )
                                       )
                                .addContainerGap(52, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(backButton)
                                .addComponent(headingLabel)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGap(10)
                                .addComponent(subheadingLabel)
                                .addGap(15)
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(encrypt)
                                        .addComponent(sign)
                                        .addComponent(compress)
                                        .addComponent(radix))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(cast)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(des)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(idea)
                                .addGap(20)
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(plaintextLabel)
                                        .addComponent(plaintextScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))

                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(importMessageLabel)
                                        .addComponent(importMessage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(keyLabel)
                                        .addComponent(publicKey, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(secretKeyLabel)
                                        .addComponent(secretKey, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(passphraseLabel)
                                        .addComponent(passphraseField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addComponent(enableDecrypt)
                                .addPreferredGap(ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                                .addComponent(beginButton)
                                .addPreferredGap(ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                                .addGap(30))
        );
        setComponent(this.mainPanel);
    }

    private void importMessage(){
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
                this.message.setText(sb.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void begin() {
        if(this.message.getText().length() == 0) {
            errorView.setMessage("Message isn\'t provided!");
            errorView.setVisible(true);
            return;
        }

        boolean encryptEnabled = encrypt.isSelected();
        if(encryptEnabled && publicKey.getSelectedItem() == null){
            errorView.setMessage("Public key isn\'t provided!");
            errorView.setVisible(true);
            return;
        }

        boolean signEnabled = sign.isSelected();
        if(signEnabled && secretKey.getSelectedItem() == null){
            errorView.setMessage("Secret key isn\'t provided!");
            errorView.setVisible(true);
            return;
        }
        if(signEnabled && passphraseField.getText().length() == 0){
            errorView.setMessage("Passphrase isn\'t provided!");
            errorView.setVisible(true);
            return;
        }

        boolean compressionEnabled = compress.isSelected();
        boolean radix64Enabled = radix.isSelected();

        int algorithm = -1;
        if(des.isSelected())
            algorithm = PGPEncryptedData.TRIPLE_DES;
        else if(idea.isSelected())
            algorithm = PGPEncryptedData.IDEA;
        else if(cast.isSelected())
            algorithm = PGPEncryptedData.CAST5;

        File file = new File(TEMP_MESSAGE_FILENAME);
        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(TEMP_MESSAGE_FILENAME);
            fileWriter.write(message.getText());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PGPEncrypt encryption;
        try {
            encryption = new PGPEncrypt(this.message.getText(), file, encryptEnabled, signEnabled, compressionEnabled, radix64Enabled, algorithm,
                    (KeyInfo) publicKey.getSelectedItem(), (KeyInfo) secretKey.getSelectedItem(), new String(passphraseField.getPassword()));

            if(enableDecrypt.isSelected()) {
                PGPDecrypt decryption = initDecrypt(encryption);
                if(decryption != null)
                    app.showEncryptionAndDecryptionView(this, encryption, decryption);
            }
            else
                app.showPgpView(this, encryption);

        } catch (PGPException e) {
            errorView.setMessage(e.getMessage());
            errorView.setVisible(true);
        }
    }

    private void back(){
        app.showEncDecView(this);
    }

    private PGPDecrypt initDecrypt(PGPEncrypt encrypt){
        File file = new File(ConfigureEncryptionView.TEMP_MESSAGE_FILENAME);
        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(ConfigureEncryptionView.TEMP_MESSAGE_FILENAME);
            fileWriter.write(encrypt.outputData);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        FileInputStream fileInputStream = null;
        byte[] data = null;

        try {
            fileInputStream = new FileInputStream(file);

            byte[] bytes = Files.readAllBytes(Paths.get(ConfigureEncryptionView.TEMP_MESSAGE_FILENAME));
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
                getUserPassphrase();
                data = checkPassphrase(data, passphrase);
                encryptedData = data;
            } catch (PGPException | IOException ex) {
                errorView.setMessage("Passphrase isn\'t correct!");
                errorView.setVisible(true);
                return null;
            }
        }

        PGPDecrypt decrypt = null;
        try {
            decrypt = new PGPDecrypt(encrypt.outputData, file, passphrase, data, radixEnabled, encryptionEnabled, radix64Data, encryptedData, this.privateKey);

        } catch (PGPException e) {
            errorView.setMessage("Error!");
            errorView.setVisible(true);
        }

        return decrypt;
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
                PGPSecretKeyRing secretKeyRing = KeyManagement.getSecretKeyRing(pgpPublicKeyEncryptedData.getKeyID());

                if (secretKeyRing != null) {

                    Iterator<PGPSecretKey> secretKeyIterator = secretKeyRing.getSecretKeys();
                    PGPSecretKey masterKey = secretKeyIterator.next();
                    PGPSecretKey secretKey = secretKeyIterator.next();
                    this.privateKey = masterKey;

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
}
