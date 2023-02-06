package pgp.views;

import org.bouncycastle.openpgp.PGPException;
import pgp.PgpApp;
import pgp.keys.KeyManagement;
import pgp.keys.UserInfo;

import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

public class GenerateKeyView extends JDialog {

    private JTextField nameTextField;
    private JTextField emailTextField;
    private JPasswordField passphraseTextField;

    private JPanel mainPanel;
    private JComboBox signAlgorithmComboBox;
    private JComboBox encryptionAlgorithmComboBox;

    public GenerateKeyView(Frame parent) {
        super(parent, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Key Generator");
        setResizable(false);
        setPreferredSize(new Dimension(420, 450));
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        this.mainPanel = new JPanel();
        this.mainPanel.setPreferredSize(new Dimension(420, 450));

        JLabel heading = new JLabel("Key Generator");
        heading.setFont(PgpApp.headingFont);

        JLabel nameLabel = new JLabel("Name");
        nameLabel.setFont(PgpApp.labelFont);

        nameTextField = new JTextField();
        nameTextField.setFont(PgpApp.textfieldFont);
        nameTextField.setColumns(10);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(PgpApp.labelFont);

        emailTextField = new JTextField();
        emailTextField.setFont(PgpApp.textfieldFont);
        emailTextField.setColumns(10);

        JLabel passphraseLabel = new JLabel("Passphrase");
        passphraseLabel.setFont(PgpApp.labelFont);

        passphraseTextField = new JPasswordField();
        passphraseTextField.setFont(PgpApp.textfieldFont);
        passphraseTextField.setColumns(10);

        JLabel signAlgorithLabel = new JLabel("Sign Algorithm");
        signAlgorithLabel.setFont(PgpApp.labelFont);

        signAlgorithmComboBox = new JComboBox();
        signAlgorithmComboBox.setFont(PgpApp.textfieldFont);
        signAlgorithmComboBox.addItem("RSA-1024");
        signAlgorithmComboBox.addItem("RSA-2048");
        signAlgorithmComboBox.addItem("RSA-4096");
        signAlgorithmComboBox.addItem("DSA-1024");
        signAlgorithmComboBox.addItem("DSA-2048");


        JLabel encryptionAlgorithmLabel = new JLabel("Encryption Algorithm");
        encryptionAlgorithmLabel.setFont(PgpApp.labelFont);

        encryptionAlgorithmComboBox = new JComboBox();
        encryptionAlgorithmComboBox.setFont(PgpApp.textfieldFont);
        encryptionAlgorithmComboBox.addItem("RSA-1024");
        encryptionAlgorithmComboBox.addItem("RSA-2048");
        encryptionAlgorithmComboBox.addItem("RSA-4096");
        encryptionAlgorithmComboBox.addItem("ElGamal-1024");
        encryptionAlgorithmComboBox.addItem("ElGamal-2048");
        encryptionAlgorithmComboBox.addItem("ElGamal-4096");


        JButton generateButton = new JButton("GENERATE");
        generateButton.setFont(PgpApp.buttonFont);
        generateButton.addActionListener(e -> generateKeyPair());

        GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
        this.mainPanel.setLayout(mainPanelLayout);

        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(22)
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.LEADING)
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(encryptionAlgorithmLabel)
                                                        .addComponent(signAlgorithLabel))
                                                .addGap(18)
                                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.LEADING, false)
                                                        .addComponent(signAlgorithmComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(encryptionAlgorithmComboBox, 0, 150, Short.MAX_VALUE)))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(passphraseLabel)
                                                        .addComponent(emailLabel)
                                                        .addComponent(nameLabel))
                                                .addGap(18)
                                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(nameTextField, GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                                                        .addComponent(emailTextField, GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                                                        .addComponent(passphraseTextField))))
                                .addContainerGap(21, GroupLayout.PREFERRED_SIZE))
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(90)
                                .addComponent(heading)
                                .addContainerGap(76, Short.MAX_VALUE))
                        .addGroup(Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                .addContainerGap(65, Short.MAX_VALUE)
                                .addComponent(generateButton)
                                .addGap(155))
        );
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(26)
                                .addComponent(heading)
                                .addGap(29)
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(nameLabel)
                                        .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18)
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(emailLabel)
                                        .addComponent(emailTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18)
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(passphraseLabel)
                                        .addComponent(passphraseTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18)
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(signAlgorithLabel)
                                        .addComponent(signAlgorithmComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18)
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(encryptionAlgorithmLabel)
                                        .addComponent(encryptionAlgorithmComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(40)
                                .addComponent(generateButton)
                                .addContainerGap(45, Short.MAX_VALUE))
        );
        setContentPane(mainPanel);
    }

    private void generateKeyPair(){

        String singAlgorithm;
        int signKeySize;
        String encryptionAlgorithm;
        int encryptionKeySize;

        String selectedSignAlgorithm = (String) signAlgorithmComboBox.getSelectedItem();
        singAlgorithm = selectedSignAlgorithm.substring(0, selectedSignAlgorithm.length()-5);
        signKeySize = Integer.parseInt(selectedSignAlgorithm.substring(selectedSignAlgorithm.length()-4));

        String selectedEncryptionAlgorithm = (String) encryptionAlgorithmComboBox.getSelectedItem();
        encryptionAlgorithm = selectedEncryptionAlgorithm.substring(0, selectedEncryptionAlgorithm.length()-5);
        encryptionKeySize = Integer.parseInt(selectedEncryptionAlgorithm.substring(selectedEncryptionAlgorithm.length()-4));

        try {
            KeyManagement.createNewKeyPair(new UserInfo(nameTextField.getText(), emailTextField.getText(), new String(passphraseTextField.getPassword())),
                    singAlgorithm, signKeySize, encryptionAlgorithm, encryptionKeySize);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | PGPException e) {
            e.printStackTrace();
        }

        dispose();
    }
}
