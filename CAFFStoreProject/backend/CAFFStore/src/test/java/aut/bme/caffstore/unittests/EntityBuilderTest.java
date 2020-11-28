package aut.bme.caffstore.unittests;

import aut.bme.caffstore.data.entity.User;
import aut.bme.caffstore.service.EntityBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

class EntityBuilderTest {

    private EntityBuilder entityBuilder = new EntityBuilder();

    @Test
    void testBuildEntityWithNullPassword() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("password", null);

        assertThrows(Exception.class, () -> entityBuilder.buildUser(body));
    }

    @Test
    void testBuildEntityComplete() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        Map<String, Object> body = new HashMap<>();
        body.put("createDate", now);
        body.put("personName", "MyName");
        body.put("email", "myname.myname@myname.com");
        body.put("password", "mypass");

        User user = entityBuilder.buildUser(body);

        assertEquals(now, user.getCreateDate());
        assertEquals("MyName", user.getPersonName());
        assertEquals("myname.myname@myname.com", user.getEmail());
        assertNotEquals("mypass".getBytes(), user.getPassword());
        assertNotNull(user.getSalt());

    }
}
