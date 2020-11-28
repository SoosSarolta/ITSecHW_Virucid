package aut.bme.caffstore;

import java.io.IOException;

public class FileProcessingException extends RuntimeException {

    public FileProcessingException(IOException errorMessage) {
        super(errorMessage);
    }
}
