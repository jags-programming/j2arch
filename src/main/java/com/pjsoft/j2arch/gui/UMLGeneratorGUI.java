package com.pjsoft.j2arch.gui;

import java.io.File;

// import com.pjsoft.j2arch.uml.ConfigurationManager; // Removed as it cannot be resolved


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class UMLGeneratorGUI extends Application {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UMLGeneratorGUI.class);

    @Override
    public void start(Stage primaryStage) {
        // Create TabPane
        TabPane tabPane = new TabPane();

        // Add Configuration Tab
        ConfigurationTab configurationTab = new ConfigurationTab(primaryStage);
        Tab configTabUI = new Tab("Configuration", configurationTab.getLayout());
        configTabUI.setClosable(false);

       
        // Add Preview Tab
        PreviewTab previeTab = new PreviewTab(primaryStage);
        Tab previewTabUI = new Tab("Diagram Preview", previeTab.getLayout());
        previewTabUI.setClosable(false);

        tabPane.getTabs().addAll(configTabUI, previewTabUI);

        // Set up Scene and Stage
        Scene scene = new Scene(tabPane, 800, 600);
        configurationTab.setScene(scene);

        primaryStage.setTitle("PJ Java2UML Diagram Generator");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/favicon-32x32.png")));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}