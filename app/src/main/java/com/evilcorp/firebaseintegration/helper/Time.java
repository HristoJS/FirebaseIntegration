package com.evilcorp.firebaseintegration.helper;

import com.instacart.library.truetime.TrueTime;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by hristo.stoyanov on 23-Feb-17.
 */

public class Time {

    private static final List<Long> TIMES = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1) );

    private static final List<String> TIMES_STRING = Arrays.asList("year","month","day","hour","minute","second");


    public static void init(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    com.instacart.library.truetime.TrueTime.build().withNtpHost("time.google.com").initialize();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static Date getDate(){
        return TrueTime.isInitialized() ? TrueTime.now() : new Date();
    }

    public static long getTime(){
        return TrueTime.isInitialized() ? TrueTime.now().getTime() : new Date().getTime();
    }

    public static String getUtcTime(){
        DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return utcFormat.format(TrueTime.now());
    }

    public static Date getLocalTime(long timestamp){
        return new Date(timestamp);
    }

    public static long milisSinceOnline(long timestamp){
        return getTime() - timestamp;
    }

    public static String timeAgo(long duration) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i< TIMES.size(); i++) {
            Long current = TIMES.get(i);
            long temp = milisSinceOnline(duration) / current;
            if (temp > 0) {
                sb.append(temp)
                        .append(" ")
                        .append(TIMES_STRING.get(i))
                        .append(temp > 1 ? "s" : "")
                        .append(" ago");
                break;
            }
        }
        return sb.toString().isEmpty() ? "A moment ago" : sb.toString();
    }
}
