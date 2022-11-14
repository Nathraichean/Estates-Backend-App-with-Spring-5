package com.homework.estate_project.service.impl;

import com.homework.estate_project.dao.AgencyRepository;
import com.homework.estate_project.dao.ImageDataRepository;
import com.homework.estate_project.dao.UserRepository;
import com.homework.estate_project.entity.Agency;
import com.homework.estate_project.entity.User;
import com.homework.estate_project.entity.enums.UserRole;
import com.homework.estate_project.exception.DeleteConflictException;
import com.homework.estate_project.exception.InvalidEntityDataException;
import com.homework.estate_project.exception.NonExistingEntityException;
import com.homework.estate_project.service.ImageDataService;
import com.homework.estate_project.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service @RequiredArgsConstructor @Slf4j
public class UserServiceImpl implements UserService {
    private UserRepository userRepo;
    private AgencyRepository agencyRepo;
    private ImageDataRepository imageDataRepository;

    @Autowired
    public UserServiceImpl(UserRepository myUserRepo,
                           AgencyRepository agencyRepo,
                           ImageDataRepository imageDataRepository) {
        this.userRepo = myUserRepo;
        this.agencyRepo = agencyRepo;
        this.imageDataRepository = imageDataRepository;
    }

    @Override
    public List<User> getAllUsers() {

        List<User> users = userRepo.findAll();
        users.forEach(user -> {user.setPassword("*".repeat(user.getPassword().length()));});

        return users;
    }

    @Override
    public User getUserById(Long id) throws NonExistingEntityException {

        User user = userRepo.findById(id).orElseThrow(() -> new NonExistingEntityException(
                String.format("User with ID='%d' does not exist", id)
        ));
        return user;
    }

    @Override
    public User create(User user) throws InvalidEntityDataException {
        user.setId(null);


        if(userRepo.findByUsername(user.getUsername()).isPresent()) {
            throw new InvalidEntityDataException(
                    String.format("User with username '%s' already exists", user.getUsername()));
        }

        user.setAvatar(imageDataRepository.findById(1L).get());
        var now = LocalDateTime.now();
        user.setCreated(now);
        user.setModified(now);
        var encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        User savedUser = userRepo.save(user);
        return savedUser;
    }

    @Override
    public User update(User user) throws NonExistingEntityException, InvalidEntityDataException {
        var old = getUserById(user.getId());

        if (!old.getUsername().equals(user.getUsername())) {
            if (userRepo.findByUsername(user.getUsername()).isPresent()){
                throw new InvalidEntityDataException(
                        String.format("Username can not be changed from '%s' to '%s. Username already taken.'",
                                old.getUsername(), user.getUsername()));
            }
        }

        if (!old.getUserRole().equals(UserRole.ADMIN)){
            if (!old.getUserRole().equals(user.getUserRole())) {
                throw new InvalidEntityDataException(
                        String.format("You cannot change your role from %s to %s.'",
                                old.getUserRole(), user.getUserRole()));
            }
        }

        user.setCreated(old.getCreated());
        user.setModified(LocalDateTime.now());
        var encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        User savedUser = userRepo.save(user);
        return savedUser;
    }

    @Override
    public User getUserByUsername(String username) throws NonExistingEntityException {
        return userRepo.findByUsername(username).orElseThrow(() -> new NonExistingEntityException(
                String.format("User with username = '%s' does not exist", username)
        ));
    }

    @Override
    public User deleteUserById(Long id) throws NonExistingEntityException {

        // Saves user to be deleted
        User old = getUserById(id);
        //log.info(agencyRepo.findAgencyByOwnerEquals(old).toString());

        // Checks if user owns an agency. If yes, sets agency's owner to a default one (admin/id:1).
        if (agencyRepo.findAgencyByOwnerEquals(old) != null){
            throw new DeleteConflictException(String.format(
                    "Unable to delete user with ID: %d. The user owns the agency '%s'. Transfer agency ownership before deleting user.",id,agencyRepo.findAgencyByOwnerEquals(old).getName()));
        }
        else {
            userRepo.deleteById(id);
            return old;
        }

    }

    @Override
    public long getUsersCount() {
        return userRepo.count();
    }
}
