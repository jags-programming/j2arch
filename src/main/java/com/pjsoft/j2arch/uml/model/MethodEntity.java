package com.pjsoft.j2arch.uml.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a method within a Java class.
 * 
 * This class encapsulates the details of a method, including its name, return type,
 * parameters, and visibility. It provides functionality for adding parameters,
 * comparing methods, and generating hash codes for use in collections.
 * 
 * Responsibilities:
 * - Encapsulates the details of a method, such as name, return type, parameters, and visibility.
 * - Supports comparison of methods for sorting.
 * - Provides equality and hash code implementations for use in collections.
 * 
 * Usage Example:
 * {@code
 * MethodEntity method = new MethodEntity("calculateSum", "int");
 * method.addParameter("int");
 * method.addParameter("int");
 * method.setVisibility("public");
 * System.out.println("Method: " + method.getName() + ", Return Type: " + method.getReturnType());
 * }
 * 
 * Limitations:
 * - Assumes that the method name and return type are non-null.
 * - Does not support advanced method features such as annotations or generic types.
 * 
 * Thread Safety:
 * - This class is not thread-safe as it maintains mutable state.
 * 
 * @author PJSoft
 * @version 1.1
 * @since 1.0
 */
public class MethodEntity implements Comparable<MethodEntity> {
    private String name; // The name of the method
    private String returnType; // The return type of the method
    private List<String> parameters; // The list of parameter types
    private String visibility; // The visibility of the method (e.g., public, private)

    /**
     * Constructs a new MethodEntity with the specified name and return type.
     * 
     * @param name the name of the method.
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
     * Gets the visibility of the method.
     * 
     * @return the visibility of the method (e.g., public, private).
     * @since 1.0
     */
    public String getVisibility() {
        return visibility;
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
     * Sets the visibility of the method.
     * 
     * @param visibility the visibility to set (e.g., public, private).
     * @since 1.0
     */
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    /**
     * Compares this method with another method for sorting purposes.
     * 
     * The comparison is based on the method name.
     * 
     * @param other the other method to compare with.
     * @return a negative integer, zero, or a positive integer as this method is less
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
        return name.equals(that.name) && returnType.equals(that.returnType);
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
        return Objects.hash(name, returnType);
    }

}