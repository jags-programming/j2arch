package com.pjsoft.j2arch.uml.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Scenario
 * 
 * Represents a single sequence diagram. A scenario consists of:
 * - An entry class: The starting point of the sequence diagram.
 * - A starting method: The method in the entry class that initiates the sequence.
 * - A sequence of interactions: Caller-callee relationships that form the sequence diagram.
 * 
 * Responsibilities:
 * - Stores the entry class, starting method, and interactions for a sequence diagram.
 * - Provides methods to add interactions and retrieve scenario details.
 * - Converts the scenario into PlantUML syntax for diagram generation.
 * 
 * Dependencies:
 * - {@link Interaction}: Represents a single interaction (caller-callee relationship) in the sequence diagram.
 * 
 * Limitations:
 * - Assumes that all interactions are valid and complete.
 * - Does not validate the correctness of the generated PlantUML syntax.
 * 
 * Thread Safety:
 * - This class is not thread-safe. Concurrent modifications to the interactions list may lead to unexpected behavior.
 * 
 * Usage Example:
 * {@code
 * Scenario scenario = new Scenario("MyClass", "myMethod");
 * scenario.addInteraction(new Interaction("CallerClass", "callerMethod", "CalleeClass", "calleeMethod"));
 * String plantUmlSyntax = scenario.toPlantUmlSyntax();
 * System.out.println(plantUmlSyntax);
 * }
 * 
 * @author PJSoft
 * @version 2.2
 * @since 1.0
 */
public class Scenario {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Scenario.class);
    private final String entryClass; // The entry class of the scenario
    private final String startingMethod; // The method in the entry class that starts the scenario
    private final List<Interaction> interactions; // List of interactions in the scenario

    /**
     * Constructs a Scenario with the specified entry class and starting method.
     * 
     * Responsibilities:
     * - Initializes the entry class, starting method, and an empty list of interactions.
     * 
     * @param entryClass     The name of the entry class.
     * @param startingMethod The name of the method in the entry class that starts the scenario.
     */
    public Scenario(String entryClass, String startingMethod) {
        this.entryClass = entryClass;
        this.startingMethod = startingMethod;
        this.interactions = new ArrayList<>();
    }

    /**
     * Adds an interaction to the scenario.
     * 
     * Responsibilities:
     * - Adds a caller-callee relationship to the list of interactions.
     * 
     * @param interaction The interaction to add to the scenario.
     */
    public void addInteraction(Interaction interaction) {
        interactions.add(interaction);
    }

    /**
     * Retrieves all interactions in the scenario.
     * 
     * Responsibilities:
     * - Returns the list of interactions that form the sequence diagram.
     * 
     * @return A list of interactions in the scenario.
     */
    public List<Interaction> getInteractions() {
        return interactions;
    }

    /**
     * Retrieves the entry class of the scenario.
     * 
     * Responsibilities:
     * - Returns the name of the entry class, which is the starting point of the sequence diagram.
     * 
     * @return The name of the entry class.
     */
    public String getEntryClass() {
        return entryClass;
    }

    /**
     * Retrieves the starting method of the scenario.
     * 
     * Responsibilities:
     * - Returns the name of the starting method in the entry class that initiates the sequence.
     * 
     * @return The name of the starting method.
     */
    public String getStartingMethod() {
        return startingMethod;
    }

    /**
     * Converts the scenario to PlantUML syntax.
     * 
     * Responsibilities:
     * - Generates the PlantUML representation of the scenario.
     * - Includes the title and all interactions in the sequence diagram.
     * 
     * @return A string containing the PlantUML syntax for the scenario.
     */
    public String toPlantUmlSyntax() {
        StringBuilder plantUml = new StringBuilder();
        plantUml.append("@startuml\n");
        plantUml.append("title Sequence Diagram for ").append(entryClass)
                .append("::").append(startingMethod).append("\n");

        for (Interaction interaction : interactions) {
            plantUml.append(interaction.getCallerClass())
                    .append(" -> ")
                    .append(interaction.getCalleeClass())
                    .append(" : ")
                    .append(interaction.getCallerMethod())
                    .append(" calls ")
                    .append(interaction.getCalleeMethod())
                    .append("\n");
        }

        plantUml.append("@enduml");
        return plantUml.toString();
    }
}