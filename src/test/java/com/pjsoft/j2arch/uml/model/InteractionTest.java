package com.pjsoft.j2arch.uml;

import org.junit.jupiter.api.Test;

import com.pjsoft.j2arch.uml.model.Interaction;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Interaction} class.
 */
class InteractionTest {

    /**
     * Tests the constructor and getter methods of the Interaction class.
     */
    @Test
    void testInteractionConstructorAndGetters() {
        // Arrange
        String callerClass = "CallerClass";
        String callerMethod = "callerMethod";
        String calleeClass = "CalleeClass";
        String calleeMethod = "calleeMethod";

        // Act
        Interaction interaction = new Interaction(callerClass, callerMethod, calleeClass, calleeMethod);

        // Assert
        assertEquals(callerClass, interaction.getCallerClass(), "The caller class should match the input value");
        assertEquals(callerMethod, interaction.getCallerMethod(), "The caller method should match the input value");
        assertEquals(calleeClass, interaction.getCalleeClass(), "The callee class should match the input value");
        assertEquals(calleeMethod, interaction.getCalleeMethod(), "The callee method should match the input value");
    }


}