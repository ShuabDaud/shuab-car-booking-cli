package com.shuab.car;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

public class CarDao {
    private static final Car[] CARS;

    static {
        CARS = new Car[]{
                new Car(UUID.fromString("8ca51b2b-aaaf-4bc2-834a-e02964e10cc3"),
                        "1234",
                        new BigDecimal("89"),
                        Brand.TESLA,
                        true),
                new Car(UUID.fromString("74bc9588-780d-4877-ae7e-a7e45bb33fde"),
                        "5678",
                        new BigDecimal("50"),
                        Brand.AUDI,
                        false),
                new Car(UUID.fromString("65b2d699-a7b6-4e56-a972-38de6d0c94c4"),
                        "9101",
                        new BigDecimal("67"),
                        Brand.TOYOTA,
                        false),
                new Car(UUID.fromString("65b2d699-a7b6-4e56-a972-38de6d0b94c5"),
                        "1299",
                        new BigDecimal("88"),
                        Brand.BMW,
                        true),
        };
    }

    // find all cars
    public Car[] getCars() {
        return Arrays.copyOf(CARS, CARS.length); // no null slots to be displayed
    }

    // find a car by its id
    public Car findCarById(UUID id) {
        // if we match the id, we return
        for (Car car : getCars()) {
            if (car != null)
                if (car.getId().equals(id))
                    return car;
        }
        // otherwise, we return null
        return null;
    }
}
