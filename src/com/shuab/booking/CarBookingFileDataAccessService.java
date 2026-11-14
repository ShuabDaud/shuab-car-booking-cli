package com.shuab.booking;

import java.io.*;
import java.util.Arrays;
import java.util.UUID;

public class CarBookingFileDataAccessService implements CarBookingDao {
    private final String filePath;
    private CarBooking[] bookings;
    private int count;

    public CarBookingFileDataAccessService(String filePath) {
        this.filePath = filePath;
        bookings = new CarBooking[10];
    }

    @Override
    public CarBooking[] getBookings() {
        return Arrays.copyOf(bookings, count);
    }

    @Override
    public CarBooking findBookingById(UUID id) {
        for (int i = 0; i < count; i++) {
            CarBooking booking = bookings[i];
            if (booking != null)
                if (booking.getId().equals(id))
                    return booking;
        }
        return null;
    }

    @Override
    public void saveBooking(CarBooking booking) {
        // read existing bookings from the file
        readCarBookingFromBinaryFile(); // not checked if the file exists or not.
        // add the new booking
        if (count == bookings.length)
            expandCapacity();
        bookings[count++] = booking;

        // write them all back
        saveCarBookingToBinaryFile();
    }


    private void expandCapacity() {
            bookings = Arrays.copyOf(
                    bookings,
                    bookings.length == 0
                            ? 1
                            : bookings.length * 2);
    }


    @Override
    public void deleteBooking(UUID id) {
        // read existing bookings from the file
        readCarBookingFromBinaryFile();
        // remove the one with the matching id
        for (int i = 0; i < count; i++) {
            if (bookings[i].getId().equals(id)) {
                bookings[i] = bookings[count - 1];
                bookings[count - 1] = null;
                count--;
                return;
            }
        }
        // write the rest back
        saveCarBookingToBinaryFile();
    }

    //. other methods

    // serialization
    private void saveCarBookingToBinaryFile() {
        if (getBookings().length == 0) {
            System.out.println("The data file is empty");
            return;
        }

        try (ObjectOutputStream output =
                new ObjectOutputStream(new FileOutputStream(filePath))) {

            output.writeObject(getBookings());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // deserialization
    private void readCarBookingFromBinaryFile() {
        try (ObjectInputStream input =
                new ObjectInputStream(new FileInputStream(filePath))) {

            bookings = (CarBooking[]) input.readObject();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}

