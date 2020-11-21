package aut.bme.CAFFStore.unittests;

import aut.bme.CAFFStore.data.entity.User;
import aut.bme.CAFFStore.service.EntityBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class EntityBuilderTest {

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
        body.put("id", "1234");
        body.put("createDate", now);
        body.put("personName", "MyName");
        body.put("email", "myname.myname@myname.com");
        body.put("password", "mypass");

        User user = entityBuilder.buildUser(body);

        assertEquals("1234", user.getId());
        assertEquals(now, user.getCreateDate());
        assertEquals("MyName", user.getPersonName());
        assertEquals("myname.myname@myname.com", user.getEmail());
        assertNotEquals("mypass", user.getPassword());
        assertTrue(user.getSalt() != null);

    }
}
