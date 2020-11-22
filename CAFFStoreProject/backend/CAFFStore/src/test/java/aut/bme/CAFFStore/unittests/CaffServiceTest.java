package aut.bme.CAFFStore.unittests;

import aut.bme.CAFFStore.data.dto.BasicStringResponseDTO;
import aut.bme.CAFFStore.data.entity.Caff;
import aut.bme.CAFFStore.data.entity.User;
import aut.bme.CAFFStore.data.repository.CaffRepo;
import aut.bme.CAFFStore.data.repository.UserRepo;
import aut.bme.CAFFStore.service.CaffService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

import static aut.bme.CAFFStore.Constants.CAFF_FILES_PATH;
import static aut.bme.CAFFStore.Constants.ROOT_PATH;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class CaffServiceTest {

    @Mock
    private CaffRepo caffRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private CaffService caffService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testUploadCaffWithNonExistingUser() throws IOException, InterruptedException {
        MockMultipartFile file
                = new MockMultipartFile(
                "caffFile",
                "caff.caff",
                MediaType.TEXT_PLAIN_VALUE,
                "CAFF CONTENT".getBytes());

        String userId = "MyUserId";

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<BasicStringResponseDTO> responseEntity = caffService.uploadCaff(file, userId);

        assertEquals("User does not exist!", Objects.requireNonNull(responseEntity.getBody()).getResponse());
    }

    @Test
    void testUploadCaffSaveFile() throws IOException, InterruptedException {
        String caffFileName = "null.caff";
        String caffFullPath = CAFF_FILES_PATH + caffFileName;
        String userId = "MyUserId";
        User user = new User();
        File caffFile = new File(caffFullPath);

        MockMultipartFile file
                = new MockMultipartFile(
                "caffFile",
                "caff.caff",
                MediaType.TEXT_PLAIN_VALUE,
                "CAFF CONTENT".getBytes());


        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<BasicStringResponseDTO> responseEntity = caffService.uploadCaff(file, userId);

        assertTrue(caffFile.exists());
        assertEquals("CAFF CONTENT", FileUtils.readFileToString(caffFile, StandardCharsets.UTF_8));

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(200, responseEntity.getStatusCodeValue());

        caffFile.delete();
    }

    @Test
    void testSaveFile() throws IOException {
        String caffFileName = "mycaff.caff";
        String caffFullPath = CAFF_FILES_PATH + caffFileName;
        File caffFile = new File(caffFullPath);

        MockMultipartFile file
                = new MockMultipartFile(
                "mycaff.caff",
                "caff.caff",
                MediaType.TEXT_PLAIN_VALUE,
                "CAFF CONTENT".getBytes());

        caffService.saveCaffFile(file, caffFullPath);

        assertTrue(caffFile.exists());
        assertEquals("mycaff.caff", caffFile.getName());
        assertEquals("CAFF CONTENT", FileUtils.readFileToString(caffFile, StandardCharsets.UTF_8));

        caffFile.delete();
    }

    @Test
    void testDeleteCaffAndConnectedFiles() throws IOException {
        String caffId = "MyCaffId";
        String caffFileName = caffId + ".caff";
        String bitmapFileName = caffId + ".bmp";
        String gifFileName = caffId + ".gif";

        File caff = new File(CAFF_FILES_PATH + caffFileName);
        caff.createNewFile();

        File bitmap = new File(ROOT_PATH + bitmapFileName);
        bitmap.createNewFile();

        File gif = new File(ROOT_PATH + gifFileName);
        gif.createNewFile();

        when(caffRepo.findById(caffId)).thenReturn(Optional.of(new Caff()));

        ResponseEntity<BasicStringResponseDTO> responseEntity = caffService.deleteCaffAndConnectedFiles(caffId);

        assertFalse(caff.exists());
        assertFalse(bitmap.exists());
        assertFalse(gif.exists());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Successful deletion.", Objects.requireNonNull(responseEntity.getBody()).getResponse());
    }

    @Test
    void testDeleteCaffAndConnectedFilesCaffWithNonExistingCaff() {
        String caffId = "MyCaffId";

        when(caffRepo.findById(caffId)).thenReturn(Optional.empty());

        ResponseEntity<BasicStringResponseDTO> responseEntity = caffService.deleteCaffAndConnectedFiles(caffId);


        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Caff does not exist.", Objects.requireNonNull(responseEntity.getBody()).getResponse());
    }

    @Test
    void testDeleteCaffAndConnectedFilesCaffWithNonExistingUser() throws IOException, InterruptedException {
        String caffId = "MyCaffId";
        String caffFileName = caffId + ".caff";
        String caffFullPath = CAFF_FILES_PATH + caffFileName;
        Caff caff = new Caff();
        caff.setCreatorId("NotRealUserId");

        when(caffRepo.findById(caffId)).thenReturn(Optional.of(caff));
        when(userRepo.findById(caff.getCreatorId())).thenReturn(Optional.empty());

        ResponseEntity<BasicStringResponseDTO> responseEntity = caffService.deleteCaffAndConnectedFiles(caffId);

        File caffFile = new File(caffFullPath);
        assertFalse(caffFile.exists());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Successful deletion.", Objects.requireNonNull(responseEntity.getBody()).getResponse());

    }
}
