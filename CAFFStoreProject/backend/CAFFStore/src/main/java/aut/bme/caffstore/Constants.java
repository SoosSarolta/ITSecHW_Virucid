package aut.bme.caffstore;

public class Constants {

    public static final String BASE_PATH = "src/main/resources";
    public static final String CAFF_FILE_DIR_PATH = "src/main/resources/CaffFiles/";
    private static final String CAFF_FILE_PATH = "src/main/resources/CaffFiles/";
    private static final String ROOT_PATH = "";

    private Constants() {
    }

    public static String getBasePath() {
        return BASE_PATH;
    }

    public static String getCaffFilePath(String fileName) {
        return CAFF_FILE_PATH + fileName + ".caff";
    }

    public static String getBitmapFilePath(String fileName) {
        return ROOT_PATH + fileName + ".bmp";
    }

    public static String getGifFilePath(String fileName) {
        return ROOT_PATH + fileName + ".gif";
    }
}
