package aut.bme.CAFFStore.service;

import aut.bme.CAFFStore.data.entity.Caff;
import aut.bme.CAFFStore.data.entity.User;
import aut.bme.CAFFStore.data.repository.CaffRepo;
import aut.bme.CAFFStore.data.repository.UserRepo;
import com.google.common.io.ByteStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Optional;

import static aut.bme.CAFFStore.Constants.BASE_PATH;
import static aut.bme.CAFFStore.Constants.CAFF_FILES_BASE_PATH;

@Component
public class CaffService {

    Logger logger = LoggerFactory.getLogger(CaffService.class);

    @Autowired
    private CaffRepo caffRepo;

    @Autowired
    private UserRepo userRepo;

    public static byte[] getFileBytes(String fileName, String fileFormat) {
        byte[] fileBytes = new byte[0];
        File bitmapFile = new File(BASE_PATH + fileName + fileFormat);
        if (bitmapFile.exists()) {
            InputStream targetStream = null;
            try {
                targetStream = new FileInputStream(bitmapFile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                fileBytes = ByteStreams.toByteArray(targetStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return fileBytes;
    }

    public String uploadCaff(MultipartFile file, String userId) throws IOException, InterruptedException {
        Caff caff = new Caff();
        caffRepo.save(caff);

        String caffFileName = caff.getId() + ".caff";
        String caffFullPath = CAFF_FILES_BASE_PATH + "/" + caffFileName;

        File caffFileDir = new File(BASE_PATH);
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

        Optional<User> user = userRepo.findById(userId);
        if (user.isPresent()) {
            user.get().addCaffFile(file.getOriginalFilename());
        } else {
            throw new RuntimeException("User does not exist!");
        }

        return caff.getId();
    }

    public byte[] downloadCaff(String caffId) throws IOException {
        String caffFileName = caffId + ".caff";
        String caffFullPath = CAFF_FILES_BASE_PATH + "/" + caffFileName;
        File caffFile = new File(caffFullPath);
        if (caffFile.exists()) {
            logger.info(caffFullPath + " exists.");
            InputStream targetStream = new FileInputStream(caffFile);
            return ByteStreams.toByteArray(targetStream);
        }
        return null;
    }
}
