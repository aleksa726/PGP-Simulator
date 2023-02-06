package pgp.views;

import pgp.PgpApp;

import javax.swing.*;
import java.awt.*;
import javax.swing.GroupLayout.Alignment;

public class PassphraseInputView extends JDialog {

    private JPanel mainPanel;
    private JPasswordField passphraseField;
    private ConfigureDecryptionView configureDecryptionView;
    private KeyManagementView keyManagementView;
    private ConfigureEncryptionView configureEncryptionView;

    private String text;

    public PassphraseInputView(Frame parent, String text, ConfigureDecryptionView configureDecryptionView, KeyManagementView keyManagementView, ConfigureEncryptionView configureEncryptionView) {
        super(parent, true);
        this.text = text;
        this.configureDecryptionView = configureDecryptionView;
        this.keyManagementView = keyManagementView;
        this.configureEncryptionView = configureEncryptionView;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Enter Passphrase");
        setResizable(false);
        setPreferredSize(new Dimension(350, 170));
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        this.mainPanel = new JPanel();
        this.mainPanel.setPreferredSize(new Dimension(350, 170));

        JLabel enterPassphraseLabel = new JLabel(text);
        enterPassphraseLabel.setPreferredSize(new Dimension(250,40));
        enterPassphraseLabel.setHorizontalAlignment(JLabel.CENTER);
        enterPassphraseLabel.setVerticalAlignment(JLabel.CENTER);
        enterPassphraseLabel.setFont(PgpApp.labelFont);

        passphraseField = new JPasswordField();
        passphraseField.setColumns(10);

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(PgpApp.buttonFont);
        submitButton.addActionListener(e -> {
            if(configureDecryptionView != null) {
                configureDecryptionView.setPassphrase(new String(passphraseField.getPassword()));
                passphraseField.setText("");
            }
            else if(keyManagementView != null){
                keyManagementView.setPassphrase(new String(passphraseField.getPassword()));
                passphraseField.setText("");
            }
            else if(configureEncryptionView != null) {
                configureEncryptionView.setPassphrase(new String(passphraseField.getPassword()));
                passphraseField.setText("");
            }
            dispose();
        });

        GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
        this.mainPanel.setLayout(mainPanelLayout);

        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(enterPassphraseLabel, GroupLayout.PREFERRED_SIZE, 350, GroupLayout.PREFERRED_SIZE))
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(22)
                                .addComponent(passphraseField, GroupLayout.PREFERRED_SIZE, 290, GroupLayout.PREFERRED_SIZE))
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(117)
                                .addComponent(submitButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
        );
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(11)
                                .addComponent(enterPassphraseLabel)
                                .addGap(11)
                                .addComponent(passphraseField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(20)
                                .addComponent(submitButton))
        );

        setContentPane(mainPanel);
    }
}
