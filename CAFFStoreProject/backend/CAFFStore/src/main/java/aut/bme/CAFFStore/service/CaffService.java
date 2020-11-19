package aut.bme.CAFFStore.service;

import aut.bme.CAFFStore.data.dto.CaffDTO;
import aut.bme.CAFFStore.data.dto.CaffIdResponseEntity;
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

    public static byte[] getFileBytes(String fileName, String fileFormat, String basePath) {
        byte[] fileBytes = new byte[0];
        File file = new File(basePath + fileName + fileFormat);
        if (file.exists()) {
            logger.info("File exist: " + basePath + fileName + fileFormat);
            InputStream targetStream = null;
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
            logger.info("File does not exist: " + basePath + fileName + fileFormat);
        }
        return fileBytes;
    }

    public List<CaffDTO> getMultipleCaffDTOsById(List<String> ids) {
        List<Caff> caffDTOS = (List<Caff>) caffRepo.findAllById(ids);
        return caffDTOS.stream().map(CaffDTO::createCaffDTOWithoutBitmap).collect(Collectors.toList());
    }

    public ResponseEntity<CaffIdResponseEntity> uploadCaff(MultipartFile file, String userId) throws IOException, InterruptedException {
        Caff caff = new Caff();
        caff.setOriginalFileName(file.getOriginalFilename());
        caff.setCreatorId(userId);
        caffRepo.save(caff);

        String caffFileName = caff.getId() + ".caff";
        String caffFullPath = CAFF_FILES_PATH + caffFileName;

        Optional<User> user = userRepo.findById(userId);
        if (user.isPresent()) {
            user.get().addCaffFile(caff);
            userRepo.save(user.get());
        } else {
            return new ResponseEntity<>(new CaffIdResponseEntity("User does not exist!"), HttpStatus.BAD_REQUEST);
        }

        File caffFileDir = new File(CAFF_FILES_DIR_PATH);
        if (!caffFileDir.exists()) {
            caffFileDir.mkdir();
            logger.info("Directory created: " + BASE_PATH);
        }

        File caffFile = new File(caffFullPath);
        if (caffFile.createNewFile()) {
            try (OutputStream os = new FileOutputStream(caffFile)) {
                os.write(file.getBytes());
            }
            logger.info("File created: " + caffFullPath);
            logger.info("File size: " + caffFile.length());
        }

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

        return new ResponseEntity<>(new CaffIdResponseEntity(caff.getId()), HttpStatus.OK);
    }

    public ResponseEntity<CaffDTO> downloadCaff(String id) {
        Optional<Caff> caff = caffRepo.findById(id);
        return caff.map(value -> new ResponseEntity<>(CaffDTO.createCaffDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
    }

    public ResponseEntity<String> deleteCaffAndConnectedFiles(String caffId) {
        String caffFileName = caffId + ".caff";
        String bitmapFileName = caffId + ".bmp";
        String gifFileName = caffId + ".gif";

        Optional<Caff> caff = caffRepo.findById(caffId);
        if (caff.isEmpty()) {
            return new ResponseEntity<>("Caff does not exist.", HttpStatus.BAD_REQUEST);
        }

        Optional<User> user = userRepo.findById(caff.get().getCreatorId());
        if (user.isEmpty()) {
            user.get().removeCaffFileName(caff.get());
            logger.info("Caff file name removed from user.");
            userRepo.save(user.get());
        } else {
            logger.info("User does not exist.");
        }


        caffRepo.deleteById(caffId);
        logger.info("Caff deleted from caff db.");

        File caffFile = new File(CAFF_FILES_PATH + caffFileName);
        if (caffFile.exists()) {
            caffFile.delete();
        }

        File bitmapFile = new File(ROOT_PATH + bitmapFileName);
        if (bitmapFile.exists()) {
            bitmapFile.delete();
        }

        File gifFile = new File(ROOT_PATH + gifFileName);
        if (gifFile.exists()) {
            gifFile.delete();
        }

        return new ResponseEntity<>("Successful deletion.", HttpStatus.OK);
    }
}