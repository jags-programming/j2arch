package com.pjsoft.j2arch.config;

import com.pjsoft.j2arch.docgen.javadoc.util.JavaDocGenerationContext;
import com.pjsoft.j2arch.docgen.pumldoc.util.HtmlGenerationContext;
import com.pjsoft.j2arch.gui.util.GUIStyleContext;
import com.pjsoft.j2arch.uml.util.UMLGenerationContext;

/**
 * ContextFactory
 * 
 * An interface for creating various context objects used in the application.
 * This factory interface defines methods for creating contexts for UML generation,
 * HTML generation, JavaDoc generation, and GUI styling. Implementations of this
 * interface provide the logic for initializing these contexts with appropriate
 * configurations.
 * 
 * Responsibilities:
 * - Define methods for creating UMLGenerationContext, HTMLGenerationContext, JavaDocGenerationContext, and GUIStyleContext.
 * - Support both default/file-based configurations and custom inputs for context creation.
 * 
 * Limitations:
 * - Assumes that the provided inputs or configurations are valid and accessible.
 * - Does not enforce validation of the inputs or configurations.
 * 
 * Usage Example:
 * {@code
 * ContextFactory factory = new DefaultContextFactory(properties);
 * UMLGenerationContext umlContext = factory.createUMLContext();
 * JavaDocGenerationContext javaDocContext = factory.createJavaDocContext();
 * }
 * 
 * Author: PJSoft
 * Version: 1.2
 * Since: 1.0
 */
public interface ContextFactory {

    /**
     * Creates a JavaDocGenerationContext using default or file-based settings.
     * 
     * @return A {@link JavaDocGenerationContext} initialized with default settings.
     * @since 1.0
     */
    JavaDocGenerationContext createJavaDocContext();

    /**
     * Creates a JavaDocGenerationContext using custom inputs.
     * 
     * @param inputDirectory The input directory for JavaDoc source files.
     * @param outputDirectory The output directory for generated JavaDoc files.
     * @param indexTemplate The template file for the JavaDoc index.
     * @param classTemplate The template file for JavaDoc class documentation.
     * @param packageTemplate The template file for JavaDoc package documentation.
     * @param styleSourceFile The stylesheet file for JavaDoc.
     * @param includePackage The package inclusion filter for processing specific packages.
     * @param libsDirPath The path to the directory containing library dependencies.
     * @return A {@link JavaDocGenerationContext} initialized with the specified inputs.
     * @since 1.0
     */
    JavaDocGenerationContext createJavaDocContext(String inputDirectory, String outputDirectory, String indexTemplate,
                                                  String classTemplate, String packageTemplate, String styleSourceFile,
                                                  String includePackage, String libsDirPath);

    /**
     * Creates a UMLGenerationContext using default or file-based settings.
     * 
     * @return A {@link UMLGenerationContext} initialized with default settings.
     * @since 1.0
     */
    UMLGenerationContext createUMLContext();

    /**
     * Creates a UMLGenerationContext using custom inputs.
     * 
     * @param inputDirectory The input directory for UML source files.
     * @param outputDirectory The output directory for generated UML files.
     * @param diagramTypes The types of diagrams to generate (e.g., class, sequence).
     * @param includePackage The package inclusion filter for processing specific packages.
     * @param pumlPath The path to the directory for PlantUML files.
     * @param imagesOutputPath The path to the directory for image outputs.
     * @param unifiedClassDiagram The filename for the unified class diagram.
     * @param libsDirPath The path to the directory containing library dependencies.
     * @return A {@link UMLGenerationContext} initialized with the specified inputs.
     * @since 1.0
     */
    UMLGenerationContext createUMLContext(String inputDirectory, String outputDirectory, String diagramTypes,
                                          String includePackage, String pumlPath, String imagesOutputPath,
                                          String unifiedClassDiagram, String libsDirPath);

    /**
     * Creates an HTMLGenerationContext using default or file-based settings.
     * 
     * @return A {@link HTMLGenerationContext} initialized with default settings.
     * @since 1.0
     */
    HtmlGenerationContext createHTMLContext();

    /**
     * Creates an HTMLGenerationContext using custom inputs.
     * 
     * @param inputDirectory The input directory for HTML source files.
     * @param outputDirectory The output directory for generated HTML files.
     * @param diagramTemplateFile The template file for HTML diagram documentation.
     * @param indexTemplateFile The template file for the HTML index.
     * @param styleSourceFile The stylesheet file for HTML documentation.
     * @return A {@link HTMLGenerationContext} initialized with the specified inputs.
     * @since 1.0
     */
    HtmlGenerationContext createHTMLContext(String inputDirectory, String outputDirectory, String diagramTemplateFile,
                                            String indexTemplateFile, String styleSourceFile);

    /**
     * Creates a GUIStyleContext using default or file-based settings.
     * 
     * @return A {@link GUIStyleContext} initialized with default settings.
     * @since 1.0
     */
    GUIStyleContext createGUIStyleContext();
}
