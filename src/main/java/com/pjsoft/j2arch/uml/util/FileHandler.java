package com.pjsoft.j2arch.uml.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * File Handler
 * 
 * Handles reading, writing, validating, and managing files and directories.
 * Includes support for temporary file management.
 */
public class FileHandler {

    /**
     * Reads the content of a file.
     * 
     * @param filePath the path to the file.
     * @return the content of the file as a string.
     * @throws IOException if the file cannot be read.
     */
    public String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    /**
     * Writes content to a file.
     * 
     * @param filePath the path to the file.
     * @param content the content to write.
     * @throws IOException if the file cannot be written.
     */
    public void writeFile(String filePath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        }
    }

    /**
     * Validates if a directory exists and is valid.
     * 
     * @param directoryPath the path to the directory.
     * @return {@code true} if the directory exists and is valid, {@code false} otherwise.
     */
    public boolean validateDirectory(String directoryPath) {
        File dir = new File(directoryPath);
        return dir.exists() && dir.isDirectory();
    }

    /**
     * Recursively collects `.java` files from a directory.
     * 
     * @param directoryPath the path to the directory.
     * @return a list of `.java` file paths.
     */
    public List<String> collectJavaFiles(String directoryPath) {
        List<String> javaFiles = new ArrayList<>();
        File directory = new File(directoryPath);
        if (directory.isDirectory()) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (file.isDirectory()) {
                    javaFiles.addAll(collectJavaFiles(file.getAbsolutePath()));
                } else if (file.getName().endsWith(".java")) {
                    javaFiles.add(file.getAbsolutePath());
                }
            }
        }
        return javaFiles;
    }

    /**
     * Creates a temporary file.
     * 
     * @param prefix the prefix for the temporary file name.
     * @param suffix the suffix for the temporary file name (e.g., ".tmp").
     * @return the path to the temporary file.
     * @throws IOException if the temporary file cannot be created.
     */
    public String createTempFile(String prefix, String suffix) throws IOException {
        return Files.createTempFile(prefix, suffix).toString();
    }

    /**
     * Creates a temporary directory.
     * 
     * @param prefix the prefix for the temporary directory name.
     * @return the path to the temporary directory.
     * @throws IOException if the temporary directory cannot be created.
     */
    public String createTempDirectory(String prefix) throws IOException {
        return Files.createTempDirectory(prefix).toString();
    }

    /**
     * Deletes a temporary file or directory.
     * 
     * @param path the path to the temporary file or directory.
     * @return {@code true} if the file or directory was deleted, {@code false} otherwise.
     */
    public boolean deleteTempFileOrDirectory(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            for (File child : Objects.requireNonNull(file.listFiles())) {
                deleteTempFileOrDirectory(child.getAbsolutePath());
            }
        }
        return file.delete();
    }
}