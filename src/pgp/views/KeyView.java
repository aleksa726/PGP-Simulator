package pgp.views;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.*;
import org.jdesktop.application.FrameView;
import pgp.PgpApp;
import pgp.keys.KeyManagement;
import pgp.keys.KeyInfo;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class KeyView extends FrameView {

    private static final String TEMP_KEY_FILENAME = "key.asc";

    private KeyInfo keyInfo;
    private JPanel mainPanel;

    public KeyView(PgpApp app, KeyInfo keyInfo) {
        super(app);
        this.keyInfo = keyInfo;
        getFrame().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getFrame().setResizable(false);
        getFrame().pack();
        initComponents();
    }

    private void initComponents() {
        this.mainPanel = new JPanel();
        this.mainPanel.setPreferredSize(new Dimension(640, 540));

        JLabel keyInfoHeading = new JLabel("Key Info");
        keyInfoHeading.setFont(PgpApp.headingFont);

        JTextArea key = new JTextArea();
        key.setEditable(false);
        key.setFont(PgpApp.textfieldFont);

        Date creationDate = null;
        int days = 0;
        if(keyInfo.isPublicKey()){
            PGPPublicKeyRing publicKeyRing;
            try {
                publicKeyRing = KeyManagement.getPublicRingCollection().getPublicKeyRing(this.keyInfo.getKeyIdLong());
                KeyManagement.exportKeyToFile(this.keyInfo, new File(TEMP_KEY_FILENAME));
                key.setText(new String(Files.readAllBytes(Paths.get(TEMP_KEY_FILENAME))));

                creationDate = publicKeyRing.getPublicKey().getCreationTime();
                days = publicKeyRing.getPublicKey().getValidDays();
            } catch (PGPException | IOException e) {
                e.printStackTrace();
            }
        }
        else {
            PGPSecretKeyRing secretKeyRing;
            try {
                secretKeyRing = KeyManagement.getSecretRingCollection().getSecretKeyRing(this.keyInfo.getKeyIdLong());
                KeyManagement.exportKeyToFile(this.keyInfo, new File(TEMP_KEY_FILENAME));
                key.setText(new String(Files.readAllBytes(Paths.get(TEMP_KEY_FILENAME))));

                creationDate = secretKeyRing.getPublicKey().getCreationTime();
                days = secretKeyRing.getPublicKey().getValidDays();
            } catch (PGPException | IOException e) {
                e.printStackTrace();
            }
        }

        JScrollPane jScrollPane = new JScrollPane(key);


        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(PgpApp.labelFont);

        JLabel username = new JLabel(this.keyInfo.getUsername());
        username.setFont(PgpApp.labelFont);

        JLabel emailLabel = new JLabel("Email                       ");
        emailLabel.setFont(PgpApp.labelFont);

        JLabel email = new JLabel(this.keyInfo.getEmail());
        email.setFont(PgpApp.labelFont);

        JLabel keyIdLabel = new JLabel("Key ID");
        keyIdLabel.setFont(PgpApp.labelFont);

        JLabel keyId = new JLabel(this.keyInfo.getKeyId());
        keyId.setFont(PgpApp.labelFont);

        JLabel creationDateLabel = new JLabel("Creation date");
        creationDateLabel.setFont(PgpApp.labelFont);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(creationDate);
        String output = sdf.format(c.getTime());

        JLabel crDate = new JLabel(output);
        crDate.setFont(PgpApp.labelFont);

        JLabel expDateLabel = new JLabel("Expiration date");
        expDateLabel.setFont(PgpApp.labelFont);

        c.add(Calendar.DATE, days+1);
        output = sdf.format(c.getTime());

        JLabel exprDate = new JLabel(output);
        exprDate.setFont(PgpApp.labelFont);

        GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
        this.mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addGap(250)
                                                .addComponent(keyInfoHeading))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addGap(30)
                                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.TRAILING, false)
                                                        .addGroup(Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                                                                .addComponent(expDateLabel)
                                                                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(exprDate))
                                                        .addGroup(Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                                                                .addComponent(usernameLabel)
                                                                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(username))
                                                        .addGroup(Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                                                                .addComponent(emailLabel)
                                                                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(email))
                                                        .addGroup(Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                                                                .addComponent(keyIdLabel)
                                                                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(keyId))
                                                        .addGroup(Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                                                                .addComponent(creationDateLabel)
                                                                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(crDate))))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addGap(30)
                                                .addComponent(jScrollPane, GroupLayout.PREFERRED_SIZE, 580, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(35, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(21)
                                .addComponent(keyInfoHeading)
                                .addGap(18)
                                .addComponent(jScrollPane, GroupLayout.PREFERRED_SIZE, 260, GroupLayout.PREFERRED_SIZE)
                                .addGap(18)
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(usernameLabel)
                                        .addComponent(username))
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(emailLabel)
                                        .addComponent(email))
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(keyIdLabel)
                                        .addComponent(keyId))
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(creationDateLabel)
                                        .addComponent(crDate))
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(expDateLabel)
                                        .addComponent(exprDate))
                                .addContainerGap(15, Short.MAX_VALUE))
        );
        setComponent(this.mainPanel);
    }
}
