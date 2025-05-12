package com.pjsoft.j2arch.docgen.javadoc;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import com.pjsoft.j2arch.core.model.PackageEntity;
import com.pjsoft.j2arch.core.util.ProgressTracker;
import com.pjsoft.j2arch.core.util.ProgressTracker.WorkUnitType;
import com.pjsoft.j2arch.docgen.javadoc.service.JavaDocClassPageGenerator;
import com.pjsoft.j2arch.docgen.javadoc.service.JavaDocIndexPageGenerator;
import com.pjsoft.j2arch.docgen.javadoc.service.JavaDocPackagePageGenerator;
import com.pjsoft.j2arch.docgen.javadoc.util.JavaDocGenerationContext;
import com.pjsoft.j2arch.uml.service.ClassDiagramService;
import com.pjsoft.j2arch.uml.service.PackageDiagramService;
import com.pjsoft.j2arch.uml.util.ProjectAnalyzer;

/**
 * JavaDocGenerator
 * 
 * Entry point for the Javadoc generation process. This class orchestrates the
 * generation of documentation by delegating tasks to other components such as
 * ProjectAnalyzer, JavaDocPackagePageGenerator, JavaDocClassPageGenerator, and
 * JavaDocIndexPageGenerator.
 * 
 * Responsibilities:
 * - Orchestrate the overall documentation generation process.
 * - Delegate tasks to specialized components for package, class, and index documentation.
 * - Handle configuration, file writing, and progress tracking.
 * - Provide error handling and logging during the generation process.
 * 
 * Dependencies:
 * - {@link ProjectAnalyzer}: Analyzes the project to extract package and class information.
 * - {@link JavaDocPackagePageGenerator}: Generates package-level documentation.
 * - {@link JavaDocClassPageGenerator}: Generates class-level documentation.
 * - {@link JavaDocIndexPageGenerator}: Generates the index page for the documentation.
 * - {@link ProgressTracker}: Tracks progress and updates the status during the process.
 * 
 * Limitations:
 * - Assumes that the input project structure and configuration are valid.
 * - Relies on external templates and CSS files for documentation styling.
 * 
 * Thread Safety:
 * - This class is not thread-safe as it relies on sequential execution of tasks.
 * 
 * Usage Example:
 * {@code
 * JavaDocGenerationContext context = new JavaDocGenerationContext(...);
 * ProgressTracker progressTracker = new ProgressTracker(...);
 * JavaDocGenerator generator = new JavaDocGenerator();
 * generator.generateJavaDoc(context, progressTracker);
 * }
 * 
 * Author: PJSoft
 * Version: 1.0
 * Since: 1.0
 */
