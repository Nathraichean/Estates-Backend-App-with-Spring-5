package com.homework.estate_project.web;

import com.homework.estate_project.entity.User;
import com.homework.estate_project.exception.InvalidEntityDataException;
import com.homework.estate_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

import static com.homework.estate_project.utils.ErrorHandlingUtils.handleValidationErrors;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id:\\d+}")
    public User getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<User> addNewUser(@Valid @RequestBody User user, Errors errors) {
        handleValidationErrors(errors);

        var created = userService.create(user);
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest().pathSegment("{id}")
                        .buildAndExpand(created.getId()).toUri()
        ).body(created);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable("id") Long id, @Valid @RequestBody User user, Errors errors) {
        handleValidationErrors(errors);

        if(!id.equals(user.getId())) {
            throw new InvalidEntityDataException(
                    String.format("ID in URL='%d' is different from ID in message body = '%d'", id, user.getId()));
        }
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUserById(@PathVariable("id") Long id) {
        User deletedUser = userService.deleteUserById(id);
        return ResponseEntity.ok().body(deletedUser);
    }

    @GetMapping(value = "/count", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getUserCount() {
        return Long.toString(userService.getUsersCount());
    }
}
