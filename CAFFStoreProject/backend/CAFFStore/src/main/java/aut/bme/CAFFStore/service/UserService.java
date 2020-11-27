package aut.bme.CAFFStore.service;

import aut.bme.CAFFStore.data.dto.response.StringResponseDTO;
import aut.bme.CAFFStore.data.dto.response.CommentResponseDTO;
import aut.bme.CAFFStore.data.dto.response.UserDetailsResponseDTO;
import aut.bme.CAFFStore.data.entity.Caff;
import aut.bme.CAFFStore.data.entity.Comment;
import aut.bme.CAFFStore.data.entity.User;
import aut.bme.CAFFStore.data.repository.UserRepo;
import com.google.common.collect.Lists;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
}
