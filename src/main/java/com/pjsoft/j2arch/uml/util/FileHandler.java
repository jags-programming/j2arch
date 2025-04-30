package com.pjsoft.j2arch.uml.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * FileHandler
 * 
 * Provides utility methods for file and directory operations, including:
 * - Reading and writing file content.
 * - Validating directories.
 * - Recursively collecting `.java` files from directories.
 * - Managing temporary files and directories.
 * - Deleting files or directories recursively.
 * 
 * Responsibilities:
 * - Simplifies common file and directory operations.
 * - Ensures proper resource management using try-with-resources.
 * - Supports temporary file and directory creation for intermediate operations.
 * 
 * Limitations:
 * - Assumes valid input paths for most operations.
 * - Does not handle advanced file system operations (e.g., symbolic links).
 * 
 * Thread Safety:
 * - This class is not thread-safe. Concurrent access to the same file or directory
 *   may lead to unexpected behavior.
 * 
 * @author PJSoft
 * @version 2.2
 * @since 1.0
 */
public class FileHandler {

    /**
     * Reads the content of a file.
     * 
     * Responsibilities:
     * - Opens the file at the specified path.
     * - Reads its content line by line.
     * - Returns the content as a single string.
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
     * Responsibilities:
     * - Opens or creates the file at the specified path.
     * - Writes the provided content to the file.
     * - Overwrites the file if it already exists.
     * 
     * @param filePath the path to the file.
     * @param content  the content to write.
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
     * Responsibilities:
     * - Checks if the directory exists.
     * - Verifies that the path points to a directory (not a file).
     * 
     * @param directoryPath the path to the directory.
     * @return {@code true} if the directory exists and is valid, {@code false}
     *         otherwise.
     */
    public boolean validateDirectory(String directoryPath) {
        File dir = new File(directoryPath);
        return dir.exists() && dir.isDirectory();
    }

    /**
     * Recursively collects `.java` files from a directory.
     * 
     * Responsibilities:
     * - Traverses the directory structure recursively.
     * - Collects all `.java` files found in the directory and its subdirectories.
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
     * Responsibilities:
     * - Creates a temporary file with the specified prefix and suffix.
     * - Returns the path to the created file.
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
     * Responsibilities:
     * - Creates a temporary directory with the specified prefix.
     * - Returns the path to the created directory.
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
     * Responsibilities:
     * - Deletes the specified file or directory.
     * - If the path points to a directory, deletes all its contents recursively.
     * 
     * @param path the path to the temporary file or directory.
     * @return {@code true} if the file or directory was deleted, {@code false}
     *         otherwise.
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