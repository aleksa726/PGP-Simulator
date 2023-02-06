package pgp.views;

import pgp.PgpApp;

import javax.swing.*;
import java.awt.*;

public class ErrorView extends JDialog {

    private String message;
    private JLabel messageLabel;
    private JButton okButton;

    public ErrorView(Frame parent){
        super(parent, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setTitle("Error");
        setPreferredSize(new Dimension(300, 140));
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {

        setLayout(new FlowLayout());

        messageLabel = new JLabel(message);
        messageLabel.setFont(PgpApp.labelFont);
        messageLabel.setForeground(Color.RED);
        messageLabel.setPreferredSize(new Dimension(300,50));
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setVerticalAlignment(JLabel.CENTER);
        add(messageLabel);

        okButton = new JButton("Ok");
        okButton.setPreferredSize(new Dimension(65,25));
        okButton.addActionListener(e -> dispose());
        add(okButton);

    }

    public void setMessage(String message) {
        this.message = message;
        this.messageLabel.setText(message);
    }
}
