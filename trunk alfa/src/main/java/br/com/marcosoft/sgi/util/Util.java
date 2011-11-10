package br.com.marcosoft.sgi.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {

    public static final SimpleDateFormat DD_MM_YYYY_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public static String formatDate(Date data) {
        final String[] weekDays = {"Domingo", "Segunda", "Terça", "Quarta", "Quinta",
            "Sexta", "Sabado"};

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data);

        String weekDay = weekDays[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        return DD_MM_YYYY_FORMAT.format(data) + " " + weekDay;
    }

    public static Date parseDate(String pattern, String value)
        throws IllegalArgumentException {
        if (value != null && !value.equals("") && !value.equals("-")) {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            formatter.setLenient(false);
            ParsePosition pos = new ParsePosition(0);
            Date date = formatter.parse(value, pos);
            if (date != null && pos.getIndex() == value.length()) {
                return date;
            }
            throw new IllegalArgumentException("Data inválida.");
        }
        return null;
    }

    public static String formatMinutes(int minutes) {
        int hor = minutes / 60;
        int min = minutes - hor * 60;
        return String.format("%02d:%02d", hor, min);
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            return;
        }
    }

}
