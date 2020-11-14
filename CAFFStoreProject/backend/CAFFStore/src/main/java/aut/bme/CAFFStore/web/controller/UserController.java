package aut.bme.CAFFStore.web.controller;

import aut.bme.CAFFStore.data.entity.User;
import aut.bme.CAFFStore.data.repository.UserRepo;
import aut.bme.CAFFStore.data.util.password.PasswordManager;
import aut.bme.CAFFStore.web.util.EntityBuilder;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    private EntityBuilder entityBuilder = new EntityBuilder();

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<String> register(@RequestBody Map<String, Object> body) {
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

    @RequestMapping("/login")
    public ResponseEntity<Object> login(@RequestParam(value = "email") String email, @RequestParam(value = "password") String password) {
        Optional<User> user = userRepo.findByEmail(email);

        if (!user.isPresent())
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        if (user.isPresent() && PasswordManager.match(user.get().getPassword(), password, user.get().getSalt())) {
            User newUser = userRepo.save(user.get());
            return new ResponseEntity<>(newUser, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
}