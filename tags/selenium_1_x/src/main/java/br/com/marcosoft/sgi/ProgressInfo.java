package br.com.marcosoft.sgi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;

import br.com.marcosoft.sgi.model.TaskDailySummary;
import br.com.marcosoft.sgi.util.AWTUtilitiesWrapper;
import br.com.marcosoft.sgi.util.Util;

/**
 *
 */
public class ProgressInfo extends JFrame {

    /**
     * Serial.
     */
    private static final long serialVersionUID = -6989750848125302233L;

    private javax.swing.JLabel lblAtividade;
    private javax.swing.JLabel lblDuracao;
    private javax.swing.JLabel lblMacro;
    private javax.swing.JLabel lblProgresso;
    private javax.swing.JLabel lblProjeto;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel txtAtividade;
    private javax.swing.JLabel txtDuracao;
    private javax.swing.JLabel txtMacro;
    private javax.swing.JLabel txtProgresso;
    private JPanel jp;
    private javax.swing.JLabel txtProjeto;

    /** Creates new form NewJPanel */
    public ProgressInfo(String title) {
        initComponents(title);
    }

    private void initComponents(String title) {

        GridBagLayout thisLayout1 = new GridBagLayout();
        thisLayout1.columnWidths = new int[] {7};
        thisLayout1.rowHeights = new int[] {7};
        thisLayout1.columnWeights = new double[] {0.1};
        thisLayout1.rowWeights = new double[] {0.1};
        getContentPane().setLayout(thisLayout1);

        {
            jp = new JPanel();

            Color blueBorder = new Color(21,38,82);
            Color blueBackGround = new Color(58,71,106);
            jp.setBorder(new LineBorder(blueBorder, 3, true));
            jp.setBackground(blueBackGround);

            GridBagLayout thisLayout = new GridBagLayout();
            thisLayout.columnWeights = new double[] {0.0, 0.7};
            thisLayout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
            thisLayout.rowHeights = new int[] {7, 7, 7, 7, 7, 7};
            thisLayout.columnWidths = new int[] {109};
            jp.setLayout(thisLayout);


            getContentPane().add(jp, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            {
                lblProjeto = new javax.swing.JLabel();
                jp.add(lblProjeto, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));
                lblProjeto.setForeground(new java.awt.Color(254, 254, 254));
                lblProjeto.setText("Projeto");
            }
            {
                txtProgresso = new javax.swing.JLabel();
                jp.add(txtProgresso, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                txtProgresso.setBackground(java.awt.SystemColor.text);
                txtProgresso.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
                txtProgresso.setOpaque(true);
                txtProgresso.setSize(395, 20);
                txtProgresso.setPreferredSize(new java.awt.Dimension(100, 20));
            }
            {
                txtProjeto = new javax.swing.JLabel();
                jp.add(txtProjeto, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 10), 0, 0));
                txtProjeto.setBackground(java.awt.SystemColor.text);
                txtProjeto.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
                txtProjeto.setOpaque(true);
                txtProjeto.setSize(395, 20);
                txtProjeto.setPreferredSize(new java.awt.Dimension(0, 20));
            }
            {
                lblMacro = new javax.swing.JLabel();
                jp.add(lblMacro, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));
                lblMacro.setForeground(new java.awt.Color(254, 254, 254));
                lblMacro.setText("Macro");
            }
            {
                txtMacro = new javax.swing.JLabel();
                jp.add(txtMacro, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 10), 0, 0));
                txtMacro.setBackground(java.awt.SystemColor.text);
                txtMacro.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
                txtMacro.setOpaque(true);
                txtMacro.setSize(395, 20);
                txtMacro.setPreferredSize(new java.awt.Dimension(0, 20));
            }
            {
                lblAtividade = new javax.swing.JLabel();
                jp.add(lblAtividade, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));
                lblAtividade.setForeground(new java.awt.Color(254, 254, 254));
                lblAtividade.setText("Atividade");
            }
            {
                txtAtividade = new javax.swing.JLabel();
                jp.add(txtAtividade, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 10), 0, 0));
                txtAtividade.setBackground(java.awt.SystemColor.text);
                txtAtividade.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
                txtAtividade.setOpaque(true);
                txtAtividade.setSize(395, 20);
                txtAtividade.setPreferredSize(new java.awt.Dimension(0, 20));
            }
            {
                lblDuracao = new javax.swing.JLabel();
                jp.add(lblDuracao, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));
                lblDuracao.setForeground(new java.awt.Color(254, 254, 254));
                lblDuracao.setText("Duração");
            }
            {
                txtDuracao = new javax.swing.JLabel();
                jp.add(txtDuracao, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 10, 10), 0, 0));
                txtDuracao.setBackground(java.awt.SystemColor.text);
                txtDuracao.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
                txtDuracao.setOpaque(true);
                txtDuracao.setSize(395, 20);
                txtDuracao.setPreferredSize(new java.awt.Dimension(0, 20));
            }
            {
                lblTitle = new javax.swing.JLabel();
                jp.add(lblTitle, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 15, 10), 0, 0));
                lblTitle.setBackground(new java.awt.Color(249, 249, 249));
                lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                lblTitle.setText(title);
                lblTitle.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
                lblTitle.setOpaque(true);
                lblTitle.setSize(679, 22);
                lblTitle.setPreferredSize(new java.awt.Dimension(0, 22));
            }
            {
                lblProgresso = new javax.swing.JLabel();
                lblProgresso.setText("Apropriando");
                jp.add(lblProgresso, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));
                lblProgresso.setForeground(new java.awt.Color(254, 254, 254));
            }
            jp.setOpaque(true);
        }

        this.setUndecorated(true);
        this.pack();
        this.setSize(739, 222);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        centerMe();

        addEfects();
        this.setAlwaysOnTop(true);
        this.setVisible(true);

    }

    private void centerMe() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        int screenWidth = screenSize.width;

        this.setLocation(screenWidth / 2 - this.getWidth() / 2, 30);
    }


    private void addEfects() {
        boolean isOpacityControlSupported = AWTUtilitiesWrapper.isTranslucencySupported(AWTUtilitiesWrapper.TRANSLUCENT);
        if (isOpacityControlSupported) {
            AWTUtilitiesWrapper.setWindowOpacity(this, 0.85f);
        }
//        boolean isShapingSupported = AWTUtilitiesWrapper.isTranslucencySupported(AWTUtilitiesWrapper.PERPIXEL_TRANSPARENT);
//        if (isShapingSupported) {
//            Shape shape = new RoundRectangle2D.Float(0, 0, this.getWidth(), this.getHeight(), 30, 30);
//            AWTUtilitiesWrapper.setWindowShape(this, shape);
//        }

    }

    public void setInfo(String progresso, TaskDailySummary tds) {
        txtProgresso.setText(progresso);
        txtAtividade.setText(tds.getFirstTask().getDescricao());
        txtDuracao.setText(Util.formatMinutes(tds.getSum()));
        txtMacro.setText(tds.getFirstTask().getMacro());
        txtProjeto.setText(tds.getFirstTask().getProjeto());
    }

    public static void main(String[] args) {
        new ProgressInfo("casa").setInfo("1/1", new TaskDailySummary());
    }

}
