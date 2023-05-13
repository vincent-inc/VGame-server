package com.vincent.inc.VGame.model;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Time {
    private int month;
    private int day;
    private int year;
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
}
