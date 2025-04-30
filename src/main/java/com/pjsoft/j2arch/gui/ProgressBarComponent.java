package com.pjsoft.j2arch.gui;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

/**
 * ProgressBarComponent
 * 
 * A reusable JavaFX component that combines a progress bar and a status label.
 * 
 * Responsibilities:
 * - Displays a progress bar to indicate task progress.
 * - Displays a status message below the progress bar.
 * - Provides methods to update progress, update the status message, and reset the component.
 * 
 * Dependencies:
 * - JavaFX: Used for building the UI components.
 * 
 * Limitations:
 * - Assumes that updates to the progress bar and status label are performed on the JavaFX Application Thread.
 * - Does not handle advanced styling or customization beyond basic functionality.
 * 
 * Thread Safety:
 * - This class uses `Platform.runLater` to ensure thread-safe updates to the JavaFX UI components.
 * 
 * Usage Example:
 * {@code
 * ProgressBarComponent progressBarComponent = new ProgressBarComponent();
 * progressBarComponent.updateProgress(0.5); // Set progress to 50%
 * progressBarComponent.updateStatus("Task is halfway complete.");
 * progressBarComponent.reset(); // Reset the progress bar and status label
 * }
 * 
 * Author: PJSoft
 * Version: 2.2
 * Since: 1.0
 */
public class ProgressBarComponent extends VBox {

    private final ProgressBar progressBar; // The progress bar to display task progress
    private final Label statusLabel;       // The label to display status messages

    /**
     * Constructs a new ProgressBarComponent.
     * 
     * Responsibilities:
     * - Initializes the progress bar and status label.
     * - Adds the components to a vertical layout (VBox).
     */
    public ProgressBarComponent() {
        // Initialize the progress bar
        progressBar = new ProgressBar(0); // Initially 0% progress
        progressBar.setPrefWidth(300); // Set width

        // Initialize the status label
        statusLabel = new Label();
        statusLabel.setWrapText(true);

        // Add components to the VBox
        setSpacing(10);
        getChildren().addAll(progressBar, statusLabel);
    }

    /**
     * Updates the progress bar value.
     * 
     * Responsibilities:
     * - Sets the progress value of the progress bar.
     * - Ensures the update is performed on the JavaFX Application Thread.
     * 
     * @param progress A value between 0.0 (0%) and 1.0 (100%).
     */
    public void updateProgress(double progress) {
        Platform.runLater(() -> progressBar.setProgress(progress));
    }

    /**
     * Updates the status message.
     * 
     * Responsibilities:
     * - Sets the text of the status label.
     * - Ensures the update is performed on the JavaFX Application Thread.
     * 
     * @param message The status message to display.
     */
    public void updateStatus(String message) {
        Platform.runLater(() -> statusLabel.setText(message));
    }

    /**
     * Resets the progress bar and status label.
     * 
     * Responsibilities:
     * - Resets the progress bar to 0% progress.
     * - Clears the text of the status label.
     * - Ensures the reset is performed on the JavaFX Application Thread.
     */
    public void reset() {
        Platform.runLater(() -> {
            progressBar.setProgress(0);
            statusLabel.setText("");
        });
    }

    /**
     * Gets the progress bar instance.
     * 
     * Responsibilities:
     * - Provides access to the underlying ProgressBar instance.
     * 
     * @return The ProgressBar instance.
     */
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * Gets the status label instance.
     * 
     * Responsibilities:
     * - Provides access to the underlying Label instance.
     * 
     * @return The Label instance.
     */
    public Label getStatusLabel() {
        return statusLabel;
    }
}