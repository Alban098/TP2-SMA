package engine.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {

    public static String loadResource(String fileName) throws Exception {
        Path path = Path.of("resources/" + fileName);
        return Files.readString(path);
    }

    public static List<String> readAllLines(String fileName) throws Exception {
        Path path = Path.of("resources/" + fileName);
        return Files.readAllLines(path);
    }
}