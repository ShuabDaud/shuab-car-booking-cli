package com.shuab.user;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.UUID;

public class UserFileDataAccessService implements UserDao {
    private final String filePath;
    private User[] users;
    private int count;

    public UserFileDataAccessService(String filePath) throws IOException {
        if (!filePath.endsWith(".dat"))
            throw new IllegalArgumentException("Invalid data file");

        this.filePath = filePath;
        this.users = new User[10];

        File dataFile = new File(filePath);
        if (dataFile.exists())
            readUsersFromBinaryFile();
        else {
            readAndPopulateUsersCSVFile();
            saveUsersToBinaryFile();
        }

    }

    @Override
    public User[] getUsers() {
        return Arrays.copyOf(users, count);
    }

    @Override
    public User findUserById(UUID id) {
        for (User user : getUsers()) {
            if (user != null) {
                if (user.getId().equals(id))
                    return user;
            }
        }
        return null;
    }

    private void readAndPopulateUsersCSVFile() throws IOException {
        String csvFilePath = "src/com/shuab/users.csv";
        File file = new File(csvFilePath);
        if (!file.exists()) {
            throw new FileNotFoundException("No such [%s] file exists%n".formatted(csvFilePath));
        }

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
                users[count++] = new User(UUID.fromString(data[0].trim()), data[1].trim());
            }
        }
    }

    // serialization
    private void saveUsersToBinaryFile() throws IOException {
        if (getUsers().length == 0) {
            System.out.println("The CSV file is empty");
            return;
        }

        try (ObjectOutputStream output =
                new ObjectOutputStream(new FileOutputStream(filePath))) {

            output.writeObject(getUsers());
        }
    }

    // deserialization
    private void readUsersFromBinaryFile() throws IOException {
        try (ObjectInputStream input =
                new ObjectInputStream(new FileInputStream(filePath))) {

            User[] loadedUsers =  (User[]) input.readObject();
            users = loadedUsers;
            count = loadedUsers.length;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to restore User objects ", e);
        }
    }


}

class Test {
    static void main() {
        try {
            UserFileDataAccessService service = new UserFileDataAccessService(
                    "src/com/shuab/users.dat"
                    );
            for (User user : service.getUsers()) {
                System.out.println(user);
            }
        } catch (IOException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
