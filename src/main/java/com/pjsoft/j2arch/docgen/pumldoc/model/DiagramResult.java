package com.pjsoft.j2arch.docgen.pumldoc.model;

/**
 * DiagramResult
 * 
 * Represents the result of processing a PlantUML diagram. This class encapsulates
 * details about the processing status, including the filename, base filename,
 * success status, a descriptive message, and metadata about the diagram.
 * 
 * Responsibilities:
 * - Stores the result of processing a `.puml` file.
 * - Provides access to metadata about the processed diagram.
 * 
 * Dependencies:
 * - {@link DiagramInfo}: Represents metadata about the diagram.
 * 
 * Limitations:
 * - Assumes that the provided inputs (e.g., filename, message) are valid and non-null.
 * - Does not perform validation on the content of the diagram.
 * 
 * Thread Safety:
 * - This class is immutable and therefore thread-safe.
 * 
 * Usage Example:
 * {@code
 * DiagramResult result = new DiagramResult(
 *     "example.puml",
 *     "example",
 *     true,
 *     "Diagram generated successfully",
 *     new DiagramInfo(...)
 * );
 * if (result.isSuccess()) {
 *     System.out.println("Success: " + result.getMessage());
 * }
 * }
 * 
 * Author: PJSoft
 * Version: 2.2
 * Since: 1.0
 */
public class DiagramResult {
    private final String filename; // The name of the processed file
    private final String baseFilename; // The base name of the file without extension
    private final boolean success; // Indicates whether the processing was successful
    private final String message; // A message describing the result of the processing
    private final DiagramInfo diagramInfo; // Metadata about the processed diagram

    /**
     * Constructs a new DiagramResult object.
     * 
     * Responsibilities:
     * - Initializes the result of processing a `.puml` file.
     * 
     * @param filename The name of the file.
     * @param baseFilename The base name of the file without extension.
     * @param success The success status of the diagram processing.
     * @param message The message describing the result.
     * @param diagramInfo The information about the diagram.
     */
    public DiagramResult(String filename, String baseFilename, boolean success, String message, DiagramInfo diagramInfo) {
        this.filename = filename;
        this.baseFilename = baseFilename;
        this.success = success;
        this.message = message;
        this.diagramInfo = diagramInfo;
    }

    /**
     * Gets the name of the file.
     * 
     * @return The name of the file.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Gets the base name of the file without extension.
     * 
     * @return The base name of the file without extension.
     */
    public String getBaseFilename() {
        return baseFilename;
    }

    /**
     * Gets the success status of the diagram processing.
     * 
     * @return The success status of the diagram processing.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Gets the message describing the result.
     * 
     * @return The message describing the result.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the information about the diagram.
     * 
     * @return The information about the diagram.
     */
    public DiagramInfo getDiagramInfo() {
        return diagramInfo;
    }
}
