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

        // get the existing booking
        CarBooking[] bookings = getBookings();

        // the user cannot have more booked cars than we have in the booking list
        Car[] bookedCars = new Car[bookings.length];
        int counter = 0;

        // we examine every booking in the bookings array
        for (CarBooking booking : bookings) {
            // we skip every empty cell which holds null.
            if (booking == null) continue;
            // if that is not that case, we try to extract the user from the booking.
            User bookingUser = booking.getUser();

            // then we check if the booking belongs to that extracted user
            if (bookingUser != null &&
                booking.getStatus().equals(BookingStatus.ACTIVE) &&
                bookingUser.getId().equals(id)) {

                // then we extract that car from the booking and add to the array
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

        long days = ChronoUnit.DAYS.between(startDate, endDate);

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

/*class Testing {
    static CarBookingService dao = new CarBookingService();
    static UserService daoUser = new UserService();
    static CarService daoCar = new CarService();

    static void main() {

        displayBooking(dao);

        saveBooking(dao);


        displayBooking(dao);


        find(UUID.fromString("009394dc-b88e-4e17-8e91-4e8f755c7e3a"));

        delete(UUID.fromString("009394dc-b88e-4e17-8e91-4e8f755c7e3a"));
        delete(UUID.fromString("111194dc-b88e-4e17-8e91-4e8f755c4444"));
        delete(UUID.fromString("111194dc-b88e-4e17-8e91-4e8f755c4444"));

//        find(UUID.fromString("b7375ddf-9434-4062-90bf-ccefb3470e48"));

    }

    private static void find(UUID id) {
        try {
            System.out.println(dao.findBookingById(id) + "\n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println();
        }
    }

    private static void delete(UUID id) {
        try {
            dao.deleteBooking(id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println();
        }
    }

    private static void displayBooking(CarBookingService dao) {
        System.out.println("**********************************");
        System.out.println("Count: " + dao.getCounter() + '\n' +
                           "Array: " + Arrays.toString(dao.getBookings()));
        System.out.println("**********************************\n");
    }

    private static void saveBooking(CarBookingService dao) {
        try {
            dao.bookCar(
                    UUID.fromString("009394dc-b88e-4e17-8e91-4e8f755c7e3a"),
                    UUID.fromString("8ca51d2b-aaaf-4bf2-834a-e02964e10fc3"),
                    UUID.fromString("8ca51b2b-aaaf-4bc2-834a-e02964e10cc3"),
                    LocalDate.now(),
                    LocalDate.now()
            );
            dao.bookCar(
                    UUID.fromString("111194dc-b88e-4e17-8e91-4e8f755c4444"),
                    UUID.fromString("b10d126a-3608-4980-9f9c-aa179f5cebc3"),
                    UUID.fromString("74bc9588-780d-4877-ae7e-a7e45bb33fde"),
                    LocalDate.now(),
                    LocalDate.now()
            );
            dao.bookCar(
                    UUID.fromString("111194dc-b88e-4e17-8e91-4e8f755c4444"),
                    UUID.fromString("b10d126a-3608-4980-9f9c-aa179f5cebc3"),
                    UUID.fromString("74bc9588-780d-4877-ae7e-a7e45bb33fde"),
                    LocalDate.now(),
                    LocalDate.now()
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println();
        }
    }
}*/

