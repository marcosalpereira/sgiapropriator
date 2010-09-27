package br.com.marcosoft.sgi;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import br.com.marcosoft.sgi.model.Task;
import br.com.marcosoft.sgi.model.TaskRecord;
import br.com.marcosoft.sgi.util.Util;

public class InputProjectDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private static boolean ajustou;
    private JButton btnDone;
    private JScrollPane jScrollPane1;
    private JButton btnCancelar;
    private JPanel jPanel1;
    private JLabel lblMensagem;
    private JLabel lblAtividade;
    private JTable tblTask;

    public static void main(final String[] args) {
        TaskRecord taskRecord = new TaskRecord();
        taskRecord.setData(new Date());
        taskRecord.setTask(new Task()); 
        System.out.println(selecionarProjeto(taskRecord));
    }

    /**
     * Dialogo para que se possa ajustar os dados da apropriacao.
     * @param taskRecord task
     * @return <code>true</code> se ajustou.
     */
    public static boolean selecionarProjeto(TaskRecord taskRecord) {
        final InputProjectDialog inst = new InputProjectDialog(taskRecord);
        
        ajustou = false;
        inst.setVisible(true);
        inst.dispose();

        return ajustou;
    }

    private InputProjectDialog(TaskRecord taskRecord) {
        super((Frame) null, true);
        initGUI();

        Task task = taskRecord.getTask();
        String[][] dados = new String[][] { { Util.DD_MM_YYYY_FORMAT.format(taskRecord.getData()), task.getUgCliente(),
            task.isLotacaoSuperior() ? "Sim" : "Não", task.getMacro(),
            task.getDescricao(), task.getHoraInicio(), task.getHoraTermino() } };
        TableModel tblResultsModel = new DefaultTableModel(dados, 
            new String[] { "Data", "UG Cliente", "Lot.Superior", "Macro", "Atividade", "Inicio", "Fim" });
        this.tblTask.setModel(tblResultsModel);
    }

    private void initGUI() {
        this.setTitle("Ajustar as informações para a atividade");
        setAlwaysOnTop(true);
        GridBagLayout thisLayout = new GridBagLayout();
        thisLayout.rowWeights = new double[] {0.0, 0.0, 0.1};
        thisLayout.rowHeights = new int[] {7, 7, 7};
        thisLayout.columnWeights = new double[] {0.0, 0.1};
        thisLayout.columnWidths = new int[] {7, 7};
        getContentPane().setLayout(thisLayout);
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        getContentPane().setBackground(new Color(21,38,82));
        {
        	lblAtividade = new JLabel();
        	getContentPane().add(lblAtividade, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        	lblAtividade.setText("Atividade");
        	lblAtividade.setBackground(new java.awt.Color(255,255,255));
        	lblAtividade.setForeground(new java.awt.Color(255,255,255));
        	lblAtividade.setFont(new java.awt.Font("AlArabiya",1,12));
        }
        {
        	jScrollPane1 = new JScrollPane();
        	getContentPane().add(jScrollPane1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 13));
        	{
        		TableModel tblTaskModel = 
        			new DefaultTableModel(
        					new String[][] { { "", "", "", "", "", "", "" } },
        					new String[] { "Data", "UG Cliente", "Lot.Superior", "Macro", "Atividade", "Inicio", "Fim" });
        		tblTask = new JTable();
        		jScrollPane1.setViewportView(tblTask);
        		tblTask.setModel(tblTaskModel);
        	}
        }
        {
        	lblMensagem = new JLabel();
        	getContentPane().add(lblMensagem, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
        	lblMensagem.setText("<html><center>Ajuste os campos na janela do SGI como se fosse fazer a apropriação. <br />Quando tiver terminado, acione o botão 'Terminei Ajustes' para continuar o processo de apropriação</center></html>");
        	lblMensagem.setForeground(new java.awt.Color(255,255,255));
        	lblMensagem.setFont(new java.awt.Font("Arial",3,20));
        	lblMensagem.setHorizontalAlignment(SwingConstants.CENTER);
        	lblMensagem.setHorizontalTextPosition(SwingConstants.CENTER);
        }
        {
        	jPanel1 = new JPanel();
        	FlowLayout jPanel1Layout = new FlowLayout();
        	jPanel1Layout.setAlignment(FlowLayout.RIGHT);
        	jPanel1.setLayout(jPanel1Layout);
        	getContentPane().add(jPanel1, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        	jPanel1.setOpaque(false);
        	{
        		btnDone = new JButton();
        		jPanel1.add(btnDone);
        		btnDone.setText("Terminei Ajustes");
        		btnDone.addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent evt) {
        				btnSelecionarActionPerformed(evt);
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
        ajustarTamanhoPosicao();
    }

    private void ajustarTamanhoPosicao() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        this.setSize(screenSize.width, 196);
        this.setLocation(1, 30);
    }

    private void btnSelecionarActionPerformed(
        @SuppressWarnings("unused") final ActionEvent evt) {
        ajustou = true;
        setVisible(false);
    }
    
    private void btnCancelarActionPerformed(@SuppressWarnings("unused") ActionEvent evt) {
        ajustou = false;
        setVisible(false);
    }

}