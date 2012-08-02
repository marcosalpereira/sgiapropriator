package br.com.marcosoft.sgi.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {

    public static final SimpleDateFormat DD_MM_YYYY_FORMAT = new SimpleDateFormat(
        "dd/MM/yyyy");

    public static String formatDate(Date data) {
        final String[] weekDays = {"Domingo", "Segunda", "Terça", "Quarta", "Quinta",
            "Sexta", "Sabado"};

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(data);

        final String weekDay = weekDays[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        return DD_MM_YYYY_FORMAT.format(data) + " " + weekDay;
    }

    public static Date parseDate(String pattern, String value)
        throws IllegalArgumentException {
        if (value != null && !value.equals("") && !value.equals("-")) {
            final SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            formatter.setLenient(false);
            final ParsePosition pos = new ParsePosition(0);
            final Date date = formatter.parse(value, pos);
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
