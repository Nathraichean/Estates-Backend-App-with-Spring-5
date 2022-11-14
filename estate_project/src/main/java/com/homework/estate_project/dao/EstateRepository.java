package com.homework.estate_project.dao;

import com.homework.estate_project.entity.Estate;
import com.homework.estate_project.entity.ImageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstateRepository extends PagingAndSortingRepository<Estate, Long>, JpaSpecificationExecutor<Estate> {

    Page<Estate> findAll(Specification<Estate> spec, Pageable pageable);
    Optional<Estate> findEstatesByPhotosContains(ImageData imageData);

}
