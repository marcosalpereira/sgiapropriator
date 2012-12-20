package br.com.marcosoft.sgi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.com.marcosoft.sgi.util.Cipher;

public class TestCipher {

    @Test
    public void testCriptUncript() {
        final String plain = "abcdefghijk";
        assertEquals(plain, Cipher.uncript(Cipher.cript(plain)));
    }

    @Test
    public void testInterlace() {
        assertEquals("abc1def2ghi3jkl4mno5pq", Cipher.interlace("abcdefghijklmnopq", "12345", 3));
    }

    @Test
    public void testUninterlace() {
        assertEquals("12345", Cipher.uninterlace("abc1def2ghi3jkl4mno5pq", 3));
    }

}
