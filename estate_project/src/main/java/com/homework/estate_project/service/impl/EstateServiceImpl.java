package com.homework.estate_project.service.impl;

import com.homework.estate_project.dao.EstateRepository;
import com.homework.estate_project.dao.ImageDataRepository;
import com.homework.estate_project.dao.UserRepository;
import com.homework.estate_project.entity.Estate;
import com.homework.estate_project.entity.ImageData;
import com.homework.estate_project.entity.User;
import com.homework.estate_project.entity.enums.GenericStatus;
import com.homework.estate_project.exception.InvalidEntityDataException;
import com.homework.estate_project.exception.NonExistingEntityException;
import com.homework.estate_project.service.EstateService;
import com.homework.estate_project.utils.PagingHeaders;
import com.homework.estate_project.utils.PagingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service @RequiredArgsConstructor @Slf4j
public class EstateServiceImpl implements EstateService {

    private EstateRepository estateRepo;
    private UserRepository userRepo;
    private ImageDataRepository imageDataRepo;

    @Autowired
    public EstateServiceImpl(EstateRepository estateRepo,UserRepository userRepo, ImageDataRepository imageDataRepo) {
        this.estateRepo = estateRepo;
        this.userRepo = userRepo;
        this.imageDataRepo = imageDataRepo;
    }

    @Override
    public List<Estate> getAllEstates() {
        ArrayList<Estate> list = new ArrayList<>();
        estateRepo.findAll().forEach(list::add);
        return list;
    }
    @Override
    public Estate get(Long id) {
        return estateRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Can not find the entity estate (%s = %s).", "id", id)));
    }
    @Override
    public PagingResponse get(Specification<Estate> spec, HttpHeaders headers, Sort sort) {
        if (isRequestPaged(headers)) {
            return get(spec, buildPageRequest(headers, sort));
        } else {
            final List<Estate> entities = get(spec, sort);
            return new PagingResponse((long) entities.size(), 0L, 0L, 0L, 0L, entities);
        }
    }

    private boolean isRequestPaged(HttpHeaders headers) {
        return headers.containsKey(PagingHeaders.PAGE_NUMBER.getName()) && headers.containsKey(PagingHeaders.PAGE_SIZE.getName());
    }

    private Pageable buildPageRequest(HttpHeaders headers, Sort sort) {
        int page = Integer.parseInt(Objects.requireNonNull(headers.get(PagingHeaders.PAGE_NUMBER.getName())).get(0));
        int size = Integer.parseInt(Objects.requireNonNull(headers.get(PagingHeaders.PAGE_SIZE.getName())).get(0));
        return PageRequest.of(page, size, sort);
    }

    @Override
    public PagingResponse get(Specification<Estate> spec, Pageable pageable) {
        Page<Estate> page = estateRepo.findAll(spec, pageable);
        List<Estate> content = page.getContent();
        return new PagingResponse(page.getTotalElements(), (long) page.getNumber(), (long) page.getNumberOfElements(), pageable.getOffset(), (long) page.getTotalPages(), content);
    }

    @Override
    public List<Estate> get(Specification<Estate> spec, Sort sort) {
        return estateRepo.findAll(spec, sort);
    }

    @Override
    public Estate getEstateById(Long id) throws NonExistingEntityException {
        return estateRepo.findById(id).orElseThrow(() -> new NonExistingEntityException(
                String.format("Estate with ID: '%d' does not exist.", id)
        ));
    }

    @Override
    public Estate createEstate(Estate estate) throws InvalidEntityDataException {
        estate.setId(null);

        // Check for DataInitializer user, if not present, set default user to be User 1 .
        Optional<User> providedUser = userRepo.findById(1L);
        if (estate.getOwner() != null){
            providedUser = userRepo.findById(estate.getOwner().getId());
        }

        if (estate.getOwner() == null) {
            var loggedUser = getLoggedUser();
            if (loggedUser != null) {
                estate.setOwner(loggedUser);
            }
        }
        if (estate.getOwner() == null) {
            //throw new InvalidEntityDataException("You cannot create an estate without being signed in.");
            //estate.setOwner(userRepo.findById(1L).get());
            if (providedUser.isPresent()){
                estate.setOwner(providedUser.get());
                estate.setOwnerId(providedUser.get().getId());
            }
            else {
                throw new InvalidEntityDataException(String.format(
                        "The provided estate owner ID (%d) does not exist in our database.",
                        estate.getOwner().getId()));
            }
        }

        if ((estate.getUsableArea() + estate.getSharedArea()) != estate.getTotalArea()){
            throw new InvalidEntityDataException(String.format(
                    "The provided estate's area values don't add up. Total area (%d) should equal usable area(%d)+shared area(%d)",
                    estate.getTotalArea(),estate.getUsableArea(),estate.getSharedArea()));
        }

        if (estate.getPhotos() == null){
            estate.setPhotos(List.of(imageDataRepo.findById(1L).get()));
        }

        estate.setModified(LocalDateTime.now());
        return estateRepo.save(estate);
    }

    @Override
    public Estate updateEstate(Estate estate) throws NonExistingEntityException, InvalidEntityDataException {

        Estate oldEstate = getEstateById(estate.getId());

        if (estate.getOwner() == null) {
            var loggedUser = getLoggedUser();
            if (loggedUser != null) {
                estate.setOwner(loggedUser);
            }
            if (!oldEstate.getOwner().equals(loggedUser)){
                //throw new UnautorizedException("You are not the owner of this agency. You cannot modify it.");
            };
        }
        if (estate.getOwner() == null) {
            //throw new InvalidEntityDataException("You cannot create an agency without being signed in.");
            estate.setOwner(oldEstate.getOwner());
        }

        if (oldEstate.getStatus() != GenericStatus.PENDING){
            throw new InvalidEntityDataException(String.format
                    ("The article cannot be edited after it is approved. Required status for modification: PENDING, Current status: %s"
                            ,oldEstate.getStatus().toString()));
        }

        if (!estate.getOwner().equals(oldEstate.getOwner())) {
            throw new InvalidEntityDataException(String.format(
                    "You cannot update estates published by other users. Provided: %s, Actual: %s",
                    estate.getOwner().getUsername(),oldEstate.getOwner().getUsername()));
        }
        else {
            estate.setCreated(oldEstate.getCreated());
            estate.setViews(oldEstate.getViews());
            estate.setPhotos(oldEstate.getPhotos());
            estate.setModified(LocalDateTime.now());
            if ((estate.getSharedArea()+estate.getUsableArea()) != estate.getTotalArea()){
                throw new InvalidEntityDataException(String.format(
                        "The combined sum of the shared and usable areas need to total to the total area. Provided Usable:%d, Shared:%d, Total:%d",
                        estate.getUsableArea(),estate.getSharedArea(),estate.getTotalArea()));
            }
            return estateRepo.save(estate);
        }
        //throw new InvalidEntityDataException("Invalid entity submitted. Uncaught reason.");
    }

    @Override
    @Transactional
    public Estate deleteEstateById(Long id) throws NonExistingEntityException {
        if (estateRepo.findById(id).isPresent()){
            Estate deleted = estateRepo.findById(id).get();
            deleted.getPhotos().removeAll(deleted.getPhotos());
            deleted.getFeatures().removeAll(deleted.getFeatures());
            estateRepo.deleteById(id);
            return deleted;
        }
        else throw new NonExistingEntityException("Estate with provided ID does not exist.");
    }

    @Override
    public long getEstateCount() {
        return estateRepo.count();
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
