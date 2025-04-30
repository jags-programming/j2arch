package com.pjsoft.j2arch.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a package in the source code.
 * 
 * This class encapsulates information about a package, including its fully qualified name,
 * the classes/interfaces it contains, and its relationships with other packages. It also
 * optionally stores the path to a package diagram for visualization purposes.
 * 
 * Responsibilities:
 * - Store metadata about a package, including its name, classes, and related packages.
 * - Provide methods to add classes and related packages.
 * - Optionally store and retrieve the path to the package diagram.
 * 
 * Limitations:
 * - Assumes that the package name is valid and unique within the context of the application.
 * - Does not validate the correctness of the classes or related packages added.
 * 
 * Usage Example:
 * {@code
 * PackageEntity packageEntity = new PackageEntity("com.example");
 * packageEntity.addClass(new CodeEntity("com.example.ClassA"));
 * packageEntity.addRelatedPackage(new PackageEntity("com.example.subpackage"));
 * System.out.println(packageEntity);
 * }
 * 
 * Author: PJSoft
 * Version: 1.1
 * Since: 1.0
 */
public class PackageEntity {

    private final String name; // Fully qualified package name
    private final List<CodeEntity> classes; // Classes/interfaces in the package
    private final List<PackageEntity> relatedPackages; // Related packages
    private String packageDiagram; // Path to the package diagram (optional)

    /**
     * Constructor to initialize the package entity with its name.
     *
     * @param name The fully qualified name of the package.
     */
    public PackageEntity(String name) {
        this.name = name;
        this.classes = new ArrayList<>();
        this.relatedPackages = new ArrayList<>();
    }

    // Getters and Setters

    /**
     * Gets the fully qualified name of the package.
     * 
     * @return The package name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of classes/interfaces in the package.
     * 
     * @return A list of {@link CodeEntity} objects representing the classes/interfaces.
     */
    public List<CodeEntity> getClasses() {
        return classes;
    }

    /**
     * Gets the list of related packages.
     * 
     * @return A list of {@link PackageEntity} objects representing related packages.
     */
    public List<PackageEntity> getRelatedPackages() {
        return relatedPackages;
    }

    /**
     * Gets the path to the package diagram.
     * 
     * @return The path to the package diagram, or {@code null} if not set.
     */
    public String getPackageDiagram() {
        return packageDiagram;
    }

    /**
     * Sets the path to the package diagram.
     * 
     * @param packageDiagram The path to the package diagram.
     */
    public void setPackageDiagram(String packageDiagram) {
        this.packageDiagram = packageDiagram;
    }

    // Methods to add classes and related packages

    /**
     * Adds a class or interface to the package.
     * 
     * @param codeEntity The {@link CodeEntity} representing the class or interface to add.
     */
    public void addClass(CodeEntity codeEntity) {
        if (codeEntity != null) {
            classes.add(codeEntity);
        }
    }

    /**
     * Adds a related package to the package.
     * 
     * @param packageEntity The {@link PackageEntity} representing the related package to add.
     */
    public void addRelatedPackage(PackageEntity packageEntity) {
        if (packageEntity != null) {
            relatedPackages.add(packageEntity);
        }
    }

    /**
     * Returns a string representation of the package entity.
     * 
     * @return A string containing the package name, number of classes, and number of related packages.
     */
    @Override
    public String toString() {
        return "PackageEntity{name='" + name + "', classes=" + classes.size() +
               ", relatedPackages=" + relatedPackages.size() + "}";
    }
}
