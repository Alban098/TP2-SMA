package engine.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Utils {

    /**
     * Return the content of a file as a String
     * @param fileName the path of the file to load
     * @return the content of the file as a String
     * @throws Exception thrown if the file doesn't exist or can't be opened
     */
    public static String loadResource(String fileName) throws Exception {
        Path path = Path.of("resources/" + fileName);
        return Files.readString(path);
    }
    /**
     * Return the content of a file as a String array
     * @param fileName the path of the file to load
     * @return the content of the file as a String array
     * @throws Exception thrown if the file doesn't exist or can't be opened
     */
    public static List<String> readAllLines(String fileName) throws Exception {
        Path path = Path.of("resources/" + fileName);
        return Files.readAllLines(path);
    }
}