package aut.bme.CAFFStore.service;

import aut.bme.CAFFStore.data.dto.response.CommentResponseDTO;
import aut.bme.CAFFStore.data.dto.response.StringResponseDTO;
import aut.bme.CAFFStore.data.dto.response.UserDetailsResponseDTO;
import aut.bme.CAFFStore.data.dto.response.UserResponseDTO;
import aut.bme.CAFFStore.data.entity.Caff;
import aut.bme.CAFFStore.data.entity.Comment;
import aut.bme.CAFFStore.data.entity.User;
import aut.bme.CAFFStore.data.repository.UserRepo;
import aut.bme.CAFFStore.data.util.password.PasswordManager;
import com.google.common.collect.Lists;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static aut.bme.CAFFStore.security.JWTTokenGenerator.getJWTToken;

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

        if (body.get("id") != null) {
            String userId = body.get("id").toString();

            Optional<User> userOptional = userRepo.findById(userId);
            if (userOptional.isPresent()) {
                body.put("password", Base64.encodeBase64String(userOptional.get().getPassword()));
                body.put("salt", Base64.encodeBase64String(userOptional.get().getSalt()));
            } else
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        User newUser;
        try {
            newUser = entityBuilder.buildUser(body);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        if (newUser != null) {
            userRepo.save(newUser);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    public UserDetailsResponseDTO createUserDetailsDTO(User user) {
        return new UserDetailsResponseDTO(
                user.getId(),
                user.getPersonName(),
                user.getEmail(),
                user.getComments()
                        .stream()
                        .sorted(Comparator.comparing(Comment::getTimeStamp))
                        .map(CommentResponseDTO::createCommentDTO)
                        .collect(Collectors.toList()),
                caffService.getMultipleCaffDTOsById(
                        Lists.newArrayList(user.getCaffs().stream().map(Caff::getId).collect(Collectors.toList()))));
    }

    public ResponseEntity<UserResponseDTO> login(String email, String password) {
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

    public ResponseEntity<UserDetailsResponseDTO> getUserDetailsById(String id) {
        Optional<User> user = userRepo.findById(id);
        return user.map(value -> new ResponseEntity<>(createUserDetailsDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
    }

    public ResponseEntity<StringResponseDTO> updateUserName(String id, String username) {
        Optional<User> user = userRepo.findById(id);
        if (user.isPresent()) {
            user.get().setPersonName(username);
            userRepo.save(user.get());
            return new ResponseEntity<>(new StringResponseDTO("Successful update."), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return new ResponseEntity<>(UserResponseDTO.createUserDTOs(userRepo.findAll()), HttpStatus.OK);
    }

    public ResponseEntity<String> deleteUser(String id) {
        if (userRepo.findById(id).isPresent()) {
            userRepo.deleteById(id);
            return new ResponseEntity<>("Successful deletion.", HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
}
