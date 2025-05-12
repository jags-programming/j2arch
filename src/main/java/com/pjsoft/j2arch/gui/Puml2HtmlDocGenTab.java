package com.pjsoft.j2arch.gui;

import com.pjsoft.j2arch.core.util.ConfigurationValidator;
import com.pjsoft.j2arch.core.util.ProgressTracker;
import com.pjsoft.j2arch.core.util.ProgressTracker.UseCase;
import com.pjsoft.j2arch.docgen.pumldoc.Puml2HtmlDocGenerator;

import com.pjsoft.j2arch.docgen.pumldoc.util.HtmlGenerationContext;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * HtmlDocGenTab
 * 
 * Represents the "HTML DocGen" tab in the GUI. This tab allows users to:
 * - Configure input and output directories for HTML documentation generation.
 * - Specify a title for the generated documentation.
 * - Trigger the HTML documentation generation process.
 * - Display progress and status updates during the generation process.
 * 
 * Responsibilities:
 * - Provides a user interface for configuring HTML documentation generation settings.
 * - Validates the configuration before starting the generation process.
 * - Uses a progress bar to display task progress and status updates.
 * - Handles errors and updates the UI accordingly.
 * 
 * Dependencies:
 * - {@link HTMLGenerationContext}: Provides configuration details for HTML documentation generation.
 * - {@link Puml2HtmlDocGenerator}: Handles the actual HTML documentation generation process.
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
 * HTMLGenerationContext context = new HTMLGenerationContext(...);
 * HtmlDocGenTab tab = new HtmlDocGenTab(primaryStage, context);
 * }
 * 
 * Author: PJSoft
 * Version: 2.2
 * Since: 1.0
 */
public class Puml2HtmlDocGenTab extends Tab {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Puml2HtmlDocGenTab.class);

    private final HtmlGenerationContext htmlContext; // Initial context for HTML documentation generation

    /**
     * Constructs a new HtmlDocGenTab.
     * 
     * Responsibilities:
     * - Initializes the layout and UI components for the tab.
     * - Sets up event handlers for directory selection and HTML documentation generation.
     * 
     * @param primaryStage       The primary stage of the JavaFX application.
     * @param initialHtmlContext The initial context for HTML documentation generation.
     */
    public Puml2HtmlDocGenTab(Stage primaryStage, HtmlGenerationContext initialHtmlContext) {
        this.htmlContext = initialHtmlContext;
        setText("HTML DocGen");

        // Layout for the tab
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        // Input directory field and button
        Label inputDirLabel = new Label("PUML Directory:");
        TextField inputDirField = new TextField(htmlContext.getInputDirectory());
        inputDirField.setPromptText("Select PUML directory...");
        Button inputDirButton = new Button("Browse");

        // Output directory field and button
        Label outputDirLabel = new Label("Output Directory:");
        TextField outputDirField = new TextField(htmlContext.getOutputDirectory());
        outputDirField.setPromptText("Select output directory...");
        Button outputDirButton = new Button("Browse");

        // Documentation title field
        Label docTitleLabel = new Label("Documentation Title:");
        TextField docTitleField = new TextField("PlantUML Documentation");

        // Generate button
        Button generateButton = new Button("Generate HTML Documentation");

        // Progress bar component
        ProgressBarComponent progressBarComponent = new ProgressBarComponent();

        // Add components to the grid
        grid.add(inputDirLabel, 0, 0);
        grid.add(inputDirField, 1, 0);
        grid.add(inputDirButton, 2, 0);

        grid.add(outputDirLabel, 0, 1);
        grid.add(outputDirField, 1, 1);
        grid.add(outputDirButton, 2, 1);

        grid.add(docTitleLabel, 0, 2);
        grid.add(docTitleField, 1, 2);

        grid.add(generateButton, 1, 3);

        // Add the progress bar component
        grid.add(progressBarComponent, 0, 4, 3, 1); // Span across 3 columns
        setContent(grid);

        // Directory chooser for input and output directories
        DirectoryChooser directoryChooser = new DirectoryChooser();

        // Input directory button action
        inputDirButton.setOnAction(event -> {
            File selectedDir = directoryChooser.showDialog(primaryStage);
            if (selectedDir != null) {
                inputDirField.setText(selectedDir.getAbsolutePath());
            }
        });

        // Output directory button action
        outputDirButton.setOnAction(event -> {
            File selectedDir = directoryChooser.showDialog(primaryStage);
            if (selectedDir != null) {
                outputDirField.setText(selectedDir.getAbsolutePath());
            }
        });

        // Generate button action
        generateButton.setOnAction(event -> {
            // Create ProgressTracker
            ProgressTracker progressTracker = new ProgressTracker(progressBarComponent, UseCase.HTML_DOC_GENERATION);
            String inputDir = inputDirField.getText().trim();
            String outputDir = outputDirField.getText().trim();
            String docTitle = docTitleField.getText().trim();

            if (inputDir.isEmpty() || outputDir.isEmpty()) {
                progressTracker.onStatusUpdate("Error: Both input and output directories must be specified.");
                return;
            }

            // Create a new HTMLGenerationContext
            HtmlGenerationContext newHtmlContext = new HtmlGenerationContext(
                    inputDir,
                    outputDir,
                    initialHtmlContext.getDiagramTemplateFile(),
                    initialHtmlContext.getIndexTemplateFile(),
                    initialHtmlContext.getStyleSourceFile()
            );

            try {
                progressTracker.onStatusUpdate("Input validation started...");
                ConfigurationValidator.validateContext(newHtmlContext);
                progressTracker.onStatusUpdate("Input validation completed.");
            } catch (IOException e) {
                progressTracker.onStatusUpdate(e.getMessage());
                logger.error("Directories/files validation failed: ", e.getMessage());
                return;
            }

            // Run the generation process in a background thread
            new Thread(() -> {
                try {
                    Puml2HtmlDocGenerator generator = new Puml2HtmlDocGenerator();
                    progressTracker.onStatusUpdate("HTML document generation started...");
                    generator.generateHtmlDocumentation(docTitle, newHtmlContext, progressTracker);

                    // Update message label on the JavaFX Application Thread
                    Platform.runLater(() -> {
                        progressTracker.onStatusUpdate("HTML documentation generated successfully! Output available at: " + outputDir);
                    });
                } catch (IOException e) {
                    // Update message label on the JavaFX Application Thread
                    Platform.runLater(() -> {
                        progressTracker.onStatusUpdate("Error: " + e.getMessage());
                    });
                }
            }).start();
        });
    }
}