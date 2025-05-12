package com.pjsoft.j2arch.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a Java class or interface.
 * 
 * This class encapsulates the details of a Java entity, including its name,
 * methods, fields, annotations, constructors, and relationships with other entities.
 * It provides functionality for adding, removing, and retrieving methods, fields,
 * annotations, constructors, and relationships.
 * 
 * Responsibilities:
 * - Encapsulates the details of a Java class or interface.
 * - Manages methods, fields, annotations, constructors, and relationships associated with the entity.
 * - Supports equality checks, hash code generation, and string representation.
 * - Provides thread-safe access to methods, fields, and relationships.
 * 
 * Limitations:
 * - Assumes that the entity name is non-null and non-empty.
 * - Does not support advanced features such as generics or method-level annotations with parameters.
 * 
 * Thread Safety:
 * - This class is thread-safe for concurrent access to methods, fields, and relationships.
 * 
 * Usage Example:
 * {@code
 * CodeEntity entity = new CodeEntity("com.example.MyClass");
 * entity.addMethod(new MethodEntity("calculateSum", "int"));
 * entity.addField(new FieldEntity("id", "int"));
 * entity.addAnnotation("Entity");
 * System.out.println("Entity: " + entity.getName());
 * }
 * 
 * Author: PJSoft
 * Version: 1.2
 * Since: 1.0
 */
