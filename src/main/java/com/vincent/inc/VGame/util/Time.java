package com.vincent.inc.VGame.util;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Time {
    public static final int MAX_MONTH = 12;
    public static final int MAX_HOURS = 24;
    public static final int MAX_MINUTE = 60;
    public static final int MAX_SECOND = 60;

    private int year;
    private int month;
    private int day;
    private int hours;
    private int minute;
    private int second;

    public Time() {
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();

        this.month = localDate.getMonthValue();
        this.day = localDate.getDayOfMonth();
        this.year = localDate.getYear();
        this.hours = localTime.getHour();
        this.minute = localTime.getMinute();
        this.second = localTime.getSecond();
    }

    public LocalDate toLocalDate() {
        return LocalDate.of(this.year, this.month, this.day);
    }

    public LocalTime toLocalTime() {
        return LocalTime.of(this.hours, this.minute, this.second);
    }

    public boolean isBefore(Time time) {
        if(this.toLocalDate().isBefore(time.toLocalDate()))
            return true;

        if(this.toLocalTime().isBefore(time.toLocalTime()))
            return true;

        return false;
    }

    public boolean isAfter(Time time) {
        if(this.toLocalDate().isAfter(time.toLocalDate()))
            return true;

        if(this.toLocalTime().isAfter(time.toLocalTime()))
            return true;

        return false;
    }

    public int getMaxDay(int month) {
        switch(month) {
            case 2:
                return 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
        }
    }

    public int getMaxDay() {
        return this.getMaxDay(this.month);
    }

    public void increaseYear(int year) {
        this.year += year;
    }

    public void increaseMonth(int month) {
        this.month += month;
        if(this.month > MAX_MONTH) {
            this.increaseYear(this.month / MAX_MONTH);
            this.month = this.month % MAX_MONTH;
        }
    }

    public void increaseDay(int day) {
        this.day += day;
        int MAX_DAY = this.getMaxDay();
        if(this.day > MAX_DAY) {
            this.increaseMonth(this.day / MAX_DAY);
            this.day = this.day % this.getMaxDay(this.month);
        }
    }

    public void increaseHours(int hours) {
        this.hours += hours;
        if(this.hours > MAX_HOURS) {
            this.increaseDay(this.hours / MAX_HOURS);
            this.hours = this.hours % MAX_HOURS;
        }
    }

    public void increaseMinute(int minute) {
        this.minute += minute;
        if(this.minute > MAX_MINUTE) {
            this.increaseHours(this.minute / MAX_MINUTE);
            this.minute = this.minute % MAX_MINUTE;
        }
    }

    public void increaseSecond(int second) {
        this.second += second;
        if(this.second > MAX_SECOND) {
            this.increaseMinute(this.second / MAX_SECOND);
            this.second = this.second % MAX_SECOND;
        }
    }
}