public class JavaDocGenerator {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JavaDocGenerator.class);

    /**
     * Default constructor for JavaDocGenerator.
     */
    public JavaDocGenerator() {
        // Default constructor
    }

    /**
     * Orchestrates the entire Javadoc generation process.
     * 
     * Responsibilities:
     * - Analyzes the project to extract package and class information.
     * - Generates package-level and class-level documentation.
     * - Generates the index page for the documentation.
     * - Copies CSS files to the output directory.
     * 
     * @param context         The {@link JavaDocGenerationContext} containing configuration details.
     * @param progressTracker The {@link ProgressTracker} to track progress and update status.
     * @throws IOException If an error occurs during the documentation generation process.
     */
    public void generateJavaDoc(JavaDocGenerationContext context, ProgressTracker progressTracker) throws IOException {
        try {
            // Step 1: Analyze the project to extract package and class information
            progressTracker.onStatusUpdate("Analyzing project...");
            ProjectAnalyzer projectAnalyzer = new ProjectAnalyzer();
            Map<String, PackageEntity> packageEntities = projectAnalyzer.analyzeProjectForPackages(context, progressTracker);
            progressTracker.onStatusUpdate("Project analysis completed");

            // Step 2: Generate package-level documentation
            progressTracker.onStatusUpdate("Generating package-level documentation...");
            generatePackageDocumentation(packageEntities, context, progressTracker);
            progressTracker.onStatusUpdate("Package documentation completed");

            // Step 3: Generate class-level documentation
            progressTracker.onStatusUpdate("Generating class-level documentation...");
            generateClassDocumentation(packageEntities, context, progressTracker);
            progressTracker.onStatusUpdate("Class level documentation completed");

            // Step 4: Generate the index page
            progressTracker.addTotalUnits(WorkUnitType.INDEX_PAGE, 1);
            progressTracker.onStatusUpdate("Generating index page...");
            generateIndexDocumentation(packageEntities, context);
            progressTracker.onStatusUpdate("Index page generated");
            progressTracker.addCompletedUnits(WorkUnitType.INDEX_PAGE, 1);

            // Step 5: Copy CSS to the output directory
            progressTracker.addTotalUnits(WorkUnitType.CSS, 1);
            progressTracker.onStatusUpdate("Copying CSS to output directory...");
            copyCssToOutputDirectory(context);
            progressTracker.onStatusUpdate("CSS file copied to destination");
            progressTracker.addCompletedUnits(WorkUnitType.CSS, 1);

            logger.info("Javadoc generation completed successfully!");
        } catch (Exception e) {
            logger.error("Error during Javadoc generation: " + e.getMessage());
        }
    }

    /**
     * Generates package-level documentation by delegating to {@link JavaDocPackagePageGenerator}.
     * 
     * Responsibilities:
     * - Generates package diagrams using {@link PackageDiagramService}.
     * - Generates package-level documentation pages.
     * 
     * @param packageEntities A map of package names to {@link PackageEntity} objects.
     * @param context         The {@link JavaDocGenerationContext} containing configuration details.
     * @param progressTracker The {@link ProgressTracker} to track progress and update status.
     * @throws IOException If an error occurs during package documentation generation.
     */
    private void generatePackageDocumentation(Map<String, PackageEntity> packageEntities,
            JavaDocGenerationContext context, ProgressTracker progressTracker) throws IOException {

        PackageDiagramService packageDiagramService = new PackageDiagramService();
        JavaDocPackagePageGenerator packagePageGenerator = new JavaDocPackagePageGenerator();

        Collection<PackageEntity> packages = packageEntities.values();
        progressTracker.addTotalUnits(WorkUnitType.PACKAGE_DOC, packages.size());
        for (PackageEntity packageEntity : packages) {
            // Step 1: Generate package diagram
            packageDiagramService.generatePackageDiagram(packageEntity, context);

            // Step 2: Generate package page
            packagePageGenerator.generatePackagePage(packageEntity, context);

            // Update progress tracker
            progressTracker.addCompletedUnits(WorkUnitType.PACKAGE_DOC, 1);
        }

        logger.info("Package documentation generation completed.");
    }

    /**
     * Generates class-level documentation by delegating to {@link JavaDocClassPageGenerator}.
     * 
     * Responsibilities:
     * - Generates class diagrams using {@link ClassDiagramService}.
     * - Generates class-level documentation pages.
     * 
     * @param packageEntities A map of package names to {@link PackageEntity} objects.
     * @param context         The {@link JavaDocGenerationContext} containing configuration details.
     * @param progressTracker The {@link ProgressTracker} to track progress and update status.
     * @throws IOException If an error occurs during class documentation generation.
     */
    private void generateClassDocumentation(Map<String, PackageEntity> packageEntities,
            JavaDocGenerationContext context, ProgressTracker progressTracker) throws IOException {

        ClassDiagramService classDiagramService = new ClassDiagramService();
        JavaDocClassPageGenerator classPageGenerator = new JavaDocClassPageGenerator();

        Collection<PackageEntity> packages = packageEntities.values();
        progressTracker.addTotalUnits(WorkUnitType.CLASS_DOC, packages.size());
        for (PackageEntity packageEntity : packages) {
            for (var codeEntity : packageEntity.getClasses()) {
                // Step 1: Generate class diagram
                classDiagramService.generateClassDiagram(codeEntity, context);

                // Step 2: Generate class page
                classPageGenerator.generateClassPage(codeEntity, context);

                
            }
            // Update progress tracker
            progressTracker.addCompletedUnits(WorkUnitType.CLASS_DOC, 1);
        }

        logger.info("Class documentation generation completed.");
    }

    /**
     * Generates the index page by delegating to {@link JavaDocIndexPageGenerator}.
     * 
     * Responsibilities:
     * - Generates the index page linking all package and class documentation.
     * 
     * @param packageEntities A map of package names to {@link PackageEntity} objects.
     * @param context         The {@link JavaDocGenerationContext} containing configuration details.
     * @throws IOException If an error occurs during index page generation.
     */
    private void generateIndexDocumentation(Map<String, PackageEntity> packageEntities,
            JavaDocGenerationContext context) throws IOException {
        JavaDocIndexPageGenerator indexPageGenerator = new JavaDocIndexPageGenerator();
        indexPageGenerator.generateIndexPage(packageEntities, context);

        logger.info("Index page generation completed.");
    }

    /**
     * Copies the CSS file from the classpath to the output directory.
     * 
     * Responsibilities:
     * - Reads the CSS file from the classpath.
     * - Writes the CSS file to the specified output directory.
     * 
     * @param context The {@link JavaDocGenerationContext} containing configuration details.
     * @throws IOException If an error occurs during the file copy operation.
     */
    private void copyCssToOutputDirectory(JavaDocGenerationContext context) throws IOException {
        String cssClasspath = context.getStyleSourceFile();
        String cssOutputPath = context.getStyleOutputFile();

        try (InputStream inputStream = getClass().getResourceAsStream(cssClasspath);
                OutputStream outputStream = new FileOutputStream(cssOutputPath)) {

            if (inputStream == null) {
                throw new FileNotFoundException("CSS file not found in classpath: " + cssClasspath);
            }

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            logger.debug("CSS file copied to: " + cssOutputPath);
        }
    }
}