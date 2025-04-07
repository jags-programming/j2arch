package com.pjsoft.j2arch.uml;

import org.junit.jupiter.api.Test;

import com.pjsoft.j2arch.uml.model.Interaction;
import com.pjsoft.j2arch.uml.model.Scenario;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Scenario} class.
 */
class ScenarioTest {

    /**
     * Tests adding interactions to a scenario and generating PlantUML syntax.
     */
    @Test
    void testAddInteractionAndGeneratePlantUmlSyntax() {
        // Create a Scenario instance
        Scenario scenario = new Scenario("EntryClass", "startingMethod");

        // Add interactions to the scenario
        Interaction interaction1 = new Interaction("EntryClass", "startingMethod", "CalleeClass1", "calleeMethod1");
        Interaction interaction2 = new Interaction("CalleeClass1", "calleeMethod1", "CalleeClass2", "calleeMethod2");

        scenario.addInteraction(interaction1);
        scenario.addInteraction(interaction2);

        // Verify interactions are added correctly
        List<Interaction> interactions = scenario.getInteractions();
        assertAll(
            () -> assertEquals(2, interactions.size(), "There should be 2 interactions in the scenario"),
            () -> assertEquals(interaction1, interactions.get(0), "The first interaction should match interaction1"),
            () -> assertEquals(interaction2, interactions.get(1), "The second interaction should match interaction2")
        );

        // Generate PlantUML syntax and verify the output
        String plantUmlSyntax = scenario.toPlantUmlSyntax();
        String expectedPlantUml = """
                @startuml
                title Sequence Diagram for EntryClass::startingMethod
                EntryClass -> CalleeClass1 : startingMethod calls calleeMethod1
                CalleeClass1 -> CalleeClass2 : calleeMethod1 calls calleeMethod2
                @enduml
                """;

        assertEquals(expectedPlantUml.trim(), plantUmlSyntax.trim(), "The generated PlantUML syntax should match the expected output");
    }
}