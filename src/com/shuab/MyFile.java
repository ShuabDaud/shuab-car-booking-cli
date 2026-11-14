package com.shuab;

import com.shuab.user.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class MyFile {
    static User[] users = new User[10];
    private int count;

    void main() {
        String filePath = "src/com/shuab/users.csv";

        try {
            BufferedReader reader =
                    new BufferedReader(new FileReader(filePath));
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                users[i] = new User(
                        UUID.fromString(data[0]),
                        data[1]);
                count++;
                i++;
            }
            for (User user : getUsers()) {
                System.out.println(user);
            }
            } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    User[] getUsers() {
        return Arrays.copyOf(users, count);
    }
}
