package com.pjsoft.j2arch.gui;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.pjsoft.j2arch.config.ConfigurationLoader;
import com.pjsoft.j2arch.config.DefaultContextFactory;
import com.pjsoft.j2arch.core.util.ConfigurationValidator;
import com.pjsoft.j2arch.core.util.GUIStylePathResolver;
import com.pjsoft.j2arch.core.util.ProgressTracker;
import com.pjsoft.j2arch.core.util.ProgressTracker.UseCase;
import com.pjsoft.j2arch.gui.util.GUIStyleContext;
import com.pjsoft.j2arch.uml.UMLDiagramGenerator;
import com.pjsoft.j2arch.uml.util.UMLGenerationContext;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * ConfigurationTab
 * 
 * Represents the "Configuration" tab in the GUI. This tab allows users to:
 * - Configure input and output directories for UML diagram generation.
 * - Select diagram types and include package filters.
 * - Choose between default settings, file-based settings, or custom inputs.
 * - Apply themes to the application.
 * - Trigger the UML diagram generation process.
 * 
 * Responsibilities:
 * - Provides a user interface for configuring UML diagram generation settings.
 * - Validates the configuration before starting the generation process.
 * - Applies themes dynamically to the application.
 * - Uses a progress bar to display task progress and status updates.
 * 
 * Dependencies:
 * - {@link GUIStyleContext}: Provides configuration details for GUI styling.
 * - {@link UMLGenerationContext}: Provides configuration details for UML diagram generation.
 * - {@link UMLDiagramGenerator}: Handles the actual UML diagram generation process.
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
 * GUIStyleContext guiStyleContext = new GUIStyleContext(...);
 * UMLGenerationContext umlContext = new UMLGenerationContext(...);
 * ConfigurationTab tab = new ConfigurationTab(primaryStage, guiStyleContext, umlContext);
 * }
 * 
 * Author: PJSoft
 * Version: 2.2
 * Since: 1.0
 */
