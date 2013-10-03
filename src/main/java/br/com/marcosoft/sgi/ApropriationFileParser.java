package br.com.marcosoft.sgi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import br.com.marcosoft.sgi.model.ApropriationFile;
import br.com.marcosoft.sgi.model.Projeto;
import br.com.marcosoft.sgi.model.ProjetoAlm;
import br.com.marcosoft.sgi.model.ProjetoSgi;
import br.com.marcosoft.sgi.model.Task;
import br.com.marcosoft.sgi.model.TaskRecord;
import br.com.marcosoft.sgi.util.CharsetDetector;
import br.com.marcosoft.sgi.util.Util;

public class ApropriationFileParser {
    //Posicao do tipo de registro
    private static final int POS_TIPO_REGISTRO = 0;

    //Tipos de registro
    private static final String TR_ATIVIDADE = "reg";
    private static final String TR_CONFIG = "cfg";
    private static final String TR_PROJETO = "prj";
    private static final String TR_CAPTURE = "cap";

    //Registros de configuracao
    private static final int CFG_QUANTIDADE_CAMPOS = 3;
    private static final int POS_CFG_VALOR_PROPRIEDADE = 2;
    private static final int POS_CFG_PROPRIEDADE = 1;

    //Projetos
    private static final int PRJ_QUANTIDADE_CAMPOS = 2;
    private static final int POS_PRJ_PROJETOS = 1;

    //Registros de registro de atividade
    private static final int POS_REG_NUMERO_LINHA = 1;
    private static final int POS_REG_DATA = 2;
    @SuppressWarnings("unused")
    private static final int POS_REG_DIA_SEMANA = 3;
    private static final int POS_REG_AJUSTAR_INFORMACOES = 4;
    private static final int POS_REG_UG_CLIENTE = 5;
    @SuppressWarnings("unused")
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
        final ApropriationFile ret = new ApropriationFile(this.inputFile);

        final BufferedReader input = getReader(this.inputFile);

        try {
            String line = null;
            while ((line = input.readLine()) != null) {
                parseLine(ret, line);
            }
        }
        finally {
            input.close();
        }


        return ret;
    }

    private void parseLine(final ApropriationFile ret, String line) throws IOException {
        final String[] fields = line.split("\\|", -1);
        if (TR_CONFIG.equals(fields[POS_TIPO_REGISTRO])) {
            parseConfig(ret, fields);

        } else if (TR_PROJETO.equals(fields[POS_TIPO_REGISTRO])) {
            parseProjects(ret, fields);

        } else if (TR_ATIVIDADE.equals(fields[POS_TIPO_REGISTRO])) {
            parseAtividade(ret, fields);

        } else if (TR_CAPTURE.equals(fields[POS_TIPO_REGISTRO])) {
            ret.setCaptureProjects(true);
        }
    }

    private void parseAtividade(final ApropriationFile ret, final String[] fields)
        throws IOException {
        if (fields.length != REG_QUANTIDADE_CAMPOS) {
            throw new IOException(
                "Erro lendo as atividades. Quantidade de campos difere da esperada!");
        }
        final int duracao = Integer.parseInt(fields[POS_REG_DURACAO]);
        if (duracao != 0) {
            final TaskRecord taskRecord = new TaskRecord();
            taskRecord.setData(Util.parseDate(Util.DD_MM_YY_FORMAT, fields[POS_REG_DATA]));
            taskRecord.setDuracao(duracao);
            taskRecord.setTask(parseTask(fields));
            ret.adicionarTasksRecord(taskRecord);
        }
    }

    private void parseProjects(final ApropriationFile ret, final String[] fields)
        throws IOException {
        if (fields.length != PRJ_QUANTIDADE_CAMPOS) {
            throw new IOException(
                "Erro lendo os projetos: Quantidade de campos difere da esperada!");
        }
        final String projetosStr = fields[POS_PRJ_PROJETOS];
        if (projetosStr.length() == 0) return;

        final String[] projetos = projetosStr.split("\"");
        for (final String prj : projetos) {
            final Projeto projeto = parseProjeto(prj);
            if (projeto != null) {
                ret.getProjects().add(projeto);
            }
        }

    }

    private Projeto parseProjeto(final String prj) {
        if (prj.length() > 1) {
            return new Projeto(prj);
        }
        return null;
    }

    private void parseConfig(final ApropriationFile ret, final String[] fields)
        throws IOException {
        if (fields.length != CFG_QUANTIDADE_CAMPOS) {
            throw new IOException(
                "Erro lendo as configurações da planilha. Quantidade de campos difere da esperada!");
        }
        ret.getConfig().setProperty(
            fields[POS_CFG_PROPRIEDADE].trim(), fields[POS_CFG_VALOR_PROPRIEDADE].trim());
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

    private Task parseTask(final String[] fields) throws IOException {
        final Task task = new Task();
        task.setAjustarInformacoes("Sim".equals(fields[POS_REG_AJUSTAR_INFORMACOES]));
        task.setSistema(fields[POS_REG_AJUSTAR_INFORMACOES]);
        task.setNumeroLinha(fields[POS_REG_NUMERO_LINHA]);
        task.setProjeto(parseProjeto(fields));
        task.setMacro(fields[POS_REG_MACRO]);
        task.setTipoHora(fields[POS_REG_TIPO_HORA]);
        task.setInsumo(fields[POS_REG_INSUMO]);
        task.setTipoInsumo(fields[POS_REG_TIPO_INSUMO]);
        task.setDescricao(fields[POS_REG_DESCRICAO]);
        task.setHoraInicio(fields[POS_REG_HORA_INICIO]);
        task.setHoraTermino(fields[POS_REG_HORA_TERMINO]);

        return task;
    }

    private Projeto parseProjeto(String[] fields) throws IOException {
        final String[] dados = fields[POS_REG_NOME_PROJETO].split(";");
        if (Util.isSistemaAlm(fields[POS_REG_AJUSTAR_INFORMACOES])) {
            if (dados.length < 2) {
                throw new IOException(
                    "Erro lendo projeto ALM: Quantidade de campos difere da esperada!");
            }
            return new ProjetoAlm(dados[0], dados[1]);
        } else {
            if (dados.length == 2) {
                return new ProjetoSgi(dados[0], dados[1]);
            }
            return new ProjetoSgi(fields[POS_REG_UG_CLIENTE], fields[POS_REG_NOME_PROJETO]);
        }

    }

}
