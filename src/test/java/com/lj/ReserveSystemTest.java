package com.lj;

import com.lj.data.Database;
import com.lj.data.RentDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/*
    General note fo tests:
    For each type three cars are available
*/

class ReserveSystemTest {


    private final ReserveSystem reserveSystem = new ReserveSystemImpl();

    /*
        1.
        Parallel 30 rent requests for the same period of time from different users
        10 requests for sedan , 10 requests for van, 10 for suv
        3 per type shall pass as only, 3 cars per type are available

        2.
        Random subsequent rent requests for different periods of time for random types
     */
    @Test
    void reserve_three_types_parallel_requests_for_same_period_and_other_periods() throws InterruptedException, ExecutionException, ExecutionException {


        ZonedDateTime nextWeek = ZonedDateTime.now().plusWeeks(1);

        ExecutorService srv = Executors.newFixedThreadPool(4);

        int noOfRequests = 30;

        Future<RentDetails>[] arr = (Future<RentDetails>[]) new Future[noOfRequests];

        int noOfConfirmedReservations = 0;

        Callable<RentDetails> call;

        for (int i = 0; i < 10; i++) {
            Integer ii = i;
            call = () -> reserveSystem.reserve(ii, CarType.VAN, nextWeek, nextWeek.plusDays(1));
            arr[ii] = srv.submit(call);

            Integer iii = ii + 10;
            call = () -> reserveSystem.reserve(iii, CarType.SEDAN, nextWeek, nextWeek.plusDays(1));
            arr[iii] = srv.submit(call);

            Integer iiii = ii + 20;
            call = () -> reserveSystem.reserve(iiii, CarType.SUV, nextWeek, nextWeek.plusDays(1));
            arr[iiii] = srv.submit(call);
        }


        int rentedVans = 0;
        int rentedSedans = 0;
        int rentedSuvs = 0;

        for (int i = 0; i < noOfRequests; i++) {

            while (!arr[i].isDone()) {
                Thread.sleep(10);
            }

            if (arr[i].isDone()) {
                RentDetails rentDetails = arr[i].get();

                if (rentDetails.getCarId() > -1) {
                    noOfConfirmedReservations++;

                    assertEquals(nextWeek, rentDetails.getSt());
                    assertEquals(nextWeek.plusDays(1), rentDetails.getEnd());

                    if(rentDetails.getCarType().equals(CarType.VAN)) {
                        rentedVans++;
                    }
                    if(rentDetails.getCarType().equals(CarType.SEDAN)) {
                        rentedSedans++;
                    }
                    if(rentDetails.getCarType().equals(CarType.SUV)) {
                        rentedSuvs++;
                    }
                }

            }
        }

        assertEquals(9, noOfConfirmedReservations);
        assertEquals(3, rentedVans);
        assertEquals(3, rentedSedans);
        assertEquals(3, rentedSuvs);

        // suv occupied date
        RentDetails details;
        details  = reserveSystem.reserve(1, CarType.SUV, nextWeek, nextWeek.plusDays(1));
        assertFalse(details.isAdded());

        // suv not occupied date
        details  = reserveSystem.reserve(1, CarType.SUV, nextWeek.plusDays(2), nextWeek.plusDays(4));
        assertTrue(details.isAdded());
        assertEquals(nextWeek.plusDays(2), details.getSt());
        assertEquals(nextWeek.plusDays(4), details.getEnd());
        assertEquals(details.getCarType(), CarType.SUV);

        details  = reserveSystem.reserve(1, CarType.SUV, nextWeek.plusDays(2), nextWeek.plusDays(4));
        assertTrue(details.isAdded());
        assertEquals(nextWeek.plusDays(2), details.getSt());
        assertEquals(nextWeek.plusDays(4), details.getEnd());
        assertEquals(details.getCarType(), CarType.SUV);


        details  = reserveSystem.reserve(1, CarType.SUV, nextWeek.plusDays(2), nextWeek.plusDays(4));
        assertTrue(details.isAdded());
        assertEquals(nextWeek.plusDays(2), details.getSt());
        assertEquals(nextWeek.plusDays(4), details.getEnd());
        assertEquals(details.getCarType(), CarType.SUV);

        details  = reserveSystem.reserve(1, CarType.SUV, nextWeek.plusDays(2), nextWeek.plusDays(4));
        assertFalse(details.isAdded());



        // 100 requests for suv for occupied date

        noOfRequests = 100;

        arr = (Future<RentDetails>[]) new Future[noOfRequests];

        for (int i = 0; i < noOfRequests; i++) {
            Integer ii = i;
            call = () -> reserveSystem.reserve(ii, CarType.SUV, nextWeek.plusDays(2), nextWeek.plusDays(4));
            arr[ii] = srv.submit(call);
        }

        for (int i = 0; i < noOfRequests; i++) {
            while (!arr[i].isDone()) {
                Thread.sleep(10);
            }
            if (arr[i].isDone()) {
                RentDetails rentDetails = arr[i].get();
                assertFalse(rentDetails.isAdded());
            }
        }
    }

