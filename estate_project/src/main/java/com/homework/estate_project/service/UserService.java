package com.homework.estate_project.service;

import com.homework.estate_project.entity.User;
import com.homework.estate_project.exception.InvalidEntityDataException;
import com.homework.estate_project.exception.NonExistingEntityException;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(Long id) throws NonExistingEntityException;

    User create(User user) throws InvalidEntityDataException;

    User update(User user) throws NonExistingEntityException, InvalidEntityDataException;

    User getUserByUsername(String username) throws NonExistingEntityException;
    User deleteUserById(Long id) throws NonExistingEntityException;

    long getUsersCount();
}
