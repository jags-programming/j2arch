package com.pjsoft.j2arch.uml.util;

import java.util.*;

import com.pjsoft.j2arch.core.model.CodeEntity;
import com.pjsoft.j2arch.core.model.MethodEntity;
import com.pjsoft.j2arch.core.model.Relative;
import com.pjsoft.j2arch.uml.model.Interaction;
import com.pjsoft.j2arch.uml.model.Scenario;

/**
 * ScenarioBuilder
 * 
 * Responsible for generating scenarios (sequence diagrams) from CodeEntity
 * objects. A scenario represents a sequence of interactions starting from a
 * specific entry class and method, traversing the caller-callee relationships
 * recursively.
 * 
 * Responsibilities:
 * - Identifies entry classes (classes that are not callees).
 * - Generates scenarios for each entry class and its methods.
 * - Dynamically resolves caller-callee relationships during traversal.
 * - Avoids cycles in the relationship graph using a visited set.
 * 
 * Dependencies:
 * - {@link CodeEntity}: Represents a class or interface in the codebase.
 * - {@link MethodEntity}: Represents a method in a class or interface.
 * - {@link Relative}: Represents relationships between code entities (e.g.,
 * caller-callee relationships).
 * - {@link Scenario}: Represents a sequence diagram for a specific entry class
 * and method.
 * - {@link Interaction}: Represents a single interaction in a sequence diagram.
 * 
 * Limitations:
 * - Assumes that the provided CodeEntity objects are complete and accurate.
 * - Does not handle relationships other than CALLER_CALLEE.
 * - Does not validate the correctness of the generated scenarios.
 * 
 * @author PJSoft
 * @version 2.2
 * @since 1.0
 */
