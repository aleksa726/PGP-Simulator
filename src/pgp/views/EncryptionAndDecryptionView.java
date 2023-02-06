package pgp.views;

import org.jdesktop.application.FrameView;
import pgp.PgpApp;
import pgp.algorithm.PGPDecrypt;
import pgp.algorithm.PGPEncrypt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EncryptionAndDecryptionView extends FrameView {

    private static final int WIDTH = 760;
    private static final int HEIGHT = 500;

    private JTextArea messageTextArea;
    private JButton secretKeyButton;
    private JTextArea signedMessage;
    private JTextArea compressedMessageEncr;
    private JButton senderPublicKey;
    private JTextArea encryptedMessageTextArea;
    private JTextArea compressionMessage;
    private JTextArea compressedMessageTextArea;
    private JTextArea encryptedMessageRadixTextArea;
    private JTextArea radixTextArea;

    private PgpApp app;

    private PGPEncrypt encrypt;
    private PGPDecrypt decrypt;

    private JPanel contentPane;

    private JTextArea decrypt_messageTextArea;
    private JButton decrypt_secretKeyButton;
    private JTextArea decrypt_signedMessage;
    private JTextArea decrypt_compressedMessageEncr;
    private JButton reciverSecretKey;
    private JTextArea decrypt_encryptedMessageTextArea;
    private JTextArea decrypt_compressionMessage;
    private JTextArea decrypt_compressedMessageTextArea;
    private JTextArea decrypt_encryptedMessageRadixTextArea;
    private JTextArea decrypt_radixTextArea;

    public EncryptionAndDecryptionView(PgpApp app, PGPEncrypt encrypt, PGPDecrypt decrypt) {
        super(app);
        this.app = app;
        this.encrypt = encrypt;
        this.decrypt = decrypt;
        getFrame().setDefaultCloseOperation(3);
        getFrame().setPreferredSize(new Dimension(WIDTH,HEIGHT*2));
        getFrame().setResizable(false);
        getFrame().pack();
        UIManager.put("TabbedPane.focus", new Color(0, 0, 0, 0));
        initComponents();
    }

    private void initComponents() {

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setPreferredSize(new Dimension(WIDTH,HEIGHT*2));
        contentPane.setLayout(null);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(0,0, WIDTH, HEIGHT-30);
        tabbedPane.setFont(PgpApp.checkboxFont);


        if(encrypt.isSignEnabled()) {
            JPanel signPanel = new JPanel();
            tabbedPane.addTab("Sign", null, signPanel, null);

            JLabel messageLabel = new JLabel("Message");
            messageLabel.setFont(PgpApp.labelFont);

            messageTextArea = new JTextArea();
            messageTextArea.setColumns(10);
            messageTextArea.setText(encrypt.getMessage());
            messageTextArea.setEditable(false);
            messageTextArea.setRows(5);
            messageTextArea.setFont(PgpApp.textfieldFont);

            JScrollPane messageScrollPane = new JScrollPane(messageTextArea);
            messageScrollPane.setFont(PgpApp.textfieldFont);

            JLabel senderSecretKey = new JLabel("Sender Secret Key");
            senderSecretKey.setFont(PgpApp.labelFont);

            secretKeyButton = new JButton();
            secretKeyButton.setText(encrypt.getSecretKeyInfo().toString());
            secretKeyButton.addActionListener(e -> app.showKey(encrypt.getSecretKeyInfo()));
            secretKeyButton.setFont(PgpApp.buttonFont);

            JLabel signedMessageLabel = new JLabel("Signed Message");
            signedMessageLabel.setFont(PgpApp.labelFont);

            signedMessage = new JTextArea();
            signedMessage.setColumns(10);
            signedMessage.setText(encrypt.getSignedData());
            signedMessage.setEditable(false);
            signedMessage.setRows(15);
            signedMessage.setLineWrap(true);
            signedMessage.setFont(PgpApp.textfieldFont);

            JScrollPane signedMessageScrollPane = new JScrollPane(signedMessage);
            signedMessageScrollPane.setFont(PgpApp.textfieldFont);

            GroupLayout signLayout = new GroupLayout(signPanel);
            signLayout.setHorizontalGroup(
                    signLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(signLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                            .addComponent(signedMessageLabel)
                                            .addComponent(senderSecretKey)
                                            .addComponent(messageLabel)
                                    )
                                    .addGap(10)
                                    .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addComponent(messageScrollPane, GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                                            .addComponent(secretKeyButton)
                                            .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                    .addComponent(signedMessageScrollPane,GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                                            )
                                    )
                                    .addContainerGap(90, Short.MAX_VALUE))
            );
            signLayout.setVerticalGroup(
                    signLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(signLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(messageLabel)
                                            .addComponent(messageScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    )
                                    .addGap(10)
                                    .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(senderSecretKey)
                                            .addComponent(secretKeyButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(10)
                                    .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(signedMessageLabel)
                                            .addComponent(signedMessageScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(10)
                                    .addContainerGap(75, Short.MAX_VALUE))
            );
            signPanel.setLayout(signLayout);
        }

        if(encrypt.isCompressionEnabled()) {
            JPanel compressionPanel = new JPanel();
            tabbedPane.addTab("Compress", null, compressionPanel, null);

            JLabel signedMessageJLabel = new JLabel();
            signedMessageJLabel.setFont(PgpApp.labelFont);
            if(encrypt.isSignEnabled())
                signedMessageJLabel.setText("Signed Message");
            else
                signedMessageJLabel.setText("Message");

            compressionMessage = new JTextArea();
            compressionMessage.setColumns(50);
            if(encrypt.isSignEnabled())
                compressionMessage.setText(encrypt.getSignedData());
            else
                compressionMessage.setText(encrypt.getMessage());
            compressionMessage.setEditable(false);
            compressionMessage.setRows(11);
            compressionMessage.setLineWrap(true);
            compressionMessage.setFont(PgpApp.textfieldFont);

            JScrollPane compressionMessageScrollPane = new JScrollPane(compressionMessage);
            compressionMessageScrollPane.setFont(PgpApp.textfieldFont);

            JLabel compressLabel = new JLabel("Compressed Message");
            compressLabel.setFont(PgpApp.labelFont);

            compressedMessageTextArea = new JTextArea();
            compressedMessageTextArea.setColumns(50);
            compressedMessageTextArea.setText(encrypt.getCompressedData());
            compressedMessageTextArea.setEditable(false);
            compressedMessageTextArea.setRows(11);
            compressedMessageTextArea.setLineWrap(true);
            compressedMessageTextArea.setFont(PgpApp.textfieldFont);

            JScrollPane compressedMessageScrollPane = new JScrollPane(compressedMessageTextArea);
            compressedMessageScrollPane.setFont(PgpApp.textfieldFont);

            GroupLayout compressionLayout = new GroupLayout(compressionPanel);
            compressionLayout.setHorizontalGroup(
                    compressionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(compressionLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(compressionLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                            .addComponent(compressLabel)
                                            .addComponent(signedMessageJLabel)
                                    )
                                    .addGap(10)
                                    .addGroup(compressionLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(compressedMessageScrollPane, GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
                                            .addComponent(compressionMessageScrollPane)
                                    )
                                    .addContainerGap(90, Short.MAX_VALUE))
            );
            compressionLayout.setVerticalGroup(
                    compressionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(compressionLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(compressionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(signedMessageJLabel)
                                            .addComponent(compressionMessageScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(compressionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                                    .addGap(10)
                                    .addGroup(compressionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                                    .addGap(10)
                                    .addGroup(compressionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(compressLabel)
                                            .addComponent(compressedMessageScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(10)
                                    .addGroup(compressionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                                    .addContainerGap(75, Short.MAX_VALUE))
            );
            compressionPanel.setLayout(compressionLayout);
        }

        if(encrypt.isEncryptEnabled()) {
            JPanel encryptionPanel = new JPanel();
            tabbedPane.addTab("Encryption", null, encryptionPanel, null);

            JLabel compressedMessageLabel = new JLabel();
            compressedMessageLabel.setFont(PgpApp.labelFont);
            if(encrypt.isCompressionEnabled())
                compressedMessageLabel.setText("Compressed Message");
            else if(encrypt.isSignEnabled())
                compressedMessageLabel.setText("Signed Message");
            else
                compressedMessageLabel.setText("Message");

            compressedMessageEncr = new JTextArea();
            compressedMessageEncr.setColumns(50);
            if(encrypt.isCompressionEnabled())
                compressedMessageEncr.setText(encrypt.getCompressedData());
            else if(encrypt.isSignEnabled())
                compressedMessageEncr.setText(encrypt.getSignedData());
            else
                compressedMessageEncr.setText(encrypt.getMessage());
            compressedMessageEncr.setEditable(false);
            compressedMessageEncr.setRows(10);
            compressedMessageEncr.setLineWrap(true);
            compressedMessageEncr.setFont(PgpApp.textfieldFont);

            JScrollPane compressedMessageEncrScrollPane = new JScrollPane(compressedMessageEncr);
            compressedMessageEncrScrollPane.setFont(PgpApp.textfieldFont);


            JLabel publickeyLabel = new JLabel("Recever Public Key");
            publickeyLabel.setFont(PgpApp.labelFont);

            senderPublicKey = new JButton();
            senderPublicKey.setText(encrypt.getPublicKeyInfo().toString());
            senderPublicKey.addActionListener(e -> app.showKey(encrypt.getPublicKeyInfo()));
            senderPublicKey.setFont(PgpApp.buttonFont);

            JLabel encryptedMessageLabel = new JLabel("Encrypted Message");
            encryptedMessageLabel.setFont(PgpApp.labelFont);

            encryptedMessageTextArea = new JTextArea();
            encryptedMessageTextArea.setColumns(50);
            encryptedMessageTextArea.setText(encrypt.getEncryptedData());
            encryptedMessageTextArea.setEditable(false);
            encryptedMessageTextArea.setRows(10);
            encryptedMessageTextArea.setLineWrap(true);
            encryptedMessageTextArea.setFont(PgpApp.textfieldFont);

            JScrollPane encryptedMessageScrollPane = new JScrollPane(encryptedMessageTextArea);
            encryptedMessageScrollPane.setFont(PgpApp.textfieldFont);

            GroupLayout encryptionLayout = new GroupLayout(encryptionPanel);
            encryptionLayout.setHorizontalGroup(
                    encryptionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(encryptionLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                            .addComponent(encryptedMessageLabel, GroupLayout.Alignment.TRAILING)
                                            .addComponent(publickeyLabel, GroupLayout.Alignment.TRAILING)
                                            .addComponent(compressedMessageLabel, GroupLayout.Alignment.TRAILING))
                                    .addGap(10)
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addComponent(compressedMessageEncrScrollPane)
                                            .addComponent(senderPublicKey)
                                            .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                    .addComponent(encryptedMessageScrollPane,GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
                                            )
                                    )
                                    .addContainerGap(90, Short.MAX_VALUE))
            );
            encryptionLayout.setVerticalGroup(
                    encryptionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(encryptionLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(compressedMessageLabel)
                                            .addComponent(compressedMessageEncrScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(0)
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                                    .addGap(10)
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(publickeyLabel)
                                            .addComponent(senderPublicKey, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(0)
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                                    .addGap(10)
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(encryptedMessageLabel)
                                            .addComponent(encryptedMessageScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(10)
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                                    .addContainerGap(100, Short.MAX_VALUE)
                            )
            );
            encryptionPanel.setLayout(encryptionLayout);
        }


        if(encrypt.isRadix64Enabled()) {

            JPanel radixPanel = new JPanel();
            tabbedPane.addTab("Radix-64", null, radixPanel, null);

            JLabel encryptedMessageRadixLabel = new JLabel();
            encryptedMessageRadixLabel.setFont(PgpApp.labelFont);
            if(encrypt.isEncryptEnabled())
                encryptedMessageRadixLabel.setText("Encrypted Message");
            else if(encrypt.isCompressionEnabled())
                encryptedMessageRadixLabel.setText("Compressed Message");
            else if(encrypt.isSignEnabled())
                encryptedMessageRadixLabel.setText("Signed Message");
            else
                encryptedMessageRadixLabel.setText("Message");

            encryptedMessageRadixTextArea = new JTextArea();
            encryptedMessageRadixTextArea.setColumns(50);

            if(encrypt.isEncryptEnabled())
                encryptedMessageRadixTextArea.setText(encrypt.getEncryptedData());
            else if(encrypt.isCompressionEnabled())
                encryptedMessageRadixTextArea.setText(encrypt.getCompressedData());
            else if(encrypt.isSignEnabled())
                encryptedMessageRadixTextArea.setText(encrypt.getSignedData());
            else
                encryptedMessageRadixTextArea.setText(encrypt.getMessage());

            encryptedMessageRadixTextArea.setEditable(false);
            encryptedMessageRadixTextArea.setRows(12);
            encryptedMessageRadixTextArea.setLineWrap(true);
            encryptedMessageRadixTextArea.setFont(PgpApp.textfieldFont);

            JScrollPane encryptedMessageRadixScrollPane = new JScrollPane(encryptedMessageRadixTextArea);
            encryptedMessageRadixScrollPane.setFont(PgpApp.textfieldFont);


            JLabel radixLabel = new JLabel("Radix-64 Message");
            radixLabel.setFont(PgpApp.labelFont);

            radixTextArea = new JTextArea();
            radixTextArea.setColumns(55);
            radixTextArea.setText(encrypt.getRadix64Data());
            radixTextArea.setEditable(false);
            radixTextArea.setRows(12);
            radixTextArea.setFont(PgpApp.textfieldFont);

            JScrollPane radixScrollPane = new JScrollPane(radixTextArea);
            radixScrollPane.setFont(PgpApp.textfieldFont);

            GroupLayout radixLayout = new GroupLayout(radixPanel);
            radixLayout.setHorizontalGroup(
                    radixLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(radixLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(radixLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                            .addComponent(encryptedMessageRadixLabel)
                                            .addComponent(radixLabel)
                                            .addGap(50)
                                    )
                                    .addGap(10)
                                    .addGroup(radixLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(radixScrollPane, GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
                                            .addComponent(encryptedMessageRadixScrollPane)
                                    )
                                    .addContainerGap(90, Short.MAX_VALUE))
            );
            radixLayout.setVerticalGroup(
                    radixLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(radixLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(radixLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(encryptedMessageRadixScrollPane)
                                            .addComponent(encryptedMessageRadixLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    )
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(radixLayout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                                    .addGap(10)
                                    .addGroup(radixLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(radixLabel)
                                            .addComponent(radixScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    )
                                    .addGap(10)
                                    .addGroup(radixLayout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                                    .addContainerGap(75, Short.MAX_VALUE))
            );
            radixPanel.setLayout(radixLayout);
        }

        contentPane.add(tabbedPane);





        JTabbedPane decryptionPane = new JTabbedPane(JTabbedPane.TOP);
        decryptionPane.setBounds(0, HEIGHT-30, WIDTH, HEIGHT);
        decryptionPane.setFont(PgpApp.checkboxFont);


        if(decrypt.isRadix64Enabled()) {

            JPanel radixPanel = new JPanel();
            decryptionPane.addTab("Radix-64", null, radixPanel, null);

            JLabel encryptedMessageRadixLabel = new JLabel("Ciphertext");
            encryptedMessageRadixLabel.setFont(PgpApp.labelFont);

            decrypt_encryptedMessageRadixTextArea = new JTextArea();
            decrypt_encryptedMessageRadixTextArea.setColumns(10);
            decrypt_encryptedMessageRadixTextArea.setText(decrypt.getMessage());
            decrypt_encryptedMessageRadixTextArea.setEditable(false);
            decrypt_encryptedMessageRadixTextArea.setRows(12);
            decrypt_encryptedMessageRadixTextArea.setFont(PgpApp.textfieldFont);
            decrypt_encryptedMessageRadixTextArea.setLineWrap(true);

            JScrollPane encryptedMessageRadixScrollPane = new JScrollPane(decrypt_encryptedMessageRadixTextArea);
            encryptedMessageRadixScrollPane.setFont(PgpApp.textfieldFont);


            JLabel radixLabel = new JLabel("Radix-64 Decode");
            radixLabel.setFont(PgpApp.labelFont);

            decrypt_radixTextArea = new JTextArea();
            decrypt_radixTextArea.setColumns(55);
            decrypt_radixTextArea.setText(decrypt.getRadix64Data());
            decrypt_radixTextArea.setEditable(false);
            decrypt_radixTextArea.setRows(12);
            decrypt_radixTextArea.setFont(PgpApp.textfieldFont);
            decrypt_radixTextArea.setLineWrap(true);

            JScrollPane radixScrollPane = new JScrollPane(decrypt_radixTextArea);
            radixScrollPane.setFont(PgpApp.textfieldFont);


            JButton endButton1 = new JButton();
            endButton1.setFont(PgpApp.buttonFont);
            endButton1.setPreferredSize(new Dimension(100,20));
            endButton1.setText("End");
            endButton1.addActionListener(e -> end());


            GroupLayout radixLayout = new GroupLayout(radixPanel);
            radixLayout.setHorizontalGroup(
                    radixLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(radixLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(radixLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                            .addComponent(encryptedMessageRadixLabel)
                                            .addComponent(radixLabel)
                                            .addGap(50)
                                    )
                                    .addGap(10)
                                    .addGroup(radixLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(radixScrollPane, GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
                                            .addComponent(encryptedMessageRadixScrollPane)
                                            .addGroup(radixLayout.createSequentialGroup()
                                                    .addComponent(endButton1, 100, 100, 100)
                                            )
                                    )
                                    .addContainerGap(90, Short.MAX_VALUE))
            );
            radixLayout.setVerticalGroup(
                    radixLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(radixLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(radixLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(encryptedMessageRadixScrollPane)
                                            .addComponent(encryptedMessageRadixLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    )
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)

                                    .addGroup(radixLayout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                                    .addGap(10)
                                    .addGroup(radixLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(radixLabel)
                                            .addComponent(radixScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    )
                                    .addGap(10)
                                    .addGroup(radixLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(endButton1)
                                    )
                                    .addContainerGap(75, Short.MAX_VALUE))
            );
            radixPanel.setLayout(radixLayout);
        }

        if(decrypt.isEncryptEnabled()) {

            JPanel encryptionPanel = new JPanel();

            decryptionPane.addTab("Encryption", null, encryptionPanel, null);

            JLabel compressedMessageLabel = new JLabel();
            if(decrypt.isRadix64Enabled())
                compressedMessageLabel.setText("Radix-64 Decode");
            else
                compressedMessageLabel.setText("Ciphertext");

            compressedMessageLabel.setFont(PgpApp.labelFont);

            decrypt_compressedMessageEncr = new JTextArea();
            decrypt_compressedMessageEncr.setColumns(10);

            if(decrypt.isRadix64Enabled())
                decrypt_compressedMessageEncr.setText(decrypt.getRadix64Data());
            else
                decrypt_compressedMessageEncr.setText(decrypt.getMessage());

            decrypt_compressedMessageEncr.setEditable(false);
            decrypt_compressedMessageEncr.setRows(10);
            decrypt_compressedMessageEncr.setLineWrap(true);
            decrypt_compressedMessageEncr.setFont(PgpApp.textfieldFont);

            JScrollPane compressedMessageEncrScrollPane = new JScrollPane(decrypt_compressedMessageEncr);
            compressedMessageEncrScrollPane.setFont(PgpApp.textfieldFont);


            JLabel secretKeyLabel = new JLabel("Reciver Secret Key");
            secretKeyLabel.setFont(PgpApp.labelFont);

            reciverSecretKey = new JButton();
            reciverSecretKey.setText(decrypt.getSecretKeyInfo().toString());
            reciverSecretKey.addActionListener(e -> app.showKey(decrypt.getSecretKeyInfo()));
            reciverSecretKey.setFont(PgpApp.buttonFont);


            JLabel encryptedMessageLabel = new JLabel("Decrypted Message");
            encryptedMessageLabel.setFont(PgpApp.labelFont);


            decrypt_encryptedMessageTextArea = new JTextArea();
            decrypt_encryptedMessageTextArea.setColumns(10);
            if(!decrypt.isSignEnabled() && !decrypt.isCompressionEnabled())
                decrypt_encryptedMessageTextArea.setText(decrypt.getDecryptedMessage());
            else
                decrypt_encryptedMessageTextArea.setText(decrypt.getEncryptedData());
            decrypt_encryptedMessageTextArea.setEditable(false);
            decrypt_encryptedMessageTextArea.setRows(9);
            decrypt_encryptedMessageTextArea.setLineWrap(true);
            encryptedMessageLabel.setFont(PgpApp.textfieldFont);

            JScrollPane encryptedMessageScrollPane = new JScrollPane(decrypt_encryptedMessageTextArea);
            encryptedMessageScrollPane.setFont(PgpApp.textfieldFont);


            JButton endButton2 = new JButton();
            endButton2.setFont(PgpApp.buttonFont);
            endButton2.setPreferredSize(new Dimension(100,20));
            endButton2.setText("End");
            endButton2.addActionListener(e -> end());



            GroupLayout encryptionLayout = new GroupLayout(encryptionPanel);
            encryptionLayout.setHorizontalGroup(
                    encryptionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(encryptionLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                            .addComponent(encryptedMessageLabel, GroupLayout.Alignment.TRAILING)
                                            .addComponent(secretKeyLabel, GroupLayout.Alignment.TRAILING)
                                            .addComponent(compressedMessageLabel, GroupLayout.Alignment.TRAILING))
                                    .addGap(10)
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addComponent(compressedMessageEncrScrollPane)
                                            .addComponent(reciverSecretKey)
                                            .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                    .addComponent(encryptedMessageScrollPane,GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
                                                    .addGroup(encryptionLayout.createSequentialGroup()
                                                            .addComponent(endButton2, 100, 100, 100)
                                                    )
                                            )
                                    )
                                    .addContainerGap(90, Short.MAX_VALUE))
            );
            encryptionLayout.setVerticalGroup(
                    encryptionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(encryptionLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(compressedMessageLabel)
                                            .addComponent(compressedMessageEncrScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(0)
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                                    .addGap(10)
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(secretKeyLabel)
                                            .addComponent(reciverSecretKey, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(0)
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                                    .addGap(10)
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(encryptedMessageLabel)
                                            .addComponent(encryptedMessageScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(10)
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(endButton2)
                                    )
                                    .addContainerGap(100, Short.MAX_VALUE))
            );
            encryptionPanel.setLayout(encryptionLayout);

        }

        if(decrypt.isCompressionEnabled()) {

            JPanel compressionPanel = new JPanel();
            decryptionPane.addTab("Compress", null, compressionPanel, null);

            JLabel compressedMessageLabel = new JLabel();
            compressedMessageLabel.setFont(PgpApp.labelFont);

            if(!decrypt.isRadix64Enabled() && !decrypt.isEncryptEnabled())
                compressedMessageLabel.setText("Ciphertext");
            else if(decrypt.isRadix64Enabled() && !decrypt.isEncryptEnabled())
                compressedMessageLabel.setText("Radix-64 Decode");
            else
                compressedMessageLabel.setText("Decrypted Message");

            decrypt_compressionMessage = new JTextArea();
            decrypt_compressionMessage.setColumns(10);

            if(decrypt.isEncryptEnabled())
                decrypt_compressionMessage.setText(decrypt.getEncryptedData());
            else if(decrypt.isRadix64Enabled())
                decrypt_compressionMessage.setText(decrypt.getRadix64Data());
            else
                decrypt_compressionMessage.setText(decrypt.getMessage());

            decrypt_compressionMessage.setEditable(false);
            decrypt_compressionMessage.setRows(11);
            decrypt_compressionMessage.setLineWrap(true);
            decrypt_compressionMessage.setFont(PgpApp.textfieldFont);

            JScrollPane compressionMessageScrollPane = new JScrollPane(decrypt_compressionMessage);
            compressionMessageScrollPane.setFont(PgpApp.textfieldFont);


            JLabel compressLabel = new JLabel("Decompressed Message");
            compressLabel.setFont(PgpApp.labelFont);

            decrypt_compressedMessageTextArea = new JTextArea();
            decrypt_compressedMessageTextArea.setColumns(10);
            if(decrypt.isSignEnabled())
                decrypt_compressedMessageTextArea.setText(decrypt.getCompressedData());
            else
                decrypt_compressedMessageTextArea.setText(decrypt.getDecryptedMessage());
            decrypt_compressedMessageTextArea.setEditable(false);
            decrypt_compressedMessageTextArea.setRows(10);
            decrypt_compressedMessageTextArea.setLineWrap(true);
            decrypt_compressedMessageTextArea.setFont(PgpApp.textfieldFont);

            JScrollPane compressedMessageScrollPane = new JScrollPane(decrypt_compressedMessageTextArea);
            compressedMessageScrollPane.setFont(PgpApp.textfieldFont);

            JButton endButton3 = new JButton();
            endButton3.setFont(PgpApp.buttonFont);
            endButton3.setPreferredSize(new Dimension(100,20));
            endButton3.setText("End");
            endButton3.addActionListener(e -> end());



            GroupLayout compressLayout = new GroupLayout(compressionPanel);
            compressLayout.setHorizontalGroup(
                    compressLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(compressLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(compressLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                            .addComponent(compressLabel)
                                            .addComponent(compressedMessageLabel)
                                    )
                                    .addGap(10)
                                    .addGroup(compressLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(compressedMessageScrollPane, GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                                            .addComponent(compressionMessageScrollPane)
                                            .addGroup(compressLayout.createSequentialGroup()
                                                    .addComponent(endButton3, 100, 100, 100)
                                            )
                                    )
                                    .addContainerGap(90, Short.MAX_VALUE))
            );
            compressLayout.setVerticalGroup(
                    compressLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(compressLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(compressLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(compressedMessageLabel)
                                            .addComponent(compressionMessageScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(compressLayout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                                    .addGap(10)
                                    .addGroup(compressLayout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                                    .addGap(10)
                                    .addGroup(compressLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(compressLabel)
                                            .addComponent(compressedMessageScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(compressLayout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                                    .addGap(10)
                                    .addGroup(compressLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addGroup(compressLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(endButton3)
                                            )
                                    )
                                    .addContainerGap(75, Short.MAX_VALUE))
            );
            compressionPanel.setLayout(compressLayout);
        }

        if(decrypt.isSignEnabled()) {
            JPanel signPanel = new JPanel();
            decryptionPane.addTab("Sign", null, signPanel, null);

            JLabel signLabel = new JLabel();
            signLabel.setFont(PgpApp.labelFont);

            if(!decrypt.isRadix64Enabled() && !decrypt.isEncryptEnabled() && !decrypt.isCompressionEnabled())
                signLabel.setText("Ciphertext");
            else if(decrypt.isRadix64Enabled() && !decrypt.isEncryptEnabled() && !decrypt.isCompressionEnabled())
                signLabel.setText("Radix-64 Decode");
            else if(!decrypt.isRadix64Enabled() && decrypt.isEncryptEnabled() && !decrypt.isCompressionEnabled())
                signLabel.setText("Decrypted Message");
            else
                signLabel.setText("Decompressed Message");

            decrypt_messageTextArea = new JTextArea();
            decrypt_messageTextArea.setColumns(10);

            if(decrypt.isCompressionEnabled())
                decrypt_messageTextArea.setText(decrypt.getCompressedData());
            else if(decrypt.isEncryptEnabled())
                decrypt_messageTextArea.setText(decrypt.getEncryptedData());
            else if(decrypt.isRadix64Enabled())
                decrypt_messageTextArea.setText(decrypt.getRadix64Data());
            else
                decrypt_messageTextArea.setText(decrypt.getMessage());

            decrypt_messageTextArea.setEditable(false);
            decrypt_messageTextArea.setRows(12);
            decrypt_messageTextArea.setLineWrap(true);
            decrypt_messageTextArea.setFont(PgpApp.textfieldFont);

            JScrollPane messageScrollPane = new JScrollPane(decrypt_messageTextArea);
            messageScrollPane.setFont(PgpApp.textfieldFont);

            JLabel senderPublicKeyLabel = new JLabel("Sender Public Key");
            senderPublicKeyLabel.setFont(PgpApp.labelFont);

            decrypt_secretKeyButton = new JButton();
            decrypt_secretKeyButton.setFont(PgpApp.buttonFont);
            decrypt_secretKeyButton.setText(decrypt.getPublicKeyInfo().toString());
            decrypt_secretKeyButton.addActionListener(e -> app.showKey(decrypt.getPublicKeyInfo()));

            JLabel messageLabel = new JLabel("Message");
            messageLabel.setFont(PgpApp.labelFont);

            decrypt_signedMessage = new JTextArea();
            decrypt_signedMessage.setColumns(10);
            decrypt_signedMessage.setText(decrypt.getDecryptedMessage());
            decrypt_signedMessage.setEditable(false);
            decrypt_signedMessage.setRows(7);
            decrypt_signedMessage.setFont(PgpApp.textfieldFont);

            JScrollPane signedMessageScrollPane = new JScrollPane(decrypt_signedMessage);
            signedMessageScrollPane.setFont(PgpApp.textfieldFont);

            JButton endButton = new JButton();
            endButton.setFont(PgpApp.buttonFont);
            endButton.setPreferredSize(new Dimension(100,20));
            endButton.setText("End");
            endButton.addActionListener(e -> end());


            GroupLayout signLayout = new GroupLayout(signPanel);
            signLayout.setHorizontalGroup(
                    signLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(signLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                            .addComponent(messageLabel)
                                            .addComponent(senderPublicKeyLabel)
                                            .addComponent(signLabel))
                                    .addGap(10)
                                    .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addComponent(messageScrollPane, GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                                            .addComponent(decrypt_secretKeyButton)
                                            .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                    .addComponent(signedMessageScrollPane,GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                                                    .addGroup(signLayout.createSequentialGroup()
                                                            .addComponent(endButton, 100, 100, 100)
                                                    )
                                            )
                                    )
                                    .addContainerGap(90, Short.MAX_VALUE))
            );
            signLayout.setVerticalGroup(
                    signLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(signLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(signLabel)
                                            .addComponent(messageScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    )
                                    .addGap(10)
                                    .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(senderPublicKeyLabel)
                                            .addComponent(decrypt_secretKeyButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(10)
                                    .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(messageLabel)
                                            .addComponent(signedMessageScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(10)
                                    .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                                    .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(endButton)
                                            )
                                    )
                                    .addContainerGap(75, Short.MAX_VALUE))
            );
            signPanel.setLayout(signLayout);
        }



        contentPane.add(decryptionPane);


        setComponent(contentPane);

    }


    private void end(){
        app.showEncDecView(this);
    }

}
