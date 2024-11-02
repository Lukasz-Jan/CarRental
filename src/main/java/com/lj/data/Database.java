package com.lj.data;

import com.lj.utils.DateUtils;
import com.lj.utils.Printer;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class Database {

    private static Database instance;

    private ConcurrentHashMap<Integer, List<RentDetails>> vans;
    private ConcurrentHashMap<Integer, List<RentDetails>> sedans;
    private ConcurrentHashMap<Integer, List<RentDetails>> suvs;

    private Database() {

        vans = new ConcurrentHashMap<>();
        for (int i = 0; i < 3; i++) {
            vans.put(i, new ArrayList<>());
        }
        sedans = new ConcurrentHashMap<>();
        for (int i = 0; i < 3; i++) {
            sedans.put(i, new ArrayList<>());
        }
        suvs = new ConcurrentHashMap<>();
        for (int i = 0; i < 3; i++) {
            suvs.put(i, new ArrayList<>());
        }
    }

    public static Database instance() {

        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public RentDetails reserveSuv(Integer userId, RentDetails requestPeriod) {

        for (Map.Entry<Integer, List<RentDetails>> en : suvs.entrySet()) {

            BiFunction<Integer, List<RentDetails>, List<RentDetails>> addPeriodForCarLmb = (carId, list) -> addPeriodForCar(carId, requestPeriod, list, userId);

            Integer vanId = en.getKey();

            List<RentDetails> rentDetailsList = suvs.compute(vanId, addPeriodForCarLmb);

            if (requestPeriod.isAdded()) {

                RentDetails rentDetails = rentDetailsList.get(rentDetailsList.size() - 1);

                return rentDetails;
            }
        }

        return requestPeriod;
    }

    public RentDetails reserveSedan(Integer userId, RentDetails requestPeriod) {

        for (Map.Entry<Integer, List<RentDetails>> en : sedans.entrySet()) {

            BiFunction<Integer, List<RentDetails>, List<RentDetails>> addPeriodForCarLmb = (carId, list) -> addPeriodForCar(carId, requestPeriod, list, userId);

            Integer vanId = en.getKey();

            List<RentDetails> rentDetailsList = sedans.compute(vanId, addPeriodForCarLmb);

            if (requestPeriod.isAdded()) {

                RentDetails rentDetails = rentDetailsList.get(rentDetailsList.size() - 1);

                return rentDetails;
            }
        }

        return requestPeriod;
    }

    public RentDetails reserveVan(Integer userId, RentDetails requestPeriod) {

        for (Map.Entry<Integer, List<RentDetails>> en : vans.entrySet()) {


            BiFunction<Integer, List<RentDetails>, List<RentDetails>> addPeriodForCarLmb = (carId, list) -> addPeriodForCar(carId, requestPeriod, list, userId);


            Integer vanId = en.getKey();

            List<RentDetails> rentDetailsList = vans.compute(vanId, addPeriodForCarLmb);

            if (requestPeriod.isAdded()) {

                RentDetails rentDetails = rentDetailsList.get(rentDetailsList.size() - 1);

                return rentDetails;
            }
        }

        return requestPeriod;
    }

    private List<RentDetails> addPeriodForCar(int carId, RentDetails requestArg, List<RentDetails> periodList, Integer userId) {


        if (periodList.isEmpty()) {

            RentDetails rentDetails = new RentDetails(requestArg.getSt(), requestArg.getEnd(), userId, carId, true, requestArg.getCarType());

            periodList.add(rentDetails);
            //System.out.println("    Car id " + carId + " rented " + requestArg.getSt().toLocalDate() + "   " + requestArg.getEnd().toLocalDate());
            requestArg.setAdded(true);

        } else {

            for (RentDetails rentPeriod : periodList) {

                ZonedDateTime argStart = requestArg.getSt();
                ZonedDateTime argEnd = requestArg.getEnd();

                boolean isInside = false;
                if (DateUtils.isInside(argStart, rentPeriod) || DateUtils.isInside(argEnd, rentPeriod)) {
                    isInside = true;
                }

                boolean argContains = false;
                if (DateUtils.argContainsExistingPeriod(argStart, argEnd, rentPeriod)) {
                    argContains = true;
                }

                if(isInside || argContains) {
                    requestArg.setAdded(false);
                    return periodList;
                }
            }

            RentDetails rentDetails = new RentDetails(requestArg.getSt(), requestArg.getEnd(), userId, carId, true, requestArg.getCarType());
            requestArg.setAdded(true);
            periodList.add(rentDetails);

            //System.out.println("    Car id " + carId + " rented " + requestArg.getSt().toLocalDate() + "   " + requestArg.getEnd().toLocalDate());
        }

        return periodList;
    }


    // exposed for tests
    public void clearDatabse() {

        //vans = new ConcurrentHashMap<>();
        vans.clear();
        for (int i = 0; i < 3; i++) {
            vans.put(i, new ArrayList<>());
        }

        sedans.clear();
        for (int i = 0; i < 3; i++) {
            sedans.put(i, new ArrayList<>());
        }

        suvs.clear();
        for (int i = 0; i < 3; i++) {
            suvs.put(i, new ArrayList<>());
        }
    }
}
