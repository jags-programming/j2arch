package com.pjsoft.j2arch.uml.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Scenario
 * 
 * Represents a single sequence diagram. A scenario consists of an entry class,
 * a starting method, and a sequence of interactions (caller-callee relationships)
 * that form the sequence diagram.
 */
public class Scenario {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Scenario.class);
    private final String entryClass;
    private final String startingMethod; // The method in the entry class that starts the scenario
    private final List<Interaction> interactions; // List of interactions in the scenario

    /**
     * Constructs a Scenario with the specified entry class and starting method.
     * 
     * @param entryClass The name of the entry class.
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
     * An interaction represents a caller-callee relationship between two classes
     * and their respective methods.
     * 
     * @param interaction The interaction to add to the scenario.
     */
    public void addInteraction(Interaction interaction) {
        interactions.add(interaction);
    }

    /**
     * Retrieves all interactions in the scenario.
     * 
     * @return A list of interactions in the scenario.
     */
    public List<Interaction> getInteractions() {
        return interactions;
    }

    /**
     * Retrieves the entry class of the scenario.
     * 
     * The entry class is the starting point of the sequence diagram.
     * 
     * @return The name of the entry class.
     */
    public String getEntryClass() {
        return entryClass;
    }

    /**
     * Retrieves the starting method of the scenario.
     * 
     * The starting method is the method in the entry class that initiates the
     * sequence of interactions in the scenario.
     * 
     * @return The name of the starting method.
     */
    public String getStartingMethod() {
        return startingMethod;
    }

    /**
     * Converts the scenario to PlantUML syntax.
     * 
     * This method generates the PlantUML representation of the scenario, which
     * can be used to create a sequence diagram. The syntax includes the title
     * and all interactions in the scenario.
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