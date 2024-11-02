package com.lj;

import com.lj.data.Database;
import com.lj.data.RentDetails;

import java.time.ZonedDateTime;

public abstract class ReserveSystem {


    public RentDetails reserve(Integer userId, CarType type, ZonedDateTime argStart, ZonedDateTime argEnd) {

        boolean validation =  checkArguments(userId,  type,  argStart,  argEnd);

        RentDetails requestDetails = new RentDetails(argStart, argEnd, userId, -1, false, type);

        if(!validation) {
            return requestDetails;
        }

        requestDetails = reserveInternal(userId,  requestDetails);

        return requestDetails;
    }

    public abstract boolean checkArguments(Integer userId, CarType type, ZonedDateTime argStart, ZonedDateTime argEnd);

    public abstract RentDetails reserveInternal(Integer userId, RentDetails requestDetails);

    public abstract Database getDatabaseService();
}
