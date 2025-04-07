# UML Diagram Generator

## Overview
The **UML Diagram Generator** is a Java-based tool that analyzes Java source code and generates UML diagrams, including **Class Diagrams** and **Sequence Diagrams**. This project is currently under active development.

## Project Status
- ✅ **Requirements Document Created**
- ✅ **Design Document Completed**
- ✅ **PUML Files for Class & Sequence Diagrams Defined**
- ✅ **Code Structure Implemented** (Methods to be developed)
- ⏳ **Unit Testing & Implementation in Progress**

## Features (Planned & In Progress)
- 🔹 Parse Java source files to extract **classes, methods, and relationships**.
- 🔹 Generate **UML Class Diagrams** from parsed code.
- 🔹 Generate **UML Sequence Diagrams** from method interactions.
- 🔹 Support multiple output formats (**PlantUML, PNG, SVG**).
- 🔹 Provide both **CLI and Graphical UI** for interaction.
- 🔹 Store UML diagrams using **file-based and database storage options**.
- 🔹 Integrate with **CI/CD pipelines** for automated UML generation.

## Project Structure
```
LICENSE.txt
pom.xml
README.md

Documents/
    PJJava2UMLGeneratorAnalysisDesign.docx
    PJJava2UMLGeneratorRequirements.docx
    
Documents/puml/
    ClassDiagram.puml
    ClassDiagramDetailed.puml
    SequenceDiagramGenerateClassDiagram.puml
    SequenceDiagramGenerateSequenceDiagram.puml
    SequenceDiagramLoadManageConfig.puml
    SequenceDiagramParseJavaFiles.puml
    
src/
    main/
        java/com/pjsoft/uml/
            ClassDiagramService.java
            CLIApplication.java
            CodeEntity.java
            ConfigurationManager.java
            FieldEntity.java
            FileHandler.java
            GraphicalUI.java
            JavaParserService.java
            MethodEntity.java
            RelationshipManager.java
            SequenceDiagramService.java
            StorageService.java
            UMLDiagramGenerator.java
    resources/
    test/
```

## Installation
### Prerequisites
- **Java 23**
- **Maven 3.6+**

### Build & Run
Clone the repository and build the project using Maven:
```sh
mvn clean package
```
Run the application using:
```sh
mvn exec:java -P run
```

## Usage
### Command-Line Interface (CLI)
To generate a UML class diagram:
```sh
java -jar uml-generator.jar --class-diagram src/main/java
```
To generate a sequence diagram:
```sh
java -jar uml-generator.jar --sequence-diagram src/main/java
```

### Graphical UI (Upcoming Feature)
A **GUI interface** for visualizing UML diagrams will be added in future versions.

## Development Roadmap
### Phase 1: Code Completion & Unit Testing *(Current Phase)*
- [ ] Implement missing methods in core classes
- [ ] Add unit tests for `JavaParserService`, `ClassDiagramService`, and `SequenceDiagramService`

### Phase 2: CLI & Logging Enhancements
- [ ] Improve CLI argument handling
- [ ] Integrate **SLF4J Logging**
- [ ] Add Exception Handling for file parsing, invalid Java syntax, and storage failures

### Phase 3: Graphical UI Implementation
- [ ] Implement JavaFX/Swing-based UI
- [ ] Render UML diagrams in real-time

### Phase 4: CI/CD & Documentation Finalization
- [ ] Automate builds with **GitHub Actions**
- [ ] Finalize documentation & tutorials

## Contributing
Contributions are welcome! To contribute:
1. Fork the repository
2. Create a feature branch (`feature-branch-name`)
3. Commit changes and create a pull request

## License
This project is licensed under the **MIT License**.

---
🚀 **Project Status: In Development** | 📅 **Next Milestone: Code Implementation**