package com.shuab.car;

import java.io.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

public class CarFileDataAccessService implements CarDao {
    private String dataFilePath;
    private String csvFilePath;
    private int count;
    private Car[] cars;

    public CarFileDataAccessService(String csvFilePath,
                                    String dataFilePath) throws IOException {
        if (!csvFilePath.endsWith(".csv"))
            throw new IllegalArgumentException("Unsupported! Please use [.csv] file format");
        if (!dataFilePath.endsWith(".dat"))
            throw new IllegalArgumentException("Unsupported! Please use [.dat] file format");

        this.dataFilePath = dataFilePath;
        cars = new Car[10];

        if (new File(dataFilePath).exists())
            readCarsFromBinaryFile(); // ObjectInputStream
        else {
            readAndPopulateCarsFromCSVFile();
            saveCarsToBinaryFile(); // ObjectOutputStream
        }
    }


    @Override
    public Car[] getCars() {
        return Arrays.copyOf(cars, count);
    }

    @Override
    public Car findCarById(UUID id) {
        for (Car car : getCars()) {
            if (car != null)
                if (car.getId().equals(id))
                    return car;
        }

        return null;
    }

    private void readAndPopulateCarsFromCSVFile() throws IOException {
        File file = new File(csvFilePath);
        if (!file.exists())
            throw new FileNotFoundException("No such [%s] file exists%n".formatted(csvFilePath));

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(csvFilePath))) {

            String header = reader.readLine();
            if (header == null) {
                System.out.println("Empty file");
                return;
            }

            String line;
            int column = header.split(",").length;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < column) continue;
                cars[count++] = new Car(
                        UUID.fromString(data[0].trim()),
                        data[1].trim(),
                        new BigDecimal(data[2].trim()),
                        Brand.valueOf(data[3].trim()),
                        Boolean.parseBoolean(data[4].trim())
                );
            }
        }
    }

    // serialization
    private void saveCarsToBinaryFile() throws IOException {
        if (getCars().length == 0) {
            System.out.println("The file is empty");
            return;
        }

        try (ObjectOutputStream output =
                     new ObjectOutputStream(new FileOutputStream(dataFilePath))) {
            output.writeObject(getCars());
        }
    }

    // deserialization
    private void readCarsFromBinaryFile() throws IOException {
        try (ObjectInputStream input =
                     new ObjectInputStream(new FileInputStream(dataFilePath))) {

            cars = (Car[]) input.readObject();
            count = cars.length;

        } catch (ClassNotFoundException e) {
            throw new IOException("Cannot restore cars from the specified path: %s"
                    .formatted(dataFilePath));
        }
    }
}
