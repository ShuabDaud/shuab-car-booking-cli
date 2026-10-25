package com.shuab.booking;

import java.util.Arrays;
import java.util.UUID;

public class CarBookingDao {
    private CarBooking[] bookings;
    private int counter;
    private static final int INITIAL_SIZE = 5;

    public CarBookingDao() {
        this(INITIAL_SIZE);
    }

    public CarBookingDao(int initialSize) {
        bookings = new CarBooking[initialSize];
    }

    public CarBooking[] getBookings() {
        return Arrays.copyOf(bookings, counter);
    }

    public int getCounter() {
        return counter;
    }

    // find booking its id
    public CarBooking findBookingById(UUID id) {
        for (int i = 0; i < counter; i++) {
            CarBooking booking = bookings[i];
            if (booking != null)
                if (booking.getId().equals(id))
                    return booking;
        }

        return null;
    }

    // save booking
    public void saveBooking(CarBooking booking) {
        // the counter is below the array size, we add the booking.
        if (counter == bookings.length) {
            // if so, we can create a longer array,
            // copy the items to the newer one.
            expandArrayCapacity();
        }

        bookings[counter++] = booking;
    }

    // we won't annoy the user to tell 'no booking space available'!
    private void expandArrayCapacity() {
        bookings = Arrays.copyOf(
                bookings,
                bookings.length == 0
                        ? 1
                        : bookings.length * 2);
    }

    // delete booking
    public void deleteBooking(UUID id) {
        for (int i = 0; i < counter; i++) {
            if (bookings[i].getId().equals(id)) {
                bookings[i] = bookings[counter - 1];
                bookings[counter - 1] = null;
                counter--;
                return;
            }
        }
    }
}

