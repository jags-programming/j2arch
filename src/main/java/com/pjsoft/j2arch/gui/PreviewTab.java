package com.pjsoft.j2arch.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.transform.Scale;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pjsoft.j2arch.uml.util.UMLGenerationContext;

/**
 * PreviewTab
 * 
 * Represents the "Diagram Preview" tab in the GUI. This tab allows users to:
 * - Browse and select an output directory containing generated diagram images.
 * - Preview images (e.g., `.png`, `.jpg`) in the selected directory.
 * - Navigate between images using navigation buttons.
 * - Zoom in, zoom out, and pan the displayed image.
 * 
 * Responsibilities:
 * - Provides a user interface for browsing and previewing diagram images.
 * - Handles zooming, panning, and navigation between images.
 * - Loads images from the output directory specified in the {@link UMLGenerationContext}.
 * 
 * Dependencies:
 * - {@link UMLGenerationContext}: Provides configuration details for the output directory.
 * - JavaFX: Used for building the UI components.
 * 
 * Limitations:
 * - Assumes that the output directory contains valid image files.
 * - Does not handle advanced image formats or corrupted files.
 * - Requires user interaction for directory selection if the output directory is not pre-configured.
 * 
 * Thread Safety:
 * - This class is not thread-safe as it relies on JavaFX's single-threaded model.
 * 
 * Usage Example:
 * {@code
 * Stage primaryStage = new Stage();
 * UMLGenerationContext umlContext = new UMLGenerationContext(...);
 * PreviewTab previewTab = new PreviewTab(primaryStage, umlContext);
 * BorderPane layout = previewTab.getLayout();
 * }
 * 
 * Author: PJSoft
 * Version: 2.2
 * Since: 1.0
 */
