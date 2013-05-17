package br.com.marcosoft.sgi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import br.com.marcosoft.sgi.model.ApropriationFile;
import br.com.marcosoft.sgi.model.Projeto;

public class ApropriationFileParserTest {

    @Test
    public void testParseEmptyProjects() throws IOException {
        final ApropriationFile apropriationFile = parse("/parse/projects/emptyProjects.csv");
        assertTrue(apropriationFile.getProjects().isEmpty());
    }

    @Test
    public void testParseOneProject() throws IOException {
        final ApropriationFile apropriationFile = parse("/parse/projects/oneProject.csv");
        assertEquals(Arrays.asList(new Projeto("projeto sgi")), apropriationFile.getProjects());
    }

    @Test
    public void testParseProjectsQtdParametrosInvalido() {
        try {
            parse("/parse/projects/qtdParametrosInvalido.csv");
        } catch (final IOException e) {
            assertEquals(
                "Erro lendo os projetos: Quantidade de campos difere da esperada!",
                e.getMessage());
        }
    }

    @Test
    public void testParseConfigQtdParametrosInvalido() {
        try {
            parse("/parse/config/qtdParametrosInvalido.csv");
        } catch (final IOException e) {
            assertEquals(
                "Erro lendo as configurações da planilha. Quantidade de campos difere da esperada!",
                e.getMessage());
        }
    }

    @Test
    public void testParseConfigAppProperty() throws IOException {
        final ApropriationFile apropriationFile = parse("/parse/config/appProperty.csv");
        assertEquals("123", apropriationFile.getConfig().getCpf());
    }

    @Test
    public void testParseConfigSysProperty() throws IOException {
        final String sysProperty = "sys.property";
        System.clearProperty(sysProperty);
        assertNull(System.getProperty(sysProperty));

        parse("/parse/config/sysProperty.csv");
        assertEquals("valor", System.getProperty(sysProperty));
    }

    @Test
    public void testParseTwoProjects() throws IOException {
        final ApropriationFile apropriationFile = parse("/parse/projects/twoProjects.csv");
        assertEquals(
            Arrays.asList(new Projeto("projeto sgi"), new Projeto("projeto x")),
            apropriationFile.getProjects());
    }

    @Test
    public void testParseCapture() throws IOException {
        final ApropriationFile apropriationFile = parse("/parse/capture/capture.csv");
        assertTrue(apropriationFile.isCaptureProjects());
    }



    private ApropriationFile parse(String arquivoCsv) throws IOException {
        final ApropriationFileParser fileParser =
            new ApropriationFileParser(getFile(arquivoCsv));
        return fileParser.parse();
    }

    private File getFile(String filename) {
        final String fullname = this.getClass().getResource(filename).getFile();
        return new File(fullname);
    }

}
