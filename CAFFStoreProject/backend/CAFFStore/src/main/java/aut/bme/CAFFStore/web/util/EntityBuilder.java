package aut.bme.CAFFStore.web.util;

import aut.bme.CAFFStore.data.Role;
import aut.bme.CAFFStore.data.entity.User;
import aut.bme.CAFFStore.data.repository.UserRepo;
import aut.bme.CAFFStore.data.util.password.PasswordManager;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class EntityBuilder {
    @Autowired
    private UserRepo userRepo;

    public void EntityBuilder() {
    }

    public User buildUser(Map<String, Object> payload) throws Exception {
        User user = new User();

        if (payload.get("id") != null) {
            user.setId(payload.get("id").toString());
        }

        if (payload.get("createDate") != null)
            user.setCreateDate(LocalDateTime.parse(payload.get("createDate").toString()));
        else user.setCreateDate(LocalDateTime.now());

        if (payload.get("personName") != null) user.setPersonName(payload.get("personName").toString());
        else throw new Exception("personName null!");

        if (payload.get("email") != null) user.setEmail(payload.get("email").toString());
        else throw new Exception("email null!");

        if (payload.get("password") != null && payload.get("salt") != null) {
            //already has password and salt
            user.setSalt(Base64.decodeBase64(payload.get("salt").toString()));
            user.setPassword(Base64.decodeBase64(payload.get("password").toString()));
        } else {
            //new user, only has password
            if (payload.get("password") != null) {
                user.setSalt(PasswordManager.generateSalt());
                user.setPassword(PasswordManager.hashAndSalt(payload.get("password").toString(), user.getSalt()));
            } else throw new Exception("password null!");
        }
        user.setRole(Role.USER);
        return user;
    }
}
