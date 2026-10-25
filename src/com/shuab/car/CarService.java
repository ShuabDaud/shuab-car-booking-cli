package com.shuab.car;

import com.shuab.booking.BookingStatus;
import com.shuab.booking.CarBooking;

import java.util.Arrays;
import java.util.UUID;

public class CarService {
    private final CarDao carDao;


    public CarService() {
        this.carDao = new CarDao();
    }

    // get all cars
    public Car[] getCars() {
        return carDao.getCars();
    }

    // get a car by its id
    public Car getCarById(UUID id) {
        if (id == null)
            throw new IllegalArgumentException("Car id cannot be null");

        Car car = carDao.findCarById(id);
        if (car == null)
            throw new IllegalArgumentException("Car with [%s] is not found.".formatted(id));

        return car;
    }

    // get all available cars
    public Car[] getAvailableCars(CarBooking[] bookings) {
        // obtain all cars
        Car[] cars = getCars();
        // obtain current bookings
        CarBooking[] carBookings = bookings == null
                ? new CarBooking[0]
                : bookings;
        // create an empty array for the available cars
        Car[] availableCars = new Car[cars.length];
        int counter = 0;

        // for each car,
        for (Car car : cars) {
            boolean booked = false;

            // we will check every booking
            // TODO: if booking is not null, then we can loop... I will try this later.
            for (CarBooking booking : carBookings) {
                // skip any null slot for the bookings array
                if (booking == null)
                    continue;

                if (booking.getStatus().equals(BookingStatus.ACTIVE) &&
                        booking.getCar().getId().equals(car.getId())) {
                    booked = true; // mark this car as booked and no longer available
                    break;
                }
            }
            // after inner loop breaks, continue the body of the outer loop
            if (!booked) {
                availableCars[counter++] = car;
            }
        }

        // return the available cars list.
        return Arrays.copyOf(availableCars, counter); // to not display unused slots
    }

    // get available electric cars
    public Car[] getAvailableElectricCars(CarBooking[] bookings) {
        Car[] availableCars = getAvailableCars(bookings);
        Car[] electricCars = new Car[availableCars.length];
        int counter = 0;

        // for each available car, if it's electric, add to the array, and increment the counter.
        for (Car car : availableCars) {
            if (car.isElectric())
                electricCars[counter++] = car;
        }

        return Arrays.copyOf(electricCars, counter);
    }
}

/*
class Test3 {
    static void main() {
        CarService service = new CarService();

        System.out.println(Arrays.toString(service.getCars()));

        try {
            System.out.println(service.getCarById(
                    null));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
*/
