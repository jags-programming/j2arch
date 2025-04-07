package com.pjsoft.j2arch.uml.model;

import java.util.Objects;

/**
 * Represents a field within a Java class.
 * 
 * This class encapsulates the details of a field, including its name, type, and visibility.
 * It provides functionality for comparing fields, checking equality, and generating hash codes
 * for use in collections.
 * 
 * Responsibilities:
 * - Encapsulates the details of a field, such as name, type, and visibility.
 * - Supports comparison of fields for sorting.
 * - Provides equality and hash code implementations for use in collections.
 * 
 * Usage Example:
 * {@code
 * FieldEntity field = new FieldEntity("id", "int");
 * field.setVisibility("private");
 * System.out.println("Field: " + field.getName() + ", Type: " + field.getType());
 * }
 * 
 * Limitations:
 * - Assumes that the field name and type are non-null.
 * - Does not support advanced field features such as annotations or generics.
 * 
 * Thread Safety:
 * - This class is not thread-safe as it maintains mutable state.
 * 
 * @author PJSoft
 * @version 1.1
 * @since 1.0
 */
public class FieldEntity implements Comparable<FieldEntity> {
    private String name; // The name of the field
    private String type; // The type of the field
    private String visibility; // The visibility of the field (e.g., public, private)

    /**
     * Constructs a new FieldEntity with the specified name and type.
     * 
     * @param name the name of the field.
     * @param type the type of the field.
     * @since 1.0
     */
    public FieldEntity(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Gets the name of the field.
     * 
     * @return the name of the field.
     * @since 1.0
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the field.
     * 
     * @param name the name to set.
     * @since 1.0
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the type of the field.
     * 
     * @return the type of the field.
     * @since 1.0
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the field.
     * 
     * @param type the type to set.
     * @since 1.0
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the visibility of the field.
     * 
     * @return the visibility of the field (e.g., public, private).
     * @since 1.0
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * Sets the visibility of the field.
     * 
     * @param visibility the visibility to set (e.g., public, private).
     * @since 1.0
     */
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    /**
     * Compares this field with another field for sorting purposes.
     * 
     * The comparison is based on the field name.
     * 
     * @param other the other field to compare with.
     * @return a negative integer, zero, or a positive integer as this field is less
     *         than, equal to, or greater than the specified field.
     * @since 1.0
     */
    @Override
    public int compareTo(FieldEntity other) {
        return this.name.compareTo(other.name); // Compare by name
    }

    /**
     * Checks if this field is equal to another object.
     * 
     * Two fields are considered equal if they have the same name and type.
     * 
     * @param obj the object to compare with.
     * @return {@code true} if the fields are equal, {@code false} otherwise.
     * @since 1.0
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        FieldEntity that = (FieldEntity) obj;
        return name.equals(that.name) && type.equals(that.type);
    }

    /**
     * Generates a hash code for this field.
     * 
     * The hash code is based on the field name and type.
     * 
     * @return the hash code for this field.
     * @since 1.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}