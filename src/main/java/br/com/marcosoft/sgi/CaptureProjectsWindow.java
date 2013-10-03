package br.com.marcosoft.sgi;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;

import br.com.marcosoft.sgi.model.Projeto;
import br.com.marcosoft.sgi.model.ProjetoSgi;
import br.com.marcosoft.sgi.po.ApropriationPage;
import br.com.marcosoft.sgi.util.AWTUtilitiesWrapper;
import br.com.marcosoft.sgi.util.MoveMouseListener;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class CaptureProjectsWindow extends JDialog {

    /**
     * Serial.
     */
    private static final long serialVersionUID = -6989750848125302233L;

    private JPanel jp;
    private JPanel filtro;
    private JTextField txtUgCliente;
    private JButton btnCancelar;
    private JButton btnConfirmar;
    private JPanel jPanel1;
    private JLabel jLabel2;
    private JPanel selecionados;
    private JButton btnAdicionarProjeto;
    private JLabel jLabel1;
    private JPanel resultados;
    private JScrollPane jScrollPane1;
    private JList lstSelecionados;
    private JComboBox cboProjetos;
    private JButton btnCarregarProjetos;
    private JCheckBox chkLotacaoSuperior;
    private JLabel lblTitle;
    private JLabel lblProgresso;

    private Collection<Projeto> projetosSelecionados;

    private final ApropriationPage apropriationPage;

    public static void main(String[] args) {
        final Projeto projeto = new ProjetoSgi("SUPDE", "SISCON");

        final ApropriationPage apropriationPage = new ApropriationPage() {
            @Override
            public Collection<String> capturarProjetos(String ugCliente,
                boolean lotacaoSuperior) {
                return Arrays.asList("casa");
            }

            @Override
            public Selenium getSelenium() {
                return new DefaultSelenium(null) {
                    @Override
                    public boolean isElementPresent(String locator) {
                        return true;
                    }
                };
            }
        };

        final CaptureProjectsWindow c = new CaptureProjectsWindow(
            "tit", Arrays.asList(projeto), apropriationPage);
        System.out.println(c.getSelectedProjects());
    }

    /** Creates new form NewJPanel
     * @param title titulo da janela
     * @param apropriationPage pagina de apropriacao
     * @param projetos projetos atuais
     **/
    public CaptureProjectsWindow(String title, Collection<Projeto> projetos, ApropriationPage apropriationPage) {
        this.apropriationPage = apropriationPage;
        initComponents(title, projetos);
    }

    private void initComponents(String title, Collection<Projeto> projetos) {

    	final GridBagLayout thisLayout1 = new GridBagLayout();
    	thisLayout1.rowWeights = new double[] {1.0};
    	thisLayout1.rowHeights = new int[] {7};
    	thisLayout1.columnWeights = new double[] {1.0};
    	thisLayout1.columnWidths = new int[] {7};
        getContentPane().setLayout(thisLayout1);

        {
            jp = new JPanel();

            final Color blueBackGround = new Color(58,71,106);
            jp.setBorder(new LineBorder(new java.awt.Color(230,230,250), 3, true));
            jp.setBackground(blueBackGround);

            final GridBagLayout thisLayout = new GridBagLayout();
            thisLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.1, 0.0};
            thisLayout.rowHeights = new int[] {7, 7, 7, 7, 7};
            thisLayout.columnWeights = new double[] {0.1};
            thisLayout.columnWidths = new int[] {7};
            jp.setLayout(thisLayout);

            getContentPane().add(jp, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

            jp.setOpaque(true);
            {
            	lblTitle = new javax.swing.JLabel();
            	jp.add(lblTitle, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(3, 10, 3, 10), 0, 0));
            	lblTitle.setBackground(new java.awt.Color(249,249,249));
            	lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            	lblTitle.setText(title);
            	lblTitle.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
            	lblTitle.setOpaque(true);
            	lblTitle.setSize(679, 22);
            	lblTitle.setPreferredSize(new java.awt.Dimension(0,22));
            }
            {
            	filtro = new JPanel();
            	final FlowLayout filtroLayout = new FlowLayout();
            	filtroLayout.setAlignment(FlowLayout.LEFT);
            	filtroLayout.setHgap(10);
            	filtro.setLayout(filtroLayout);
            	jp.add(filtro, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            	filtro.setBackground(new java.awt.Color(58,71,106));
            	{
            		lblProgresso = new javax.swing.JLabel();
            		filtro.add(lblProgresso);
            		lblProgresso.setText("UG.Cliente");
            		lblProgresso.setForeground(new java.awt.Color(254,254,254));
            		lblProgresso.setPreferredSize(new java.awt.Dimension(124, 15));
            	}
            	{
            		txtUgCliente = new JTextField();
            		filtro.add(txtUgCliente);
            		txtUgCliente.setBackground(java.awt.SystemColor.text);
            		txtUgCliente.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
            		txtUgCliente.setOpaque(true);
            		txtUgCliente.setSize(395, 20);
            		txtUgCliente.setPreferredSize(new java.awt.Dimension(100,20));
            	}
            	{
            		chkLotacaoSuperior = new JCheckBox();
            		filtro.add(chkLotacaoSuperior);
            		chkLotacaoSuperior.setText("Lotação Superior");
            		chkLotacaoSuperior.setBackground(new java.awt.Color(58,71,106));
            		chkLotacaoSuperior.setForeground(new java.awt.Color(255,255,255));
            	}
            	{
            		btnCarregarProjetos = new JButton();
            		filtro.add(btnCarregarProjetos);
            		btnCarregarProjetos.setText("Carregar Projetos");
            		btnCarregarProjetos.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            btnCarregarProjetosActionPerformed(evt);
                        }
                    });
            	}
            }
            {
            	resultados = new JPanel();
            	jp.add(resultados, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 10, 0, 10), 0, 0));
            	final GridBagLayout jPanel1Layout = new GridBagLayout();
            	jPanel1Layout.rowWeights = new double[] {0.1};
            	jPanel1Layout.rowHeights = new int[] {7};
            	jPanel1Layout.columnWeights = new double[] {0.0, 0.1, 0.0};
            	jPanel1Layout.columnWidths = new int[] {7, 7, 7};
            	resultados.setBackground(new java.awt.Color(58,71,106));
            	resultados.setLayout(jPanel1Layout);
            	{
            		jLabel1 = new JLabel();
            		resultados.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
            		jLabel1.setText("Projetos Carregados");
            		jLabel1.setForeground(new java.awt.Color(254,254,254));
            	}
            	{
            		final ComboBoxModel cboProjetosModel = new DefaultComboBoxModel();
            		cboProjetos = new JComboBox();
            		resultados.add(cboProjetos, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
            		cboProjetos.setModel(cboProjetosModel);
            	}
            	{
            		btnAdicionarProjeto = new JButton();
            		resultados.add(btnAdicionarProjeto, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            		btnAdicionarProjeto.setText("Adicionar");
            		btnAdicionarProjeto.addActionListener(new ActionListener() {
            			public void actionPerformed(ActionEvent evt) {
            				btnAdicionarProjetoActionPerformed(evt);
            			}
            		});
            	}
            }
            {
            	selecionados = new JPanel();
            	jp.add(selecionados, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 10, 0, 10), 0, 0));
            	final GridBagLayout jPanel1Layout1 = new GridBagLayout();
            	jPanel1Layout1.rowWeights = new double[] {0.0, 0.1};
            	jPanel1Layout1.rowHeights = new int[] {7, 7};
            	jPanel1Layout1.columnWeights = new double[] {0.1};
            	jPanel1Layout1.columnWidths = new int[] {7};
            	selecionados.setBackground(new java.awt.Color(58,71,106));
            	selecionados.setLayout(jPanel1Layout1);
            	{
            		jLabel2 = new JLabel();
            		selecionados.add(jLabel2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            		jLabel2.setText("Projetos Selecionados [Duplo click para remover o projeto]");
            		jLabel2.setForeground(new java.awt.Color(254,254,254));
            	}
            	{
            		jScrollPane1 = new JScrollPane();
            		selecionados.add(jScrollPane1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            		{
            			final ListModel lstSelecionadosModel =
            					new DefaultComboBoxModel(projetos.toArray());
            			lstSelecionados = new JList();
            			jScrollPane1.setViewportView(lstSelecionados);
            			lstSelecionados.setModel(lstSelecionadosModel);
            			lstSelecionados.setToolTipText("Duplo click para remover o projeto");
            			lstSelecionados.addMouseListener(new MouseAdapter() {
            				@Override
            				public void mouseClicked(MouseEvent evt) {
            					lstSelecionadosMouseClicked(evt);
            				}
            			});
            		}
            	}
            }
            {
            	jPanel1 = new JPanel();
            	jp.add(jPanel1, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));
            	final FlowLayout jPanel1Layout2 = new FlowLayout();
            	jPanel1Layout2.setAlignment(FlowLayout.RIGHT);
            	jPanel1Layout2.setHgap(10);
            	jPanel1.setBackground(new java.awt.Color(58,71,106));
            	jPanel1.setLayout(jPanel1Layout2);
            	{
            		btnConfirmar = new JButton();
            		jPanel1.add(btnConfirmar);
            		btnConfirmar.setText("Confirmar");
            		btnConfirmar.addActionListener(new ActionListener() {
            			public void actionPerformed(ActionEvent evt) {
            				btnConfirmarActionPerformed(evt);
            			}
            		});
            	}
            	{
            		btnCancelar = new JButton();
            		jPanel1.add(btnCancelar);
            		btnCancelar.setText("Cancelar");
            		btnCancelar.addActionListener(new ActionListener() {
            			public void actionPerformed(ActionEvent evt) {
            				btnCancelarActionPerformed(evt);
            			}
            		});
            	}
            }
        }

        final MoveMouseListener mml = new MoveMouseListener(jp);
        jp.addMouseListener(mml);
        jp.addMouseMotionListener(mml);
        jp.setCursor(new Cursor(Cursor.MOVE_CURSOR));

        this.setUndecorated(true);
        this.pack();
        this.setSize(755, 387);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        centerMe();

        AWTUtilitiesWrapper.setOpacity(this);
        this.setTitle("Importar projetos");
        this.setAlwaysOnTop(true);
        setModal(true);
        this.setVisible(true);

    }

    private void centerMe() {
        final Toolkit tk = Toolkit.getDefaultToolkit();
        final Dimension screenSize = tk.getScreenSize();
        final int screenWidth = screenSize.width;

        this.setLocation(screenWidth / 2 - this.getWidth() / 2, 30);
    }

    private void btnAdicionarProjetoActionPerformed(@SuppressWarnings("unused") ActionEvent evt) {
        final Projeto projeto = new ProjetoSgi(txtUgCliente.getText(), (String) cboProjetos.getSelectedItem());
        final DefaultComboBoxModel model = (DefaultComboBoxModel) lstSelecionados.getModel();
        model.addElement(projeto);
    }

    private void btnConfirmarActionPerformed(@SuppressWarnings("unused") ActionEvent evt) {
        final DefaultComboBoxModel model  = (DefaultComboBoxModel) lstSelecionados.getModel();
        this.projetosSelecionados = new ArrayList<Projeto>();
        for (int i=0; i<model.getSize(); i++) {
            this.projetosSelecionados.add((Projeto) model.getElementAt(i));
        }
    	this.dispose();

    }

    private void btnCancelarActionPerformed(@SuppressWarnings("unused") ActionEvent evt) {
    	this.dispose();
    	this.projetosSelecionados = null;
    }

    private void btnCarregarProjetosActionPerformed(@SuppressWarnings("unused") ActionEvent evt) {
        final Cursor cursorAnterior = this.getCursor();
        this.jp.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        final Collection<String> nomesProjetos =
            apropriationPage.capturarProjetos(
                txtUgCliente.getText(), chkLotacaoSuperior.isSelected());
        cboProjetos.setModel(new DefaultComboBoxModel(nomesProjetos.toArray()));
        this.jp.setCursor(cursorAnterior);
    }

    public Collection<Projeto> getSelectedProjects() {
        return projetosSelecionados;
    }

    private void lstSelecionadosMouseClicked(MouseEvent evt) {
        final int selectedIndex = lstSelecionados.getSelectedIndex();
        if (evt.getClickCount() == 2 && selectedIndex != -1) {
            final DefaultComboBoxModel model = (DefaultComboBoxModel) lstSelecionados.getModel();
            model.removeElementAt(selectedIndex);
        }
    }

}
