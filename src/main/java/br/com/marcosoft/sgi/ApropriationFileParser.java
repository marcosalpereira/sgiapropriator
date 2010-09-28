package br.com.marcosoft.sgi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import br.com.marcosoft.sgi.model.ApropriationFile;
import br.com.marcosoft.sgi.model.Task;
import br.com.marcosoft.sgi.model.TaskRecord;
import br.com.marcosoft.sgi.util.Util;
import static br.com.marcosoft.sgi.ColunasPlanilha.*;

public class ApropriationFileParser {

    private final File inputFile;

    public ApropriationFileParser(final File inputFile) {
        this.inputFile = inputFile;
    }

    public ApropriationFile parse() throws IOException {
        final ApropriationFile ret = new ApropriationFile();

        final BufferedReader input = new BufferedReader(new FileReader(this.inputFile));
        String line = null;
        while ((line = input.readLine()) != null) {
            final String[] fields = line.split("\\|");
            if (TIPO_REGISTRO_CFG.equals(fields[COL_TIPO_REGISTRO])) {
                if (fields.length != CFG_QUANTIDADE_CAMPOS)
                    continue;
                ret.getConfig().put(fields[COL_CFG_PROPRIEDADE].trim(), fields[COL_CFG_VALOR_PROPRIEDADE].trim());

            } else if (TIPO_REGISTRO_REG.equals(fields[COL_TIPO_REGISTRO])) {
                if (fields.length != REG_QUANTIDADE_CAMPOS)
                    continue;

                final int duracao = Integer.parseInt(fields[COLUNA_DURACAO]);
                if (duracao == 0)
                    continue;

                final TaskRecord taskRecord = new TaskRecord();
                taskRecord.setData(Util.parseDate("dd/MM/yy", fields[COLUNA_DATA]));
                taskRecord.setDuracao(duracao);
                taskRecord.setTask(parseTask(fields));
                ret.getTasksRecords().add(taskRecord);
            }
        }
        input.close();
        return ret;
    }

    // 0 1 2 3 4 5 6 7 8 9 10 11 12 13
    // reg|17/05/10|row|SUNFJ|Sim|(P)- ESAF|Implementação|Normal|Análise de
    // Sistemas|Novo Sistema|At 1|08:00|09:00|60
    private Task parseTask(final String[] fields) {
        final Task task = new Task();
        task.setAjustarInformacoes("Sim".equals(fields[COLUNA_AJUSTAR_INFORMACOES]));
        task.setNumeroLinha(fields[COLUNA_NUMERO_LINHA]);
        task.setUgCliente(fields[COLUNA_UG_CLIENTE]);
        task.setLotacaoSuperior(!"Não".equals(fields[COLUNA_LOTACAO_SUPERIOR]));
        task.setProjeto(fields[COLUNA_NOME_PROJETO]);
        task.setMacro(fields[COLUNA_MACRO]);
        task.setTipoHora(fields[COLUNA_TIPO_HORA]);
        task.setInsumo(fields[COLUNA_INSUMO]);
        task.setTipoInsumo(fields[COLUNA_TIPO_INSUMO]);
        task.setDescricao(fields[COLUNA_DESCRICAO]);
        task.setHoraInicio(fields[COLUNA_HORA_INICIO]);
        task.setHoraTermino(fields[COLUNA_HORA_TERMINO]);
        return task;
    }

}
