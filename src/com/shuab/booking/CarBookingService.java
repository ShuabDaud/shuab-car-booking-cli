package com.shuab.booking;

import com.shuab.car.Car;
import com.shuab.car.CarService;
import com.shuab.user.User;
import com.shuab.user.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.UUID;

public class CarBookingService {
    private final CarBookingDao carBookingDao;
    private final UserService userService;
    private final CarService carService;

    public CarBookingService() {
        this.carBookingDao = new CarBookingDao();
        this.userService = new UserService();
        this.carService = new CarService();

    }

    public CarBooking[] getBookings() {
        return carBookingDao.getBookings();
    }

    public CarBooking findBookingById(UUID id) {
        if (id == null)
            throw new IllegalArgumentException("Booking id can't be null");

        CarBooking booking = carBookingDao.findBookingById(id);
        if (booking == null)
            throw new IllegalArgumentException("There is no such booking🙅‍♂️");

        return booking;
    }

    // return all cars booked by a desired user
    public Car[] getUserBookedCars(UUID id) {
        // check whether the user exists
        User user = userService.getUserById(id);
        if (user == null) throw new IllegalArgumentException("User not found");

        CarBooking[] bookings = getBookings();

        Car[] bookedCars = new Car[bookings.length];
        int counter = 0;

        for (CarBooking booking : bookings) {
            if (booking == null) continue;
            User bookingUser = booking.getUser();

            if (bookingUser != null &&
                booking.getStatus().equals(BookingStatus.ACTIVE) &&
                bookingUser.getId().equals(id)) {

                bookedCars[counter++] = booking.getCar();
            }
        }

        // finally we return only the cars that we found for that particular user
        return Arrays.copyOf(bookedCars, counter);
    }

    // book car
    public CarBooking bookCar(UUID userId, UUID carId,
                              LocalDate startDate, LocalDate endDate) {
        User user = userService.getUserById(userId);

        // if this is not an exist user, then we reject this booking
        if (user == null) throw new IllegalArgumentException("User not found");

        Car car = carService.getCarById(carId);
        if (car == null) throw new IllegalArgumentException("Car not found");

        if (startDate == null || endDate == null)
            throw new IllegalArgumentException("Both start date and end date are required");

        validateStartDate(startDate);
        validateEndDate(startDate, endDate);
        checkIfTheCarWasBooked(carId);

        // calc the price
//        int days = startDate.isEqual(endDate)
//                ? 1 // duration of the rental will 1 day if these date are equal
//                : startDate.until(endDate).getDays();

        long days = startDate.equals(endDate)
                ? 1
                : ChronoUnit.DAYS.between(startDate, endDate);

        BigDecimal price = car.getRentalPricePerDay()
                .multiply(BigDecimal.valueOf(days));

        // we create the booking now.
        CarBooking booking = new CarBooking(
                UUID.randomUUID(),
                user,
                car,
                startDate,
                endDate,
                price,
                BookingStatus.ACTIVE,
                LocalDateTime.now()
        );

        carBookingDao.saveBooking(booking);
//        System.out.println("Saved!");

        return booking;
    }

    public void checkIfTheCarWasBooked(UUID carId) {
        for (CarBooking booking : getBookings()) {
            if (booking.getStatus().equals(BookingStatus.ACTIVE) &&
                booking.getCar().getId().equals(carId))
                throw new IllegalArgumentException("This car has already been booked");
        }
    }

    public void validateStartDate(LocalDate startDate) {
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Start date can't be in the past."
            );
        }
    }

    public void validateEndDate(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date must be after this date 👉 " + startDate
            );
        }
    }

    public void deleteBooking(UUID id) {
        // we find it first,
        findBookingById(id);

        // then if exists, we delete it
        carBookingDao.deleteBooking(id);
    }
}