public class PreviewTab {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PreviewTab.class);

    private final BorderPane layout; // The main layout of the tab
    private final UMLGenerationContext umlContext; // Context for UML generation configuration

    /**
     * Constructs a new PreviewTab.
     * 
     * Responsibilities:
     * - Initializes the layout and UI components for the preview tab.
     * - Sets up event handlers for browsing directories, navigating images, and zooming.
     * 
     * @param primaryStage The primary stage of the JavaFX application.
     * @param umlContext   The UML generation context containing configuration details.
     */
    public PreviewTab(Stage primaryStage, UMLGenerationContext umlContext) {
        this.umlContext = umlContext;

        // Label and text field for directory selection
        Label selectionLabel = new Label("Select a folder:");
        TextField pathField = new TextField();
        pathField.setEditable(false);
        Button browseButton = new Button("Browse...");

        // ImageView for displaying images
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);

        // Scale for zooming
        Scale scale = new Scale(1, 1, 0, 0);
        imageView.getTransforms().add(scale);

        // ScrollPane for zoom and pan functionality
        ScrollPane imageContainer = new ScrollPane(imageView);
        imageContainer.setPannable(true);
        imageContainer.setFitToWidth(true);
        imageContainer.setFitToHeight(true);
        imageContainer.setPadding(new Insets(10));

        // Zoom buttons
        Button zoomInButton = new Button("Zoom In");
        Button zoomOutButton = new Button("Zoom Out");

        // Pan toggle button
        ToggleButton panToggle = new ToggleButton("🖐️ Pan");
        panToggle.setSelected(true);
        panToggle.setOnAction(e -> imageContainer.setPannable(panToggle.isSelected()));

        // Attach event handlers for zoom buttons
        zoomInButton.setOnAction(event -> handleZoomIn(scale));
        zoomOutButton.setOnAction(event -> handleZoomOut(scale));

        // Toolbar for zoom and pan controls
        ToolBar toolBar = new ToolBar(zoomInButton, zoomOutButton, panToggle);

        // Navigation buttons
        Button leftButton = new Button("<");
        Button rightButton = new Button(">");
        leftButton.setDisable(true);
        rightButton.setDisable(true);

        // Navigation box
        HBox navigationBox = new HBox(10, leftButton, rightButton);
        navigationBox.setAlignment(Pos.CENTER);

        // Directory selection box
        HBox selectionBox = new HBox(10, selectionLabel, pathField, browseButton);
        selectionBox.setAlignment(Pos.CENTER_LEFT);
        selectionBox.setPadding(new Insets(10));

        // Main layout
        layout = new BorderPane();
        layout.setTop(new VBox(toolBar, selectionBox));
        layout.setCenter(imageContainer);
        layout.setBottom(navigationBox);

        // Image file list and current index
        List<File> imageFiles = new ArrayList<>();
        final int[] currentIndex = { 0 };

        // Load images from the output directory
        loadOutputDirectory(imageFiles, pathField, imageView, leftButton, rightButton, currentIndex, scale);

        // Event handlers for browsing and navigation
        browseButton.setOnAction(event -> handleBrowse(primaryStage, pathField, imageFiles, imageView, scale, leftButton, rightButton, currentIndex));
        leftButton.setOnAction(event -> handleLeftNavigation(imageFiles, imageView, scale, currentIndex, leftButton, rightButton));
        rightButton.setOnAction(event -> handleRightNavigation(imageFiles, imageView, scale, currentIndex, leftButton, rightButton));
    }

    private void handleZoomIn(Scale scale) {
        double newScaleX = scale.getX() * 1.1;
        double newScaleY = scale.getY() * 1.1;
        if (newScaleX <= 5) {
            scale.setX(newScaleX);
            scale.setY(newScaleY);
        }
    }

    private void handleZoomOut(Scale scale) {
        double newScaleX = scale.getX() * 0.9;
        double newScaleY = scale.getY() * 0.9;
        if (newScaleX >= 0.5) {
            scale.setX(newScaleX);
            scale.setY(newScaleY);
        }
    }

    private void handleBrowse(Stage primaryStage, TextField pathField, List<File> imageFiles, ImageView imageView, Scale scale, Button leftButton, Button rightButton, int[] currentIndex) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");

        File selectedDirectory = directoryChooser.showDialog(primaryStage);

        if (selectedDirectory != null) {
            pathField.setText(selectedDirectory.getAbsolutePath());
            imageFiles.clear();

            File[] files = selectedDirectory.listFiles(
                    (dir, name) -> name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg"));
            if (files != null) {
                imageFiles.addAll(Arrays.asList(files));
            }

            if (!imageFiles.isEmpty()) {
                currentIndex[0] = 0;
                resetImageView(imageView, scale);
                displayImage(imageFiles.get(currentIndex[0]), imageView);
                updateNavigationButtons(leftButton, rightButton, currentIndex[0], imageFiles.size());
            } else {
                imageView.setImage(null);
                leftButton.setDisable(true);
                rightButton.setDisable(true);
            }
        }
    }

    private void handleLeftNavigation(List<File> imageFiles, ImageView imageView, Scale scale, int[] currentIndex, Button leftButton, Button rightButton) {
        if (currentIndex[0] > 0) {
            currentIndex[0]--;
            resetImageView(imageView, scale);
            displayImage(imageFiles.get(currentIndex[0]), imageView);
            updateNavigationButtons(leftButton, rightButton, currentIndex[0], imageFiles.size());
        }
    }

    private void handleRightNavigation(List<File> imageFiles, ImageView imageView, Scale scale, int[] currentIndex, Button leftButton, Button rightButton) {
        if (currentIndex[0] < imageFiles.size() - 1) {
            currentIndex[0]++;
            resetImageView(imageView, scale);
            displayImage(imageFiles.get(currentIndex[0]), imageView);
            updateNavigationButtons(leftButton, rightButton, currentIndex[0], imageFiles.size());
        }
    }

    private void loadOutputDirectory(List<File> imageFiles, TextField pathField, ImageView imageView, Button leftButton, Button rightButton, int[] currentIndex, Scale scale) {
        try {
            String outputDirectory = umlContext.getOutputDirectory();
            if (outputDirectory == null || outputDirectory.isEmpty()) {
                logger.warn("Output directory is not set. Unable to load images for preview.");
                return;
            }

            File outputDir = new File(outputDirectory);
            if (outputDir.exists() && outputDir.isDirectory()) {
                pathField.setText(outputDirectory);
                File[] files = outputDir.listFiles(
                        (dir, name) -> name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg"));
                if (files != null) {
                    imageFiles.addAll(Arrays.asList(files));
                }

                if (!imageFiles.isEmpty()) {
                    currentIndex[0] = 0;
                    resetImageView(imageView, scale);
                    displayImage(imageFiles.get(currentIndex[0]), imageView);
                    updateNavigationButtons(leftButton, rightButton, currentIndex[0], imageFiles.size());
                } else {
                    pathField.clear();
                }
            }
        } catch (Exception e) {
            logger.error("Error loading output directory: {}", e.getMessage());
            pathField.clear();
        }
    }

    private void resetImageView(ImageView imageView, Scale scale) {
        scale.setX(1);
        scale.setY(1);
        imageView.setTranslateX(0);
        imageView.setTranslateY(0);
    }

    private void displayImage(File file, ImageView imageView) {
        try {
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
        } catch (Exception e) {
            imageView.setImage(null);
            logger.error("Error loading image: {}", e.getMessage());
        }
    }

    private void updateNavigationButtons(Button leftButton, Button rightButton, int currentIndex, int totalImages) {
        leftButton.setDisable(currentIndex == 0);
        rightButton.setDisable(currentIndex == totalImages - 1);
    }

    /**
     * Retrieves the main layout of the tab.
     * 
     * @return The {@link BorderPane} layout of the tab.
     */
    public BorderPane getLayout() {
        return layout;
    }
}