package com.homework.estate_project.web;


import com.homework.estate_project.dto.agency.AgencyDetailsDto;
import com.homework.estate_project.dto.agency.AgencyUpdateDto;
import com.homework.estate_project.dto.estate.EstateCreateDto;
import com.homework.estate_project.dto.estate.EstateDetailsDto;
import com.homework.estate_project.dto.estate.EstateUpdateDto;
import com.homework.estate_project.entity.Agency;
import com.homework.estate_project.entity.Estate;
import com.homework.estate_project.entity.User;
import com.homework.estate_project.exception.InvalidEntityDataException;
import com.homework.estate_project.service.EstateService;
import com.homework.estate_project.utils.PagingHeaders;
import com.homework.estate_project.utils.PagingResponse;
import net.kaczmarzyk.spring.data.jpa.domain.*;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Join;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;

import java.util.List;

import static com.homework.estate_project.dto.mapping.EstateDtoMapper.*;
import static com.homework.estate_project.utils.ErrorHandlingUtils.handleValidationErrors;

@RestController
@RequestMapping("/api/estate")
public class EstateRestController {

    @Autowired
    private EstateService estateService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Estate>> get(
            @Join(path = "owner", alias="owner")
            @And({
                    @Spec(path = "owner.id", params = "ownerId", spec = Equal.class),
                    @Spec(path = "location", params = "location", paramSeparator=',', spec = In.class),
                    @Spec(path = "price", params = {"fromPrice","toPrice"}, spec = Between.class),
                    @Spec(path = "price", params = "minPrice", spec = GreaterThan.class),
                    @Spec(path = "price", params = "maxPrice", spec = LessThan.class),
                    @Spec(path = "totalArea", params = "minTotalArea", spec = GreaterThan.class),
                    @Spec(path = "totalArea", params = "maxTotalArea", spec = LessThan.class),
                    @Spec(path = "totalArea", params = {"fromTotalArea","toTotalArea"}, spec = Between.class),
                    @Spec(path = "usableArea", params = {"fromUsableArea","toUsableArea"}, spec = Between.class),
                    @Spec(path = "usableArea", params = "minUsableArea", spec = GreaterThan.class),
                    @Spec(path = "usableArea", params = "maxUsableArea", spec = LessThan.class),
                    @Spec(path = "floor", params = {"fromFloor","toFloor"}, spec = Between.class),
                    @Spec(path = "floor", params = "minFloor", spec = GreaterThan.class),
                    @Spec(path = "floor", params = "maxFloor", spec = LessThan.class),
                    @Spec(path = "floorType", paramSeparator=',', spec = In.class),
                    @Spec(path = "type", paramSeparator=',', spec = In.class),
                    @Spec(path = "status", paramSeparator=',', spec = In.class),
                    @Spec(path = "buildYear", params = {"fromBuildYear","toBuildYear"}, spec = Between.class),
                    @Spec(path = "buildYear", params = "minBuildYear", spec = GreaterThan.class),
                    @Spec(path = "buildYear", params = "maxBuildYear", spec = LessThan.class),
                    @Spec(path = "buildType", paramSeparator=',', spec = In.class),
                    @Spec(path = "createdDateOnly", params = "dateCreated", spec = Equal.class),
                    @Spec(path = "createdDateOnly", params = {"fromDateCreated", "toDateCreated"}, spec = Between.class),
                    @Spec(path = "createdDateOnly", params = "minDateCreated", spec = GreaterThan.class),
                    @Spec(path = "createdDateOnly", params = "maxDateCreated", spec = LessThan.class)
            })
            Specification<Estate> estateSpec, Sort sort, @RequestHeader HttpHeaders headers) {

        final PagingResponse response = estateService.get(estateSpec, headers, sort);
        return new ResponseEntity<>(response.getElements(), returnHttpHeaders(response), HttpStatus.OK);
    }

    @GetMapping("/{id:\\d+}")
    public Estate getEstateById(@PathVariable("id") Long id) {
        return estateService.getEstateById(id);
    }

    @PostMapping
    public ResponseEntity<EstateDetailsDto> addNewEstate(@Valid @RequestBody EstateCreateDto estateDto, Errors errors) {
        handleValidationErrors(errors);

        var created = estateService.createEstate(mapEstateCreateDtoToEstate(estateDto));
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest().pathSegment("{id}")
                        .buildAndExpand(created.getId()).toUri()
        ).body(mapEstateToEstateDetailDto(created));
    }

    @DeleteMapping("/{id}")
    public Estate deleteEstateById(@PathVariable("id") Long id) {
        return estateService.deleteEstateById(id);
    }

    @PutMapping("/{id}")
    public EstateDetailsDto updateEstate(@PathVariable("id") Long id, @Valid @RequestBody EstateUpdateDto estateDto, Errors errors) {
        handleValidationErrors(errors);

        if(!id.equals(estateDto.id())) {
            throw new InvalidEntityDataException(
                    String.format("ID in URL='%d' is different from ID in message body = '%d'", id, estateDto.id()));
        }
        return mapEstateToEstateDetailDto((estateService.updateEstate(mapEstateUpdateDtoToEstate(estateDto))));
    }

    public HttpHeaders returnHttpHeaders(PagingResponse response) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(PagingHeaders.COUNT.getName(), String.valueOf(response.getCount()));
        headers.set(PagingHeaders.PAGE_SIZE.getName(), String.valueOf(response.getPageSize()));
        headers.set(PagingHeaders.PAGE_OFFSET.getName(), String.valueOf(response.getPageOffset()));
        headers.set(PagingHeaders.PAGE_NUMBER.getName(), String.valueOf(response.getPageNumber()));
        headers.set(PagingHeaders.PAGE_TOTAL.getName(), String.valueOf(response.getPageTotal()));
        return headers;
    }
}
