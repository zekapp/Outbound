package com.outbound.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zeki on 22/10/2014.
 */
public final class TimeUtil {

    public static String convertDateProperFormat(Date date) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");

        Date currentTime = new Date();

        long d1 = currentTime.getTime();
        long d2 = date.getTime();

        int day = (int) Math.abs((d1-d2)/(1000*60*60*24));

        if(day > 0) {
//            return dateFormatter.format(date);
            return convertDateFormatAccordingToday(date) ;
        }
        else{
            return timeFormatter.format(date);
        }

    }

    public static String convertDateFormatAccordingToday(Date createdAt) {
        SimpleDateFormat dateformatter = new SimpleDateFormat("EEE, d MMM yyyy");
        Date currentDate = new Date();
        int daysBetween = daysBetween(currentDate, createdAt);

        if(daysBetween == 0){
            return "Today";
        }else if(daysBetween == 1)
        {
            return "Yesterday";
        }else
            return  dateformatter.format(createdAt);
    }

    public static String convertTimeFormat(Date createdAt) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
        return  timeFormatter.format(createdAt);
    }

    public static String convertDateTimeFormat(Date createdAt) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy");

        return formatter.format(createdAt);
    }

    public static int daysBetween(Date bigDate, Date smallDate) {
        Date bDate;
        Date sDate;

        Calendar bcal = Calendar.getInstance();
        bcal.setTime(bigDate);
        bcal.set(Calendar.HOUR_OF_DAY, 0);
        bcal.set(Calendar.MINUTE, 0);
        bcal.set(Calendar.SECOND, 0);
        bcal.set(Calendar.MILLISECOND, 0);
        bDate= bcal.getTime();

        Calendar scal = Calendar.getInstance();
        scal.setTime(smallDate);
        scal.set(Calendar.HOUR_OF_DAY, 0);
        scal.set(Calendar.MINUTE, 0);
        scal.set(Calendar.SECOND, 0);
        scal.set(Calendar.MILLISECOND, 0);
        sDate= scal.getTime();


        long d1 = bDate.getTime();
        long d2 = sDate.getTime();

        return  (int) Math.abs((d1-d2)/(1000*60*60*24));
    }
}
