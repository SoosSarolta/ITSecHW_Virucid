package aut.bme.caffstore.unittests;

import aut.bme.caffstore.data.Role;
import aut.bme.caffstore.data.dto.response.StringResponseDTO;
import aut.bme.caffstore.data.dto.response.UserDetailsResponseDTO;
import aut.bme.caffstore.data.dto.response.UserResponseDTO;
import aut.bme.caffstore.data.entity.User;
import aut.bme.caffstore.data.repository.UserRepo;
import aut.bme.caffstore.service.CaffService;
import aut.bme.caffstore.service.EntityBuilder;
import aut.bme.caffstore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static aut.bme.caffstore.data.util.password.PasswordManager.generateSalt;
import static aut.bme.caffstore.data.util.password.PasswordManager.hashAndSalt;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private CaffService caffService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private EntityBuilder entityBuilder = new EntityBuilder();

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private User user;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        clearUser();
    }

    @Test
    void testRegisterWithAlreadyRegisteredEmail() {
        Map<String, Object> body = new HashMap<>();
        body.put("email", "example.example@example.com");

        when(userRepo.existsByEmail("example.example@example.com")).thenReturn(true);

        ResponseEntity<StringResponseDTO> responseEntity = userService.register(body);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("There's already a user registered with this email.",
                Objects.requireNonNull(responseEntity.getBody()).getResponse());
    }

    @Test
    void testRegister() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("email", "example.example@example.com");
        body.put("personName", "Example Person");
        body.put("password", "eXaMpLePaSsWoRd");

        when(userRepo.existsByEmail("example.example@example.com")).thenReturn(false);
        when(entityBuilder.buildUser(body)).thenReturn(new User());

        ResponseEntity<StringResponseDTO> responseEntity = userService.register(body);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Successful registration.", Objects.requireNonNull(responseEntity.getBody()).getResponse());
    }

    @Test
    void testLogin() {
        String email = "example.example@example.com";
        String password = "eXaMpLePaSsWoRd";

        user.setId("UserId");
        user.setEmail("example.example@example.com");
        user.setPersonName("Example Person");
        user.setRole(Role.USER);
        user.setSalt(generateSalt());
        user.setPassword(hashAndSalt("eXaMpLePaSsWoRd", user.getSalt()));

        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(user);

        ResponseEntity<UserResponseDTO> responseEntity = userService.login(email, password);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(UserResponseDTO.createUserDTO(user).getId(), Objects.requireNonNull(responseEntity.getBody()).getId());
        assertEquals(UserResponseDTO.createUserDTO(user).getEmail(), responseEntity.getBody().getEmail());
        assertEquals(UserResponseDTO.createUserDTO(user).getUsername(), responseEntity.getBody().getUsername());
        assertEquals(UserResponseDTO.createUserDTO(user).getRole(), responseEntity.getBody().getRole());
    }

    @Test
    void testLoginWithWrongPassword() {
        String email = "example.example@example.com";
        String wrongPassword = "clearlyNotThePassword";

        user.setId("UserId");
        user.setEmail("example.example@example.com");
        user.setPersonName("Example Person");
        user.setRole(Role.USER);
        user.setSalt(generateSalt());
        user.setPassword(hashAndSalt("eXaMpLePaSsWoRd", user.getSalt()));

        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(user);

        ResponseEntity<UserResponseDTO> responseEntity = userService.login(email, wrongPassword);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void testLoginWithEmptyUser() {
        String email = "example.example@example.com";
        String password = "eXaMpLePaSsWoRd";

        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        ResponseEntity<UserResponseDTO> responseEntity = userService.login(email, password);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void testGetUserDetailsById() {
        user.setId("UserId");
        user.setEmail("example.example@example.com");
        user.setPersonName("Example Person");
        user.setRole(Role.USER);
        user.setSalt(generateSalt());
        user.setPassword(hashAndSalt("eXaMpLePaSsWoRd", user.getSalt()));

        when(userRepo.findById(anyString())).thenReturn(Optional.of(user));

        ResponseEntity<UserDetailsResponseDTO> responseEntity = userService.getUserDetailsById("UserId");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(user.createUserDetailsDTO(), responseEntity.getBody());
    }

    @Test
    void testGetUserDetailsByIdWithWrongId() {
        when(userRepo.findById(anyString())).thenReturn(Optional.empty());

        ResponseEntity<UserDetailsResponseDTO> responseEntity = userService.getUserDetailsById("UserId");

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void testUpdateUserName() {
        when(userRepo.findById(anyString())).thenReturn(Optional.of(user));

        ResponseEntity<StringResponseDTO> responseEntity = userService.updateUserName("UserId", "New User Name");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Successful update.", Objects.requireNonNull(responseEntity.getBody()).getResponse());
    }

    @Test
    void testUpdateUserNameWithWrongId() {
        when(userRepo.findById(anyString())).thenReturn(Optional.empty());

        ResponseEntity<StringResponseDTO> responseEntity = userService.updateUserName("UserId", "New User Name");

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Cannot update username.", Objects.requireNonNull(responseEntity.getBody()).getResponse());
    }

    @Test
    void testGetAllUsers() {
        user.setId("UserId");
        user.setEmail("example.example@example.com");
        user.setPersonName("Example Person");
        user.setRole(Role.USER);

        when(userRepo.findAll()).thenReturn(List.of(user, user));

        ResponseEntity<List<UserResponseDTO>> responseEntity = userService.getAllUsers();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(2, Objects.requireNonNull(responseEntity.getBody()).size());
        assertEquals(UserResponseDTO.createUserDTOs(List.of(user, user)), responseEntity.getBody());
    }

    @Test
    void testDeleteUser() {
        when(userRepo.findById(anyString())).thenReturn(Optional.of(user));

        ResponseEntity<String> responseEntity = userService.deleteUser("UserId");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Successful deletion.", responseEntity.getBody());
    }

    @Test
    void testDeleteUserWithWrongId() {
        when(userRepo.findById(anyString())).thenReturn(Optional.empty());

        ResponseEntity<String> responseEntity = userService.deleteUser("UserId");

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("User does not exist.", responseEntity.getBody());
    }

    private void clearUser() {
        user.setId(null);
        user.setCreateDate(null);
        user.setPersonName(null);
        user.setEmail(null);
        user.setPassword(null);
        user.setSalt(null);
        user.setCaffs(new ArrayList<>());
        user.setComments(new ArrayList<>());
        user.setRole(Role.USER);
    }

}
