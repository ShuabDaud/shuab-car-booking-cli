package com.shuab.user;

import java.util.Arrays;
import java.util.UUID;

public class UserService {
    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDao();
    }

    // get all users
    public User[] getUsers() {
        return userDao.getUsers();
    }

    // get a user by id
    public User getUserById(UUID id) {
        // 1. we handle if id is null
        if (id == null) throw new IllegalArgumentException("User id cannot be null");

        // 2. we also handle if the user with that id is not found
        User user = userDao.findUserById(id);
        if (user == null)
            throw new IllegalArgumentException("User not found");
        // finally we return only if the user is found

        return user;
    }
}
