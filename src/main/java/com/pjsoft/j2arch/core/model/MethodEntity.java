package com.pjsoft.j2arch.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a method within a Java class.
 * 
 * This class encapsulates the details of a method, including its name, return
 * type,
 * parameters, visibility, annotations, and constructors. It provides
 * functionality
 * for adding parameters, annotations, and constructors, comparing methods, and
 * generating hash codes for use in collections.
 * 
 * Responsibilities:
 * - Encapsulates the details of a method, such as name, return type,
 * parameters, visibility, and annotations.
 * - Supports comparison of methods for sorting.
 * - Provides equality and hash code implementations for use in collections.
 * - Allows adding constructors and annotations to the method.
 * 
 * Limitations:
 * - Assumes that the method name and return type are non-null.
 * - Does not support advanced method features such as generic types or
 * method-level annotations with parameters.
 * 
 * Thread Safety:
 * - This class is not thread-safe as it maintains mutable state.
 * 
 * Usage Example:
 * {@code
 * MethodEntity method = new MethodEntity("calculateSum", "int");
 * method.addParameter("int");
 * method.addParameter("int");
 * method.setVisibility("public");
 * method.addAnnotation("Override");
 * System.out.println("Method: " + method.getName() + ", Return Type: " + method.getReturnType());
 * }
 * 
 * Author: PJSoft
 * Version: 1.2
 * Since: 1.0
 */
public class MethodEntity implements Comparable<MethodEntity> {
    private String name; // The name of the method
    private String returnType; // The return type of the method
    private List<String> parameters; // The list of parameter types
    private String visibility; // The visibility of the method (e.g., public, private)
    private final List<MethodEntity> constructors = new ArrayList<>(); // List of constructors associated with the
                                                                       // method
    private final List<String> annotations = new ArrayList<>(); // List of annotations applied to the method

    /**
     * Constructs a new MethodEntity with the specified name and return type.
     * 
     * @param name       the name of the method.
     * @param returnType the return type of the method.
     * @since 1.0
     */
    public MethodEntity(String name, String returnType) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = new ArrayList<>();
    }

    /**
     * Gets the name of the method.
     * 
     * @return the name of the method.
     * @since 1.0
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the return type of the method.
     * 
     * @return the return type of the method.
     * @since 1.0
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * Gets the list of parameter types for the method.
     * 
     * @return a list of parameter types.
     * @since 1.0
     */
    public List<String> getParameters() {
        return parameters;
    }

    /**
     * Sets the list of parameter types for the method.
     * 
     * @param parameters the list of parameter types to set.
     * @since 1.1
     */
    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    /**
     * Gets the visibility of the method.
     * 
     * @return the visibility of the method (e.g., public, private).
     * @since 1.0
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * Sets the visibility of the method.
     * 
     * @param visibility the visibility to set (e.g., public, private).
     * @since 1.0
     */
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    /**
     * Adds a parameter type to the method.
     * 
     * @param paramType the type of the parameter to add.
     * @since 1.0
     */
    public void addParameter(String paramType) {
        parameters.add(paramType);
    }

    /**
     * Gets the list of constructors associated with the method.
     * 
     * @return a list of {@link MethodEntity} objects representing constructors.
     * @since 1.1
     */
    public List<MethodEntity> getConstructors() {
        return constructors;
    }

    /**
     * Adds a constructor to the method.
     * 
     * @param constructor the {@link MethodEntity} representing the constructor to
     *                    add.
     * @since 1.1
     */
    public void addConstructor(MethodEntity constructor) {
        if (constructor != null) {
            constructors.add(constructor);
        }
    }

    /**
     * Gets the list of annotations applied to the method.
     * 
     * @return a list of annotations.
     * @since 1.2
     */
    public List<String> getAnnotations() {
        return annotations;
    }

    /**
     * Adds an annotation to the method.
     * 
     * @param annotation the annotation to add.
     * @since 1.2
     */
    public void addAnnotation(String annotation) {
        if (annotation != null && !annotation.isEmpty()) {
            annotations.add(annotation);
        }
    }

    /**
     * Compares this method with another method for sorting purposes.
     * 
     * The comparison is based on the method name.
     * 
     * @param other the other method to compare with.
     * @return a negative integer, zero, or a positive integer as this method is
     *         less
     *         than, equal to, or greater than the specified method.
     * @since 1.0
     */
    @Override
    public int compareTo(MethodEntity other) {
        return this.name.compareTo(other.name); // Compare by name
    }

    /**
     * Checks if this method is equal to another object.
     * 
     * Two methods are considered equal if they have the same name and return type.
     * 
     * @param obj the object to compare with.
     * @return {@code true} if the methods are equal, {@code false} otherwise.
     * @since 1.0
     */

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        MethodEntity that = (MethodEntity) obj;
        return name.equals(that.name) &&
                returnType.equals(that.returnType) &&
                parameters.equals(that.parameters); // Include parameter types
    }

    /**
     * Generates a hash code for this method.
     * 
     * The hash code is based on the method name and return type.
     * 
     * @return the hash code for this method.
     * @since 1.0
     */

    @Override
    public int hashCode() {
        return Objects.hash(name, returnType, parameters); // Include parameter types
    }

    public boolean hasParameterType(String parameterType) {
        return parameters != null && parameters.contains(parameterType);
    }

    public boolean hasAnnotation(String annotation) {
        return annotations.contains(annotation);
    }
}