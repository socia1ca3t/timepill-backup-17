package com.socia1ca3t.timepillbackup.util;

import java.time.Duration;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {


    public static long minutesBetweenDate(Date startDate, Date endDate) {

        return durationBetweenDate(startDate, endDate).toMinutes();
    }


    public static long millisBetweenDate(Date startDate, Date endDate) {

        return durationBetweenDate(startDate, endDate).toMillis();
    }


    public static Duration durationBetweenDate(Date startDate, Date endDate) {


        return Duration.between(
                startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }


}
