package com.pjsoft.j2arch.uml.model;

/**
 * Interaction
 * 
 * Represents a single interaction (method call) in a sequence diagram.
 */
public class Interaction {

    private final String callerClass;
    private final String callerMethod;
    private final String calleeClass;
    private final String calleeMethod;

    /**
     * Constructs an Interaction with the specified details.
     * 
     * @param callerClass the class making the call.
     * @param callerMethod the method making the call.
     * @param calleeClass the class being called.
     * @param calleeMethod the method being called.
     */
    public Interaction(String callerClass, String callerMethod, String calleeClass, String calleeMethod) {
        this.callerClass = callerClass;
        this.callerMethod = callerMethod;
        this.calleeClass = calleeClass;
        this.calleeMethod = calleeMethod;
    }

    public String getCallerClass() {
        return callerClass;
    }

    public String getCallerMethod() {
        return callerMethod;
    }

    public String getCalleeClass() {
        return calleeClass;
    }

    public String getCalleeMethod() {
        return calleeMethod;
    }
}