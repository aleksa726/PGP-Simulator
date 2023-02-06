package pgp.views;

import org.jdesktop.application.FrameView;
import pgp.PgpApp;
import pgp.algorithm.PGPDecrypt;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class PgpDecryptView extends FrameView {

    private static final int WIDTH = 760;
    private static final int HEIGHT = 800;
    private JTextArea messageTextArea;
    private JButton secretKeyButton;
    private JTextArea signedMessage;
    private JTextArea compressedMessageEncr;
    private JButton reciverSecretKey;
    private JTextArea encryptedMessageTextArea;
    private JTextArea compressionMessage;
    private JTextArea compressedMessageTextArea;
    private JTextArea encryptedMessageRadixTextArea;
    private JTextArea radixTextArea;

    private JPanel mainPanel;

    private PGPDecrypt decrypt;

    private PgpApp app;

    public PgpDecryptView(PgpApp app, PGPDecrypt decrypt) {
        super(app);
        this.app = app;
        this.decrypt = decrypt;
        getFrame().setDefaultCloseOperation(3);
        getFrame().setResizable(false);
        getFrame().pack();
        UIManager.put("TabbedPane.focus", new Color(0, 0, 0, 0));
        initComponents();
    }


    private void initComponents() {

        this.mainPanel = new JPanel();
        this.mainPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(PgpApp.checkboxFont);
        GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
        this.mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, WIDTH+20, GroupLayout.PREFERRED_SIZE)
        );
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, HEIGHT+10, GroupLayout.PREFERRED_SIZE)
        );

        if(decrypt.isRadix64Enabled()) {

            JPanel radixPanel = new JPanel();
            tabbedPane.addTab("Radix-64", null, radixPanel, null);

            JLabel encryptedMessageRadixLabel = new JLabel("Ciphertext");
            encryptedMessageRadixLabel.setFont(PgpApp.labelFont);

            encryptedMessageRadixTextArea = new JTextArea();
            encryptedMessageRadixTextArea.setColumns(50);
            encryptedMessageRadixTextArea.setText(decrypt.getMessage());
            encryptedMessageRadixTextArea.setEditable(false);
            encryptedMessageRadixTextArea.setRows(12);
            encryptedMessageRadixTextArea.setFont(PgpApp.textfieldFont);
            encryptedMessageRadixTextArea.setLineWrap(true);

            JScrollPane encryptedMessageRadixScrollPane = new JScrollPane(encryptedMessageRadixTextArea);
            encryptedMessageRadixScrollPane.setFont(PgpApp.textfieldFont);


            JLabel radixLabel = new JLabel("Radix-64 Decode");
            radixLabel.setFont(PgpApp.labelFont);

            radixTextArea = new JTextArea();
            radixTextArea.setColumns(55);
            radixTextArea.setText(decrypt.getRadix64Data());
            radixTextArea.setEditable(false);
            radixTextArea.setRows(12);
            radixTextArea.setFont(PgpApp.textfieldFont);
            radixTextArea.setLineWrap(true);

            JScrollPane radixScrollPane = new JScrollPane(radixTextArea);
            radixScrollPane.setFont(PgpApp.textfieldFont);

            ImageIcon myPicture = new ImageIcon("resources\\pgp_decryption_radix.png");
            myPicture.setImage(myPicture.getImage().getScaledInstance(760,250, Image.SCALE_SMOOTH));
            JLabel picLabel = new JLabel(myPicture);

            JButton endButton1 = new JButton();
            endButton1.setFont(PgpApp.buttonFont);
            endButton1.setPreferredSize(new Dimension(100,20));
            endButton1.setText("End");
            endButton1.addActionListener(e -> end());

            JButton exportButton1 = new JButton();
            exportButton1.setPreferredSize(new Dimension(200,20));
            exportButton1.setText("Export message");
            exportButton1.addActionListener(e -> exportMessage());
            exportButton1.setFont(PgpApp.buttonFont);

            GroupLayout radixLayout = new GroupLayout(radixPanel);
            radixLayout.setHorizontalGroup(
                    radixLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(picLabel, 760, 760, 760)
                            .addGroup(radixLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(radixLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(encryptedMessageRadixLabel)
                                        .addComponent(radixLabel)
                                        .addGap(50)
                                    )
                                    .addGap(18)
                                    .addGroup(radixLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(radixScrollPane, GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
                                            .addComponent(encryptedMessageRadixScrollPane)
                                            .addGroup(radixLayout.createSequentialGroup()
                                                    .addComponent(exportButton1, 150, 150, 150)
                                                    .addGap(20)
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
                                    .addGap(18)
                                    .addGroup(radixLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(radixLabel)
                                            .addComponent(radixScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    )
                                    .addGap(18)
                                    .addComponent(picLabel, 250,250,250)
                                    .addGap(18)
                                    .addGroup(radixLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(exportButton1)
                                            .addComponent(endButton1)
                                    )
                                    .addContainerGap(75, Short.MAX_VALUE))
            );
            radixPanel.setLayout(radixLayout);
        }

        if(decrypt.isEncryptEnabled()) {

            JPanel encryptionPanel = new JPanel();

            tabbedPane.addTab("Encryption", null, encryptionPanel, null);

            JLabel compressedMessageLabel = new JLabel();
            if(decrypt.isRadix64Enabled())
                compressedMessageLabel.setText("Radix-64 Decode");
            else
                compressedMessageLabel.setText("Ciphertext");

            compressedMessageLabel.setFont(PgpApp.labelFont);

            compressedMessageEncr = new JTextArea();
            compressedMessageEncr.setColumns(50);

            if(decrypt.isRadix64Enabled())
                compressedMessageEncr.setText(decrypt.getRadix64Data());
            else
                compressedMessageEncr.setText(decrypt.getMessage());

            compressedMessageEncr.setEditable(false);
            compressedMessageEncr.setRows(10);
            compressedMessageEncr.setLineWrap(true);
            compressedMessageEncr.setFont(PgpApp.textfieldFont);

            JScrollPane compressedMessageEncrScrollPane = new JScrollPane(compressedMessageEncr);
            compressedMessageEncrScrollPane.setFont(PgpApp.textfieldFont);


            JLabel secretKeyLabel = new JLabel("Reciver Secret Key");
            secretKeyLabel.setFont(PgpApp.labelFont);

            reciverSecretKey = new JButton();
            reciverSecretKey.setText(decrypt.getSecretKeyInfo().toString());
            reciverSecretKey.addActionListener(e -> app.showKey(decrypt.getSecretKeyInfo()));
            reciverSecretKey.setFont(PgpApp.buttonFont);


            JLabel encryptedMessageLabel = new JLabel("Decrypted Message");
            encryptedMessageLabel.setFont(PgpApp.labelFont);


            encryptedMessageTextArea = new JTextArea();
            encryptedMessageTextArea.setColumns(50);
            if(!decrypt.isSignEnabled() && !decrypt.isCompressionEnabled())
                encryptedMessageTextArea.setText(decrypt.getDecryptedMessage());
            else
                encryptedMessageTextArea.setText(decrypt.getEncryptedData());
            encryptedMessageTextArea.setEditable(false);
            encryptedMessageTextArea.setRows(10);
            encryptedMessageTextArea.setLineWrap(true);
            encryptedMessageLabel.setFont(PgpApp.textfieldFont);

            JScrollPane encryptedMessageScrollPane = new JScrollPane(encryptedMessageTextArea);
            encryptedMessageScrollPane.setFont(PgpApp.textfieldFont);

            ImageIcon myPicture = new ImageIcon("resources\\pgp_decryption_decrypt.png");
            myPicture.setImage(myPicture.getImage().getScaledInstance(760,250, Image.SCALE_SMOOTH));
            JLabel picLabel = new JLabel(myPicture);

            JButton endButton2 = new JButton();
            endButton2.setFont(PgpApp.buttonFont);
            endButton2.setPreferredSize(new Dimension(100,20));
            endButton2.setText("End");
            endButton2.addActionListener(e -> end());

            JButton exportButton2 = new JButton();
            exportButton2.setPreferredSize(new Dimension(200,20));
            exportButton2.setText("Export message");
            exportButton2.addActionListener(e -> exportMessage());
            exportButton2.setFont(PgpApp.buttonFont);

            GroupLayout encryptionLayout = new GroupLayout(encryptionPanel);
            encryptionLayout.setHorizontalGroup(
                    encryptionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(picLabel, 760, 760, 760)
                            .addGroup(encryptionLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                            .addComponent(encryptedMessageLabel, GroupLayout.Alignment.TRAILING)
                                            .addComponent(secretKeyLabel, GroupLayout.Alignment.TRAILING)
                                            .addComponent(compressedMessageLabel, GroupLayout.Alignment.TRAILING))
                                    .addGap(18)
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addComponent(compressedMessageEncrScrollPane)
                                            .addComponent(reciverSecretKey)
                                            .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                    .addComponent(encryptedMessageScrollPane,GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                                                    .addGroup(encryptionLayout.createSequentialGroup()
                                                            .addComponent(exportButton2, 150, 150, 150)
                                                            .addGap(20)
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
                                    .addGap(18)
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(secretKeyLabel)
                                            .addComponent(reciverSecretKey, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(0)
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                                    .addGap(18)
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(encryptedMessageLabel)
                                            .addComponent(encryptedMessageScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(18)
                                    .addComponent(picLabel, 250,250,250)
                                    .addGap(18)
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(exportButton2)
                                            .addComponent(endButton2)
                                    )
                                    .addContainerGap(100, Short.MAX_VALUE))
            );
            encryptionPanel.setLayout(encryptionLayout);

        }

        if(decrypt.isCompressionEnabled()) {

            JPanel compressionPanel = new JPanel();
            tabbedPane.addTab("Compress", null, compressionPanel, null);

            JLabel compressedMessageLabel = new JLabel();
            compressedMessageLabel.setFont(PgpApp.labelFont);

            if(!decrypt.isRadix64Enabled() && !decrypt.isEncryptEnabled())
                compressedMessageLabel.setText("Ciphertext");
            else if(decrypt.isRadix64Enabled() && !decrypt.isEncryptEnabled())
                compressedMessageLabel.setText("Radix-64 Decode");
            else
                compressedMessageLabel.setText("Decrypted Message");

            compressionMessage = new JTextArea();
            compressionMessage.setColumns(50);

            if(decrypt.isEncryptEnabled())
                compressionMessage.setText(decrypt.getEncryptedData());
            else if(decrypt.isRadix64Enabled())
                compressionMessage.setText(decrypt.getRadix64Data());
            else
               compressionMessage.setText(decrypt.getMessage());

            compressionMessage.setEditable(false);
            compressionMessage.setRows(11);
            compressionMessage.setLineWrap(true);
            compressionMessage.setFont(PgpApp.textfieldFont);

            JScrollPane compressionMessageScrollPane = new JScrollPane(compressionMessage);
            compressionMessageScrollPane.setFont(PgpApp.textfieldFont);


            JLabel compressLabel = new JLabel("Decompressed Message");
            compressLabel.setFont(PgpApp.labelFont);

            compressedMessageTextArea = new JTextArea();
            compressedMessageTextArea.setColumns(50);
            if(decrypt.isSignEnabled())
                compressedMessageTextArea.setText(decrypt.getCompressedData());
            else
                compressedMessageTextArea.setText(decrypt.getDecryptedMessage());
            compressedMessageTextArea.setEditable(false);
            compressedMessageTextArea.setRows(11);
            compressedMessageTextArea.setLineWrap(true);
            compressedMessageTextArea.setFont(PgpApp.textfieldFont);

            JScrollPane compressedMessageScrollPane = new JScrollPane(compressedMessageTextArea);
            compressedMessageScrollPane.setFont(PgpApp.textfieldFont);

            ImageIcon myPicture = new ImageIcon("resources\\pgp_decryption_decompress.png");
            myPicture.setImage(myPicture.getImage().getScaledInstance(760,250, Image.SCALE_SMOOTH));
            JLabel picLabel = new JLabel(myPicture);

            JButton endButton3 = new JButton();
            endButton3.setFont(PgpApp.buttonFont);
            endButton3.setPreferredSize(new Dimension(100,20));
            endButton3.setText("End");
            endButton3.addActionListener(e -> end());

            JButton exportButton3 = new JButton();
            exportButton3.setPreferredSize(new Dimension(200,20));
            exportButton3.setText("Export message");
            exportButton3.addActionListener(e -> exportMessage());
            exportButton3.setFont(PgpApp.buttonFont);

            GroupLayout compressLayout = new GroupLayout(compressionPanel);
            compressLayout.setHorizontalGroup(
                    compressLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(picLabel, 760, 760, 760)
                            .addGroup(compressLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(compressLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                            .addComponent(compressLabel)
                                            .addComponent(compressedMessageLabel)
                                    )
                                    .addGap(18)
                                    .addGroup(compressLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(compressedMessageScrollPane, GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
                                            .addComponent(compressionMessageScrollPane)
                                            .addGroup(compressLayout.createSequentialGroup()
                                                    .addComponent(exportButton3, 150, 150, 150)
                                                    .addGap(20)
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
                                    .addGap(18)
                                    .addGroup(compressLayout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                                    .addGap(18)
                                    .addGroup(compressLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(compressLabel)
                                            .addComponent(compressedMessageScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(compressLayout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                                    .addGap(18)
                                    .addComponent(picLabel, 250,250,250)
                                    .addGap(18)
                                    .addGroup(compressLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addGroup(compressLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(exportButton3)
                                                    .addComponent(endButton3)
                                            )
                                    )
                                    .addContainerGap(75, Short.MAX_VALUE))
            );
            compressionPanel.setLayout(compressLayout);
        }

        if(decrypt.isSignEnabled()) {
            JPanel signPanel = new JPanel();
            tabbedPane.addTab("Sign", null, signPanel, null);

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

            messageTextArea = new JTextArea();
            messageTextArea.setColumns(10);

            if(decrypt.isCompressionEnabled())
                messageTextArea.setText(decrypt.getCompressedData());
            else if(decrypt.isEncryptEnabled())
                messageTextArea.setText(decrypt.getEncryptedData());
            else if(decrypt.isRadix64Enabled())
                messageTextArea.setText(decrypt.getRadix64Data());
            else
                messageTextArea.setText(decrypt.getMessage());

            messageTextArea.setEditable(false);
            messageTextArea.setRows(13);
            messageTextArea.setLineWrap(true);
            messageTextArea.setFont(PgpApp.textfieldFont);

            JScrollPane messageScrollPane = new JScrollPane(messageTextArea);
            messageScrollPane.setFont(PgpApp.textfieldFont);

            JLabel senderPublicKeyLabel = new JLabel("Sender Public Key");
            senderPublicKeyLabel.setFont(PgpApp.labelFont);

            secretKeyButton = new JButton();
            secretKeyButton.setFont(PgpApp.buttonFont);
            secretKeyButton.setText(decrypt.getPublicKeyInfo().toString());
            secretKeyButton.addActionListener(e -> app.showKey(decrypt.getPublicKeyInfo()));

            JLabel messageLabel = new JLabel("Message");
            messageLabel.setFont(PgpApp.labelFont);

            signedMessage = new JTextArea();
            signedMessage.setColumns(50);
            signedMessage.setText(decrypt.getDecryptedMessage());
            signedMessage.setEditable(false);
            signedMessage.setRows(7);
            signedMessage.setFont(PgpApp.textfieldFont);

            JScrollPane signedMessageScrollPane = new JScrollPane(signedMessage);
            signedMessageScrollPane.setFont(PgpApp.textfieldFont);

            ImageIcon myPicture = new ImageIcon("resources\\pgp_decryption_sign.png");
            myPicture.setImage(myPicture.getImage().getScaledInstance(760,250, Image.SCALE_SMOOTH));
            JLabel picLabel = new JLabel(myPicture);

            JButton endButton = new JButton();
            endButton.setFont(PgpApp.buttonFont);
            endButton.setPreferredSize(new Dimension(100,20));
            endButton.setText("End");
            endButton.addActionListener(e -> end());

            JButton exportButton4 = new JButton();
            exportButton4.setPreferredSize(new Dimension(200,20));
            exportButton4.setText("Export message");
            exportButton4.addActionListener(e -> exportMessage());
            exportButton4.setFont(PgpApp.buttonFont);

            GroupLayout signLayout = new GroupLayout(signPanel);
            signLayout.setHorizontalGroup(
                    signLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(picLabel, 760, 760, 760)
                            .addGroup(signLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(messageLabel)
                                        .addComponent(senderPublicKeyLabel)
                                        .addComponent(signLabel))
                                    .addGap(18)
                                    .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addComponent(messageScrollPane, GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
                                            .addComponent(secretKeyButton)
                                            .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                    .addComponent(signedMessageScrollPane,GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
                                                    .addGroup(signLayout.createSequentialGroup()
                                                            .addComponent(exportButton4, 150, 150, 150)
                                                            .addGap(20)
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
                                    .addGap(18)
                                    .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(senderPublicKeyLabel)
                                            .addComponent(secretKeyButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(18)
                                    .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(messageLabel)
                                            .addComponent(signedMessageScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(18)
                                    .addComponent(picLabel, 250,250,250)
                                    .addGap(18)
                                    .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                                    .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(exportButton4)
                                                    .addComponent(endButton)
                                            )
                                    )
                                    .addContainerGap(75, Short.MAX_VALUE))
            );
            signPanel.setLayout(signLayout);
        }

        setComponent(this.mainPanel);
    }

    private void exportMessage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("C:\\Users\\Aleksa\\Desktop"));
        int response = fileChooser.showSaveDialog(null);
        if(response == JFileChooser.APPROVE_OPTION) {
            try {
                PrintWriter file = new PrintWriter(fileChooser.getSelectedFile().getAbsolutePath(), "UTF-8");
                String data = decrypt.getDecryptedMessage();
                file.println(data);
                file.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void end(){
        app.showEncDecView(this);
    }
}