public class CodeEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(CodeEntity.class);

    private final String name; // The name of the entity (e.g., class or interface)
    private final Set<MethodEntity> methods; // Set of methods in the entity
    private final Set<FieldEntity> fields; // Set of fields in the entity
    private final List<Relative> relatives; // List of relationships for class diagrams
    private final List<MethodEntity> constructors = new ArrayList<>(); // List of constructors
    private final List<String> annotations = new ArrayList<>(); // List of annotations
    private String classDiagram; // Path to the class diagram (optional)
    private boolean isEntryPoint; // Indicates if this class is an entry point
    private String entryPointType;
    
    public String getEntryPointType() {
        return entryPointType;
    }

    public void setEntryPointType(String entryPointType) {
        this.entryPointType = entryPointType;
    }

    public boolean isEntryPoint() {
        return isEntryPoint;
    }

    public void setEntryPoint(boolean isEntryPoint) {
        this.isEntryPoint = isEntryPoint;
    }

    /**
     * Constructs a new CodeEntity with the specified name.
     * 
     * @param name the name of the entity (must not be null or empty).
     * @throws IllegalArgumentException if the name is null or empty.
     * @since 1.0
     */
    public CodeEntity(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Entity name cannot be null or empty");
        }
        this.name = name;
        this.methods = new ConcurrentSkipListSet<>();
        this.fields = new ConcurrentSkipListSet<>();
        this.relatives = new ArrayList<>();
    }

    /**
     * Returns the name of the entity.
     * 
     * @return the name of the entity.
     * @since 1.0
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the path to the class diagram.
     * 
     * @return the path to the class diagram, or {@code null} if not set.
     * @since 1.2
     */
    public String getClassDiagram() {
        return classDiagram;
    }

    /**
     * Sets the path to the class diagram.
     * 
     * @param classDiagram the path to the class diagram.
     * @since 1.2
     */
    public void setClassDiagram(String classDiagram) {
        this.classDiagram = classDiagram;
    }

    /**
     * Returns an unmodifiable set of methods in the entity.
     * 
     * @return an unmodifiable set of methods.
     * @since 1.0
     */
    public Set<MethodEntity> getMethods() {
        return Collections.unmodifiableSet(methods);
    }

    /**
     * Returns an unmodifiable set of fields in the entity.
     * 
     * @return an unmodifiable set of fields.
     * @since 1.0
     */
    public Set<FieldEntity> getFields() {
        return Collections.unmodifiableSet(fields);
    }

    /**
     * Returns an unmodifiable list of relatives (relationships) in the entity.
     * 
     * @return an unmodifiable list of relatives.
     * @since 1.0
     */
    public List<Relative> getRelatives() {
        return Collections.unmodifiableList(relatives);
    }

    /**
     * Retrieves a list of relatives filtered by the specified relationship type.
     * 
     * @param relationshipType the type of relationship to filter by.
     * @return a list of relatives matching the specified relationship type.
     * @since 1.0
     */
    public List<Relative> getRelativesByRelationshipType(Relative.RelationshipType relationshipType) {
        List<Relative> filteredRelatives = new ArrayList<>();
        for (Relative relative : relatives) {
            if (relative.getRelationshipType() == relationshipType) {
                filteredRelatives.add(relative);
            }
        }
        return filteredRelatives;
    }

    /**
     * Adds a method to the entity.
     * 
     * @param method the method to add (must not be null and must have a valid name).
     * @throws IllegalArgumentException if the method is null or has an invalid name.
     * @return the current CodeEntity instance (fluent API).
     * @since 1.0
     */
    public CodeEntity addMethod(MethodEntity method) {
        if (method == null || method.getName() == null || method.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Method name cannot be null or empty");
        }
        if (methods.add(method)) {
            logger.debug("Method added: {}", method.getName());
        } else {
            logger.warn("Duplicate method not added: {}", method.getName());
        }
        return this; // Fluent API
    }

    /**
     * Adds a field to the entity.
     * 
     * @param field the field to add (must not be null and must have a valid name).
     * @throws IllegalArgumentException if the field is null or has an invalid name.
     * @return the current CodeEntity instance (fluent API).
     * @since 1.0
     */
    public CodeEntity addField(FieldEntity field) {
        if (field == null || field.getName() == null || field.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Field name cannot be null or empty");
        }
        if (fields.add(field)) {
            logger.debug("Field added: {}", field.getName());
        } else {
            logger.warn("Duplicate field not added: {}", field.getName());
        }
        return this; // Fluent API
    }

    /**
     * Adds a relative (relationship) to the entity.
     * 
     * @param relative the relative to add (must not be null).
     * @throws IllegalArgumentException if the relative is null.
     * @return the current CodeEntity instance (fluent API).
     * @since 1.0
     */
    public CodeEntity addRelative(Relative relative) {
        if (relative == null) {
            throw new IllegalArgumentException("Relative cannot be null");
        }
        relatives.add(relative);
        logger.debug("Relative added: {}", relative);
        return this; // Fluent API
    }

    /**
     * Removes a method from the entity.
     * 
     * @param method the method to remove.
     * @return true if the method was removed, false otherwise.
     * @since 1.0
     */
    public boolean removeMethod(MethodEntity method) {
        boolean removed = methods.remove(method);
        if (removed) {
            logger.debug("Method removed: {}", method.getName());
        }
        return removed;
    }

    /**
     * Removes a field from the entity.
     * 
     * @param field the field to remove.
     * @return true if the field was removed, false otherwise.
     * @since 1.0
     */
    public boolean removeField(FieldEntity field) {
        boolean removed = fields.remove(field);
        if (removed) {
            logger.debug("Field removed: {}", field.getName());
        }
        return removed;
    }

    /**
     * Gets the list of constructors in the entity.
     * 
     * @return a list of constructors.
     * @since 1.1
     */
    public List<MethodEntity> getConstructors() {
        return constructors;
    }

    /**
     * Adds a constructor to the entity.
     * 
     * @param constructor the constructor to add.
     * @since 1.1
     */
    public void addConstructor(MethodEntity constructor) {
        if (constructor != null) {
            constructors.add(constructor);
        }
    }

    /**
     * Gets the list of annotations applied to the entity.
     * 
     * @return a list of annotations.
     * @since 1.2
     */
    public List<String> getAnnotations() {
        return annotations;
    }

    /**
     * Adds an annotation to the entity.
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
     * Checks if this entity is equal to another object.
     * 
     * Two entities are considered equal if they have the same name, methods,
     * fields, and relationships.
     * 
     * @param obj the object to compare.
     * @return true if the entities are equal, false otherwise.
     * @since 1.0
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        CodeEntity that = (CodeEntity) obj;
        return Objects.equals(name, that.name) &&
                Objects.equals(methods, that.methods) &&
                Objects.equals(fields, that.fields) &&
                Objects.equals(relatives, that.relatives);
    }

    /**
     * Returns the hash code of this entity.
     * 
     * The hash code is based on the entity's name, methods, fields, and relationships.
     * 
     * @return the hash code of the entity.
     * @since 1.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, methods, fields, relatives);
    }

    /**
     * Returns a string representation of the entity.
     * 
     * The string includes the entity's name, the number of methods, fields, and
     * relationships.
     * 
     * @return a string representation of the entity.
     * @since 1.0
     */
    @Override
    public String toString() {
        return "CodeEntity{" +
                "name='" + name + '\'' +
                ", methods=" + methods.size() +
                ", fields=" + fields.size() +
                ", relatives=" + relatives.size() +
                '}';
    }

    public boolean hasMainMethod() {
        return methods.stream()
                .anyMatch(m -> m.getName().equals("main") && m.hasParameterType("String[]"));
    }

    public boolean hasAnyAnnotation(List<String> annotationList) {
        return annotations.stream().anyMatch(annotationList::contains);
    }

  
    
}