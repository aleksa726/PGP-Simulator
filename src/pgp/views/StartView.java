package pgp.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.jdesktop.application.FrameView;
import pgp.PgpApp;
import java.awt.*;

public class StartView extends FrameView {

    private PgpApp app;

    private JRadioButton encryptionRadioButton;
    private JRadioButton decryptionRadioButton;
    private JRadioButton keyManagementRadioButton;

    private ButtonGroup buttonGroup;

    private JPanel contentPane;

    public StartView(PgpApp app) {
        super(app);
        getFrame().setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getFrame().setPreferredSize(new Dimension(420,300));
        getFrame().setResizable(false);
        getFrame().pack();
        this.app = app;
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("RadioButton.focus", new Color(0, 0, 0, 0));
        initComponents();
    }


    private void initComponents() {

        buttonGroup = new ButtonGroup();

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        contentPane.setLayout(null);

        JLabel headingLabel = new JLabel("PGP Visualisation");
        headingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headingLabel.setFont(PgpApp.headingFont);
        headingLabel.setBounds(-20, 29, 450, 40);
        contentPane.add(headingLabel);

        JLabel subheadingLabel = new JLabel("Choose operation:");
        subheadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subheadingLabel.setFont(PgpApp.subheadingFont);
        subheadingLabel.setBounds(-25, 73, 450, 31);
        contentPane.add(subheadingLabel);

        encryptionRadioButton = new JRadioButton("Encryption");
        encryptionRadioButton.setFont(PgpApp.checkboxFont);
        encryptionRadioButton.setBounds(145, 112, 103, 21);

        contentPane.add(encryptionRadioButton);
        buttonGroup.add(encryptionRadioButton);
        encryptionRadioButton.setSelected(true);

        decryptionRadioButton = new JRadioButton("Decryption");
        decryptionRadioButton.setFont(PgpApp.checkboxFont);
        decryptionRadioButton.setBounds(145, 135, 103, 21);
        contentPane.add(decryptionRadioButton);
        buttonGroup.add(decryptionRadioButton);


        keyManagementRadioButton = new JRadioButton("Key Management");
        keyManagementRadioButton.setFont(PgpApp.checkboxFont);
        keyManagementRadioButton.setBounds(145, 158, 125, 21);
        contentPane.add(keyManagementRadioButton);
        buttonGroup.add(keyManagementRadioButton);

        JButton nextButton = new JButton("NEXT");
        nextButton.setBounds(155, 200, 85, 21);
        nextButton.setFont(PgpApp.buttonFont);
        nextButton.addActionListener(e -> nextBt());
        contentPane.add(nextButton);

        this.setComponent(contentPane);
    }

    private void nextBt(){
        if (encryptionRadioButton.isSelected())
            app.showConfigurePgpView(this);
        else if (decryptionRadioButton.isSelected())
            app.showConfigureDecryption(this);
        else if(keyManagementRadioButton.isSelected())
            app.showKeyManagement(this);
    }
}