    /*
        3 vans available so last two for the covering dates fail
        first van reserved in subsequent 3 periods
        2 vans reserved in periods overlaping first van period
        then two requests not reserved in overlapping periods
        last request ackowledged fosecond van and second period
    */
    @Test
    void reserve_vans_in_sequence_sequence_dates() {

        ZonedDateTime nextWeek = ZonedDateTime.now().plusWeeks(1);

        RentDetails details = reserveSystem.reserve(1, CarType.VAN, nextWeek, nextWeek.plusDays(1));
        assertEquals(true, details.isAdded());

        details = reserveSystem.reserve(2, CarType.VAN, nextWeek.plusDays(2), nextWeek.plusDays(3));
        assertEquals(true, details.isAdded());

        details = reserveSystem.reserve(3, CarType.VAN, nextWeek.plusDays(4), nextWeek.plusDays(5));
        assertEquals(true, details.isAdded());

        details = reserveSystem.reserve(4, CarType.VAN, nextWeek, nextWeek.plusDays(1));
        assertEquals(true, details.isAdded());

        details = reserveSystem.reserve(5, CarType.VAN, nextWeek, nextWeek.plusDays(1));
        assertEquals(true, details.isAdded());

        details = reserveSystem.reserve(5, CarType.VAN, nextWeek, nextWeek.plusDays(1));
        assertEquals(false, details.isAdded());

        details = reserveSystem.reserve(5, CarType.VAN, nextWeek, nextWeek.plusDays(1));
        assertEquals(false, details.isAdded());

        details = reserveSystem.reserve(5, CarType.VAN, nextWeek.plusDays(2), nextWeek.plusDays(3));
        assertEquals(true, details.isAdded());
    }

    /*
    20 parallel requests for the same period for vans
    only 3 vans are available so only 3 succeed

    then one subsequent call in overlapping date not rented
    then 3 calls in other dates rented
     */
    @Test
    void reserve_vans_parallel_request_for_same_date() throws InterruptedException, ExecutionException, ExecutionException {

        ZonedDateTime nextWeek = ZonedDateTime.now().plusWeeks(1);

        ExecutorService srv = Executors.newFixedThreadPool(4);

        int noOfRequests = 10;

        Future<RentDetails>[] arr = (Future<RentDetails>[]) new Future[noOfRequests];

        int noOfConfirmedReservations = 0;
        Set<Integer> carIds = new HashSet<>();

        for(int i = 0; i < noOfRequests; i++) {
            final Integer ii = i;
            Callable<RentDetails> call = () -> { return reserveSystem.reserve(ii, CarType.VAN, nextWeek, nextWeek.plusDays(1));     };
            arr[ii] = srv.submit(call);
        }


        for(int i = 0 ; i < noOfRequests; i++) {

            while(!arr[i].isDone()) {
                Thread.sleep(10);
            }

            if(arr[i].isDone()) {
                RentDetails rentDetails = arr[i].get();

                if(rentDetails.getCarId() > -1) {
                    noOfConfirmedReservations++;

                    assertEquals(nextWeek, rentDetails.getSt());
                    assertEquals(nextWeek.plusDays(1), rentDetails.getEnd());
                    carIds.add(rentDetails.getCarId());
                }
            }
        }

        assertEquals(3, noOfConfirmedReservations);
        assertEquals(3, carIds.size());

        RentDetails details = reserveSystem.reserve(1, CarType.VAN, nextWeek, nextWeek.plusDays(1));
        assertEquals(false, details.isAdded());

        details = reserveSystem.reserve(20, CarType.VAN, nextWeek.plusDays(2), nextWeek.plusDays(3));
        assertEquals(true, details.isAdded());
        carIds.add(details.getCarId());

        details = reserveSystem.reserve(21, CarType.VAN, nextWeek.plusDays(2), nextWeek.plusDays(3));
        assertEquals(true, details.isAdded());
        carIds.add(details.getCarId());

        details = reserveSystem.reserve(22, CarType.VAN, nextWeek.plusDays(2), nextWeek.plusDays(3));
        assertEquals(true, details.isAdded());
        carIds.add(details.getCarId());

        assertEquals( nextWeek.plusDays(2), details.getSt());
        assertEquals( nextWeek.plusDays(3), details.getEnd());

        assertEquals(3, carIds.size());
    }

