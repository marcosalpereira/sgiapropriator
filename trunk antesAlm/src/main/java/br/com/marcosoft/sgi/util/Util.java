package br.com.marcosoft.sgi.util;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {

    /**
     * DD_MM_YYYY Date formatter não "Lenient".
     */
    public static final DateFormat DD_MM_YYYY_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    static {
        DD_MM_YYYY_FORMAT.setLenient(false);
    }

    /**
     * DD_MM_YY Date formatter não "Lenient".
     */
    public static final DateFormat DD_MM_YY_FORMAT = new SimpleDateFormat("dd/MM/yy");
    static {
        DD_MM_YY_FORMAT.setLenient(false);
    }

    public static String formatDate(Date data) {
        final String[] weekDays = {"Domingo", "Segunda", "Terça", "Quarta", "Quinta",
            "Sexta", "Sabado"};

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(data);

        final String weekDay = weekDays[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        return DD_MM_YYYY_FORMAT.format(data) + " " + weekDay;
    }

    public static Date parseDate(DateFormat dateFormat, String value)
        throws IllegalArgumentException {
        if (value != null && !value.equals("") && !value.equals("-")) {
            final ParsePosition pos = new ParsePosition(0);
            final Date date = dateFormat.parse(value, pos);
            if (date != null && pos.getIndex() == value.length()) {
                return date;
            }
            throw new IllegalArgumentException("Data inválida.");
        }
        return null;
    }

    public static String formatMinutes(int minutes) {
        final int hor = minutes / 60;
        final int min = minutes - hor * 60;
        return String.format("%02d:%02d", hor, min);
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
            return;
        }
    }

    public static boolean isSimilar(String str1, String str2) {
        final String str1SemSpc = str1.replaceAll("\\W", "").toLowerCase();
        final String str2SemSpc = str2.replaceAll("\\W", "").toLowerCase();
        if (str1SemSpc.equals(str2SemSpc))
            return true;
        for (int i = str1SemSpc.length() - 1; i > 15; i--) {
            final String substring = str1SemSpc.substring(0, i);
            if (str2SemSpc.startsWith(substring)) {
                return true;
            }
        }
        return false;
    }

}
