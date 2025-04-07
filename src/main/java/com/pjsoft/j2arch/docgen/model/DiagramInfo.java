package com.pjsoft.j2arch.docgen.model;

/**
 * DiagramInfo represents the information about a PlantUML diagram.
 * It includes the diagram's ID, title, description, and the path to the generated image.
 */
public class DiagramInfo {
    private final String id;
    private final String title;
    private final String description;
    private final String imagePath;

    /**
     * Constructs a new DiagramInfo object.
     *
     * @param id The unique identifier for the diagram
     * @param title The title of the diagram
     * @param description The description of the diagram
     * @param imagePath The path to the generated image of the diagram
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
     * @return The unique identifier for the diagram
     */
    public String getId() { return id; }

    /**
     * Gets the title of the diagram.
     *
     * @return The title of the diagram
     */
    public String getTitle() { return title; }

    /**
     * Gets the description of the diagram.
     *
     * @return The description of the diagram
     */
    public String getDescription() { return description; }

    /**
     * Gets the path to the generated image of the diagram.
     *
     * @return The path to the generated image of the diagram
     */
    public String getImagePath() { return imagePath; }
}