    /*
    vans are requested
    3 vans available so last two for the covering dates fail
     */
    @Test
    void reserve_vans_in_sequence_1() {

        ZonedDateTime now = ZonedDateTime.now();

        Set<Integer> carIdsSet = new HashSet<>();

        RentDetails details = null;

        details = reserveSystem.reserve(1, CarType.VAN, now, now.plusDays(1));
        assertTrue(details.isAdded());
        assertEquals(now, details.getSt());
        assertEquals(now.plusDays(1), details.getEnd());
        assertEquals(details.getCarType(), CarType.VAN);
        int firstCarId = details.getCarId();
        carIdsSet.add(firstCarId);

        details = reserveSystem.reserve(2, CarType.VAN, now.plusHours(1), now.plusDays(1));
        assertTrue(details.isAdded());
        assertEquals(now.plusHours(1), details.getSt());
        assertEquals(now.plusDays(1), details.getEnd());
        assertEquals(details.getCarType(), CarType.VAN);
        int secondCarId = details.getCarId();
        carIdsSet.add(secondCarId);
        assertNotEquals(firstCarId, secondCarId);


        details = reserveSystem.reserve(3, CarType.VAN, now.plusHours(12), now.plusDays(4));


        assertTrue(details.isAdded());
        assertEquals(now.plusHours(12), details.getSt());
        assertEquals(now.plusDays(4), details.getEnd());
        assertEquals(details.getCarType(), CarType.VAN);
        int thirdCarId = details.getCarId();
        carIdsSet.add(thirdCarId);
        assertNotEquals(firstCarId, thirdCarId);

        assertEquals(3, carIdsSet.size());

        details = reserveSystem.reserve(4, CarType.VAN, now, now.plusDays(1));
        assertEquals(false, details.isAdded());

        details = reserveSystem.reserve(5, CarType.VAN, now, now.plusDays(1));
        assertEquals(false, details.isAdded());
    }

    /*
        3 vans available so last two for the covering dates fail
        also overlapping dates
    */
    @Test
    void reserve_vans_in_sequence_overlaping_dates() throws InterruptedException {

        ZonedDateTime nextWeek = ZonedDateTime.now().plusWeeks(1);

        RentDetails details = reserveSystem.reserve(1, CarType.VAN, nextWeek, nextWeek.plusDays(1));
        assertEquals(true, details.isAdded());

        details = reserveSystem.reserve(2, CarType.VAN, nextWeek.plusHours(1), nextWeek.plusDays(1));
        assertEquals(true, details.isAdded());

        details = reserveSystem.reserve(3, CarType.VAN, nextWeek.minusDays(1), nextWeek.plusDays(4));
        assertEquals(true, details.isAdded());

        details = reserveSystem.reserve(4, CarType.VAN, nextWeek, nextWeek.plusDays(1));
        assertEquals(false, details.isAdded());

        details = reserveSystem.reserve(5, CarType.VAN, nextWeek, nextWeek.plusDays(1));
        assertEquals(false, details.isAdded());
    }


    @Test
    void throw_illegal_argument() throws InterruptedException {

        ZonedDateTime nextWeek = ZonedDateTime.now().plusWeeks(1);

        RentDetails resp = reserveSystem.reserve(1, CarType.VAN, nextWeek, nextWeek.minusDays(1));

        assertEquals(-1,  resp.getCarId());

    }

