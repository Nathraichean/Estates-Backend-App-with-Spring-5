package com.homework.estate_project.utils;

import com.homework.estate_project.dto.agency.AgencyCreateDto;
import com.homework.estate_project.exception.InvalidEntityDataException;

import java.util.HashMap;


public class StringToDtoUtils {
    public static AgencyCreateDto mapStringToAgencyCreateDto(String input) throws InvalidEntityDataException {
        try {
            String properties[] = input.split(",");
            HashMap<String, String> propertiesMap = new HashMap<>();
            for (int i = 0; i < properties.length; i++) {
                propertiesMap.put(properties[i].split(":")[0],properties[i].split(":")[1]);
            }
            AgencyCreateDto estate = new AgencyCreateDto(propertiesMap.get("name"), propertiesMap.get("description"));
            return estate;
        }
        catch (Error error){
            throw new InvalidEntityDataException("Invalid agency object provided in string format. Error msg:" + error.getMessage());
        }

    }
}
