package com.shuab.booking;

import java.util.UUID;

public interface CarBookingDao {

    CarBooking[] getBookings();

    CarBooking findBookingById(UUID id);

    void saveBooking(CarBooking booking);

    void deleteBooking(UUID id);
}