public class ScenarioBuilder {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ScenarioBuilder.class);
    private final String projectPackage;

    /**
     * Constructs a ScenarioBuilder with the specified project package.
     * 
     * @param projectPackage The package name to filter project-specific classes.
     */
    public ScenarioBuilder(String projectPackage) {
        this.projectPackage = projectPackage;
    }

    /**
     * Identifies entry classes (classes that are not callees).
     * 
     * An entry class is a class that is not called by any other class in the
     * provided list of CodeEntity objects. These classes serve as starting points
     * for scenarios.
     * 
     * @param codeEntities The list of CodeEntity objects representing the parsed
     *                     classes.
     * @return A list of entry classes.
     * @throws IllegalArgumentException If the codeEntities list is null.
     */

    private List<CodeEntity> identifyEntryClasses(List<CodeEntity> codeEntities) {
        if (codeEntities == null) {
            throw new IllegalArgumentException("CodeEntities cannot be null");
        }

        // Collect classes already flagged as entry points
        return codeEntities.stream()
                .filter(CodeEntity::isEntryPoint)
                .toList();
    }

    /**
     * Generates a list of scenarios from the provided CodeEntity objects.
     * 
     * This method identifies entry classes and generates scenarios for each entry
     * class and its methods. Each scenario represents a sequence diagram starting
     * from a specific method of an entry class. The method uses the provided list
     * of CodeEntity objects to dynamically resolve relationships during traversal.
     * 
     * @param codeEntities The list of CodeEntity objects representing the parsed
     *                     classes.
     * @return A list of scenarios representing sequence diagrams.
     * @throws IllegalArgumentException If the codeEntities list is null.
     */
    public List<Scenario> getScenarios(List<CodeEntity> codeEntities) {

        if (codeEntities == null) {
            throw new IllegalArgumentException("CodeEntities cannot be null");
        }

        // Step 1: Identify entry classes
        List<CodeEntity> entryClasses = identifyEntryClasses(codeEntities);

        // Step 2: Build scenarios for each entry class and its methods
        List<Scenario> scenarios = new ArrayList<>();
        for (CodeEntity entryClass : entryClasses) {
            // Get scenarios for the current entry class
            List<Scenario> entryClassScenarios = getScenariosByEntryClass(entryClass, codeEntities);
            // Merge the scenarios into the main list
            scenarios.addAll(entryClassScenarios);
        }

        return scenarios; // Return the list of all scenarios for all entry classes
    }

    /**
     * Generates a list of scenarios for a given entry class.
     * 
     * This method iterates over all methods of the entry class and generates a
     * separate scenario for each method. Each scenario represents a sequence
     * diagram starting from the given method and traversing the caller-callee
     * relationships recursively. The method uses the provided list of CodeEntity
     * objects to dynamically resolve callees during traversal.
     * 
     * @param entryClass   The entry class to generate scenarios for.
     * @param codeEntities The list of all CodeEntity objects, used to dynamically
     *                     look up callees.
     * @return A list of scenarios starting from the entry class.
     * @throws IllegalArgumentException If the entryClass is null.
     */
    private List<Scenario> getScenariosByEntryClass(CodeEntity entryClass, List<CodeEntity> codeEntities) {
        if (entryClass == null) {
            throw new IllegalArgumentException("EntryClass cannot be null");
        }

        List<Scenario> scenarios = new ArrayList<>();

        // Iterate over all methods of the entry class
        for (MethodEntity method : entryClass.getMethods()) {
            // Create a new scenario for each method
            Scenario scenario = new Scenario(entryClass.getName(), method.getName());
            Set<String> visited = new HashSet<>(); // To avoid cycles

            // Build the scenario starting from this method
            // buildScenarioFromMethod(entryClass, method.getName(), scenario, visited,
            // codeEntities);
            buildScenarioFromMethod(entryClass, method, scenario, visited, codeEntities);
            // Add the built scenario to the list
            scenarios.add(scenario);
        }

        return scenarios;
    }


    private void buildScenarioFromMethod(CodeEntity entryClass, MethodEntity startingMethod, Scenario scenario,
            Set<String> visited, List<CodeEntity> codeEntities) {
        if (entryClass == null || startingMethod == null || scenario == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        // Create a unique key for the current class and method to track visited nodes
        String visitedKey = entryClass.getName() + "::" + startingMethod.getName();
        if (visited.contains(visitedKey)) {
            return; // Avoid cycles
        }
        visited.add(visitedKey);

        // Get all CALLER_CALLEE relationships for the entry class
        List<Relative> callerCalleeRelatives = entryClass
                .getRelativesByRelationshipType(Relative.RelationshipType.CALLER_CALLEE);

        for (Relative relative : callerCalleeRelatives) {
            // Check if the relationship starts from the given method
            if (relative.getCallerMethodEntity().equals(startingMethod)) {

                // Look up the full CodeEntity for the callee
                CodeEntity calleeEntity = findCodeEntityByName(codeEntities, relative.getCalleeEntity().getName());
                if (calleeEntity == null) {
                    logger.warn("Callee entity not found: {}", relative.getCalleeEntity().getName());
                    continue;
                }

                // Add the interaction to the scenario
                scenario.addInteraction(new Interaction(
                        entryClass.getName(),
                        startingMethod.getName(),
                        calleeEntity.getName(),
                        relative.getCalleeMethodEntity().getName()));

                // Recursively build the scenario for the callee
                buildScenarioFromMethod(calleeEntity, relative.getCalleeMethodEntity(), scenario, visited,
                        codeEntities);
            }
        }
    }

    /**
     * Retrieves the list of parameter types for a specified method in the given
     * code entity.
     *
     * @param codeEntity the code entity containing the methods to search
     * @param methodName the name of the method whose parameter types are to be
     *                   retrieved
     * @return a list of parameter types for the specified method, or an empty list
     *         if the method
     *         is not found or has no parameters
     */
    private List<String> getMethodParameterTypes(CodeEntity codeEntity, String methodName) {
        return codeEntity.getMethods().stream()
                .filter(m -> m.getName().equals(methodName))
                .map(MethodEntity::getParameters)
                .findFirst()
                .orElse(Collections.emptyList());
    }

    /**
     * Finds a CodeEntity by its name from the provided list of CodeEntity objects.
     * 
     * @param codeEntities The list of CodeEntity objects to search.
     * @param name         The name of the CodeEntity to find.
     * @return The CodeEntity with the specified name, or null if not found.
     */
    private CodeEntity findCodeEntityByName(List<CodeEntity> codeEntities, String name) {
        for (CodeEntity codeEntity : codeEntities) {
            if (codeEntity.getName().equals(name)) {
                return codeEntity;
            }
        }
        return null; // Return null if not found
    }
}