package aut.bme.caffstore.unittests;

import aut.bme.caffstore.data.dto.response.BitmapResponseDTO;
import aut.bme.caffstore.data.dto.response.CaffDetailsResponseDTO;
import aut.bme.caffstore.data.dto.response.StringResponseDTO;
import aut.bme.caffstore.data.entity.Caff;
import aut.bme.caffstore.data.entity.User;
import aut.bme.caffstore.data.repository.CaffRepo;
import aut.bme.caffstore.data.repository.UserRepo;
import aut.bme.caffstore.service.CaffService;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static aut.bme.caffstore.Constants.*;
import static aut.bme.caffstore.UnitTestConstants.getTestCaffFilePath;
import static aut.bme.caffstore.service.CaffService.getFileBytes;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class CaffServiceTest {

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
        Caff caffWithId = new Caff();
        caffWithId.setId("validCaffFile");

        MockMultipartFile file
                = new MockMultipartFile(
                "caffFile",
                "caff.caff",
                MediaType.TEXT_PLAIN_VALUE,
                "CAFF CONTENT".getBytes());

        String userId = "MyUserId";

        when(userRepo.findById(userId)).thenReturn(Optional.empty());
        when(caffRepo.save(any(Caff.class))).thenReturn(caffWithId);

        ResponseEntity<StringResponseDTO> responseEntity = caffService.uploadCaff(file, userId);

        assertEquals("User does not exist!", Objects.requireNonNull(responseEntity.getBody()).getResponse());
    }

    @Test
    void testUploadWithValidCaffFile() throws IOException, InterruptedException {
        String caffTestFullPath = getTestCaffFilePath("validCaffFile");
        String userId = "MyUserId";
        User user = new User();

        Caff caffWithId = new Caff();
        caffWithId.setOriginalFileName("validCaffFile.caff");
        caffWithId.setCreatorId(userId);
        caffWithId.setId("validCaffFile");

        MockMultipartFile multipartFile
                = new MockMultipartFile(
                "caffFile",
                "validCaffFile.caff",
                MediaType.TEXT_PLAIN_VALUE,
                getFileBytes(caffTestFullPath));


        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(caffRepo.save(any(Caff.class))).thenReturn(caffWithId);

        ResponseEntity<StringResponseDTO> responseEntity = caffService.uploadCaff(multipartFile, userId);

        String responseCaffId = getCaffFilePath(Objects.requireNonNull(responseEntity.getBody()).getResponse());

        assertTrue(new File(responseCaffId).exists());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(200, responseEntity.getStatusCodeValue());

        caffService.deleteFile(getCaffFilePath("validCaffFile"));
        caffService.deleteFile(getBitmapFilePath("validCaffFile"));
        caffService.deleteFile(getGifFilePath("validCaffFile"));
    }


    @Test
    void testUploadWithInvalidCaffFile() throws IOException, InterruptedException {
        String caffTestFullPath = getTestCaffFilePath("invalidCaffFile");
        String userId = "MyUserId";
        User user = new User();

        Caff caffWithId = new Caff();
        caffWithId.setOriginalFileName("invalidCaffFile.caff");
        caffWithId.setCreatorId(userId);
        caffWithId.setId("invalidCaffFile");

        MockMultipartFile multipartFile
                = new MockMultipartFile(
                "caffFile",
                "invalidCaffFile.caff",
                MediaType.TEXT_PLAIN_VALUE,
                getFileBytes(caffTestFullPath));


        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(caffRepo.save(any(Caff.class))).thenReturn(caffWithId);

        ResponseEntity<StringResponseDTO> responseEntity = caffService.uploadCaff(multipartFile, userId);

        String responseCaffId = getCaffFilePath(Objects.requireNonNull(responseEntity.getBody()).getResponse());

        assertFalse(new File(responseCaffId).exists());

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(400, responseEntity.getStatusCodeValue());
        assertEquals("Uploaded caff file is invalid.", responseEntity.getBody().getResponse());

        caffService.deleteFile(getCaffFilePath("validCaffFile"));
        caffService.deleteFile(getBitmapFilePath("validCaffFile"));
        caffService.deleteFile(getGifFilePath("validCaffFile"));
    }

    @Test
    void testSaveFile() throws IOException {
        String caffFullPath = getCaffFilePath("mycaff");
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
        String caffFileName = getCaffFilePath(caffId);
        String bitmapFileName = getBitmapFilePath(caffId);
        String gifFileName = getGifFilePath(caffId);

        File caff = new File(caffFileName);
        caff.createNewFile();

        File bitmap = new File(bitmapFileName);
        bitmap.createNewFile();

        File gif = new File(gifFileName);
        gif.createNewFile();

        when(caffRepo.findById(caffId)).thenReturn(Optional.of(new Caff()));

        ResponseEntity<StringResponseDTO> responseEntity = caffService.deleteCaffAndConnectedFiles(caffId);

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

        ResponseEntity<StringResponseDTO> responseEntity = caffService.deleteCaffAndConnectedFiles(caffId);


        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Caff does not exist.", Objects.requireNonNull(responseEntity.getBody()).getResponse());
    }

    @Test
    void testDeleteCaffAndConnectedFilesCaffWithNonExistingUser() {
        String caffId = "MyCaffId";
        String caffFullPath = getCaffFilePath(caffId);
        Caff caff = new Caff();
        caff.setCreatorId("NotRealUserId");

        when(caffRepo.findById(caffId)).thenReturn(Optional.of(caff));
        when(userRepo.findById(caff.getCreatorId())).thenReturn(Optional.empty());

        ResponseEntity<StringResponseDTO> responseEntity = caffService.deleteCaffAndConnectedFiles(caffId);

        File caffFile = new File(caffFullPath);
        assertFalse(caffFile.exists());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Successful deletion.", Objects.requireNonNull(responseEntity.getBody()).getResponse());
    }

    @Test
    void testGetAllCaffs() {
        Caff caff = new Caff();
        caff.setId("CaffId");
        caff.setOriginalFileName("Original");

        when(caffRepo.findAll()).thenReturn(List.of(caff, caff));

        ResponseEntity<List<BitmapResponseDTO>> responseEntity = caffService.getAllCaffs();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(2, Objects.requireNonNull(responseEntity.getBody()).size());
        assertEquals(BitmapResponseDTO.createCaffDTOs(List.of(caff, caff)), responseEntity.getBody());
    }

    @Test
    void testGetCaffDetailsById() {
        Caff caff = new Caff();
        caff.setId("CaffId");
        caff.setOriginalFileName("Original");
        caff.setCreatorId("Creator");

        when(caffRepo.findById(anyString())).thenReturn(Optional.of(caff));

        ResponseEntity<CaffDetailsResponseDTO> responseEntity = caffService.getCaffDetailsById("CaffId");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(CaffDetailsResponseDTO.createCaffDetailsDTO(caff), responseEntity.getBody());
    }

    @Test
    void testGetCaffDetailsByIdWithWrongId() {
        Caff caff = new Caff();
        caff.setId("CaffId");
        caff.setOriginalFileName("Original");
        caff.setCreatorId("Creator");

        when(caffRepo.findById(anyString())).thenReturn(Optional.empty());

        ResponseEntity<CaffDetailsResponseDTO> responseEntity = caffService.getCaffDetailsById("CaffId");

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void testDownloadCaff() throws IOException {
        String caffFileName = getCaffFilePath("validCaffFile");

        File caff = new File(caffFileName);
        caff.createNewFile();

        ResponseEntity<byte[]> responseEntity = caffService.downloadCaff("validCaffFile");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        caff.delete();
    }

    @Test
    void testDownloadCaffWithNotExistingCaff() throws IOException {
        ResponseEntity<byte[]> responseEntity = caffService.downloadCaff("notExistingCaffFile");

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

}
