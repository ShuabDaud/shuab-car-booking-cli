package com.shuab;

import java.io.IOException;

public class Main {
    static void main(String[] args) {
        try {
            CarBookingSystem bookingSystem = new CarBookingSystem();
            bookingSystem.run();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
