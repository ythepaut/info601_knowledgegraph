package utils;

import java.io.*;

public abstract class FileManager {

    public static String readFile(String path) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(path));
        String currentLine;
        StringBuilder total = new StringBuilder();
        while ((currentLine = reader.readLine()) != null) {
            total.append(currentLine);
        }

        return total.toString();
    }

    public static void writeFile(String path, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(content);

        writer.close();
    }

}