    @Test
    void end_before_start() throws Throwable {

        ZonedDateTime nextWeek = ZonedDateTime.now().plusWeeks(1);

        RentDetails resp = reserveSystem.reserve(1, CarType.VAN, nextWeek, nextWeek.minusDays(1));

        assertEquals(-1,  resp.getCarId());

    }

      /*
        two reserve systems
        parallel requests to each
    */

    @Test
    void two_reserve_systems() throws InterruptedException, ExecutionException, ExecutionException {

        final ReserveSystem reserveSystem = new ReserveSystemImpl();

        final ReserveSystem reserveSystemOne = new ReserveSystemImpl();


        ZonedDateTime nextWeek = ZonedDateTime.now().plusWeeks(1);

        ExecutorService srv = Executors.newFixedThreadPool(4);

        int noOfRequests = 30;

        Future<RentDetails>[] arr = (Future<RentDetails>[]) new Future[noOfRequests];
        Future<RentDetails>[] arrOne = (Future<RentDetails>[]) new Future[noOfRequests];

        int noOfConfirmedReservations = 0;

        Callable<RentDetails> call;

        for (int i = 0, k = 0; i < 10; i++, k++) {

            Integer ii = i;

            call = () -> reserveSystemOne.reserve(ii, CarType.VAN, nextWeek, nextWeek.plusDays(1));
            arrOne[ii] = srv.submit(call);

            call = () -> reserveSystem.reserve(ii, CarType.VAN, nextWeek, nextWeek.plusDays(1));
            arr[ii] = srv.submit(call);


            Integer iii = ii + 10;
            call = () -> reserveSystem.reserve(iii, CarType.SEDAN, nextWeek, nextWeek.plusDays(1));
            arr[iii] = srv.submit(call);

            call = () -> reserveSystemOne.reserve(iii, CarType.SEDAN, nextWeek, nextWeek.plusDays(1));
            arrOne[iii] = srv.submit(call);

            Integer iiii = ii + 20;
            call = () -> reserveSystem.reserve(iiii, CarType.SUV, nextWeek, nextWeek.plusDays(1));
            arr[iiii] = srv.submit(call);

            call = () -> reserveSystemOne.reserve(iiii, CarType.SUV, nextWeek, nextWeek.plusDays(1));
            arrOne[iiii] = srv.submit(call);


        }


        int rentedVans = 0;
        int rentedSedans = 0;
        int rentedSuvs = 0;

        for (int i = 0; i < noOfRequests; i++) {

            while (!arr[i].isDone()) {
                Thread.sleep(10);
            }

            if (arr[i].isDone()) {
                RentDetails rentDetails = arr[i].get();

                if (rentDetails.getCarId() > -1) {
                    noOfConfirmedReservations++;

                    assertEquals(nextWeek, rentDetails.getSt());
                    assertEquals(nextWeek.plusDays(1), rentDetails.getEnd());

                    if (rentDetails.getCarType().equals(CarType.VAN)) {
                        rentedVans++;
                    }
                    if (rentDetails.getCarType().equals(CarType.SEDAN)) {
                        rentedSedans++;
                    }
                    if (rentDetails.getCarType().equals(CarType.SUV)) {
                        rentedSuvs++;
                    }
                }

            }
        }

        for (int i = 0; i < noOfRequests; i++) {

            while (!arrOne[i].isDone()) {
                Thread.sleep(10);
            }

            if (arrOne[i].isDone()) {
                RentDetails rentDetails = arrOne[i].get();

                if (rentDetails.getCarId() > -1) {
                    noOfConfirmedReservations++;

                    assertEquals(nextWeek, rentDetails.getSt());
                    assertEquals(nextWeek.plusDays(1), rentDetails.getEnd());

                    if (rentDetails.getCarType().equals(CarType.VAN)) {
                        rentedVans++;
                    }
                    if (rentDetails.getCarType().equals(CarType.SEDAN)) {
                        rentedSedans++;
                    }
                    if (rentDetails.getCarType().equals(CarType.SUV)) {
                        rentedSuvs++;
                    }
                }

            }
        }

        assertEquals(9, noOfConfirmedReservations);
        assertEquals(3, rentedVans);
        assertEquals(3, rentedSedans);
        assertEquals(3, rentedSuvs);


    }


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {

        Database databaseService = reserveSystem.getDatabaseService();
        databaseService.clearDatabse();
    }
}