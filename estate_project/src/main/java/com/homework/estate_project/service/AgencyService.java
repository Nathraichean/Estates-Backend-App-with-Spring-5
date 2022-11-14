package com.homework.estate_project.service;

import com.homework.estate_project.entity.Agency;
import com.homework.estate_project.exception.InvalidEntityDataException;
import com.homework.estate_project.exception.NonExistingEntityException;

import java.util.List;

public interface AgencyService {

    List<Agency> getAllAgencies();

    Agency getAgencyById(Long id) throws NonExistingEntityException;

    Agency createAgency(Agency user) throws InvalidEntityDataException;


    Agency updateAgency(Agency user) throws NonExistingEntityException, InvalidEntityDataException;

    Agency deleteAgencyById(Long id) throws NonExistingEntityException;

    long getAgencyCount();
}
