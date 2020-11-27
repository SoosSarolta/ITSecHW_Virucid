package aut.bme.CAFFStore.service;

import aut.bme.CAFFStore.data.dto.BasicStringResponseDTO;
import aut.bme.CAFFStore.data.dto.CaffDTO;
import aut.bme.CAFFStore.data.dto.CaffDownloadDTO;
import aut.bme.CAFFStore.data.entity.Caff;
import aut.bme.CAFFStore.data.entity.User;
import aut.bme.CAFFStore.data.repository.CaffRepo;
import aut.bme.CAFFStore.data.repository.UserRepo;
import com.google.common.io.ByteStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static aut.bme.CAFFStore.Constants.*;

@Component
public class CaffService {

    static Logger logger = LoggerFactory.getLogger(CaffService.class);

    @Autowired
    private CaffRepo caffRepo;

    @Autowired
    private UserRepo userRepo;

    public static byte[] getFileBytes(String path) {
        byte[] fileBytes = new byte[0];
        File file = new File(path);
        if (file.exists()) {
            logger.info("File exist: " + path);
            InputStream targetStream;
            try {
                targetStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                fileBytes = ByteStreams.toByteArray(targetStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            logger.info("File does not exist: " + path);
        }
        return fileBytes;
    }

    public static String getFileContent(String path) {
        File file = new File(path);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(isr)
            ) {

                StringBuilder stringBuilder = new StringBuilder();
                String str;
                while ((str = reader.readLine()) != null) {
                    stringBuilder.append(str);
                }
                return stringBuilder.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public List<CaffDTO> getMultipleCaffDTOsById(List<String> ids) {
        List<Caff> caffDTOS = (List<Caff>) caffRepo.findAllById(ids);
        return caffDTOS.stream().map(CaffDTO::createCaffDTOWithoutBitmap).collect(Collectors.toList());
    }

    public ResponseEntity<BasicStringResponseDTO> uploadCaff(MultipartFile file, String userId) throws IOException, InterruptedException {
        Caff caff = new Caff();
        caff.setOriginalFileName(file.getOriginalFilename());
        caff.setCreatorId(userId);
        caffRepo.save(caff);

        Optional<User> user = userRepo.findById(userId);
        if (user.isPresent()) {
            user.get().addCaffFile(caff);
            userRepo.save(user.get());
        } else {
            return new ResponseEntity<>(new BasicStringResponseDTO("User does not exist!"), HttpStatus.BAD_REQUEST);
        }

        String caffFileName = caff.getId() + ".caff";
        String caffFullPath = CAFF_FILES_PATH + caffFileName;

        checkCaffDirectory();

        saveCaffFile(file, caffFullPath);
        parseCaffFile(caff, caffFullPath);

        return new ResponseEntity<>(new BasicStringResponseDTO(caff.getId()), HttpStatus.OK);
    }

    public void saveCaffFile(MultipartFile file, String caffFullPath) throws IOException {
        File caffFile = new File(caffFullPath);
        if (caffFile.createNewFile()) {
            try (OutputStream os = new FileOutputStream(caffFile)) {
                os.write(file.getBytes());
            }
            logger.info("File created: " + caffFullPath);
        }


    }

    private void parseCaffFile(Caff caff, String caffFullPath) throws IOException, InterruptedException {
        if (caff.getId() != null) {
            Process process = Runtime.getRuntime().exec("cmd /c start /wait "
                    + BASE_PATH
                    + "/ciff_caff_parser.exe "
                    + caffFullPath + " "
                    + caff.getId());
            logger.info("Waiting for parser to finish...");
            process.waitFor();
            logger.info("Parser finished successfully.");
        }
    }

    private void checkCaffDirectory() {
        File caffFileDir = new File(CAFF_FILES_DIR_PATH);
        if (!caffFileDir.exists()) {
            if (caffFileDir.mkdir()) {
                logger.info("Directory created: " + BASE_PATH);
            } else {
                logger.info("Was not able to create directory: " + BASE_PATH);
            }
        }
    }

    public ResponseEntity<CaffDownloadDTO> downloadCaff(String id) {
        Optional<Caff> caff = caffRepo.findById(id);
        if (caff.isPresent()) {
            CaffDownloadDTO caffDownloadDTO = CaffDownloadDTO.createCaffDownloadDTO(caff.get());
            if (caffDownloadDTO.getCaffFile().isEmpty()) {
                return new ResponseEntity<>(null,
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(caffDownloadDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<BasicStringResponseDTO> deleteCaffAndConnectedFiles(String caffId) {
        String caffFileName = caffId + ".caff";
        String bitmapFileName = caffId + ".bmp";
        String gifFileName = caffId + ".gif";

        Optional<Caff> caff = caffRepo.findById(caffId);
        if (caff.isEmpty()) {
            return new ResponseEntity<>(new BasicStringResponseDTO("Caff does not exist."), HttpStatus.BAD_REQUEST);
        }

        removeCaffFromUser(caff.get());

        caffRepo.deleteById(caffId);
        logger.info("Caff deleted from caff db.");

        File caffFile = new File(CAFF_FILES_PATH + caffFileName);
        if (caffFile.exists()) {
            if (caffFile.delete()) {
                logger.info("Caff deleted: " + caffFile.getPath());
            } else {
                logger.info("Not able to delete caff, or it does not exist: " + caffFile.getPath());
            }
        }

        File bitmapFile = new File(ROOT_PATH + bitmapFileName);
        if (bitmapFile.exists()) {
            if (bitmapFile.delete()) {
                logger.info("Bitmap deleted: " + bitmapFile.getPath());
            } else {
                logger.info("Not able to delete bitmap, or it does not exist: " + bitmapFile.getPath());
            }
        }

        File gifFile = new File(ROOT_PATH + gifFileName);
        if (gifFile.exists()) {
            if (gifFile.delete()) {
                logger.info("Gif deleted: " + gifFile.getPath());
            } else {
                logger.info("Not able to delete gif, or it does not exist: " + gifFile.getPath());
            }
        }

        return new ResponseEntity<>(new BasicStringResponseDTO("Successful deletion."), HttpStatus.OK);
    }

    private void removeCaffFromUser(Caff caff) {
        Optional<User> user = userRepo.findById(caff.getCreatorId());
        if (user.isPresent()) {
            user.get().removeCaffFileName(caff);
            logger.info("Caff file name removed from user.");
            userRepo.save(user.get());
        } else {
            logger.info("User does not exist.");
        }
    }
}
