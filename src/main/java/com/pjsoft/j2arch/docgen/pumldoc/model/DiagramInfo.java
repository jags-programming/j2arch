package com.pjsoft.j2arch.docgen.pumldoc.model;

/**
 * DiagramInfo
 * 
 * Represents the information about a PlantUML diagram. This class encapsulates
 * metadata about the diagram, including its unique identifier, title, description,
 * and the path to the generated image.
 * 
 * Responsibilities:
 * - Stores metadata about a PlantUML diagram.
 * - Provides access to the diagram's ID, title, description, and image path.
 * 
 * Limitations:
 * - Assumes that the provided inputs (e.g., ID, title, description, image path) are valid and non-null.
 * - Does not validate the correctness of the image path or other metadata.
 * 
 * Thread Safety:
 * - This class is immutable and therefore thread-safe.
 * 
 * Usage Example:
 * {@code
 * DiagramInfo diagramInfo = new DiagramInfo(
 *     "diagram1",
 *     "Class Diagram",
 *     "This is a class diagram showing relationships between classes.",
 *     "/images/diagram1.svg"
 * );
 * System.out.println("Diagram Title: " + diagramInfo.getTitle());
 * }
 * 
 * Author: PJSoft
 * Version: 2.2
 * Since: 1.0
 */
public class DiagramInfo {
    private final String id; // The unique identifier for the diagram
    private final String title; // The title of the diagram
    private final String description; // The description of the diagram
    private final String imagePath; // The path to the generated image of the diagram

    /**
     * Constructs a new DiagramInfo object.
     * 
     * Responsibilities:
     * - Initializes metadata about a PlantUML diagram.
     * 
     * @param id          The unique identifier for the diagram.
     * @param title       The title of the diagram.
     * @param description The description of the diagram.
     * @param imagePath   The path to the generated image of the diagram.
     */
    public DiagramInfo(String id, String title, String description, String imagePath) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
    }

    /**
     * Gets the unique identifier for the diagram.
     * 
     * @return The unique identifier for the diagram.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the title of the diagram.
     * 
     * @return The title of the diagram.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the description of the diagram.
     * 
     * @return The description of the diagram.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the path to the generated image of the diagram.
     * 
     * @return The path to the generated image of the diagram.
     */
    public String getImagePath() {
        return imagePath;
    }
}