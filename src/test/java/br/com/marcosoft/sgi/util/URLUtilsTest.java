package br.com.marcosoft.sgi.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class URLUtilsTest {

    @Test
    public void testEncode() {
        assertEquals("SUPDE-Atividade%20N%C3%A3o%20Software",
            URLUtils.encode("SUPDE-Atividade Não Software"));
    }

}
