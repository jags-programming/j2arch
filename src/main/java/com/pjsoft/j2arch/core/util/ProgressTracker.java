package com.pjsoft.j2arch.core.util;

import javafx.application.Platform;
import javafx.scene.control.Label;
import com.pjsoft.j2arch.gui.ProgressBarComponent;

/**
 * ProgressTracker
 * 
 * Tracks the progress of various tasks in the application, such as Javadoc generation,
 * UML diagram generation, and HTML documentation generation. This class supports both
 * CLI-based and GUI-based progress tracking.
 * 
 * Responsibilities:
 * - Track the total and completed units of work.
 * - Update progress in the CLI or GUI.
 * - Handle task weightage for different types of work units.
 * - Provide status updates during task execution.
 * 
 * Dependencies:
 * - {@link ProgressBarComponent}: For updating the progress bar in the GUI.
 * - {@link Platform}: For updating GUI components on the JavaFX application thread.
 * 
 * Limitations:
 * - Assumes that the total units of work are initialized before tracking progress.
 * - Not thread-safe for concurrent modifications outside synchronized methods.
 * 
 * Usage Example:
 * {@code
 * ProgressBarComponent progressBar = ...;
 * ProgressTracker tracker = new ProgressTracker(progressBar, ProgressTracker.UseCase.JAVA_DOC_GENERATION);
 * tracker.initializeTaskCounts(100, 10);
 * tracker.addCompletedUnits(ProgressTracker.WorkUnitType.CLASS_DOC, 5);
 * tracker.onStatusUpdate("Class documentation generated.");
 * }
 * 
 * Author: PJSoft
 * Since: 1.0
 */
public class ProgressTracker {

    /**
     * Enum representing different types of work units.
     */
    public enum WorkUnitType {
        CSS,
        FILE_PARSING,
        PACKAGE_DOC,
        CLASS_DOC,
        INDEX_PAGE,
        SEQUENCE_DIAGRAM,
        CLASS_DIAGRAM
    }

    /**
     * Enum representing different use cases for progress tracking.
     */
    public enum UseCase {
        JAVA_DOC_GENERATION,
        UML_DIAGRAM_GENERATION,
        HTML_DOC_GENERATION
    }

    private final ProgressBarComponent progressBarComponent; // GUI progress bar component
    private final boolean isCLI; // Flag to indicate if tracking is CLI-based
    private final UseCase useCase; // The use case for progress tracking

    private int totalUnits = 0; // Total units of work
    private int completedUnits = 0; // Completed units of work

    // Task weightage (as proportions of the total workload) for Javadoc generation
    private static final int CSS_WEIGHT = 1;
    private static final int FILE_PARSING_WEIGHT = 3;
    private static final int PACKAGE_DOC_WEIGHT = 2;
    private static final int CLASS_DOC_WEIGHT = 2;
    private static final int INDEX_PAGE_WEIGHT = 2;

    // Task weightage for UML diagram generation
    private static final int SEQUENCE_DIAGRAM_WEIGHT = 5;
    private static final int CLASS_DIAGRAM_WEIGHT = 5;

    /**
     * Constructor for GUI-based progress tracking.
     * 
     * @param progressBarComponent The progress bar component for updating progress in the GUI.
     * @param useCase              The use case for progress tracking.
     */
    public ProgressTracker(ProgressBarComponent progressBarComponent, UseCase useCase) {
        this.progressBarComponent = progressBarComponent;
        this.isCLI = false;
        this.useCase = useCase;
    }

    /**
     * Constructor for CLI-based progress tracking.
     * 
     * @param useCase The use case for progress tracking.
     */
    public ProgressTracker(UseCase useCase) {
        this.progressBarComponent = null;
        this.isCLI = true;
        this.useCase = useCase;
    }

    /**
     * Adds to the total number of work units.
     * 
     * @param units The number of units to add.
     */
    public synchronized void addTotalUnits(WorkUnitType type, int units) {
        int weightedUnits = applyWeight(type, units);
        this.totalUnits += weightedUnits;
       updateProgress();
    }

    /**
     * Adds completed work units and updates progress.
     * 
     * @param units The number of completed units to add.
     */
    private synchronized void addCompletedUnits(int units) {
        this.completedUnits += units;
        updateProgress();
    }

