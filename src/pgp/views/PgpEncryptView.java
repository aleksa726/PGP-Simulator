package pgp.views;

import org.jdesktop.application.FrameView;
import pgp.PgpApp;
import pgp.algorithm.PGPEncrypt;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class PgpEncryptView extends FrameView {

    private static final int WIDTH = 760;
    private static final int HEIGHT = 800;

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

    private JPanel mainPanel;

    private PGPEncrypt encrypt;

    private PgpApp app;

    public PgpEncryptView(PgpApp app, PGPEncrypt encrypt) {
        super(app);
        this.app = app;
        this.encrypt = encrypt;
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
                mainPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, WIDTH+20, GroupLayout.PREFERRED_SIZE)
        );
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, HEIGHT+10, GroupLayout.PREFERRED_SIZE)
        );

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



            ImageIcon myPicture = new ImageIcon("resources\\pgp_sign.png");
            myPicture.setImage(myPicture.getImage().getScaledInstance(760,250, Image.SCALE_SMOOTH));
            JLabel picLabel = new JLabel(myPicture);



            JButton endButton1 = new JButton();
            endButton1.setPreferredSize(new Dimension(100,20));
            endButton1.setText("End");
            endButton1.addActionListener(e -> end());
            endButton1.setFont(PgpApp.buttonFont);

            JButton exportButton1 = new JButton();
            exportButton1.setPreferredSize(new Dimension(200,20));
            exportButton1.setText("Export message");
            exportButton1.addActionListener(e -> exportMessage());
            exportButton1.setFont(PgpApp.buttonFont);

            GroupLayout signLayout = new GroupLayout(signPanel);
            signLayout.setHorizontalGroup(
                    signLayout.createParallelGroup(Alignment.LEADING)
                            .addComponent(picLabel, 760, 760, 760)
                            .addGroup(signLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(signLayout.createParallelGroup(Alignment.TRAILING)
                                                    .addComponent(signedMessageLabel)
                                                    .addComponent(senderSecretKey)
                                                    .addComponent(messageLabel)
                                    )
                                    .addGap(18)
                                    .addGroup(signLayout.createParallelGroup(Alignment.LEADING, false)
                                            .addComponent(messageScrollPane, GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                                            .addComponent(secretKeyButton)
                                            .addGroup(signLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                    .addComponent(signedMessageScrollPane,GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                                                    .addGroup(signLayout.createSequentialGroup()
                                                            .addComponent(exportButton1, 150, 150, 150)
                                                            .addGap(20)
                                                            .addComponent(endButton1, 100, 100, 100)
                                                    )
                                            )
                                    )
                                    .addContainerGap(90, Short.MAX_VALUE))
            );
            signLayout.setVerticalGroup(
                    signLayout.createParallelGroup(Alignment.LEADING)
                            .addGroup(signLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(signLayout.createParallelGroup(Alignment.BASELINE)
                                            .addComponent(messageLabel)
                                            .addComponent(messageScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(ComponentPlacement.UNRELATED)
                                    .addGroup(signLayout.createParallelGroup(Alignment.BASELINE)
                                    )
                                    .addGap(18)
                                    .addGroup(signLayout.createParallelGroup(Alignment.BASELINE)
                                            .addComponent(senderSecretKey)
                                            .addComponent(secretKeyButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(18)
                                    .addGroup(signLayout.createParallelGroup(Alignment.BASELINE)
                                            .addComponent(signedMessageLabel)
                                            .addComponent(signedMessageScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(18)
                                    .addComponent(picLabel, 250,250,250)
                                    .addGap(18)
                                    .addGroup(signLayout.createParallelGroup(Alignment.BASELINE)
                                            .addComponent(exportButton1)
                                            .addComponent(endButton1)
                                    )
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
            compressionMessage.setRows(10);
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

            ImageIcon myPicture = new ImageIcon("resources\\pgp_compress.png");
            myPicture.setImage(myPicture.getImage().getScaledInstance(760,250, Image.SCALE_SMOOTH));
            JLabel picLabel = new JLabel(myPicture);

            JButton endButton2 = new JButton();
            endButton2.setPreferredSize(new Dimension(100,20));
            endButton2.setText("End");
            endButton2.addActionListener(e -> end());
            endButton2.setFont(PgpApp.buttonFont);

            JButton exportButton2 = new JButton();
            exportButton2.setPreferredSize(new Dimension(200,20));
            exportButton2.setText("Export message");
            exportButton2.addActionListener(e -> exportMessage());
            exportButton2.setFont(PgpApp.buttonFont);

            GroupLayout compressionLayout = new GroupLayout(compressionPanel);
            compressionLayout.setHorizontalGroup(
                    compressionLayout.createParallelGroup(Alignment.LEADING)
                            .addComponent(picLabel, 760, 760, 760)
                            .addGroup(compressionLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(compressionLayout.createParallelGroup(Alignment.TRAILING)
                                            .addComponent(compressLabel)
                                            .addComponent(signedMessageJLabel)
                                    )
                                    .addGap(18)
                                    .addGroup(compressionLayout.createParallelGroup(Alignment.TRAILING, false)
                                            .addComponent(compressedMessageScrollPane, GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
                                            .addComponent(compressionMessageScrollPane)
                                            .addGroup(compressionLayout.createSequentialGroup()
                                                    .addComponent(exportButton2, 150, 150, 150)
                                                    .addGap(20)
                                                    .addComponent(endButton2, 100, 100, 100)
                                            )
                                    )
                                    .addContainerGap(90, Short.MAX_VALUE))
            );
            compressionLayout.setVerticalGroup(
                    compressionLayout.createParallelGroup(Alignment.LEADING)
                            .addGroup(compressionLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(compressionLayout.createParallelGroup(Alignment.BASELINE)
                                            .addComponent(signedMessageJLabel)
                                            .addComponent(compressionMessageScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(ComponentPlacement.UNRELATED)
                                    .addGroup(compressionLayout.createParallelGroup(Alignment.BASELINE))
                                    .addGap(18)
                                    .addGroup(compressionLayout.createParallelGroup(Alignment.BASELINE))
                                    .addGap(18)
                                    .addGroup(compressionLayout.createParallelGroup(Alignment.BASELINE)
                                            .addComponent(compressLabel)
                                            .addComponent(compressedMessageScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(18)
                                    .addComponent(picLabel, 250,250,250)
                                    .addGap(18)
                                    .addGroup(compressionLayout.createParallelGroup(Alignment.BASELINE))
                                    .addGroup(compressionLayout.createParallelGroup(Alignment.BASELINE)
                                            .addComponent(exportButton2)
                                            .addComponent(endButton2)
                                    )
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

            ImageIcon myPicture = new ImageIcon("resources\\pgp_encrypt.png");
            myPicture.setImage(myPicture.getImage().getScaledInstance(760,250, Image.SCALE_SMOOTH));
            JLabel picLabel = new JLabel(myPicture);

            JButton endButton3 = new JButton();
            endButton3.setPreferredSize(new Dimension(100,20));
            endButton3.setText("End");
            endButton3.addActionListener(e -> end());
            endButton3.setFont(PgpApp.buttonFont);

            JButton exportButton3 = new JButton();
            exportButton3.setPreferredSize(new Dimension(200,20));
            exportButton3.setText("Export message");
            exportButton3.addActionListener(e -> exportMessage());
            exportButton3.setFont(PgpApp.buttonFont);

            GroupLayout encryptionLayout = new GroupLayout(encryptionPanel);
            encryptionLayout.setHorizontalGroup(
                    encryptionLayout.createParallelGroup(Alignment.LEADING)
                            .addComponent(picLabel, 760, 760, 760)
                            .addGroup(encryptionLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(encryptionLayout.createParallelGroup(Alignment.LEADING)
                                            .addComponent(encryptedMessageLabel, Alignment.TRAILING)
                                            .addComponent(publickeyLabel, Alignment.TRAILING)
                                            .addComponent(compressedMessageLabel, Alignment.TRAILING))
                                    .addGap(18)
                                    .addGroup(encryptionLayout.createParallelGroup(Alignment.LEADING, false)
                                            .addComponent(compressedMessageEncrScrollPane)
                                            .addComponent(senderPublicKey)
                                            .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                    .addComponent(encryptedMessageScrollPane,GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
                                                    .addGroup(encryptionLayout.createSequentialGroup()
                                                            .addComponent(exportButton3, 150, 150, 150)
                                                            .addGap(20)
                                                            .addComponent(endButton3, 100, 100, 100)
                                                    )
                                            )
                                    )
                                    .addContainerGap(90, Short.MAX_VALUE))
            );
            encryptionLayout.setVerticalGroup(
                    encryptionLayout.createParallelGroup(Alignment.LEADING)
                            .addGroup(encryptionLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(encryptionLayout.createParallelGroup(Alignment.BASELINE)
                                            .addComponent(compressedMessageLabel)
                                            .addComponent(compressedMessageEncrScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(0)
                                    .addGroup(encryptionLayout.createParallelGroup(Alignment.BASELINE))
                                    .addGap(18)
                                    .addGroup(encryptionLayout.createParallelGroup(Alignment.BASELINE)
                                            .addComponent(publickeyLabel)
                                            .addComponent(senderPublicKey, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(0)
                                    .addGroup(encryptionLayout.createParallelGroup(Alignment.BASELINE))
                                    .addGap(18)
                                    .addGroup(encryptionLayout.createParallelGroup(Alignment.BASELINE)
                                            .addComponent(encryptedMessageLabel)
                                            .addComponent(encryptedMessageScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(18)
                                    .addComponent(picLabel, 250,250,250)
                                    .addGap(18)
                                    .addGroup(encryptionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addGroup(encryptionLayout.createParallelGroup(Alignment.BASELINE)
                                                    .addComponent(exportButton3)
                                                    .addComponent(endButton3)
                                            )
                                    )
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

            ImageIcon myPicture = new ImageIcon("resources\\pgp_radix.png");
            myPicture.setImage(myPicture.getImage().getScaledInstance(760,250, Image.SCALE_SMOOTH));
            JLabel picLabel = new JLabel(myPicture);

            JButton endButton = new JButton();
            endButton.setPreferredSize(new Dimension(100,20));
            endButton.setText("End");
            endButton.addActionListener(e -> end());
            endButton.setFont(PgpApp.buttonFont);

            JButton exportButton4 = new JButton();
            exportButton4.setPreferredSize(new Dimension(200,20));
            exportButton4.setText("Export message");
            exportButton4.addActionListener(e -> exportMessage());
            exportButton4.setFont(PgpApp.buttonFont);

            GroupLayout radixLayout = new GroupLayout(radixPanel);
            radixLayout.setHorizontalGroup(
                    radixLayout.createParallelGroup(Alignment.LEADING)
                            .addComponent(picLabel, 760, 760, 760)
                            .addGroup(radixLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(radixLayout.createParallelGroup(Alignment.TRAILING)
                                            .addComponent(encryptedMessageRadixLabel)
                                            .addComponent(radixLabel)
                                            .addGap(50)
                                    )
                                    .addGap(18)
                                    .addGroup(radixLayout.createParallelGroup(Alignment.TRAILING, false)
                                        .addComponent(radixScrollPane, GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                                        .addComponent(encryptedMessageRadixScrollPane)
                                            .addGroup(radixLayout.createSequentialGroup()
                                                    .addComponent(exportButton4, 150, 150, 150)
                                                    .addGap(20)
                                                    .addComponent(endButton, 100, 100, 100)
                                            )
                                    )
                                    .addContainerGap(90, Short.MAX_VALUE))
            );
            radixLayout.setVerticalGroup(
                    radixLayout.createParallelGroup(Alignment.LEADING)
                            .addGroup(radixLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(radixLayout.createParallelGroup(Alignment.BASELINE)
                                            .addComponent(encryptedMessageRadixScrollPane)
                                            .addComponent(encryptedMessageRadixLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            )
                                    .addPreferredGap(ComponentPlacement.UNRELATED)
                                    .addGroup(radixLayout.createParallelGroup(Alignment.BASELINE))
                                    .addGap(18)
                                    .addGroup(radixLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(radixLabel)
                                        .addComponent(radixScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    )
                                    .addGap(18)
                                    .addComponent(picLabel, 250,250,250)
                                    .addGap(18)
                                    .addGroup(radixLayout.createParallelGroup(Alignment.BASELINE)
                                            .addGroup(radixLayout.createParallelGroup(Alignment.BASELINE)
                                                    .addComponent(exportButton4)
                                                    .addComponent(endButton)
                                            )
                                    )
                                    .addContainerGap(75, Short.MAX_VALUE))
            );
            radixPanel.setLayout(radixLayout);
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
                String data = null;
                if(encrypt.isRadix64Enabled())
                    data = encrypt.getRadix64Data();
                else if(encrypt.isEncryptEnabled())
                    data = encrypt.getEncryptedData();
                else if(encrypt.isCompressionEnabled())
                    data = encrypt.getCompressedData();
                else if(encrypt.isSignEnabled())
                    data = encrypt.getSignedData();
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
