package br.com.marcosoft.sgi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import br.com.marcosoft.sgi.model.ApropriationFile;
import br.com.marcosoft.sgi.model.Task;
import br.com.marcosoft.sgi.model.TaskRecord;
import br.com.marcosoft.sgi.util.CharsetDetector;
import br.com.marcosoft.sgi.util.Util;

public class ApropriationFileParser {
    //Posicao do tipo de registro
    private static final int POS_TIPO_REGISTRO = 0;

    //Tipos de registro
    private static final String TIPO_REGISTRO_REG = "reg";
    private static final String TIPO_REGISTRO_CFG = "cfg";

    //Registros de configuracao
    private static final int CFG_QUANTIDADE_CAMPOS = 3;
    private static final int POS_CFG_VALOR_PROPRIEDADE = 2;
    private static final int POS_CFG_PROPRIEDADE = 1;

    //Registros de registro de atividade
    private static final int POS_REG_NUMERO_LINHA = 1;
    private static final int POS_REG_DATA = 2;
    @SuppressWarnings("unused")
    private static final int POS_REG_DIA_SEMANA = 3;
    private static final int POS_REG_AJUSTAR_INFORMACOES = 4;
    private static final int POS_REG_UG_CLIENTE = 5;
    private static final int POS_REG_LOTACAO_SUPERIOR = 6;
    private static final int POS_REG_NOME_PROJETO = 7;
    private static final int POS_REG_MACRO = 8;
    private static final int POS_REG_TIPO_HORA = 9;
    private static final int POS_REG_INSUMO = 10;
    private static final int POS_REG_TIPO_INSUMO = 11;
    private static final int POS_REG_DESCRICAO = 12;
    private static final int POS_REG_HORA_INICIO = 13;
    private static final int POS_REG_HORA_TERMINO = 14;
    private static final int POS_REG_DURACAO = 15;
    private static final int REG_QUANTIDADE_CAMPOS = 16;

    private final File inputFile;

    public ApropriationFileParser(final File inputFile) {
        this.inputFile = inputFile;
    }

    public ApropriationFile parse() throws IOException {
        final ApropriationFile ret = new ApropriationFile();

        final BufferedReader input = getReader(this.inputFile);

        String line = null;
        while ((line = input.readLine()) != null) {
            final String[] fields = line.split("\\|");
            if (TIPO_REGISTRO_CFG.equals(fields[POS_TIPO_REGISTRO])) {
                if (fields.length != CFG_QUANTIDADE_CAMPOS)
                    continue;
                ret.getConfig().setProperty(
                    fields[POS_CFG_PROPRIEDADE].trim(), fields[POS_CFG_VALOR_PROPRIEDADE].trim());

            } else if (TIPO_REGISTRO_REG.equals(fields[POS_TIPO_REGISTRO])) {
                if (fields.length != REG_QUANTIDADE_CAMPOS)
                    continue;

                final int duracao = Integer.parseInt(fields[POS_REG_DURACAO]);
                if (duracao == 0)
                    continue;

                final TaskRecord taskRecord = new TaskRecord();
                taskRecord.setData(Util.parseDate("dd/MM/yy", fields[POS_REG_DATA]));
                taskRecord.setDuracao(duracao);
                taskRecord.setTask(parseTask(fields));
                ret.getTasksRecords().add(taskRecord);
            }
        }
        input.close();
        return ret;
    }

    private BufferedReader getReader(File file) throws IOException {
        final InputStream inputStream = new FileInputStream(file);
        final String charset = CharsetDetector.detect(file);
        final InputStreamReader inputStreamReader;
        if (charset != null) {
            inputStreamReader = new InputStreamReader(inputStream, charset);
        } else {
            inputStreamReader = new InputStreamReader(inputStream);
        }
        return new BufferedReader(inputStreamReader);
    }

    private Task parseTask(final String[] fields) {
        final Task task = new Task();
        task.setAjustarInformacoes("Sim".equals(fields[POS_REG_AJUSTAR_INFORMACOES]));
        task.setNumeroLinha(fields[POS_REG_NUMERO_LINHA]);
        task.setUgCliente(fields[POS_REG_UG_CLIENTE]);
        task.setLotacaoSuperior(!"Não".equals(fields[POS_REG_LOTACAO_SUPERIOR]));
        task.setProjeto(fields[POS_REG_NOME_PROJETO]);
        task.setMacro(fields[POS_REG_MACRO]);
        task.setTipoHora(fields[POS_REG_TIPO_HORA]);
        task.setInsumo(fields[POS_REG_INSUMO]);
        task.setTipoInsumo(fields[POS_REG_TIPO_INSUMO]);
        task.setDescricao(fields[POS_REG_DESCRICAO]);
        task.setHoraInicio(fields[POS_REG_HORA_INICIO]);
        task.setHoraTermino(fields[POS_REG_HORA_TERMINO]);
        return task;
    }

}
