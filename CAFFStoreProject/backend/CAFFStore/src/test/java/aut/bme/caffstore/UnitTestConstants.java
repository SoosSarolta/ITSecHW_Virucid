package aut.bme.caffstore;

public class UnitTestConstants {

    public static final String BASE_PATH = "src/test/java/aut/bme/CAFFStore/resources/resources";
    public static final String CAFF_FILE_DIR_PATH = "src/test/java/aut/bme/CAFFStore/resources/CaffFiles/";
    private static final String CAFF_FILE_PATH = "src/test/java/aut/bme/CAFFStore/resources/CaffFiles/";
    private static final String ROOT_PATH = "";


    public static String getTestBasePath() {
        return BASE_PATH;
    }

    public static String getTestCaffFilePath(String fileName) {
        return CAFF_FILE_PATH + fileName + ".caff";
    }

    public static String getTestBitmapFilePath(String fileName) {
        return ROOT_PATH + fileName + ".bmp";
    }

    public static String getTestGifFilePath(String fileName) {
        return ROOT_PATH + fileName + ".gif";
    }
}
