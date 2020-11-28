package aut.bme.caffstore.service;

import aut.bme.caffstore.EntityBuilderException;
import aut.bme.caffstore.data.dto.response.StringResponseDTO;
import aut.bme.caffstore.data.dto.response.UserDetailsResponseDTO;
import aut.bme.caffstore.data.dto.response.UserResponseDTO;
import aut.bme.caffstore.data.entity.User;
import aut.bme.caffstore.data.repository.UserRepo;
import aut.bme.caffstore.data.util.password.PasswordManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static aut.bme.caffstore.security.JWTTokenGenerator.getJWTToken;

@Component
public class UserService {

    @Autowired
    private final EntityBuilder entityBuilder = new EntityBuilder();

    @Autowired
    private CaffService caffService;

    @Autowired
    private UserRepo userRepo;

    public ResponseEntity<StringResponseDTO> register(Map<String, Object> body) {
        String email = body.get("email").toString();
        if (userRepo.existsByEmail(email)) {
            return new ResponseEntity<>(new StringResponseDTO("There's already a user registered with this email."),
                    HttpStatus.BAD_REQUEST);
        }

        User newUser;
        try {
            newUser = entityBuilder.buildUser(body);
        } catch (EntityBuilderException e) {
            return new ResponseEntity<>(new StringResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        if (newUser != null) {
            userRepo.save(newUser);
            return new ResponseEntity<>(new StringResponseDTO("Successful registration."), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new StringResponseDTO("Was not able to create user."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<UserResponseDTO> login(String email, String password) {
        Optional<User> user = userRepo.findByEmail(email);

        if (user.isEmpty())
            return new ResponseEntity<>(new UserResponseDTO(), HttpStatus.BAD_REQUEST);

        boolean match = PasswordManager.match(user.get().getPassword(), password, user.get().getSalt());
        if (match) {
            UserResponseDTO userResponseDTO = UserResponseDTO.createUserDTO(userRepo.save(user.get()));
            userResponseDTO.setToken(getJWTToken(userResponseDTO.getUsername(),
                    user.get().getRole().toString(),
                    userResponseDTO.getId()));
            return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(new UserResponseDTO(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<UserDetailsResponseDTO> getUserDetailsById(String id) {
        Optional<User> user = userRepo.findById(id);
        return user.map(value -> new ResponseEntity<>(value.createUserDetailsDTO(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(new UserDetailsResponseDTO(), HttpStatus.BAD_REQUEST));
    }

    public ResponseEntity<StringResponseDTO> updateUserName(String id, String username) {
        Optional<User> user = userRepo.findById(id);
        if (user.isPresent()) {
            user.get().setPersonName(username);
            userRepo.save(user.get());
            return new ResponseEntity<>(new StringResponseDTO("Successful update."), HttpStatus.OK);
        }
        return new ResponseEntity<>(new StringResponseDTO("Cannot update username."), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return new ResponseEntity<>(UserResponseDTO.createUserDTOs(userRepo.findAll()), HttpStatus.OK);
    }

    public ResponseEntity<String> deleteUser(String id) {
        if (userRepo.findById(id).isPresent()) {
            userRepo.deleteById(id);
            return new ResponseEntity<>("Successful deletion.", HttpStatus.OK);
        }
        return new ResponseEntity<>("User does not exist.", HttpStatus.BAD_REQUEST);
    }
}
