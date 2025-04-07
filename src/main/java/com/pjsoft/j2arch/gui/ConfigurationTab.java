package com.pjsoft.j2arch.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.pjsoft.j2arch.uml.config.ConfigurationManager;
import com.pjsoft.j2arch.uml.UMLDiagramGenerator;
import java.util.List;

public class ConfigurationTab {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ConfigurationTab.class);

    private final VBox layout;
    private Scene scene;

    private final VBox customInputsBox = new VBox(10);
    private final ToggleGroup configOptionsGroup = new ToggleGroup();
    private final RadioButton defaultSettingsOption = new RadioButton("Use default settings");
    private final RadioButton fileSettingsOption = new RadioButton("Load settings from a configuration file");
    private final RadioButton customSettingsOption = new RadioButton("Enter custom inputs interactively");

    // Fields used across methods
    private final TextField inputDirField = new TextField();
    private final TextField outputDirField = new TextField();
    private final TextField diagramTypesField = new TextField("class,sequence");
    private final TextField includePackageField = new TextField();
    private final Label messageLabel = new Label();
    private final ProgressIndicator progressIndicator = new ProgressIndicator();

    public ConfigurationTab(Stage primaryStage) {
        HBox topRow = createTopRow();
        VBox configOptionsBox = createConfigurationOptionsBox();
        TitledPane customInputsPane = createCustomInputsPane(primaryStage);
        customInputsBox.getChildren().add(customInputsPane);
        customInputsBox.setVisible(false);

        Button generateButton = createGenerateButton();

        layout = new VBox(15, topRow, configOptionsBox, customInputsBox, generateButton, progressIndicator, messageLabel);
        layout.setPadding(new Insets(20));
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        // Apply the default theme initially
        setStyle(scene, "style.light");
    }

    public VBox getLayout() {
        return layout;
    }

    private HBox createTopRow() {
        Label bannerLabel = new Label("Welcome to UML Generator");
        bannerLabel.setId("bannerLabel");
        ToggleGroup themeToggleGroup = new ToggleGroup();
        RadioButton lightTheme = createThemeRadio("Light", themeToggleGroup, "style.light");
        RadioButton darkTheme = createThemeRadio("Dark", themeToggleGroup, "style.dark");
        RadioButton pastelTheme = createThemeRadio("Pastel", themeToggleGroup, "style.pastel");
        lightTheme.setSelected(true);

        HBox themeBox = new HBox(10, lightTheme, darkTheme, pastelTheme);
        themeBox.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topRow = new HBox(10, bannerLabel, spacer, themeBox);
        topRow.setPadding(new Insets(10));
        topRow.setAlignment(Pos.CENTER_LEFT);

        return topRow;
    }

    private RadioButton createThemeRadio(String label, ToggleGroup group, String styleProperty) {
        RadioButton rb = new RadioButton(label);
        rb.setToggleGroup(group);
        rb.setOnAction(e -> setStyle(scene, styleProperty));
        return rb;
    }

    private VBox createConfigurationOptionsBox() {
        Label configOptionsLabel = new Label("How do you want to proceed?");
        defaultSettingsOption.setToggleGroup(configOptionsGroup);
        fileSettingsOption.setToggleGroup(configOptionsGroup);
        customSettingsOption.setToggleGroup(configOptionsGroup);
        defaultSettingsOption.setSelected(true);

        configOptionsGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> updateCustomInputsVisibility());

        return new VBox(10, configOptionsLabel, defaultSettingsOption, fileSettingsOption, customSettingsOption);
    }

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

    private Button createGenerateButton() {
        Button generateButton = new Button("Generate UML Diagrams");
        progressIndicator.setVisible(false);

        generateButton.setOnAction(event -> {
            
            progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            progressIndicator.setVisible(true);
            messageLabel.setText("Generating UML diagrams...");

            new Thread(() -> {
                try {
                    ConfigurationManager configManager = ConfigurationManager.getInstance();
                    configManager.clearSettings();

                    if (defaultSettingsOption.isSelected()) {
                        configManager.loadDefaultConfig();
                    } else if (fileSettingsOption.isSelected()) {
                        configManager.loadFileConfig("config/application.properties");
                    } else {
                        configManager.setProperty("input.directory", inputDirField.getText());
                        configManager.setProperty("output.directory", outputDirField.getText());
                        configManager.setProperty("diagram.types", diagramTypesField.getText());
                        configManager.setProperty("include.package", includePackageField.getText());
                    }

                    UMLDiagramGenerator generator = new UMLDiagramGenerator(configManager);
                    generator.generateDiagrams();

                    Platform.runLater(() -> {
                        progressIndicator.setVisible(false);
                        progressIndicator.setProgress(0);
                        
                        messageLabel.setText("UML diagrams generated successfully!");
                    });

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        progressIndicator.setVisible(false);
                        progressIndicator.setProgress(0);
                        messageLabel.setText("Error: " + e.getMessage());
                    });
                }
            }).start();
        });

        return generateButton;
    }

    private void updateCustomInputsVisibility() {
        RadioButton selected = (RadioButton) configOptionsGroup.getSelectedToggle();
        boolean show = selected == customSettingsOption;
        customInputsBox.setVisible(show);
    }

    private void setStyle(Scene scene, String styleProperty) {
        if (scene == null) {
            logger.error("Scene not set");
            return;
        }

        ConfigurationManager configManager = ConfigurationManager.getInstance();
        String themeFilePath = configManager.getProperty(styleProperty);
        String commonStyleFilePath = configManager.getProperty("style.common");
        File themeFile = new File(themeFilePath);
        File commonStyleFile = new File(commonStyleFilePath);
      
        if (themeFile.exists() && commonStyleFile.exists()) {
            String commonStyleURI = commonStyleFile.toURI().toString();
            String themeStyleURI = themeFile.toURI().toString();
            scene.getStylesheets().setAll(themeStyleURI, commonStyleURI);
        } else {
            logger.warn("Falling back to default styles");
            loadFallbackStyle(scene, styleProperty);
        }
    }

    private void loadFallbackStyle(Scene scene, String styleProperty) {
        
        String fallbackFile;
        switch (styleProperty) {
            case "style.dark" -> fallbackFile = "styles/style_dark.css";
            case "style.pastel" -> fallbackFile = "styles/style_pastel.css";
            default -> fallbackFile = "styles/style_light.css";
        }
        
        File defaultThemeFile = new File(fallbackFile);
        File commonStyleFile =new File("styles/style_common.css");
        if (defaultThemeFile.exists() && commonStyleFile.exists()) {
            String themeURI = defaultThemeFile.toURI().toString();
            String commonStyleURI = commonStyleFile.toURI().toString();
            scene.getStylesheets().setAll(themeURI,commonStyleURI);
            
        } else {
            logger.error("No fallback style found either.");
          
        }
    }
}
