package aut.bme.CAFFStore.controller;

import aut.bme.CAFFStore.data.dto.response.StringResponseDTO;
import aut.bme.CAFFStore.data.dto.response.UserDetailsResponseDTO;
import aut.bme.CAFFStore.data.dto.response.UserResponseDTO;
import aut.bme.CAFFStore.data.entity.User;
import aut.bme.CAFFStore.data.repository.UserRepo;
import aut.bme.CAFFStore.data.util.password.PasswordManager;
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

import static aut.bme.CAFFStore.security.JWTTokenGenerator.getJWTToken;

@RestController
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<StringResponseDTO> register(@RequestBody Map<String, Object> body) {
        return userService.register(body);
    }

    @RequestMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestParam(value = "email") String email, @RequestParam(value = "password") String password) {
        Optional<User> user = userRepo.findByEmail(email);

        if (user.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        if (PasswordManager.match(user.get().getPassword(), password, user.get().getSalt())) {
            UserResponseDTO userResponseDTO = UserResponseDTO.createUserDTO(userRepo.save(user.get()));
            userResponseDTO.setToken(getJWTToken(userResponseDTO.getUsername(), user.get().getRole().toString()));
            return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public ResponseEntity<UserDetailsResponseDTO> getUserDetailsById(@PathVariable String id) {
        logger.info("Getting user with id: " + id);
        Optional<User> user = userRepo.findById(id);
        return user.map(value -> new ResponseEntity<>(userService.createUserDetailsDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    public ResponseEntity<StringResponseDTO> updateUserName(@PathVariable String id, @RequestParam String username) {
        logger.info("Updating username to " + username + "for user with id: " + id);
        Optional<User> user = userRepo.findById(id);
        if (user.isPresent()) {
            user.get().setPersonName(username);
            userRepo.save(user.get());
            return new ResponseEntity<>(new StringResponseDTO("Successful update."), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        logger.info("Getting all users");
        return new ResponseEntity<>(UserResponseDTO.createUserDTOs(userRepo.findAll()), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        logger.info("Deleting user with id: " + id);
        if (userRepo.findById(id).isPresent()) {
            userRepo.deleteById(id);
            return new ResponseEntity<>("Successful deletion.", HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
}