public class ConfigurationTab {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ConfigurationTab.class);

    private final VBox layout; // Main layout of the tab
    private final ProgressBarComponent progressBarComponent = new ProgressBarComponent(); // Progress bar for tracking progress
    private Scene scene; // Scene to apply styles

    private final VBox customInputsBox = new VBox(10); // Container for custom input fields
    private final ToggleGroup configOptionsGroup = new ToggleGroup(); // Toggle group for configuration options
    private final RadioButton defaultSettingsOption = new RadioButton("Use default settings");
    private final RadioButton fileSettingsOption = new RadioButton("Load settings from a configuration file");
    private final RadioButton customSettingsOption = new RadioButton("Enter custom inputs interactively");

    // Fields for user inputs
    private final TextField inputDirField = new TextField();
    private final TextField outputDirField = new TextField();
    private final TextField diagramTypesField = new TextField("class,sequence");
    private final TextField includePackageField = new TextField();

    private final GUIStyleContext guiStyleContext; // Context for GUI styling

    /**
     * Constructs a new ConfigurationTab.
     * 
     * Responsibilities:
     * - Initializes the layout and UI components for the tab.
     * - Sets up event handlers for directory selection and UML diagram generation.
     * 
     * @param primaryStage   The primary stage of the JavaFX application.
     * @param guiStyleContext The context for GUI styling.
     * @param umlContext      The context for UML diagram generation.
     */
    public ConfigurationTab(Stage primaryStage, GUIStyleContext guiStyleContext, UMLGenerationContext umlContext) {
        this.guiStyleContext = guiStyleContext;

        HBox topRow = createTopRow();
        VBox configOptionsBox = createConfigurationOptionsBox();
        TitledPane customInputsPane = createCustomInputsPane(primaryStage);
        customInputsBox.getChildren().add(customInputsPane);
        customInputsBox.setVisible(false);

        Button generateButton = createGenerateButton(umlContext);

        layout = new VBox(15, topRow, configOptionsBox, customInputsBox, generateButton, progressBarComponent);
        layout.setPadding(new Insets(20));
    }

    /**
     * Sets the scene for the tab and applies the default theme.
     * 
     * @param scene The scene to set and style.
     */
    public void setScene(Scene scene) {
        this.scene = scene;
        setStartupStyle(scene);
    }

    /**
     * Retrieves the main layout of the tab.
     * 
     * @return The {@link VBox} layout of the tab.
     */
    public VBox getLayout() {
        return layout;
    }

    /**
     * Creates the top row of the tab, including the banner and theme selection.
     * 
     * @return An {@link HBox} containing the top row layout.
     */
    private HBox createTopRow() {
        Label bannerLabel = new Label("Welcome to UML Generator");
        bannerLabel.setId("bannerLabel");
        ToggleGroup themeToggleGroup = new ToggleGroup();
        RadioButton lightTheme = createThemeRadio("Light", themeToggleGroup, guiStyleContext.getLightThemePath());
        RadioButton darkTheme = createThemeRadio("Dark", themeToggleGroup, guiStyleContext.getDarkThemePath());
        RadioButton pastelTheme = createThemeRadio("Pastel", themeToggleGroup, guiStyleContext.getPastelThemePath());

        selectDefaultTheme(lightTheme, darkTheme, pastelTheme);

        HBox themeBox = new HBox(10, lightTheme, darkTheme, pastelTheme);
        themeBox.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topRow = new HBox(10, bannerLabel, spacer, themeBox);
        topRow.setPadding(new Insets(10));
        topRow.setAlignment(Pos.CENTER_LEFT);

        return topRow;
    }

    /**
     * Creates a radio button for theme selection.
     * 
     * @param label     The label for the radio button.
     * @param group     The toggle group to which the radio button belongs.
     * @param stylePath The style path to apply when the radio button is selected.
     * @return A {@link RadioButton} for theme selection.
     */
    private RadioButton createThemeRadio(String label, ToggleGroup group, String stylePath) {
        RadioButton rb = new RadioButton(label);
        rb.setToggleGroup(group);
        rb.setOnAction(e -> setStyle(scene, stylePath));
        return rb;
    }

    /**
     * Creates the configuration options box for selecting configuration modes.
     * 
     * @return A {@link VBox} containing the configuration options.
     */
    private VBox createConfigurationOptionsBox() {
        Label configOptionsLabel = new Label("How do you want to proceed?");
        defaultSettingsOption.setToggleGroup(configOptionsGroup);
        fileSettingsOption.setToggleGroup(configOptionsGroup);
        customSettingsOption.setToggleGroup(configOptionsGroup);
        defaultSettingsOption.setSelected(true);

        configOptionsGroup.selectedToggleProperty()
                .addListener((obs, oldToggle, newToggle) -> updateCustomInputsVisibility());

        return new VBox(10, configOptionsLabel, defaultSettingsOption, fileSettingsOption, customSettingsOption);
    }

    /**
     * Creates the custom inputs pane for user-defined configuration.
     * 
     * @param primaryStage The primary stage of the JavaFX application.
     * @return A {@link TitledPane} containing the custom inputs layout.
     */
    private TitledPane createCustomInputsPane(Stage primaryStage) {
        Button inputDirButton = createDirectoryButton("Browse...", inputDirField, primaryStage);
        Button outputDirButton = createDirectoryButton("Browse...", outputDirField, primaryStage);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        grid.addRow(0, new Label("Input Directory:"), inputDirField, inputDirButton);
        grid.addRow(1, new Label("Output Directory:"), outputDirField, outputDirButton);
        grid.addRow(2, new Label("Diagram Types (class,sequence):"), diagramTypesField);
        grid.addRow(3, new Label("Include Package (default: all):"), includePackageField);
        grid.getStyleClass().add("custom-inputs-grid");

        TitledPane titledPane = new TitledPane("Custom Inputs", grid);
        titledPane.setCollapsible(false);
        titledPane.getStyleClass().add("custom-inputs-titled-pane");

        return titledPane;
    }

    /**
     * Creates a button for selecting a directory.
     * 
     * @param label       The label for the button.
     * @param targetField The text field to update with the selected directory path.
     * @param stage       The primary stage of the JavaFX application.
     * @return A {@link Button} for directory selection.
     */
    private Button createDirectoryButton(String label, TextField targetField, Stage stage) {
        Button button = new Button(label);
        button.setOnAction(event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            File selected = chooser.showDialog(stage);
            if (selected != null) {
                targetField.setText(selected.getAbsolutePath());
            }
        });
        return button;
    }

    /**
     * Creates the "Generate UML Diagrams" button.
     * 
     * @param context The UML generation context.
     * @return A {@link Button} to trigger UML diagram generation.
     */
    private Button createGenerateButton(UMLGenerationContext context) {
        Button generateButton = new Button("Generate UML Diagrams");
        generateButton.setOnAction(event -> handleGenerateAction(context));
        return generateButton;
    }

    /**
     * Handles the action of generating UML diagrams.
     * 
     * Responsibilities:
     * - Resets the progress bar and status.
     * - Validates the configuration and starts the generation process in a background thread.
     * 
     * @param context The UML generation context.
     */
    private void handleGenerateAction(UMLGenerationContext context) {
        progressBarComponent.reset();
        ProgressTracker progressTracker = new ProgressTracker(progressBarComponent, UseCase.UML_DIAGRAM_GENERATION);

        progressTracker.onStatusUpdate("Generating UML diagrams...");

        new Thread(() -> {
            try {
                UMLGenerationContext finalContext;

                if (defaultSettingsOption.isSelected()) {
                    finalContext = context;
                    logger.debug("Using default settings...");
                } else if (fileSettingsOption.isSelected()) {
                    String customPropertiesFileName = null;
                    finalContext = getContextFromFile(customPropertiesFileName);
                    logger.debug("Loading settings from configuration file...");
                } else {
                    finalContext = getContextFromCustomInputs(context);
                }

                progressTracker.onStatusUpdate("Validating inputs...");
                ConfigurationValidator.validateContext(finalContext);
                progressTracker.onStatusUpdate("Input validation completed.");

                UMLDiagramGenerator generator = new UMLDiagramGenerator();
                generator.generateDiagrams(finalContext, progressTracker);

                Platform.runLater(() -> {
                    progressTracker.markAllCompleted();
                    progressTracker.onStatusUpdate("UML diagrams generated successfully!");
                });
            } catch (Exception e) {
                Platform.runLater(() -> progressTracker.onStatusUpdate("Error: " + e.getMessage()));
            }
        }).start();
    }

    /**
     * Updates the visibility of the custom inputs box based on the selected configuration option.
     */
    private void updateCustomInputsVisibility() {
        RadioButton selected = (RadioButton) configOptionsGroup.getSelectedToggle();
        boolean show = selected == customSettingsOption;
        customInputsBox.setVisible(show);
    }

    /**
     * Applies the startup style to the scene based on the default theme.
     * 
     * @param scene The scene to style.
     */
    private void setStartupStyle(Scene scene) {
        String defaultThemeStyle = guiStyleContext.getDefaultStylePath();
        if (defaultThemeStyle == null || defaultThemeStyle.isEmpty()) {
            logger.debug("Default theme style is: {}, hence using dark theme.", defaultThemeStyle);
            setStyle(scene, guiStyleContext.getDarkThemePath());
        } else {
            logger.debug("Default theme style is {} and going to use the same.", defaultThemeStyle);
            setStyle(scene, defaultThemeStyle);
        }
    }

    /**
     * Applies a specific style to the scene.
     * 
     * @param scene     The scene to style.
     * @param stylePath The path to the style file.
     */
    private void setStyle(Scene scene, String stylePath) {
        if (scene == null) {
            logger.error("Scene not set");
            return;
        }

        try {
            String resolvedThemePath = GUIStylePathResolver.resolveStylePath(stylePath);
            String resolvedCommonPath = GUIStylePathResolver.resolveStylePath(guiStyleContext.getCommonStylePath());
            scene.getStylesheets().setAll(resolvedCommonPath, resolvedThemePath);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to load styles: " + stylePath, e);
        }
    }

    /**
     * Selects the default theme based on the configuration.
     * 
     * @param lightTheme  The light theme radio button.
     * @param darkTheme   The dark theme radio button.
     * @param pastelTheme The pastel theme radio button.
     */
    private void selectDefaultTheme(RadioButton lightTheme, RadioButton darkTheme, RadioButton pastelTheme) {
        String defaultThemeStyle = guiStyleContext.getDefaultStylePath();

        if (guiStyleContext.getDarkThemePath().equals(defaultThemeStyle)) {
            darkTheme.setSelected(true);
        } else if (guiStyleContext.getLightThemePath().equals(defaultThemeStyle)) {
            lightTheme.setSelected(true);
        } else if (guiStyleContext.getPastelThemePath().equals(defaultThemeStyle)) {
            pastelTheme.setSelected(true);
        } else {
            lightTheme.setSelected(true);
            logger.warn("Invalid or missing default theme. Falling back to light theme.");
        }
    }

    /**
     * Creates a UMLGenerationContext from a configuration file.
     * 
     * @param propertiesFile The path to the configuration file.
     * @return A {@link UMLGenerationContext} created from the configuration file.
     * @throws IOException If an error occurs while loading the configuration file.
     */
    UMLGenerationContext getContextFromFile(String propertiesFile) throws IOException {
        Properties fileProperties = ConfigurationLoader.loadProperties(propertiesFile);
        DefaultContextFactory contextFactory = new DefaultContextFactory(fileProperties);
        return contextFactory.createUMLContext();
    }

    /**
     * Creates a UMLGenerationContext based on custom user inputs.
     * 
     * @param initialContext The initial context to use as a fallback for missing inputs.
     * @return A new {@link UMLGenerationContext} based on user inputs and fallback values.
     */
    private UMLGenerationContext getContextFromCustomInputs(UMLGenerationContext initialContext) {
        String inputDir = inputDirField.getText();
        String outputDir = outputDirField.getText();
        String diagramTypes = diagramTypesField.getText();
        String includePackage = includePackageField.getText();

        logger.debug("Using custom inputs...");
        logger.debug("Input Directory: {}", inputDir);
        logger.debug("Output Directory: {}", outputDir);
        logger.debug("Diagram Types: {}", diagramTypes);
        logger.debug("Include Package: {}", includePackage);

        return new UMLGenerationContext(
                inputDir,
                outputDir,
                diagramTypes.isEmpty() ? initialContext.getDiagramTypes() : diagramTypes,
                includePackage,
                initialContext.getPumlPath(),
                initialContext.getImagesOutputDirectory(),
                initialContext.getUnifiedClassDiagram(),
                initialContext.getLibsDirPath());
    }
}
