package com.pjsoft.j2arch.core.model;

/**
 * Represents a relationship between two code entities in a UML diagram.
 * 
 * This class models various types of relationships, such as inheritance,
 * implementation, association, and method calls, between source and target
 * entities. It provides methods to convert these relationships into PlantUML
 * syntax for diagram generation.
 * 
 * Responsibilities:
 * - Encapsulates the details of a relationship, including its type, source, and
 * target.
 * - Supports relationships involving methods (e.g., caller-callee).
 * - Converts relationships into PlantUML syntax for diagram generation.
 * 
 * Limitations:
 * - Assumes that the source and target entities are valid and have fully
 * qualified names.
 * - Does not validate the correctness of the relationship type or entities.
 * 
 * Usage Example:
 * {@code
 * CodeEntity source = new CodeEntity("com.example.ClassA");
 * CodeEntity target = new CodeEntity("com.example.ClassB");
 * Relative relationship = new Relative(Relative.RelationshipType.INHERITANCE, target);
 * String plantUmlSyntax = relationship.toPlantUmlSyntax(source.getName());
 * System.out.println(plantUmlSyntax);
 * }
 * 
 * Dependencies:
 * - {@link CodeEntity}
 * 
 * Thread Safety:
 * - This class is not thread-safe as it maintains mutable state.
 * 
 * Author: PJSoft
 * Version: 1.1
 * Since: 1.0
 */
