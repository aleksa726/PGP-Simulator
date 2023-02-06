package pgp;

import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.View;
import pgp.algorithm.PGPDecrypt;
import pgp.algorithm.PGPEncrypt;
import pgp.keys.KeyInfo;
import pgp.views.*;

import javax.swing.*;
import java.awt.*;

public class PgpApp extends SingleFrameApplication {

    public static final Font labelFont = new Font("Monospaced", Font.PLAIN, 15);
    public static final Font buttonFont = new Font("Monospaced", Font.PLAIN, 13);
    public static final Font textfieldFont = new Font("Monospaced", Font.PLAIN, 13);
    public static final Font headingFont = new Font("Monospaced", Font.BOLD, 28);
    public static final Font subheadingFont = new Font("Monospaced", Font.PLAIN, 14);
    public static final Font checkboxFont = new Font("Monospaced", Font.PLAIN, 12);


    @Override
    protected void startup() {

        for(UIManager.LookAndFeelInfo info: javax.swing.UIManager.getInstalledLookAndFeels()) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        }

        show((View)new StartView(this));
    }

    public void showEncDecView(FrameView fv) {
        fv.getFrame().setVisible(false);
        show((View)new StartView(this));
    }
    public void showConfigurePgpView(FrameView fv) {
        fv.getFrame().setVisible(false);
        show((View)new ConfigureEncryptionView(this));
    }

    public void showPgpView(FrameView fv, PGPEncrypt encrypt) {
        fv.getFrame().setVisible(false);
        show((View)new PgpEncryptView(this, encrypt));
    }

    public void showConfigureDecryption(FrameView fv){
        fv.getFrame().setVisible(false);
        show((View)new ConfigureDecryptionView(this));
    }

    public void showPgpDecryptView(FrameView fv, PGPDecrypt decrypt){
        fv.getFrame().setVisible(false);
        show((View)new PgpDecryptView(this, decrypt));
    }

    public void showKeyManagement(FrameView fv){
        fv.getFrame().setVisible(false);
        show((View)new KeyManagementView(this));
    }


    public void showEncryptionAndDecryptionView(FrameView fv, PGPEncrypt encrypt, PGPDecrypt decrypt){
        fv.getFrame().setVisible(false);
        show((View)new EncryptionAndDecryptionView(this, encrypt, decrypt));
    }

    public void showKey(KeyInfo keyInfo){
        show((View)new KeyView(this, keyInfo));
    }

    public static PgpApp getApplication() {
        return (PgpApp) Application.getInstance(PgpApp.class);
    }

    public static void main(String[] args) {
        launch(PgpApp.class, args);
    }

}
