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

import com.pjsoft.j2arch.uml.config.ConfigurationManager;
import com.pjsoft.j2arch.uml.config.ConfigurationManager;

public class PreviewTab {

    private final BorderPane layout;

    public PreviewTab(Stage primaryStage) {
        Label selectionLabel = new Label("Select a folder:");
        TextField pathField = new TextField();
        pathField.setEditable(false);
        Button browseButton = new Button("Browse...");

        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);

        Scale scale = new Scale(1, 1, 0, 0);
        imageView.getTransforms().add(scale);

        // ✅ NEW: ScrollPane for zoom + pan
        ScrollPane imageContainer = new ScrollPane(imageView);
        imageContainer.setPannable(true);
        imageContainer.setFitToWidth(true);
        imageContainer.setFitToHeight(true);
        //imageContainer.setStyle("-fx-background-color: #f0f0f0;");
        imageContainer.setPadding(new Insets(10));

        Button zoomInButton = new Button("Zoom In");
        Button zoomOutButton = new Button("Zoom Out");

        // ✅ Optional: Pan toggle
        ToggleButton panToggle = new ToggleButton("🖐️ Pan");
        panToggle.setSelected(true);
        panToggle.setOnAction(e -> imageContainer.setPannable(panToggle.isSelected()));

        zoomInButton.setOnAction(event -> {
            double newScaleX = scale.getX() * 1.1;
            double newScaleY = scale.getY() * 1.1;
            if (newScaleX <= 5) {
                scale.setX(newScaleX);
                scale.setY(newScaleY);
            }
        });

        zoomOutButton.setOnAction(event -> {
            double newScaleX = scale.getX() * 0.9;
            double newScaleY = scale.getY() * 0.9;
            if (newScaleX >= 0.5) {
                scale.setX(newScaleX);
                scale.setY(newScaleY);
            }
        });

        ToolBar toolBar = new ToolBar(zoomInButton, zoomOutButton, panToggle);

        Button leftButton = new Button("<");
        Button rightButton = new Button(">");
        leftButton.setDisable(true);
        rightButton.setDisable(true);

        HBox navigationBox = new HBox(10, leftButton, rightButton);
        navigationBox.setAlignment(Pos.CENTER);

        HBox selectionBox = new HBox(10, selectionLabel, pathField, browseButton);
        selectionBox.setAlignment(Pos.CENTER_LEFT);
        selectionBox.setPadding(new Insets(10));

        layout = new BorderPane();
        layout.setTop(new VBox(toolBar, selectionBox));
        layout.setCenter(imageContainer);
        layout.setBottom(navigationBox);

        List<File> imageFiles = new ArrayList<>();
        final int[] currentIndex = { 0 };

        loadDefaultOutputDirectory(imageFiles, pathField, imageView, leftButton, rightButton, currentIndex, scale);

        browseButton.setOnAction(event -> {
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
        });

        leftButton.setOnAction(event -> {
            if (currentIndex[0] > 0) {
                currentIndex[0]--;
                resetImageView(imageView, scale);
                displayImage(imageFiles.get(currentIndex[0]), imageView);
                updateNavigationButtons(leftButton, rightButton, currentIndex[0], imageFiles.size());
            }
        });

        rightButton.setOnAction(event -> {
            if (currentIndex[0] < imageFiles.size() - 1) {
                currentIndex[0]++;
                resetImageView(imageView, scale);
                displayImage(imageFiles.get(currentIndex[0]), imageView);
                updateNavigationButtons(leftButton, rightButton, currentIndex[0], imageFiles.size());
            }
        });
    }

    private void loadDefaultOutputDirectory(List<File> imageFiles, TextField pathField, ImageView imageView,
            Button leftButton, Button rightButton, int[] currentIndex, Scale scale) {
        try {
            ConfigurationManager configManager = ConfigurationManager.getInstance();
            String outputDirectory = configManager.getProperty("output.directory");
            if (outputDirectory == null || outputDirectory.isEmpty()) {
                outputDirectory = configManager.getDefaultSettings().get("output.directory");
            }

            if (outputDirectory != null) {
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
            }
        } catch (Exception e) {
            System.err.println("Error loading default output directory: " + e.getMessage());
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
            System.err.println("Error loading image: " + e.getMessage());
        }
    }

    private void updateNavigationButtons(Button leftButton, Button rightButton, int currentIndex, int totalImages) {
        leftButton.setDisable(currentIndex == 0);
        rightButton.setDisable(currentIndex == totalImages - 1);
    }

    public BorderPane getLayout() {
        return layout;
    }
}