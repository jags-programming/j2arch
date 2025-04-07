package com.pjsoft.j2arch.docgen.model;

/**
 * DiagramResult represents the result of processing a PlantUML diagram.
 * It includes the filename, base filename, success status, message, and diagram information.
 */
public class DiagramResult {
    private final String filename;
    private final String baseFilename;
    private final boolean success;
    private final String message;
    private final DiagramInfo diagramInfo;

    /**
     * Constructs a new DiagramResult object.
     *
     * @param filename The name of the file
     * @param baseFilename The base name of the file without extension
     * @param success The success status of the diagram processing
     * @param message The message describing the result
     * @param diagramInfo The information about the diagram
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
     * @return The name of the file
     */
    public String getFilename() { return filename; }

    /**
     * Gets the base name of the file without extension.
     *
     * @return The base name of the file without extension
     */
    public String getBaseFilename() { return baseFilename; }

    /**
     * Gets the success status of the diagram processing.
     *
     * @return The success status of the diagram processing
     */
    public boolean isSuccess() { return success; }

    /**
     * Gets the message describing the result.
     *
     * @return The message describing the result
     */
    public String getMessage() { return message; }

    /**
     * Gets the information about the diagram.
     *
     * @return The information about the diagram
     */
    public DiagramInfo getDiagramInfo() { return diagramInfo; }
}
