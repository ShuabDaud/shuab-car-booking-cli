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

public class Main {
    // let us use the Scanner as field of the class, to apply for the utility methods later.
    private static final Scanner scanner = new Scanner(System.in);
    private static final CarBookingService carBookingService = new CarBookingService();
    private static final CarService carService = new CarService();
    private static final UserService userService = new UserService();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    static void main(String[] args) {
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
                    case 4 -> viewBookings(); //viewBookings().length == 0 ? "No Booking is available\n" :
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
                System.out.println(e.getMessage());
                System.out.println();
            }

        }
    }

    private static void viewBookings() {
        view(carBookingService.getBookings(),
                "There is no booking available");
    }

    private static void viewAllUsers() {
        view(userService.getUsers(), "No users are registered");
    }

    private static int getOption() {
        String input = scanner.nextLine().trim();

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void displayMenu() {
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

    private static boolean view(Object[] items, String message) {
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

    private static void delete() {
        if (!view(carBookingService.getBookings(),
                "No bookings are available to be deleted")) return;

        UUID id = askId("Please select a booking ID: ");

        carBookingService.deleteBooking(id);
        System.out.println("Deletion successful!");
        System.out.println();
    }

    private static void viewUserBookedCars() {
        if (!view(carBookingService.getBookings(),
                "No bookings are available")) return;

        view(userService.getUsers(), "No users available");
        UUID id = askId("➡️ select user id: ");
        User user = userService.getUserById(id);
        Car[] userBookedCars = carBookingService.getUserBookedCars(id);

        view(userBookedCars, "🙅‍♂️No cars are booked by %s. \n".formatted(user.getName()));

    }

    private static void book() {
        CarBooking[] bookings = carBookingService.getBookings();
        Car[] cars = carService.getAvailableCars(bookings);
        if (!view(cars, "No cars are available for booking")) return;

        UUID carId = askId("➡️ enter car id: ");

        // TODO: check if the car is already been booked before continuing further...
        carBookingService.checkIfTheCarWasBooked(carId);

        viewAllUsers();
        UUID userId = askId("➡️ enter user id: ");
        LocalDate startDate = askDate("enter start date (day-month-year): ");
        LocalDate endDate = askDate("enter end date (day-month-year): ");


        CarBooking booking = carBookingService.bookCar(userId, carId, startDate, endDate);
        System.out.println(displayBookingDetails(booking));


    }

    private static String displayBookingDetails(CarBooking booking) {
        return booking.getUser().getName() +
               "🎇 successfully booked a car, below is the booking details: " +
               "\n\tRef Id: " + booking.getId() +
               "\n\tUser info: " + booking.getUser() +
               "\n\tCar info: " + booking.getCar() + '\n';
    }

    private static LocalDate askDate(String message) {
        System.out.print(message);
        String input = scanner.nextLine().trim();

        try {
            return LocalDate.parse(input, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Please enter a valid date format👎");
        }
    }

    private static UUID askId(String message) {
        System.out.print(message);
        String input = scanner.nextLine().trim();

        try {
            return UUID.fromString(input);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Please enter a valid ID👎");
        }
    }
}
