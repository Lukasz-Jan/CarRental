package com.lj.data;



import com.lj.CarType;

import java.time.ZonedDateTime;

public final class RentDetails {

    private final ZonedDateTime st;
    private final ZonedDateTime end;
    private final Integer userId;
    private boolean added = false;
    private int carId;
    private final CarType carType;

    public RentDetails(ZonedDateTime st, ZonedDateTime end, Integer userId, int carId, boolean added, CarType carType) {
        this.st = st;
        this.end = end;
        this.userId = userId;
        this.carId = carId;
        this.added = added;
        this.carType = carType;
    }

    @Override
    public String toString() {
        return "Period{" +
                "st=" + st.toLocalDate() +
                ", end=" + end.toLocalDate() +
                '}';
    }


    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public ZonedDateTime getSt() {
        return st;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public int getCarId() {
        return carId;
    }

    public CarType getCarType() {
        return carType;
    }




}
