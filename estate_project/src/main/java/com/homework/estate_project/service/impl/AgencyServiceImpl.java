package com.homework.estate_project.service.impl;

import com.homework.estate_project.dao.AgencyRepository;
import com.homework.estate_project.dao.ImageDataRepository;
import com.homework.estate_project.dao.UserRepository;
import com.homework.estate_project.entity.Agency;
import com.homework.estate_project.entity.User;
import com.homework.estate_project.exception.InvalidEntityDataException;
import com.homework.estate_project.exception.NonExistingEntityException;
import com.homework.estate_project.exception.UnautorizedException;
import com.homework.estate_project.service.AgencyService;
import com.homework.estate_project.service.ImageDataService;
import com.homework.estate_project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.transaction.TransactionDefinition.ISOLATION_REPEATABLE_READ;

@Service
@Transactional
@Slf4j
public class AgencyServiceImpl implements AgencyService {

    private AgencyRepository agencyRepo;
    private final PlatformTransactionManager transactionManager;

    private final UserService userService;
    private final UserRepository userRepo;
    private final ImageDataRepository imageDataRepository;


    @Autowired
    public AgencyServiceImpl(AgencyRepository agencyRepo,
                             PlatformTransactionManager manager,
                             UserService userService,
                             UserRepository userRepo,
                             ImageDataRepository imageDataRepository) {
        this.agencyRepo = agencyRepo;
        this.transactionManager = manager;
        this.userService = userService;
        this.userRepo = userRepo;
        this.imageDataRepository = imageDataRepository;
    }

    @Override
    public List<Agency> getAllAgencies() {
        List<Agency> agencies = agencyRepo.findAll();
        return agencies;
    }

    @Override
    public Agency getAgencyById(Long id) throws NonExistingEntityException {

        Agency agency = agencyRepo.findById(id).orElseThrow(() -> new NonExistingEntityException(
                String.format("Agency with ID='%d' does not exist", id)
        ));
        return agency;
    }

    @Override
    public Agency createAgency(Agency agency) throws InvalidEntityDataException {
        agency.setId(null);

        if (agency.getOwner() == null) {
            var loggedUser = getLoggedUser();
            if (loggedUser != null) {
                agency.setOwner(loggedUser);
            }
        }
        if (agency.getOwner() == null) {
            //throw new InvalidEntityDataException("You cannot create an agency without being signed in.");
            agency.setOwner(userService.getUserById(1L));
        }

        if(agencyRepo.findByName(agency.getName()).isPresent()) {
            throw new InvalidEntityDataException(
                    String.format("Agency with name '%s' already exists", agency.getName()));
        }

        agency.setOwner(userService.getUserByUsername(agency.getOwner().getUsername()));
        agency.setOwnerId(userService.getUserByUsername(agency.getOwner().getUsername()).getId());
        agency.setAvatar(imageDataRepository.findById(1L).get());

//        Long ownerId;
//        if (agency.getOwner() != null && agency.getOwner().getId() != null) {
//            ownerId = agency.getOwner().getId();
//        } else {
//            ownerId = agency.getOwnerIdd();
//        }
//        if (ownerId != null) {
//            User owner = userRepo.findById(ownerId)
//                    .orElseThrow(() -> new InvalidEntityDataException("User with ID:" + ownerId + " does not exist."));
//            agency.setOwner(owner);
//        }

//        if (agency.getOwner() == null){
//            agency.setOwner(userRepo.findById(agency.getOwnerId()).get());
//        }

        var now = LocalDateTime.now();
        agency.setCreated(now);
        agency.setModified(now);
        return agencyRepo.save(agency);
    }

    @Override
    public Agency updateAgency(Agency agency) throws NonExistingEntityException, InvalidEntityDataException {
        var old = getAgencyById(agency.getId());

        if (agency.getOwner() == null) {
            var loggedUser = getLoggedUser();
            if (loggedUser != null) {
                agency.setOwner(loggedUser);
            }
            if (!old.getOwner().equals(loggedUser)){
                //throw new UnautorizedException("You are not the owner of this agency. You cannot modify it.");
            };
        }
        if (agency.getOwner() == null) {
            //throw new InvalidEntityDataException("You cannot create an agency without being signed in.");
            agency.setOwner(old.getOwner());
        }
        agency.setAvatar(old.getAvatar());

        // Checks if new name exists in DB
        if (!old.getName().equals(agency.getName())) {
            if (agencyRepo.findByName(agency.getName()).isPresent()){
                throw new InvalidEntityDataException(
                        "The name provided is already in use. Please choose another name for the agency.");
            }
        }

        // Checks if owner is different
        if (!old.equals(agency)){
            throw new InvalidEntityDataException("The owner of the agency cannot be changed. Please reach out to us for assistance with this.");
        }

        // Merges old broker users list with new broker users list
        List<User> tempUsersList = old.getUsersCollection();
        tempUsersList.addAll(agency.getUsersCollection());
        agency.setUsersCollection(tempUsersList);

        // Updates time created and modified
        agency.setCreated(old.getCreated());
        agency.setModified(LocalDateTime.now());

        // Saves to DB
        return agencyRepo.save(agency);
    }

    @Override
    public Agency deleteAgencyById(Long id) throws NonExistingEntityException {
        var old = getAgencyById(id);
        agencyRepo.delete(old);
        return old;
    }

    @Override
    public long getAgencyCount() {
        return agencyRepo.count();
    }

    private User getLoggedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                return (User) principal;
            }
        }
        return null;
    }
}
