package com.pjsoft.j2arch.uml.util;

import org.junit.jupiter.api.Test;

import com.pjsoft.j2arch.uml.model.CodeEntity;
import com.pjsoft.j2arch.uml.model.MethodEntity;
import com.pjsoft.j2arch.uml.model.Relative;
import com.pjsoft.j2arch.uml.model.Scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ScenarioBuilder} class.
 */
class ScenarioBuilderTest {

    /**
     * Tests the `identifyEntryClasses` method idirectly to ensure it correctly identifies entry classes.
     */
    @Test
void testGetScenarios_ShouldIdentifyEntryClassesCorrectly() {
    // Arrange
    CodeEntity classA = new CodeEntity("ClassA");
    CodeEntity classB = new CodeEntity("ClassB");
    CodeEntity classC = new CodeEntity("ClassC");
    
    MethodEntity methodA = new MethodEntity("methodA", "void");
    classA.addMethod(methodA);
    
    MethodEntity methodB = new MethodEntity("methodB", "void");
    classB.addMethod(methodB);

    
    MethodEntity methodC = new MethodEntity("methodC", "void");
    classC.addMethod(methodC);
    // Set up relationships
    classA.addRelative(new Relative(Relative.RelationshipType.CALLER_CALLEE, classB,  "methodB", "methodA"));
    classB.addRelative(new Relative(Relative.RelationshipType.CALLER_CALLEE,classC, "methodC" ,"methodB"));

    List<CodeEntity> codeEntities = List.of(classA, classB, classC);

    ScenarioBuilder scenarioBuilder = new ScenarioBuilder("com.example");

    // Act
    List<Scenario> scenarios = scenarioBuilder.getScenarios(codeEntities);

    // Assert
    assertEquals(1, scenarios.size(), "There should be 1 scenario generated");
    Scenario scenario = scenarios.get(0);
    assertEquals("ClassA", scenario.getEntryClass(), "ClassA should be the entry class");
}

    /**
     * Tests the `getScenarios` method to ensure it generates scenarios for all entry classes.
     */
    @Test
    void testGetScenarios_ShouldGenerateScenariosForEntryClasses() {
        // Arrange
        CodeEntity classA = new CodeEntity("ClassA");
        MethodEntity methodA = new MethodEntity("methodA", "void");
        classA.addMethod(methodA);

        CodeEntity classB = new CodeEntity("ClassB");
        MethodEntity methodB = new MethodEntity("methodB", "void");
        classB.addMethod(methodB);

        classA.addRelative(new Relative(Relative.RelationshipType.CALLER_CALLEE, classB,"methodB", "methodA"));

        List<CodeEntity> codeEntities = List.of(classA, classB);

        ScenarioBuilder scenarioBuilder = new ScenarioBuilder("com.example");

        // Act
        List<Scenario> scenarios = scenarioBuilder.getScenarios(codeEntities);

        // Assert
        assertEquals(1, scenarios.size(), "There should be 1 scenario generated");
        Scenario scenario = scenarios.get(0);
        assertEquals("ClassA", scenario.getEntryClass(), "The entry class should be ClassA");
        assertEquals("methodA", scenario.getStartingMethod(), "The starting method should be methodA");
        assertEquals(1, scenario.getInteractions().size(), "There should be 1 interaction in the scenario");
    }

    
}