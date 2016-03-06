package com.atreid.expresslanesimages;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by atrei_000 on 3/6/2016.
 */
public class ExpressLanesStatusRetriever {
    private String status;

    public String getDirection() {
        return direction;
    }

    private String direction = "";
    public static final String NORTH = "north";
    public static final String SOUTH = "south";
    public static final String CLOSED = "closed";

    public ExpressLanesStatusRetriever() {
        status = "unable to retrieve status";
        Calendar cal = Calendar.getInstance();
        TimeZone timeZone = TimeZone.getTimeZone("America/New_York");
        int hourOffSet = timeZone.getOffset(new Date().getTime()) / 1000 / 60 / 60;
        int hour = cal.get(Calendar.HOUR_OF_DAY) + hourOffSet; //24H
        int minute = cal.get(Calendar.MINUTE);

        int day = cal.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.SUNDAY) { //sunday
            //midnight to midnight
            status = "Open Northbound";
            direction = NORTH;
        } else if (day == Calendar.SATURDAY) {
            //Midnight to 2 p.m.
            if (hour <= 14) {
                status = "Open Southbound, closes at 2PM";
                direction = SOUTH;
            } else if (hour > 14 && hour < 16) {
                //2 p.m. to 4 p.m.
                status = "Closed, opens Northbound at 4PM";
                direction = CLOSED;
            } else {
                //4 p.m. to midnight
                status = "Open Northbound";
                direction = NORTH;
            }
        } else {
            if ((hour < 2 || (hour == 2 && minute < 30))) {
                if (day != Calendar.MONDAY) {
                    //Midnight to 2:30 a.m.
                    status = "Closed, opens Northbound at 2:30AM";
                    direction = CLOSED;
                } else {
                    status = "Open NorthBound, closes at 11AM";
                    direction = NORTH;
                }
            } else if ((hour == 2 && minute >= 30) || hour < 11) {
                //2:30 a.m. to 11 a.m.
                status = "Open Northbound, closes at 11AM";
                direction = NORTH;
            } else if (hour >= 11 && hour <= 13) {
                //11 a.m. to 1 p.m.
                status = "Closed, opens Northbound at 1PM";
                direction = CLOSED;
            } else {
                if (day != Calendar.FRIDAY) {
                    //1 p.m. to midnight
                    status = "Open Southbound, closes at Midnight";
                    direction = SOUTH;
                } else {
                    status = "Open Southbound";
                    direction = SOUTH;
                }
            }
        }
    }

    @Override
    public String toString() {
        return status;
    }
}
