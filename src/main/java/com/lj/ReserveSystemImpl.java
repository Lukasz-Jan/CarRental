package com.lj;

import com.lj.data.Database;
import com.lj.data.RentDetails;
import java.time.ZonedDateTime;

public class ReserveSystemImpl extends ReserveSystem {

    public Database getDatabaseService() {return databaseService; }

    private final Database databaseService = Database.instance();

    public boolean checkArguments(Integer userId, CarType type, ZonedDateTime argStart, ZonedDateTime argEnd) {

        if(type == null || argStart == null || argEnd == null) {
            return false;
        }

        if (argStart.isAfter(argEnd)) {
            return false;
        }

        if(argStart.compareTo(argEnd) == 0){
            return false;
        }

        return true;
    }


    public RentDetails reserveInternal(Integer userId, RentDetails requestDetails) {

        CarType carType = requestDetails.getCarType();

        if(carType.equals(CarType.VAN)) {

            RentDetails rentDetails = databaseService.reserveVan(userId, requestDetails);
            return rentDetails;
        }

        if(carType.equals(CarType.SEDAN)) {

            RentDetails rentDetails = databaseService.reserveSedan(userId, requestDetails);
            return rentDetails;
        }

        if(carType.equals(CarType.SUV)) {

            RentDetails rentDetails = databaseService.reserveSuv(userId, requestDetails);
            return rentDetails;
        }

        return requestDetails;
    }
}
