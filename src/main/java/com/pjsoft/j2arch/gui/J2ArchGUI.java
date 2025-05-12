package com.pjsoft.j2arch.gui;

import java.util.Properties;

import com.pjsoft.j2arch.config.ConfigurationLoader;
import com.pjsoft.j2arch.config.ContextFactory;
import com.pjsoft.j2arch.config.DefaultContextFactory;
import com.pjsoft.j2arch.gui.util.GUIStyleContext;
import com.pjsoft.j2arch.uml.util.UMLGenerationContext;
import com.pjsoft.j2arch.docgen.pumldoc.util.HtmlGenerationContext;
import com.pjsoft.j2arch.docgen.javadoc.util.JavaDocGenerationContext;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * UMLGeneratorGUI
 * 
 * The main GUI application for the UML Diagram Generator. This class initializes
 * the JavaFX application, sets up the main window, and manages the tabs for
 * configuration, diagram preview, HTML documentation generation, and JavaDoc
 * generation.
 * 
 * Responsibilities:
 * - Loads application configuration using {@link ConfigurationLoader}.
 * - Creates and initializes context objects using {@link ContextFactory}.
 * - Sets up the main GUI layout with tabs for different functionalities.
 * - Handles the primary stage and scene setup for the JavaFX application.
 * 
 * Dependencies:
 * - {@link ConfigurationLoader}: Loads application properties.
 * - {@link ContextFactory}: Creates context objects for different functionalities.
 * - {@link GUIStyleContext}, {@link UMLGenerationContext}, {@link HTMLGenerationContext},
 *   {@link JavaDocGenerationContext}: Provide configuration details for various tabs.
 * - JavaFX: Used for building the GUI.
 * 
 * Limitations:
 * - Assumes that the configuration file is valid and accessible.
 * - Does not handle advanced error recovery for GUI initialization failures.
 * 
 * Thread Safety:
 * - This class is not thread-safe as it relies on JavaFX's single-threaded model.
 * 
 * Usage:
 * - Run the application using the `main` method.
 * 
 * @author PJSoft
 * @version 2.2
 * @since 1.0
 */
public class J2ArchGUI extends Application {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(J2ArchGUI.class);

    /**
     * Starts the JavaFX application.
     * 
     * Responsibilities:
     * - Loads configuration properties.
     * - Initializes context objects for GUI styling, UML generation, HTML generation, and JavaDoc generation.
     * - Sets up the main window with tabs for different functionalities.
     * - Displays the primary stage.
     * 
     * @param primaryStage The primary stage for the JavaFX application.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load properties using ConfigurationLoader
            Properties properties = ConfigurationLoader.loadConfiguration();

            // Use the factory to create the contexts
            ContextFactory factory = new DefaultContextFactory(properties);
            GUIStyleContext guiStyleContext = factory.createGUIStyleContext();
            UMLGenerationContext umlContext = factory.createUMLContext();
            HtmlGenerationContext htmlContext = factory.createHTMLContext();
            JavaDocGenerationContext javaDocContext = factory.createJavaDocContext();

            // Create TabPane
            TabPane tabPane = new TabPane();

            // Add Configuration Tab
            UmlGenTab umlGenTab = new UmlGenTab(primaryStage, guiStyleContext, umlContext);
            Tab configTabUI = new Tab("UML Generator", umlGenTab.getLayout());
            configTabUI.setClosable(false);

            // Add Preview Tab
            PreviewTab previewTab = new PreviewTab(primaryStage, umlContext);
            Tab previewTabUI = new Tab("Diagram Preview", previewTab.getLayout());
            previewTabUI.setClosable(false);

            // Add HTML DocGen Tab
            Puml2HtmlDocGenTab htmlDocGenTab = new Puml2HtmlDocGenTab(primaryStage, htmlContext);
            Tab htmlDocGenTabUI = new Tab("HTML DocGen", htmlDocGenTab.getContent());
            htmlDocGenTabUI.setClosable(false);

            // Add JavaDocGen Tab
            JavaDocGenTab javaDocGenTab = new JavaDocGenTab(primaryStage, javaDocContext);
            Tab javaDocGenTabUI = new Tab("JavaDoc Generator", javaDocGenTab.getContent());
            javaDocGenTabUI.setClosable(false);

            // Add all tabs to the TabPane
            tabPane.getTabs().addAll(configTabUI, previewTabUI, htmlDocGenTabUI, javaDocGenTabUI);

            // Set up Scene and Stage
            Scene scene = new Scene(tabPane, 800, 600);
            umlGenTab.setScene(scene);

            primaryStage.setTitle("PJ Java2UML Diagram Generator");
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/favicon-32x32.png")));
            primaryStage.show();
        } catch (Exception e) {
            logger.error("Error initializing application", e);
        }
    }

    /**
     * The main entry point for the application.
     * 
     * Responsibilities:
     * - Launches the JavaFX application.
     * 
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}