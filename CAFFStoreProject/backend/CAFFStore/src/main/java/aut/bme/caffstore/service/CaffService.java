package aut.bme.caffstore.service;

import aut.bme.caffstore.FileProcessingException;
import aut.bme.caffstore.data.dto.response.BitmapResponseDTO;
import aut.bme.caffstore.data.dto.response.CaffDetailsResponseDTO;
import aut.bme.caffstore.data.dto.response.StringResponseDTO;
import aut.bme.caffstore.data.entity.Caff;
import aut.bme.caffstore.data.entity.User;
import aut.bme.caffstore.data.repository.CaffRepo;
import aut.bme.caffstore.data.repository.UserRepo;
import com.google.common.io.ByteStreams;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static aut.bme.caffstore.Constants.*;

@Component
public class CaffService {

    private static Logger logger = LoggerFactory.getLogger(CaffService.class);

    @Autowired
    private CaffRepo caffRepo;

    @Autowired
    private UserRepo userRepo;

    public static byte[] getFileBytes(String path) {
        byte[] fileBytes = new byte[0];
        File file = new File(path);
        if (file.exists()) {
            logger.info("File exist: {}", path);
            InputStream targetStream;
            try {
                targetStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new FileProcessingException(e);
            }
            try {
                fileBytes = ByteStreams.toByteArray(targetStream);
            } catch (IOException e) {
                throw new FileProcessingException(e);
            }
        } else {
            logger.info("File does not exist: {}", path);
        }
        return fileBytes;
    }

    public List<BitmapResponseDTO> getMultipleCaffDTOsById(List<String> ids) {
        List<Caff> caffDTOS = (List<Caff>) caffRepo.findAllById(ids);
        return caffDTOS.stream().map(BitmapResponseDTO::createCaffDTOWithoutBitmap).collect(Collectors.toList());
    }

    public ResponseEntity<StringResponseDTO> uploadCaff(MultipartFile file, String userId) throws IOException, InterruptedException {
        Caff caff = new Caff();
        caff.setOriginalFileName(file.getOriginalFilename());
        caff.setCreatorId(userId);
        caff = caffRepo.save(caff);

        String caffPath = getCaffFilePath(caff.getId());

        Optional<User> user = userRepo.findById(userId);
        if (user.isPresent()) {
            user.get().addCaffFile(caff);
            userRepo.save(user.get());
        } else {
            return new ResponseEntity<>(new StringResponseDTO("User does not exist!"), HttpStatus.BAD_REQUEST);
        }

        checkCaffDirectory();

        saveCaffFile(file, caffPath);
        parseCaffFile(caff.getId(), caffPath);

        return checkBitmapAndGifFileExistence(caff.getId());
    }

    private ResponseEntity<StringResponseDTO> checkBitmapAndGifFileExistence(String id) {
        File bitmapFile = new File(getBitmapFilePath(id));
        File gifFile = new File(getGifFilePath(id));
        if (!bitmapFile.exists() || !gifFile.exists()) {

            deleteFile(getBitmapFilePath(id));
            deleteFile(getGifFilePath(id));
            deleteFile(getCaffFilePath(id));

            caffRepo.findById(id).ifPresent(this::removeCaffFromUser);
            caffRepo.deleteById(id);

            return new ResponseEntity<>(new StringResponseDTO("Uploaded caff file is invalid."),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new StringResponseDTO(id), HttpStatus.OK);
    }

    public void saveCaffFile(MultipartFile file, String caffFullPath) throws IOException {
        File caffFile = new File(caffFullPath);
        if (caffFile.createNewFile()) {
            try (OutputStream os = new FileOutputStream(caffFile)) {
                os.write(file.getBytes());
            }
            logger.info("File created: {}", caffFullPath);
        }
    }

    public ResponseEntity<byte[]> downloadCaff(String id) throws IOException {
        byte[] emptyResponse = new byte[0];

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

        File file = new File(getCaffFilePath(id));
        if (file.exists()) {
            zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
            FileInputStream fileInputStream = new FileInputStream(file);

            IOUtils.copy(fileInputStream, zipOutputStream);

            fileInputStream.close();
            zipOutputStream.closeEntry();
        } else {
            logger.info("File does not exist.");
            return new ResponseEntity<>(emptyResponse, HttpStatus.BAD_REQUEST);
        }

        zipOutputStream.finish();
        zipOutputStream.flush();

        IOUtils.closeQuietly(zipOutputStream);
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);

        return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), HttpStatus.OK);
    }

    public ResponseEntity<StringResponseDTO> deleteCaffAndConnectedFiles(String caffId) {
        String caffFilePath = getCaffFilePath(caffId);
        String bitmapFilePath = getBitmapFilePath(caffId);
        String gifFilePath = getGifFilePath(caffId);

        Optional<Caff> caff = caffRepo.findById(caffId);
        if (caff.isEmpty()) {
            return new ResponseEntity<>(new StringResponseDTO("Caff does not exist."), HttpStatus.BAD_REQUEST);
        }

        removeCaffFromUser(caff.get());

        caffRepo.deleteById(caffId);
        logger.info("Caff deleted from caff db.");

        deleteFile(caffFilePath);
        deleteFile(bitmapFilePath);
        deleteFile(gifFilePath);

        return new ResponseEntity<>(new StringResponseDTO("Successful deletion."), HttpStatus.OK);
    }

    private void parseCaffFile(String caffId, String caffFullPath) throws IOException, InterruptedException {
        if (caffId != null) {
            try {
                Process process = Runtime.getRuntime().exec("cmd /c start /B /wait "
                        + BASE_PATH
                        + "/ciff_caff_parser.exe "
                        + caffFullPath + " "
                        + caffId
                        + " || exit /b");
                logger.info("Waiting for parser to finish...");
                process.waitFor();
                logger.info("Parser finished.");
            } catch (Exception e) {
                logger.info("An error occurred during parsing the caff file.");
            }
        }
    }

    private void checkCaffDirectory() {
        File caffFileDir = new File(CAFF_FILE_DIR_PATH);
        if (!caffFileDir.exists()) {
            if (caffFileDir.mkdir()) {
                logger.info("Directory created: {}", CAFF_FILE_DIR_PATH);
            } else {
                logger.info("Was not able to create directory: {}", CAFF_FILE_DIR_PATH);
            }
        }
    }

    public void deleteFile(String filePath) {
        try {
            Files.delete(Path.of(filePath));
        } catch (NoSuchFileException e) {
            logger.info("File does not exist: {}", filePath);
        } catch (IOException e) {
            logger.info("Not able to delete file: {}", filePath);
        }
    }

    private void removeCaffFromUser(Caff caff) {
        Optional<User> user = userRepo.findById(caff.getCreatorId());
        if (user.isPresent()) {
            user.get().removeCaff(caff);
            logger.info("Caff file name removed from user.");
            userRepo.save(user.get());
        } else {
            logger.info("User does not exist.");
        }
    }

    public ResponseEntity<List<BitmapResponseDTO>> getAllCaffs() {
        return new ResponseEntity<>(BitmapResponseDTO.createCaffDTOs(caffRepo.findAll()), HttpStatus.OK);
    }

    public ResponseEntity<CaffDetailsResponseDTO> getCaffDetailsById(String id) {
        Optional<Caff> caff = caffRepo.findById(id);
        return caff.map(value -> new ResponseEntity<>(CaffDetailsResponseDTO.createCaffDetailsDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(new CaffDetailsResponseDTO(), HttpStatus.BAD_REQUEST));
    }
}
