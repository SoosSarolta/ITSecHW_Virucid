package aut.bme.CAFFStore.service;

import aut.bme.CAFFStore.data.dto.response.BitmapResponseDTO;
import aut.bme.CAFFStore.data.dto.response.StringResponseDTO;
import aut.bme.CAFFStore.data.entity.Caff;
import aut.bme.CAFFStore.data.entity.User;
import aut.bme.CAFFStore.data.repository.CaffRepo;
import aut.bme.CAFFStore.data.repository.UserRepo;
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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    public static String getCaffFileContent(String fileName) {
        File file = new File(getCaffFilePath(fileName));
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(isr)) {

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

    public List<BitmapResponseDTO> getMultipleCaffDTOsById(List<String> ids) {
        List<Caff> caffDTOS = (List<Caff>) caffRepo.findAllById(ids);
        return caffDTOS.stream().map(BitmapResponseDTO::createCaffDTOWithoutBitmap).collect(Collectors.toList());
    }

    public ResponseEntity<StringResponseDTO> uploadCaff(MultipartFile file, String userId) throws IOException, InterruptedException {
        Caff caff = new Caff();
        caff.setOriginalFileName(file.getOriginalFilename());
        caff.setCreatorId(userId);
        caffRepo.save(caff);

        Optional<User> user = userRepo.findById(userId);
        if (user.isPresent()) {
            user.get().addCaffFile(caff);
            userRepo.save(user.get());
        } else {
            return new ResponseEntity<>(new StringResponseDTO("User does not exist!"), HttpStatus.BAD_REQUEST);
        }

        String caffPath = getCaffFilePath(caff.getId());

        checkCaffDirectory();

        saveCaffFile(file, caffPath);
        parseCaffFile(caff, caffPath);

        return new ResponseEntity<>(new StringResponseDTO(caff.getId()), HttpStatus.OK);
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
        File caffFileDir = new File(CAFF_FILE_DIR_PATH);
        if (!caffFileDir.exists()) {
            if (caffFileDir.mkdir()) {
                logger.info("Directory created: " + CAFF_FILE_DIR_PATH);
            } else {
                logger.info("Was not able to create directory: " + CAFF_FILE_DIR_PATH);
            }
        }
    }

    public ResponseEntity<byte[]> downloadCaff(String id) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

        File file = new File(getCaffFilePath(id));
        if (file.exists()) {
            try {
                zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
            } catch (IOException e) {
                logger.info("Error during put next entry to zip.");
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                logger.info("Error during creating input stream from file.");
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            try {
                IOUtils.copy(fileInputStream, zipOutputStream);
            } catch (IOException e) {
                logger.info("Error during copying file input stream to zip input stream.");
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            try {
                fileInputStream.close();
            } catch (IOException e) {
                logger.info("Error during closing file input stream.");
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            try {
                zipOutputStream.closeEntry();
            } catch (IOException e) {
                logger.info("Error during closing zip input stream.");
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        try {
            zipOutputStream.finish();
        } catch (IOException e) {
            logger.info("Error during finishing zip input stream.");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try {
            zipOutputStream.flush();
        } catch (IOException e) {
            logger.info("Error during flushing zip input stream.");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        IOUtils.closeQuietly(zipOutputStream);
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);

        return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), HttpStatus.OK);
    }

    public ResponseEntity<StringResponseDTO> deleteCaffAndConnectedFiles(String caffId) {
        String caffFileName = getCaffFilePath(caffId);
        String bitmapFileName = getBitmapFilePath(caffId);
        String gifFileName = getGifFilePath(caffId);

        Optional<Caff> caff = caffRepo.findById(caffId);
        if (caff.isEmpty()) {
            return new ResponseEntity<>(new StringResponseDTO("Caff does not exist."), HttpStatus.BAD_REQUEST);
        }

        removeCaffFromUser(caff.get());

        caffRepo.deleteById(caffId);
        logger.info("Caff deleted from caff db.");

        deleteFile(caffFileName);
        deleteFile(bitmapFileName);
        deleteFile(gifFileName);

        return new ResponseEntity<>(new StringResponseDTO("Successful deletion."), HttpStatus.OK);
    }

    private void deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            if (file.delete()) {
                logger.info("File deleted: " + file.getPath());
            } else {
                logger.info("Not able to delete file, or it does not exist: " + file.getPath());
            }
        }
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
