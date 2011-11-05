package br.com.marcosoft.sgi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class EsperarLoginUsuario extends JDialog {
    private static final long serialVersionUID = 1L;

    private JLabel lblMensagem;

    public EsperarLoginUsuario() {
        super((Frame) null, false);
        initGUI();
        setVisible(true);
    }

    private void initGUI() {
        this.setTitle("Esperando pelo login");
        setAlwaysOnTop(true);
        final GridBagLayout thisLayout = new GridBagLayout();
        thisLayout.rowWeights = new double[] {0.1, 0.0};
        thisLayout.rowHeights = new int[] {7, 7};
        thisLayout.columnWeights = new double[] {0.1};
        thisLayout.columnWidths = new int[] {7};
        getContentPane().setLayout(thisLayout);
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        getContentPane().setBackground(new Color(21, 38, 82));
        {
            this.lblMensagem = new JLabel();
            getContentPane().add(
                this.lblMensagem,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 10, 0, 10), 0, 0));
            this.lblMensagem
                .setText("<html>Esperando pelo login do usuário</html>");
            this.lblMensagem.setForeground(new java.awt.Color(255, 255, 255));
            this.lblMensagem.setFont(new java.awt.Font("Arial", 3, 20));
            this.lblMensagem.setHorizontalAlignment(SwingConstants.CENTER);
            this.lblMensagem.setHorizontalTextPosition(SwingConstants.CENTER);
            this.lblMensagem.setAutoscrolls(true);
            this.lblMensagem.setIconTextGap(0);
            this.lblMensagem.setVerticalTextPosition(SwingConstants.TOP);
        }
        this.setSize(707, 100);
        centerMe();
    }

    private void centerMe() {
        final Toolkit tk = Toolkit.getDefaultToolkit();
        final Dimension screenSize = tk.getScreenSize();
        final int screenWidth = screenSize.width;

        this.setLocation(screenWidth / 2 - this.getWidth() / 2, 30);
    }

}