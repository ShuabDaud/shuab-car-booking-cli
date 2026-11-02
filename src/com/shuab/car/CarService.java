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

    public Car[] getAvailableCars(CarBooking[] bookings) {
        Car[] cars = getCars();
        CarBooking[] carBookings = bookings == null
                ? new CarBooking[0]
                : bookings;
        Car[] availableCars = new Car[cars.length];
        int counter = 0;

        for (Car car : cars) {
            boolean booked = false;

            for (CarBooking booking : carBookings) {
                if (booking == null)
                    continue;

                if (booking.getStatus().equals(BookingStatus.ACTIVE) &&
                        booking.getCar().getId().equals(car.getId())) {
                    booked = true; // mark this car as booked and no longer available
                    break;
                }
            }
            if (!booked) {
                availableCars[counter++] = car;
            }
        }

        // return the available cars list.
        return Arrays.copyOf(availableCars, counter); // to not display unused slots
    }

    public Car[] getAvailableElectricCars(CarBooking[] bookings) {
        Car[] availableCars = getAvailableCars(bookings);
        Car[] electricCars = new Car[availableCars.length];
        int counter = 0;

        for (Car car : availableCars) {
            if (car.isElectric())
                electricCars[counter++] = car;
        }

        return Arrays.copyOf(electricCars, counter);
    }
}