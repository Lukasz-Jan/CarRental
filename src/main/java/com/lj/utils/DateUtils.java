package com.lj.utils;

import com.lj.data.RentDetails;

import java.time.ZonedDateTime;

public class DateUtils {

    public static boolean isInside(ZonedDateTime arg, RentDetails p) {

        ZonedDateTime st = p.getSt();
        ZonedDateTime end = p.getEnd();
        if (arg.compareTo(st) >= 0 && arg.compareTo(end) <= 0) return true;
        return false;
    }

    public static boolean argContainsExistingPeriod(ZonedDateTime argStart, ZonedDateTime argEnd, RentDetails p) {

        ZonedDateTime st = p.getSt();
        ZonedDateTime end = p.getEnd();
        if (argStart.compareTo(st) <= 0 && argEnd.compareTo(end) >= 0) return true;
        return false;
    }
}