public class Relative {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Relative.class);

    /**
     * Enum representing the types of relationships in a UML diagram.
     */
    public enum RelationshipType {
        CALLER_CALLEE, // Represents a method call relationship
        INHERITANCE, // Represents a parent-child relationship (class inheritance)
        IMPLEMENTATION, // Represents interface implementation
        ASSOCIATION // Represents an association relationship
    }

    private RelationshipType relationshipType; // The type of relationship
    private CodeEntity calleeEntity; // The related class or object (callee)
    private String calleeMethod; // The method being called in the callee (if applicable)
    private String callerMethod; // The method in the current class initiating the relationship (if applicable)
    private String details = ""; // Additional details about the relationship

    private MethodEntity callerMethodEntity;
    private MethodEntity calleeMethodEntity;

    public MethodEntity getCallerMethodEntity() {
        return callerMethodEntity;
    }

    public void setCallerMethodEntity(MethodEntity callerMethodEntity) {
        this.callerMethodEntity = callerMethodEntity;
    }

    public MethodEntity getCalleeMethodEntity() {
        return calleeMethodEntity;
    }

    public void setCalleeMethodEntity(MethodEntity calleeMethodEntity) {
        this.calleeMethodEntity = calleeMethodEntity;
    }

    /**
     * Gets additional details about the relationship.
     * 
     * @return the details of the relationship.
     * @since 1.1
     */
    public String getDetails() {
        return details;
    }

    /**
     * Sets additional details about the relationship.
     * 
     * @param details the details to set.
     * @since 1.1
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * Constructs a new Relative object for relationships without methods.
     * 
     * @param relationshipType the type of the relationship (e.g., INHERITANCE,
     *                         ASSOCIATION).
     * @param calleeEntity     the target entity of the relationship.
     * @since 1.0
     */
    public Relative(RelationshipType relationshipType, CodeEntity calleeEntity) {
        this.calleeEntity = calleeEntity;
        this.relationshipType = relationshipType;
    }

    /**
     * Constructs a new Relative object for relationships involving methods.
     * 
     * @param relationshipType the type of the relationship (e.g., CALLER_CALLEE).
     * @param calleeEntity     the target entity of the relationship.
     * @param calleeMethod     the method being called in the target entity.
     * @param callerMethod     the method in the source entity initiating the
     *                         relationship.
     * @since 1.0
     */
    public Relative(RelationshipType relationshipType, CodeEntity calleeEntity, String calleeMethod,
            String callerMethod) {
        this.relationshipType = relationshipType;
        this.calleeEntity = calleeEntity;
        this.calleeMethod = calleeMethod;
        this.callerMethod = callerMethod;
        this.details = generateDetails();
    }

    /**
     * Constructs a new Relative object with the specified relationship type, callee entity,
     * callee method entity, and caller method entity.
     *
     * @param relationshipType The type of relationship between the entities.
     * @param calleeEntity The code entity being called.
     * @param calleeMethodEntity The method entity being called.
     * @param callerMethodEntity The method entity making the call.
     */
    public Relative(Relative.RelationshipType relationshipType, CodeEntity calleeEntity,
            MethodEntity calleeMethodEntity, MethodEntity callerMethodEntity) {
        this.relationshipType = relationshipType;
        this.calleeEntity = calleeEntity;
        this.calleeMethodEntity = calleeMethodEntity;
        this.callerMethodEntity = callerMethodEntity;
    }

    /**
     * Generates additional details about the relationship based on its type.
     * 
     * @return a string containing details about the relationship.
     * @since 1.1
     */
    private String generateDetails() {
        switch (relationshipType) {
            case INHERITANCE:
                return "Extends " + calleeEntity.getName();
            case IMPLEMENTATION:
                return "Implements " + calleeEntity.getName();
            case ASSOCIATION:
                return "Field: " + (calleeMethod != null ? calleeMethod : "Unknown");
            case CALLER_CALLEE:
                return "Called Method: " + (calleeMethod != null ? calleeMethod : "Unknown") +
                        " from " + (callerMethod != null ? callerMethod : "Unknown");
            default:
                return "No additional details available";
        }
    }

    /**
     * Gets the type of the relationship.
     * 
     * @return the relationship type.
     * @since 1.0
     */
    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    /**
     * Sets the type of the relationship.
     * 
     * @param relationshipType the relationship type to set.
     * @since 1.0
     */
    public void setRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    /**
     * Gets the target entity of the relationship.
     * 
     * @return the callee entity.
     * @since 1.0
     */
    public CodeEntity getCalleeEntity() {
        return calleeEntity;
    }

    /**
     * Sets the target entity of the relationship.
     * 
     * @param calleeEntity the callee entity to set.
     * @since 1.0
     */
    public void setCalleeEntity(CodeEntity calleeEntity) {
        this.calleeEntity = calleeEntity;
    }

    /**
     * Gets the method being called in the target entity.
     * 
     * @return the callee method.
     * @since 1.0
     */
    public String getCalleeMethod() {
        return calleeMethod;
    }

    /**
     * Sets the method being called in the target entity.
     * 
     * @param calleeMethod the callee method to set.
     * @since 1.0
     */
    public void setCalleeMethod(String calleeMethod) {
        this.calleeMethod = calleeMethod;
    }

    /**
     * Gets the method in the source entity initiating the relationship.
     * 
     * @return the caller method.
     * @since 1.0
     */
    public String getCallerMethod() {
        return callerMethod;
    }

    /**
     * Sets the method in the source entity initiating the relationship.
     * 
     * @param callerMethod the caller method to set.
     * @since 1.0
     */
    public void setCallerMethod(String callerMethod) {
        this.callerMethod = callerMethod;
    }

    /**
     * Converts the relationship to a string representation.
     * 
     * @return a string representation of the relationship.
     * @since 1.0
     */
    @Override
    public String toString() {
        return "Relative{" +
                "relationshipType=" + relationshipType +
                ", calleeEntity=" + (calleeEntity != null ? calleeEntity.getName() : "null") +
                ", calleeMethod='" + calleeMethod + '\'' +
                ", callerMethod='" + callerMethod + '\'' +
                '}';
    }

    /**
     * Converts the relationship to PlantUML syntax.
     * Ensures that fully qualified names are used for both source and target
     * classes.
     * 
     * Usage Example:
     * {@code
     * String plantUmlSyntax = relative.toPlantUmlSyntax("com.example.ClassA");
     * System.out.println(plantUmlSyntax);
     * }
     * 
     * @param sourceClassName the fully qualified name of the source class.
     * @return the PlantUML syntax for the relationship.
     * @since 1.0
     */
    public String toPlantUmlSyntax(String sourceClassName) {
        String targetClassName = calleeEntity.getName(); // Fully qualified name of the callee
        switch (relationshipType) {
            case INHERITANCE:
                return sourceClassName + " <|-- " + targetClassName + " : extends";
            case IMPLEMENTATION:
                return sourceClassName + " <|.. " + targetClassName + " : implements";
            case ASSOCIATION:
                return sourceClassName + " -- " + targetClassName + " : association";
            case CALLER_CALLEE:
                return sourceClassName + " --> " + targetClassName + " : caller-callee";
            default:
                return ""; // Return an empty string for unsupported relationship types
        }
    }
}
