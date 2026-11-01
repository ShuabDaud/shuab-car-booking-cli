
package com.shuab;

import com.shuab.booking.CarBooking;
import com.shuab.booking.CarBookingService;
import com.shuab.car.Car;
import com.shuab.car.CarService;
import com.shuab.user.User;
import com.shuab.user.UserService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.UUID;

public class CarBookingSystem {
    // let us use the Scanner as field of the class, to apply for the utility methods later.
    private final Scanner scanner = new Scanner(System.in);
    private final CarBookingService carBookingService = new CarBookingService();
    private final CarService carService = new CarService();
    private final UserService userService = new UserService();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public void run() {
        boolean keepRunning = true;

        while (keepRunning) {
            // show all menu options
            displayMenu();
            int option = getOption();

            try {
                switch (option) {
                    case 1 -> book();
                    case 2 -> delete();
                    case 3 -> viewUserBookedCars();
                    case 4 -> viewBookings();
                    case 5 -> view(carService.getAvailableCars(carBookingService.getBookings()),
                            "There are no cars available");
                    case 6 -> view(carService.getAvailableElectricCars(carBookingService.getBookings()),
                            "No electric cars are available");
                    case 7 -> viewAllUsers();
                    case 8 -> {
                        System.out.println("Good bye!👋");
                        keepRunning = false;
                        scanner.close();
                    }
                    default -> System.out.println("Invalid choice😡 Please select from the below menu👇\n");
                }
            } catch (Exception e) {
                displayError(e.getMessage());
            }

        }
    }

    private void displayError(String message) {
        System.out.println(message);
        System.out.println();
    }

    private void viewBookings() {
        view(carBookingService.getBookings(),
                "There is no booking available");
    }

    private void viewAllUsers() {
        view(userService.getUsers(), "No users are registered");
    }

    private int getOption() {
        int option = -1;
        boolean valid = false;

        do {
            String input = scanner.nextLine().trim();

            try {
                option = Integer.parseInt(input);
                valid = true;
            } catch (NumberFormatException e) {
                displayError("Invalid choice😡 Please select from the above menu☝️");
                System.out.print("Please select from the menu: ");
            }
        } while (!valid);

        return option;
    }

    private void displayMenu() {
        // main menu
        System.out.println("1️⃣ - Book Car");
        System.out.println("2️⃣ - Delete Booking");
        System.out.println("3️⃣ - View All User Booked Cars");
        System.out.println("4️⃣ - View All Bookings");
        System.out.println("5️⃣ - View Available Cars");
        System.out.println("6️⃣ - View Available Electric Cars");
        System.out.println("7️⃣ - View All Users");
        System.out.println("8️⃣ - Exit");

        System.out.print("Please select from the menu: ");
    }

    private boolean view(Object[] items, String message) {
        if (items == null || items.length == 0) {
            System.out.println(message);
            System.out.println();
            return false;
        }

        for (Object item : items) {
            System.out.println(item);
        }
        System.out.println();
        return true;
    }

    private void delete() {
        if (!view(carBookingService.getBookings(),
                "No bookings are available to be deleted")) return;

        boolean deleted = false;

        do {
            UUID id = askId("Please select a booking ID: ");

            try {
                carBookingService.deleteBooking(id);
                System.out.println("Deletion successful!");
                System.out.println();
                deleted = true;

            } catch (IllegalArgumentException e) {
                displayError(e.getMessage());
            }
        } while (!deleted);
    }

    private void viewUserBookedCars() {
        if (!view(carBookingService.getBookings(), "No bookings are available")
            || !view(userService.getUsers(), "No users available")) return;

        UUID id = askUserId("➡️ select user id: ");
        User user = userService.getUserById(id);
        Car[] userBookedCars = carBookingService.getUserBookedCars(id);

        view(userBookedCars, "🙅‍♂️No cars are booked by %s. \n".formatted(user.getName()));
    }

    private void book() {
        UUID carId = askAvailableCarId();

        if (carId == null || !view(userService.getUsers(), "No users are registered")) return;

        UUID userId = askUserId("➡️ enter user id: ");
        LocalDate startDate = askStartDate();
        LocalDate endDate = askEndDate(startDate);

        CarBooking booking = carBookingService.bookCar(userId, carId, startDate, endDate);
        System.out.println(displayBookingDetails(booking));
    }

    private UUID askAvailableCarId() {
        UUID carId;
        boolean valid;

        do {
            CarBooking[] bookings = carBookingService.getBookings();
            Car[] cars = carService.getAvailableCars(bookings);

            if (!view(cars, "No cars are available for booking")) return null;

            carId = askId("➡️ enter car id: ");

            try {
                carService.getCarById(carId);
                carBookingService.checkIfTheCarWasBooked(carId);
                valid = true;
            } catch (IllegalArgumentException | IllegalStateException e) {
                displayError(e.getMessage());
                valid = false;
            }

        } while (!valid);

        return carId;
    }

    private UUID askUserId(String message) {
        UUID id;
        boolean valid;

        do {
            id = askId(message);

            try {
                userService.getUserById(id);
                valid = true;
            } catch (IllegalArgumentException e) {
                displayError(e.getMessage());
                valid = false;
            }

        } while (!valid);

        return id;
    }

    private LocalDate askStartDate() {
        LocalDate startDate;
        boolean valid;

        do {
            startDate = askDate("enter start date (day-month-year): ");

            try {
                carBookingService.validateStartDate(startDate);
                valid = true;
            } catch (IllegalArgumentException e) {
                displayError(e.getMessage());
                valid = false;
            }

        } while (!valid);

        return startDate;
    }

    private LocalDate askEndDate(LocalDate startDate) {
        LocalDate endDate;
        boolean valid;

        do {
            endDate = askDate("enter end date (day-month-year): ");

            try {
                carBookingService.validateEndDate(startDate, endDate);
                valid = true;
            } catch (IllegalArgumentException e) {
                displayError(e.getMessage());
                valid = false;
            }

        } while (!valid);

        return endDate;
    }

    private String displayBookingDetails(CarBooking booking) {
        return booking.getUser().getName() +
               "🎇 successfully booked a car, below is the booking details: " +
               "\n\tRef Id: " + booking.getId() +
               "\n\tBooking Cost: " + booking.getPrice() +
               "\n\tUser info: " + booking.getUser() +
               "\n\tCar info: " + booking.getCar() + '\n';
    }

    private LocalDate askDate(String message) {
        LocalDate date = null;
        boolean valid = false;

        do {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            try {
                date = LocalDate.parse(input, FORMATTER);
                valid = true;
            } catch (DateTimeParseException e) {
                displayError("Please enter a valid date i.e. [23-11-2026]");
            }

        } while (!valid);

        return date;
    }

    private UUID askId(String message) {
        UUID id = null;
        boolean valid = false;

        do {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            try {
                id = UUID.fromString(input);
                valid = true;
            } catch (IllegalArgumentException e) {
                displayError("Please enter a valid ID👎");
            }

        } while (!valid);

        return id;
    }
}
