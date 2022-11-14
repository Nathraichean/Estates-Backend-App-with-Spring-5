package com.homework.estate_project.web;

import com.homework.estate_project.dto.agency.AgencyCreateDto;
import com.homework.estate_project.dto.agency.AgencyDetailsDto;
import com.homework.estate_project.dto.agency.AgencyUpdateDto;
import com.homework.estate_project.entity.Agency;
import com.homework.estate_project.exception.InvalidEntityDataException;
import com.homework.estate_project.service.AgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static com.homework.estate_project.dto.mapping.AgencyDtoMapper.*;
import static com.homework.estate_project.utils.ErrorHandlingUtils.handleValidationErrors;

@RestController
@RequestMapping("/api/agency")
public class AgencyRestController {

    @Autowired
    private AgencyService agencyService;

    @GetMapping
    public List<Agency> getAllAgencies() {
        return agencyService.getAllAgencies();
    }

    @GetMapping("/{id:\\d+}")
    public Agency getAgencyById(@PathVariable("id") Long id) {
        return agencyService.getAgencyById(id);
    }

    @PostMapping
    public ResponseEntity<AgencyDetailsDto> addNewAgency(@Valid @RequestBody AgencyCreateDto agencyDto, Errors errors, Principal auth) {
        handleValidationErrors(errors);

        var created = agencyService.createAgency(mapAgencyCreateDtoToAgency(agencyDto));
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest().pathSegment("{id}")
                        .buildAndExpand(created.getId()).toUri()
        ).body(mapAgencyToAgencyDetailDto(created));
    }

    @PutMapping("/{id}")
    public AgencyDetailsDto updateAgency(@PathVariable("id") Long id, @Valid @RequestBody AgencyUpdateDto agencyDto, Errors errors) {
        handleValidationErrors(errors);

        if(!id.equals(agencyDto.id())) {
            throw new InvalidEntityDataException(
                    String.format("ID in URL='%d' is different from ID in message body = '%d'", id, agencyDto.id()));
        }
        return mapAgencyToAgencyDetailDto(agencyService.updateAgency(mapAgencyUpdateDtoToAgency(agencyDto)));
    }

    @DeleteMapping("/{id}")
    public AgencyDetailsDto deleteAgencyById(@PathVariable("id") Long id) {
        return mapAgencyToAgencyDetailDto(agencyService.deleteAgencyById(id));
    }

    @GetMapping(value = "/count", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getAgencyCount() {
        return Long.toString(agencyService.getAgencyCount());
    }
}
