package aut.bme.CAFFStore.controller;

import aut.bme.CAFFStore.data.dto.BasicStringResponseDTO;
import aut.bme.CAFFStore.data.dto.UserDTO;
import aut.bme.CAFFStore.data.dto.UserDetailsDTO;
import aut.bme.CAFFStore.data.entity.User;
import aut.bme.CAFFStore.data.repository.UserRepo;
import aut.bme.CAFFStore.data.util.password.PasswordManager;
import aut.bme.CAFFStore.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<BasicStringResponseDTO> register(@RequestBody Map<String, Object> body) {
        return userService.register(body);
    }

    @RequestMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestParam(value = "email") String email, @RequestParam(value = "password") String password) {
        Optional<User> user = userRepo.findByEmail(email);

        if (user.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        if (PasswordManager.match(user.get().getPassword(), password, user.get().getSalt())) {
            UserDTO userDTO = UserDTO.createUserDTO(userRepo.save(user.get()));
            userDTO.setToken(getJWTToken(userDTO.getUsername(), user.get().getRole().toString()));
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public ResponseEntity<UserDetailsDTO> getUserDetailsById(@PathVariable String id) {
        logger.info("Getting user with id: " + id);
        Optional<User> user = userRepo.findById(id);
        return user.map(value -> new ResponseEntity<>(userService.createUserDetailsDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    public ResponseEntity<BasicStringResponseDTO> updateUserName(@PathVariable String id, @RequestParam String username) {
        logger.info("Updating username to " + username + "for user with id: " + id);
        Optional<User> user = userRepo.findById(id);
        if (user.isPresent()) {
            user.get().setPersonName(username);
            userRepo.save(user.get());
            return new ResponseEntity<>(new BasicStringResponseDTO("Successful update."), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        logger.info("Getting all users");
        return new ResponseEntity<>(UserDTO.createUserDTOs(userRepo.findAll()), HttpStatus.OK);
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

    private String getJWTToken(String username, String role) {
        String secretKey = "mySecretKey";
        logger.info("getJWTToken: " + role);
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_" + role);

        String token = Jwts
                .builder()
                .setId("softtekJWT")
                .setSubject(username)
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(SignatureAlgorithm.HS512,
                        secretKey.getBytes()).compact();

        return "Bearer " + token;
    }
}