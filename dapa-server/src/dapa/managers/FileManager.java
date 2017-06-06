package dapa.managers;

import dapa.ServerConfig;

import java.io.*;

/**
 * Created by Dev on 29.04.2017.
 */
public class FileManager {
    private static FileManager manager = new FileManager();

    public static FileManager getInstance() {
        return manager;
    }

    /**
     * Writes text to a file
     *
     * @param path Path to the file
     * @param text Text to write
     * @return true for success, false for error
     */
    public boolean writeToFile(String path, String text) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(path), "utf-8"))) {
            writer.write(text);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reads text from a file.
     *
     * @param path Path to the file.
     * @return Content of the file
     */
    public String readFromFile(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
