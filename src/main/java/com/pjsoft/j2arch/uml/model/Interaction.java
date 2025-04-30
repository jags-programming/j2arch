package com.pjsoft.j2arch.uml.model;

/**
 * Interaction
 * 
 * Represents a single interaction (method call) in a sequence diagram.
 * 
 * Responsibilities:
 * - Stores details about a caller-callee relationship, including the classes and methods involved.
 * - Provides getter methods to retrieve interaction details.
 * 
 * Dependencies:
 * - None.
 * 
 * Limitations:
 * - Assumes that the provided class and method names are valid.
 * - Does not validate the correctness of the caller-callee relationship.
 * 
 * Thread Safety:
 * - This class is immutable and therefore thread-safe.
 * 
 * Usage Example:
 * {@code
 * Interaction interaction = new Interaction("CallerClass", "callerMethod", "CalleeClass", "calleeMethod");
 * System.out.println("Caller: " + interaction.getCallerClass());
 * System.out.println("Callee: " + interaction.getCalleeClass());
 * }
 * 
 * @author PJSoft
 * @version 2.2
 * @since 1.0
 */
public class Interaction {

    private final String callerClass;  // The class making the call
    private final String callerMethod; // The method making the call
    private final String calleeClass;  // The class being called
    private final String calleeMethod; // The method being called

    /**
     * Constructs an Interaction with the specified details.
     * 
     * Responsibilities:
     * - Initializes the caller and callee details for the interaction.
     * 
     * @param callerClass  The class making the call.
     * @param callerMethod The method making the call.
     * @param calleeClass  The class being called.
     * @param calleeMethod The method being called.
     */
    public Interaction(String callerClass, String callerMethod, String calleeClass, String calleeMethod) {
        this.callerClass = callerClass;
        this.callerMethod = callerMethod;
        this.calleeClass = calleeClass;
        this.calleeMethod = calleeMethod;
    }

    /**
     * Retrieves the class making the call.
     * 
     * @return The name of the caller class.
     */
    public String getCallerClass() {
        return callerClass;
    }

    /**
     * Retrieves the method making the call.
     * 
     * @return The name of the caller method.
     */
    public String getCallerMethod() {
        return callerMethod;
    }

    /**
     * Retrieves the class being called.
     * 
     * @return The name of the callee class.
     */
    public String getCalleeClass() {
        return calleeClass;
    }

    /**
     * Retrieves the method being called.
     * 
     * @return The name of the callee method.
     */
    public String getCalleeMethod() {
        return calleeMethod;
    }
}