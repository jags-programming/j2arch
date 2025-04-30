package com.pjsoft.j2arch.gui;

import com.pjsoft.j2arch.core.util.ConfigurationValidator;
import com.pjsoft.j2arch.core.util.ProgressTracker;
import com.pjsoft.j2arch.core.util.ProgressTracker.UseCase;
import com.pjsoft.j2arch.docgen.javadoc.JavaDocGenerator;
import com.pjsoft.j2arch.docgen.javadoc.util.JavaDocGenerationContext;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * JavaDocGenTab
 * 
 * Represents the "JavaDoc Generator" tab in the GUI. This tab allows users to:
 * - Configure input and output directories for JavaDoc generation.
 * - Trigger the JavaDoc generation process.
 * - Display progress and status updates during the generation process.
 * 
 * Responsibilities:
 * - Provides a user interface for configuring JavaDoc generation settings.
 * - Validates the configuration before starting the generation process.
 * - Uses a progress bar to display task progress and status updates.
 * - Handles errors and updates the UI accordingly.
 * 
 * Dependencies:
 * - {@link JavaDocGenerationContext}: Provides configuration details for JavaDoc generation.
 * - {@link JavaDocGenerator}: Handles the actual JavaDoc generation process.
 * - {@link ProgressTracker}: Tracks and updates progress during the generation process.
 * - JavaFX: Used for building the UI components.
 * 
 * Limitations:
 * - Assumes that the input and output directories are valid and accessible.
 * - Does not handle advanced error recovery for invalid configurations.
 * - Requires user interaction for directory selection.
 * 
 * Thread Safety:
 * - This class is not thread-safe as it relies on JavaFX's single-threaded model.
 * - Uses `Platform.runLater` to ensure thread-safe updates to the UI.
 * 
 * Usage Example:
 * {@code
 * Stage primaryStage = new Stage();
 * JavaDocGenerationContext context = new JavaDocGenerationContext(...);
 * JavaDocGenTab tab = new JavaDocGenTab(primaryStage, context);
 * }
 * 
 * Author: PJSoft
 * Version: 2.2
 * Since: 1.0
 */
