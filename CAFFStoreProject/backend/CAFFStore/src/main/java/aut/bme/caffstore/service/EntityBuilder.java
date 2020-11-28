package aut.bme.caffstore.service;

import aut.bme.caffstore.EntityBuilderException;
import aut.bme.caffstore.data.Role;
import aut.bme.caffstore.data.entity.User;
import aut.bme.caffstore.data.util.password.PasswordManager;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class EntityBuilder {

    public User buildUser(Map<String, Object> payload) throws EntityBuilderException {
        User user = new User();

        if (payload.get("createDate") != null)
            user.setCreateDate(LocalDateTime.parse(payload.get("createDate").toString()));
        else user.setCreateDate(LocalDateTime.now());

        if (payload.get("personName") != null) user.setPersonName(payload.get("personName").toString());
        else throw new EntityBuilderException("personName null!");

        if (payload.get("email") != null) user.setEmail(payload.get("email").toString());
        else throw new EntityBuilderException("email null!");

        String passwordKey = "password";
        if (payload.get(passwordKey) != null && payload.get("salt") != null) {
            //already has password and salt
            user.setSalt(Base64.decodeBase64(payload.get("salt").toString()));
            user.setPassword(Base64.decodeBase64(payload.get(passwordKey).toString()));
        } else {
            //new user, only has password
            if (payload.get(passwordKey) != null) {
                user.setSalt(PasswordManager.generateSalt());
                user.setPassword(PasswordManager.hashAndSalt(payload.get(passwordKey).toString(), user.getSalt()));
            } else throw new EntityBuilderException("password null!");
        }
        user.setRole(Role.USER);
        return user;
    }
}
