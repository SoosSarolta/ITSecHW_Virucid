package aut.bme.CAFFStore.unittests;

import aut.bme.CAFFStore.data.dto.BasicStringResponseDTO;
import aut.bme.CAFFStore.data.entity.User;
import aut.bme.CAFFStore.data.repository.UserRepo;
import aut.bme.CAFFStore.service.CaffService;
import aut.bme.CAFFStore.service.EntityBuilder;
import aut.bme.CAFFStore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private CaffService caffService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private EntityBuilder entityBuilder = new EntityBuilder();

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testRegisterWithAlreadyRegisteredEmail(){
        Map<String, Object> body = new HashMap<>();
        body.put("email", "example.example@example.com");

        when(userRepo.existsByEmail("example.example@example.com")).thenReturn(true);

        ResponseEntity<BasicStringResponseDTO> responseEntity = userService.register(body);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("There's already a user registered with this email.",
                Objects.requireNonNull(responseEntity.getBody()).getResponse());
    }

    @Test
    void testRegister() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("email", "example.example@example.com");

        when(userRepo.existsByEmail("example.example@example.com")).thenReturn(false);
        when(entityBuilder.buildUser(body)).thenReturn(new User());

        ResponseEntity<BasicStringResponseDTO> responseEntity = userService.register(body);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

}