public class JavaDocGenTab extends Tab {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JavaDocGenTab.class);

    private final JavaDocGenerationContext initialContext; // Initial context for JavaDoc generation
    private TextField inputDirField; // Input directory field
    private TextField outputDirField; // Output directory field
    private ProgressBarComponent progressBarComponent; // Progress bar component for tracking progress

    /**
     * Constructs a new JavaDocGenTab.
     * 
     * Responsibilities:
     * - Initializes the layout and UI components for the tab.
     * - Sets up event handlers for directory selection and JavaDoc generation.
     * 
     * @param primaryStage   The primary stage of the JavaFX application.
     * @param initialContext The initial context for JavaDoc generation.
     */
    public JavaDocGenTab(Stage primaryStage, JavaDocGenerationContext initialContext) {
        this.initialContext = initialContext;
        setText("JavaDoc Generator");

        // Initialize layout
        GridPane grid = initializeLayout(primaryStage);

        // Set the content of the tab
        setContent(grid);
    }

    /**
     * Initializes the layout for the tab.
     * 
     * Responsibilities:
     * - Creates and configures the grid layout.
     * - Adds input and output directory sections, progress bar, and generate button.
     * 
     * @param primaryStage The primary stage of the JavaFX application.
     * @return The initialized {@link GridPane}.
     */
    private GridPane initializeLayout(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        // Add components to the grid
        addInputDirectorySection(grid, primaryStage);
        addOutputDirectorySection(grid, primaryStage);
        addProgressBarComponent(grid);
        addGenerateButton(grid);

        return grid;
    }

    /**
     * Adds the progress bar component to the layout.
     * 
     * Responsibilities:
     * - Initializes and adds the progress bar to the grid layout.
     * 
     * @param grid The grid layout to which the progress bar is added.
     */
    private void addProgressBarComponent(GridPane grid) {
        progressBarComponent = new ProgressBarComponent();
        grid.add(progressBarComponent, 0, 3, 3, 1); // Span across 3 columns
    }

    /**
     * Adds the input directory section to the layout.
     * 
     * Responsibilities:
     * - Provides a text field and browse button for selecting the input directory.
     * 
     * @param grid         The grid layout to which the input directory section is added.
     * @param primaryStage The primary stage of the JavaFX application.
     */
    private void addInputDirectorySection(GridPane grid, Stage primaryStage) {
        Label inputDirLabel = new Label("Input Directory:");
        inputDirField = new TextField(initialContext.getInputDirectory());
        inputDirField.setPromptText("Select input directory...");
        Button inputDirButton = new Button("Browse");

        // Directory chooser logic
        DirectoryChooser directoryChooser = new DirectoryChooser();
        inputDirButton.setOnAction(event -> {
            File selectedDir = directoryChooser.showDialog(primaryStage);
            if (selectedDir != null) {
                inputDirField.setText(selectedDir.getAbsolutePath());
            }
        });

        grid.add(inputDirLabel, 0, 0);
        grid.add(inputDirField, 1, 0);
        grid.add(inputDirButton, 2, 0);
    }

    /**
     * Adds the output directory section to the layout.
     * 
     * Responsibilities:
     * - Provides a text field and browse button for selecting the output directory.
     * 
     * @param grid         The grid layout to which the output directory section is added.
     * @param primaryStage The primary stage of the JavaFX application.
     */
    private void addOutputDirectorySection(GridPane grid, Stage primaryStage) {
        Label outputDirLabel = new Label("Output Directory:");
        outputDirField = new TextField(initialContext.getOutputDirectory());
        outputDirField.setPromptText("Select output directory...");
        Button outputDirButton = new Button("Browse");

        // Directory chooser logic
        DirectoryChooser directoryChooser = new DirectoryChooser();
        outputDirButton.setOnAction(event -> {
            File selectedDir = directoryChooser.showDialog(primaryStage);
            if (selectedDir != null) {
                outputDirField.setText(selectedDir.getAbsolutePath());
            }
        });

        grid.add(outputDirLabel, 0, 1);
        grid.add(outputDirField, 1, 1);
        grid.add(outputDirButton, 2, 1);
    }

    /**
     * Adds the generate button to the layout.
     * 
     * Responsibilities:
     * - Provides a button to trigger the JavaDoc generation process.
     * 
     * @param grid The grid layout to which the generate button is added.
     */
    private void addGenerateButton(GridPane grid) {
        Button generateButton = new Button("Generate Javadoc");
        generateButton.setOnAction(event -> handleGenerateAction());
        grid.add(generateButton, 1, 2);
    }

    /**
     * Handles the action of generating JavaDocs.
     * 
     * Responsibilities:
     * - Resets the progress bar and status.
     * - Validates the configuration and starts the generation process in a background thread.
     */
    private void handleGenerateAction() {
        progressBarComponent.reset();

        // Create ProgressTracker
        ProgressTracker progressTracker = new ProgressTracker(progressBarComponent, UseCase.JAVA_DOC_GENERATION);

        // Run the generation process in a background thread
        new Thread(() -> {
            try {
                JavaDocGenerationContext contextToUse = createUpdatedContext(progressTracker);

                // Validate the context
                ConfigurationValidator.validateContext(contextToUse);

                // Initialize and run JavaDocGenerator
                JavaDocGenerator generator = new JavaDocGenerator();
                generator.generateJavaDoc(contextToUse, progressTracker);

                // Final update on success
                Platform.runLater(() -> {
                    progressTracker.markAllCompleted();
                    progressTracker.onStatusUpdate("Javadoc generation completed successfully!");
                });
            } catch (IOException ioe) {
                logger.error("Error during Javadoc generation: {}", ioe.getMessage());
                Platform.runLater(() -> progressTracker.onStatusUpdate("Error: " + ioe.getMessage()));
            } catch (IllegalArgumentException iae) {
                logger.error("Validation error: {}", iae.getMessage());
                Platform.runLater(() -> progressTracker.onStatusUpdate(iae.getMessage()));
            } catch (Exception e) {
                logger.error("Unexpected error: {}", e.getMessage(), e);
                Platform.runLater(() -> progressTracker.onStatusUpdate("Unexpected error: " + e.getMessage()));
            }
        }).start();
    }

    /**
     * Creates an updated JavaDocGenerationContext based on user input.
     * 
     * Responsibilities:
     * - Updates the context if the input or output directories have changed.
     * - Handles validation errors and updates the progress tracker.
     * 
     * @param progressTracker The progress tracker for updating status messages.
     * @return The updated {@link JavaDocGenerationContext}.
     * @throws Exception If an error occurs while creating the updated context.
     */
    private JavaDocGenerationContext createUpdatedContext(ProgressTracker progressTracker) throws Exception {
        String inputDir = inputDirField.getText().trim();
        String outputDir = outputDirField.getText().trim();

        if (inputDir.equals(initialContext.getInputDirectory()) &&
                outputDir.equals(initialContext.getOutputDirectory())) {
            return initialContext;
        }

        try {
            return new JavaDocGenerationContext(
                    inputDir,
                    outputDir,
                    initialContext.getIndexTemplateFile(),
                    initialContext.getClassTemplateFile(),
                    initialContext.getPackageTemplateFile(),
                    initialContext.getStyleSourceFile(),
                    initialContext.getIncludePackage(),
                    initialContext.getLibsDirPath());
        } catch (IllegalArgumentException iae) {
            progressTracker.onStatusUpdate(iae.getMessage());
            throw iae;
        } catch (Exception e) {
            progressTracker.onStatusUpdate(e.getMessage());
            throw e;
        }
    }
}