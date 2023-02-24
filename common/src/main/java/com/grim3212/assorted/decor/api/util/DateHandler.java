package com.grim3212.assorted.decor.api.util;

public class DateHandler {

    private static String dateFormat = "%md %mm, %yyyy";
    private static String dayNames[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thurday", "Friday", "Saturday"};
    private static String shortDays[] = {"Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat"};
    private static String monthNames[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private static String shortMonths[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private static int monthLengths[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public String getDateFormat() {
        return dateFormat;
    }

    public String getDayNames() {
        StringBuffer stringbuffer = new StringBuffer();
        if (dayNames != null) {
            stringbuffer.append(dayNames[0]);
            for (int i = 1; i < dayNames.length; i++) {
                stringbuffer.append(",");
                stringbuffer.append(dayNames[i]);
            }
        }
        return stringbuffer.toString();
    }

    public String getMonthNames() {
        StringBuffer stringbuffer = new StringBuffer();
        if (monthNames != null) {
            stringbuffer.append(monthNames[0]);
            for (int i = 1; i < monthNames.length; i++) {
                stringbuffer.append(",");
                stringbuffer.append(monthNames[i]);
            }
        }
        return stringbuffer.toString();
    }

    public String getMonthLengths() {
        StringBuffer stringbuffer = new StringBuffer();
        if (monthLengths != null) {
            stringbuffer.append(monthLengths[0]);
            for (int i = 1; i < monthLengths.length; i++) {
                stringbuffer.append(",");
                stringbuffer.append(monthLengths[i]);
            }
        }
        return stringbuffer.toString();
    }

    public static String ordinalNo(int i) {
        int j = i % 100;
        int k = i % 10;
        if (j - k == 10) {
            return "th";
        }
        switch (k) {
            case 1:
                return "st";

            case 2:
                return "nd";

            case 3:
                return "rd";
        }
        return "th";
    }

    // TODO: Clean this up
    public static String calculateDate(Long long1, int format) {
        long numDays = (long1.longValue() + 24000L) / 24000L;
        long dayOfMonth = numDays;
        int j = 0;
        int numYears = 0;
        boolean flag = false;
        do {
            if (flag) {
                break;
            }
            if (dayOfMonth > (long) monthLengths[j]) {
                dayOfMonth -= monthLengths[j];
                if (++j == monthLengths.length) {
                    j = 0;
                    numYears++;
                }
            } else {
                flag = true;
            }
        } while (true);
        int dayOfTheWeek = (int) (numDays % (long) dayNames.length);
        String days = String.valueOf(numDays);
        String weekday = String.valueOf(dayOfTheWeek + 1);
        String dayName = dayNames[dayOfTheWeek];
        String shortDay = shortDays[dayOfTheWeek];
        String week = String.valueOf(numDays / (long) dayNames.length + 1L);
        String monthDay = String.valueOf(dayOfMonth);
        String monthName = monthNames[j];
        String shortMonth = shortMonths[j];
        String year = (new StringBuilder()).append("Year ").append(String.valueOf(numYears + 1)).toString();
        String s9 = dateFormat;
        s9 = s9.replace("%dddd", dayName).replace("%dd", days).replace("%wd", weekday).replace("%ww", week);
        s9 = s9.replace("%md", (new StringBuilder()).append(monthDay).append(ordinalNo((int) dayOfMonth)).toString());
        s9 = s9.replace("%mm", monthName);
        s9 = s9.replace("%yyyy", year);

        if (format == 1) {
            return (new StringBuilder()).append(year).append(",").append(shortMonth).append(" ").append(monthDay).append(",").append(shortDay).toString();
        } else {
            return s9;
        }
    }
}
