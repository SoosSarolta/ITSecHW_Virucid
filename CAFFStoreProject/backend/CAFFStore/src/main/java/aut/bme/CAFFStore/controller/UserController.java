package aut.bme.CAFFStore.controller;

import aut.bme.CAFFStore.data.dto.response.StringResponseDTO;
import aut.bme.CAFFStore.data.dto.response.UserDetailsResponseDTO;
import aut.bme.CAFFStore.data.dto.response.UserResponseDTO;
import aut.bme.CAFFStore.data.entity.User;
import aut.bme.CAFFStore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<StringResponseDTO> register(@RequestBody Map<String, Object> body) {
        logger.info("Registering user.");
        return userService.register(body);
    }

    @RequestMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestParam(value = "email") String email, @RequestParam(value = "password") String password) {
        logger.info("Logging in with email/password.");
        return userService.login(email, password);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public ResponseEntity<UserDetailsResponseDTO> getUserDetailsById(@PathVariable String id) {
        logger.info("Getting user details with id: " + id);
        return userService.getUserDetailsById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    public ResponseEntity<StringResponseDTO> updateUserName(@PathVariable String id, @RequestParam String username) {
        logger.info("Updating username to " + username + "for user with id: " + id);
        return userService.updateUserName(id, username);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        logger.info("Getting all users.");
        return userService.getAllUsers();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        logger.info("Deleting user with id: " + id);
        return userService.deleteUser(id);
    }
}