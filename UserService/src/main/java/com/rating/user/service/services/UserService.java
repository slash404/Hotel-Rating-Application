package com.rating.user.service.services;

import com.rating.user.service.entities.User;

import java.util.List;

public interface UserService {
    //create
    User saveUser(User user);
    //get all users
    List<User> getAllUsers();
    //get user by user id
    User getUser(String userId);
}
