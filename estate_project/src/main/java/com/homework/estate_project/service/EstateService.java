package com.homework.estate_project.service;

import com.homework.estate_project.entity.Estate;
import com.homework.estate_project.exception.InvalidEntityDataException;
import com.homework.estate_project.exception.NonExistingEntityException;
import com.homework.estate_project.utils.PagingResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;

import java.util.List;

public interface EstateService {

    List<Estate> getAllEstates();

    Estate get(Long id);

    PagingResponse get(Specification<Estate> spec, HttpHeaders headers, Sort sort);

    PagingResponse get(Specification<Estate> spec, Pageable pageable);

    List<Estate> get(Specification<Estate> spec, Sort sort);

    Estate getEstateById(Long id) throws NonExistingEntityException;

    Estate createEstate(Estate estate) throws InvalidEntityDataException;

    Estate updateEstate(Estate estate) throws NonExistingEntityException, InvalidEntityDataException;

    Estate deleteEstateById(Long id) throws NonExistingEntityException;

    long getEstateCount();

}
