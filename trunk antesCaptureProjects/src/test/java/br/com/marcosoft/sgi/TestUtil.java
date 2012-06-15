package br.com.marcosoft.sgi;

import br.com.marcosoft.sgi.util.Util;
import junit.framework.TestCase;

public class TestUtil extends TestCase {

    public final void testFormatMinutes() {
        assertEquals("02:00", Util.formatMinutes(120));
        assertEquals("00:10", Util.formatMinutes(10));
        assertEquals("00:09", Util.formatMinutes(9));
    }

}