    /**
     * Adds completed work units with weight applied based on the work unit type.
     * 
     * @param type  The type of work unit.
     * @param units The number of completed units to add.
     */
    public synchronized void addCompletedUnits(WorkUnitType type, int units) {
        int weightedUnits = applyWeight(type, units);
        this.completedUnits += weightedUnits;
        updateProgress();
    }

    /**
     * Applies weight to the work units based on the work unit type.
     * 
     * @param type  The type of work unit.
     * @param units The number of units to weight.
     * @return The weighted units.
     */
    private int applyWeight(WorkUnitType type, int units) {
        switch (type) {
            case CSS:
                return units * CSS_WEIGHT;
            case FILE_PARSING:
                return units * FILE_PARSING_WEIGHT;
            case PACKAGE_DOC:
                return units * PACKAGE_DOC_WEIGHT;
            case CLASS_DOC:
                return units * CLASS_DOC_WEIGHT;
            case INDEX_PAGE:
                return units * INDEX_PAGE_WEIGHT;
            case CLASS_DIAGRAM:
                return units * CLASS_DIAGRAM_WEIGHT;
            case SEQUENCE_DIAGRAM:
                return units * SEQUENCE_DIAGRAM_WEIGHT;
            default:
                throw new IllegalArgumentException("Unknown work unit type: " + type);
        }
    }

    /**
     * Initializes task counts and computes total units for Javadoc or UML generation.
     * 
     * @param totalFiles    The total number of files to process.
     * @param directoryCount The total number of directories to process.
     */
    public synchronized void initializeTaskCounts(int totalFiles, int directoryCount) {
        System.out.println("Initializing task counts...");
        int fileParsingUnits = totalFiles * FILE_PARSING_WEIGHT;

        if (useCase == UseCase.JAVA_DOC_GENERATION) {
            int cssUnits = CSS_WEIGHT;
            int packageDocUnits = directoryCount * PACKAGE_DOC_WEIGHT;
            int classDocUnits = totalFiles * CLASS_DOC_WEIGHT;
            int indexPageUnits = INDEX_PAGE_WEIGHT;
            this.totalUnits = cssUnits + fileParsingUnits + packageDocUnits + classDocUnits + indexPageUnits;
        } else if (useCase == UseCase.UML_DIAGRAM_GENERATION) {
            int classDiagramUnits = CLASS_DIAGRAM_WEIGHT;
            int sequenceDiagramUnits = SEQUENCE_DIAGRAM_WEIGHT;
            this.totalUnits = fileParsingUnits ;//+ classDiagramUnits + sequenceDiagramUnits;
        }
    }

    /**
     * Initializes task counts for UML generation with specific diagram types.
     * 
     * @param totalFiles     The total number of files to process.
     * @param diagramTypes   The number of diagram types to generate.
     */
    /** 
    public synchronized void initializeTaskCountsForUML(int totalFiles, int diagramTypes) {
        System.out.println("Initializing task counts for UML generation...");
        int fileParsingUnits = totalFiles * FILE_PARSING_WEIGHT;
        int diagramGenerationUnits = diagramTypes * CLASS_DIAGRAM_WEIGHT;

        this.totalUnits = fileParsingUnits + diagramGenerationUnits;
    }
        */

    /**
     * Updates the progress bar or CLI progress based on completed units.
     */
    private void updateProgress() {
        if (totalUnits > 0) {
            double progress = (double) completedUnits / totalUnits;
            if (isCLI) {
                System.out.print("\rProgress: " + (int) (progress * 100) + "%");
                if (progress >= 1.0) {
                    System.out.println("\nTask Completed!");
                }
            } else {
                Platform.runLater(() -> progressBarComponent.updateProgress(progress));
            }
        }
    }

    /**
     * Updates the status message in the CLI or GUI.
     * 
     * @param message The status message to display.
     */
    public void onStatusUpdate(String message) {
        if (isCLI) {
            System.out.println("\n" + message);
        } else {
            Platform.runLater(() -> progressBarComponent.updateStatus(message));
        }
    }

    /**
     * Marks all remaining work units as completed and updates progress.
     */
    public void markAllCompleted() {
        int remainingWorkUnits = totalUnits - completedUnits;
        if (remainingWorkUnits > 0) {
            this.completedUnits += remainingWorkUnits;
            double progress = (double) completedUnits / totalUnits;
            Platform.runLater(() -> progressBarComponent.updateProgress(progress));
        }
    }
}