package com.lj.utils;

import com.lj.data.RentDetails;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Printer {

    public static void printVans(ConcurrentHashMap<Integer, List<RentDetails>> vans) {

        for (Map.Entry<Integer, List<RentDetails>> en : vans.entrySet()) {

            System.out.println("    VAN no: " + en.getKey());
            List<RentDetails> values = en.getValue();

            for (RentDetails p : values) {
                System.out.println("          " + p);
            }
        }
    }

    public static void printRentedSedans(ConcurrentHashMap<Integer, List<RentDetails>> sedans) {

        for (Map.Entry<Integer, List<RentDetails>> en : sedans.entrySet()) {

            System.out.println("    Sedan no: " + en.getKey());
            List<RentDetails> values = en.getValue();

            for (RentDetails p : values) {
                System.out.println("          " + p);
            }
        }
    }

    public void printRentedSuvs(ConcurrentHashMap<Integer, List<RentDetails>> suvs) {
        for (Map.Entry<Integer, List<RentDetails>> en : suvs.entrySet()) {

            System.out.println("    Suv no: " + en.getKey());
            List<RentDetails> values = en.getValue();

            for (RentDetails p : values) {
                System.out.println("          " + p);
            }
        }
    }
}
