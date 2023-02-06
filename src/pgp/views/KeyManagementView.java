package pgp.views;

import javafx.collections.ObservableList;
import org.bouncycastle.openpgp.PGPException;
import org.jdesktop.application.FrameView;
import pgp.PgpApp;
import pgp.keys.KeyManagement;
import pgp.keys.KeyInfo;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class KeyManagementView extends FrameView {

    private DefaultTableModel publicKeyModel = new DefaultTableModel();
    private DefaultTableModel secretKeyModel = new DefaultTableModel();

    private ObservableList<KeyInfo> publicKeys;
    private ObservableList<KeyInfo> secretKeys;

    private JPanel mainPanel;
    private JTable publicKeyTable;
    private JTable secretKeyTable;
    private PgpApp app;

    private String passphrase;
    private PassphraseInputView passphraseInputView;

    private GenerateKeyView generateKeyView;
    private ErrorView errorView;

    public KeyManagementView(PgpApp app) {
        super(app);
        getFrame().setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getFrame().setResizable(false);
        getFrame().pack();
        this.app = app;
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("RadioButton.focus", new Color(0, 0, 0, 0));
        UIManager.put("CheckBox.focus", new Color(0, 0, 0, 0));
        UIManager.put("TabbedPane.focus", new Color(0, 0, 0, 0));
        initComponents();
    }

    private void initComponents() {

        errorView = new ErrorView(getFrame());
        errorView.setVisible(false);

        passphraseInputView = new PassphraseInputView(getFrame(),"Enter passphrase", null, this, null);
        passphraseInputView.setVisible(false);

        generateKeyView = new GenerateKeyView(getFrame());
        generateKeyView.setVisible(false);

        publicKeys = KeyManagement.getPublicKeysList();
        secretKeys = KeyManagement.getSecretKeysList();

        this.mainPanel = new JPanel();
        this.mainPanel.setPreferredSize(new Dimension(800, 400));

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        GroupLayout groupLayout = new GroupLayout(mainPanel);
        this.mainPanel.setLayout(groupLayout);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 810, GroupLayout.PREFERRED_SIZE)
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
        );


        JPanel publicKeyPanel = new JPanel();
        tabbedPane.addTab("Public Key Ring", null, publicKeyPanel, null);
        tabbedPane.setFont(PgpApp.checkboxFont);

        createPublicKeyTable();

        JScrollPane sp = new JScrollPane(publicKeyTable);


        JButton generateNewKey = new JButton("Generate New Key Pair");
        generateNewKey.setFont(PgpApp.buttonFont);
        generateNewKey.addActionListener(e -> generateNewKey());

        JButton publicDelete = new JButton("Delete");
        publicDelete.setFont(PgpApp.buttonFont);
        publicDelete.addActionListener(e -> deletePublicKey());

        JButton publicImport = new JButton("Import");
        publicImport.setFont(PgpApp.buttonFont);
        publicImport.addActionListener(e -> importKey());

        JButton publicExport = new JButton("Export");
        publicExport.setFont(PgpApp.buttonFont);
        publicExport.addActionListener(e -> exportPublicKey());

        JButton backButton = new JButton("<");
        backButton.setFont(PgpApp.buttonFont);
        backButton.addActionListener(e -> back());

        GroupLayout publicKeysLayout = new GroupLayout(publicKeyPanel);
        publicKeysLayout.setHorizontalGroup(
                publicKeysLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(publicKeysLayout.createSequentialGroup()
                                .addGroup(publicKeysLayout.createParallelGroup(Alignment.LEADING)
                                        .addGroup(publicKeysLayout.createSequentialGroup()
                                            .addContainerGap()
                                            .addComponent(generateNewKey)
                                            .addPreferredGap(ComponentPlacement.RELATED, 201, Short.MAX_VALUE)
                                            .addComponent(publicDelete)
                                            .addPreferredGap(ComponentPlacement.UNRELATED)
                                            .addComponent(publicImport)
                                            .addPreferredGap(ComponentPlacement.UNRELATED)
                                            .addComponent(publicExport)
                                            .addGap(30))
                                        .addGroup(publicKeysLayout.createSequentialGroup()
                                                .addGap(50)
                                                .addComponent(sp, GroupLayout.PREFERRED_SIZE, 700, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(backButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );
        publicKeysLayout.setVerticalGroup(
                publicKeysLayout.createParallelGroup(Alignment.TRAILING)
                        .addGroup(publicKeysLayout.createSequentialGroup()
                                .addComponent(backButton)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(sp, GroupLayout.PREFERRED_SIZE, 260, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                                .addGroup(publicKeysLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(generateNewKey)
                                        .addComponent(publicDelete)
                                        .addComponent(publicImport)
                                        .addComponent(publicExport))
                                .addContainerGap())
        );
        publicKeyPanel.setLayout(publicKeysLayout);

        JPanel secretKeyPanel = new JPanel();
        tabbedPane.addTab("Secret Key Ring", null, secretKeyPanel, null);

        createSecretKeyTable();

        JScrollPane sp2 = new JScrollPane(secretKeyTable);


        JButton secretKeyDelete = new JButton("Delete");
        secretKeyDelete.setFont(PgpApp.buttonFont);
        secretKeyDelete.addActionListener(e -> deleteSecretKey());

        JButton importSecretKey = new JButton("Import");
        importSecretKey.setFont(PgpApp.buttonFont);
        importSecretKey.addActionListener(e -> importKey());

        JButton exportSecretKey = new JButton("Export");
        exportSecretKey.setFont(PgpApp.buttonFont);
        exportSecretKey.addActionListener(e -> exportSecretKey());

        JButton backButton2 = new JButton("<");
        backButton2.setFont(PgpApp.buttonFont);
        backButton2.addActionListener(e -> back());

        GroupLayout groupLayout_1 = new GroupLayout(secretKeyPanel);
        groupLayout_1.setHorizontalGroup(
                groupLayout_1.createParallelGroup(Alignment.TRAILING)
                        .addGroup(groupLayout_1.createSequentialGroup()
                                .addGap(50)
                                .addComponent(sp2, GroupLayout.PREFERRED_SIZE, 700, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(39, Short.MAX_VALUE))
                        .addGroup(groupLayout_1.createSequentialGroup()
                                .addContainerGap(336, Short.MAX_VALUE)
                                .addComponent(secretKeyDelete, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addComponent(importSecretKey, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addComponent(exportSecretKey, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
                                .addGap(30))
                        .addGroup(Alignment.LEADING, groupLayout_1.createSequentialGroup()
                                .addComponent(backButton2, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(548, Short.MAX_VALUE))
        );
        groupLayout_1.setVerticalGroup(
                groupLayout_1.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout_1.createSequentialGroup()
                                .addComponent(backButton2, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(sp2, GroupLayout.PREFERRED_SIZE, 260, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                                .addGroup(groupLayout_1.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(secretKeyDelete)
                                        .addComponent(importSecretKey)
                                        .addComponent(exportSecretKey))
                                .addContainerGap())
        );
        secretKeyPanel.setLayout(groupLayout_1);

        setComponent(this.mainPanel);
    }


    private void back(){
        app.showEncDecView(this);
    }


    private void createPublicKeyTable(){
        publicKeyTable = new JTable();
        publicKeyTable.getTableHeader().setReorderingAllowed(false);
        publicKeyTable.setDefaultEditor(Object.class, null);
        publicKeyTable.setModel(publicKeyModel);
        publicKeyTable.setFont(PgpApp.checkboxFont);
        publicKeyTable.getTableHeader().setFont(PgpApp.labelFont);


        publicKeyModel.addColumn("User");
        publicKeyModel.addColumn("Email");
        publicKeyModel.addColumn("Key ID");

        setPublicKeyModel();
    }

    private void setPublicKeyModel(){
        int size = publicKeyModel.getRowCount();
        for(int i = 0; i < size; i++) {
            publicKeyModel.removeRow(0);
        }
        for(KeyInfo k: KeyManagement.getPublicKeysList()) {
            publicKeyModel.addRow(new Object[]{
                    k.getUsername(),
                    k.getEmail(),
                    k.getKeyId()
            });
        }
    }


    private void createSecretKeyTable(){
        secretKeyTable = new JTable();
        secretKeyTable.getTableHeader().setReorderingAllowed(false);
        secretKeyTable.setDefaultEditor(Object.class, null);
        secretKeyTable.setModel(secretKeyModel);
        secretKeyTable.setFont(PgpApp.checkboxFont);
        secretKeyTable.getTableHeader().setFont(PgpApp.labelFont);

        secretKeyModel.addColumn("User");
        secretKeyModel.addColumn("Email");
        secretKeyModel.addColumn("Key ID");

        setSecretKeyModel();
    }

    private void setSecretKeyModel(){
        int size = secretKeyModel.getRowCount();
        for(int i = 0; i < size; i++) {
            secretKeyModel.removeRow(0);
        }
        for(KeyInfo k: KeyManagement.getSecretKeysList()) {
            secretKeyModel.addRow(new Object[]{
                    k.getUsername(),
                    k.getEmail(),
                    k.getKeyId()
            });
        }
    }

    private void deletePublicKey(){
        int column = 2;
        int row = this.publicKeyTable.getSelectedRow();
        if(row == -1)
            return;
        String id = (String) this.publicKeyTable.getValueAt(row, column);
        KeyInfo keyInfo = null;
        for(KeyInfo k: publicKeys){
            if(k.getKeyId().equals(id)){
                keyInfo = k;
            }
        }

        if (keyInfo != null) {
            try {
                KeyManagement.deletePublicKey(keyInfo);
                KeyManagement.getPublicKeysList().remove(keyInfo);
            } catch (PGPException e) {
                errorView.setMessage("Error - delete public key");
                errorView.setVisible(true);
            }
        }

        setPublicKeyModel();
    }

    private void deleteSecretKey(){
        int column = 2;
        int row = this.secretKeyTable.getSelectedRow();
        if(row == -1)
            return;
        String id = (String) this.secretKeyTable.getValueAt(row, column);
        KeyInfo keyInfo = null;
        for(KeyInfo k: secretKeys){
            if(k.getKeyId().equals(id)){
                keyInfo = k;
            }
        }

        if (keyInfo != null) {
            try {
                if(KeyManagement.isEncrypted(keyInfo)){
                    passphraseInputView.setVisible(true);
                    KeyManagement.deleteSecretKey(keyInfo, this.passphrase);
                }
                else {
                    KeyManagement.deleteSecretKey(keyInfo);
                }

            } catch (PGPException e) {
                errorView.setMessage("Passphrase isn\'t correct!");
                errorView.setVisible(true);
            }
        }

        setSecretKeyModel();
    }

    private void exportPublicKey(){
        int column = 2;
        int row = this.publicKeyTable.getSelectedRow();
        if(row == -1)
            return;
        String id = (String) this.publicKeyTable.getValueAt(row, column);
        KeyInfo keyInfo = null;
        for(KeyInfo k: publicKeys){
            if(k.getKeyId().equals(id)){
                keyInfo = k;
            }
        }
        exportKey(keyInfo);
    }

    private void exportSecretKey(){
        int column = 2;
        int row = this.secretKeyTable.getSelectedRow();
        if(row == -1)
            return;
        String id = (String) this.secretKeyTable.getValueAt(row, column);
        KeyInfo keyInfo = null;
        for(KeyInfo k: secretKeys){
            if(k.getKeyId().equals(id)){
                keyInfo = k;
            }
        }
        exportKey(keyInfo);
    }

    private void exportKey(KeyInfo keyInfo){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("C:\\Users\\Aleksa\\Desktop"));
        int response = fileChooser.showSaveDialog(null);
        if(response == JFileChooser.APPROVE_OPTION) {
            File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
            try {
                KeyManagement.exportKeyToFile(keyInfo, file);
            } catch (IOException | PGPException e) {
                errorView.setMessage("Error - export key");
                errorView.setVisible(true);
            }
        }
    }

    private void importKey(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("C:\\Users\\Aleksa\\Desktop"));
        int response = fileChooser.showOpenDialog(null);
        if(response == JFileChooser.APPROVE_OPTION) {

            File file = new File(fileChooser.getSelectedFile().getAbsolutePath());

            try {
                KeyManagement.importKey(file);
            } catch (PGPException e) {
                errorView.setMessage(e.getMessage());
                errorView.setVisible(true);
            } catch (IOException e) {
                errorView.setMessage("Error reading file");
                errorView.setVisible(true);
            }
        }
        setPublicKeyModel();
        setSecretKeyModel();
    }

    private void generateNewKey(){
        generateKeyView.setVisible(true);
        setPublicKeyModel();
        setSecretKeyModel();
    }


    public void setPassphrase(String passphrase){
        this.passphrase = passphrase;
    }

}
