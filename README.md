# j2arch - A tool to help architects and developers to generate various deleverables from Java source code.

## Overview
The **j2arch** is a Java-based tool that analyzes Java source code and generates UML diagrams, including **Class Diagrams** and **Sequence Diagrams**. This project is currently under active development.

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
- 🔹 Generate **Javadoc** from source code comments.
- 🔹 Generate **HTML Documentation** from source code.

Planned:
- 🔹 Generate **Design Patterns Documentation** from source code.
- 🔹 Generate **Architecture Documentation** from source code.
- 🔹 Store UML diagrams using **file-based and database storage options**.
- 🔹 Integrate with **CI/CD pipelines** for automated UML generation.

## Project Structure
```
LICENSE.txt
pom.xml
README.md

├───config
├───Documents
│   ├───images
│   └───puml
├───input
│   └───com
│       └───pjsoft
│           └───fms
│               ├───config
│               ├───controller
│               ├───exception
│               ├───init
│               ├───model
│               ├───repository
│               └───service
├───src
│   └───main
│       ├───java
│       │   └───com
│       │       └───pjsoft
│       │           └───j2arch
│       │               ├───arch
│       │               ├───cli
│       │               ├───config
│       │               ├───core
│       │               │   ├───context
│       │               │   ├───model
│       │               │   └───util
│       │               ├───docgen
│       │               │   ├───javadoc
│       │               │   │   ├───service
│       │               │   │   └───util
│       │               │   └───pumldoc
│       │               │       ├───model
│       │               │       └───util
│       │               ├───gui
│       │               │   └───util
│       │               └───uml
│       │                   ├───model
│       │                   ├───service
│       │                   └───util
│       └───resources
│           ├───config
│           ├───icons
│           ├───styles
│           │   ├───gui
│           │   ├───htmldoc
│           │   └───javadoc
│           └───templates
│               ├───htmldoc
│               └───javadoc
├───styles
│   ├───gui
│   ├───htmldoc
│   └───javadoc


└───templates
    ├───htmldoc
    └───javadoc
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
Run the CLI application using:
```sh
mvn exec:java
```
Run the GUI application using:
```sh
mvn javafx:run
```

## Contributing
Contributions are welcome! To contribute:
1. Fork the repository
2. Create a feature branch (`feature-branch-name`)
3. Commit changes and create a pull request

## License
This project is licensed under the **MIT License**.

---
🚀 **Project Status: In Development** | 📅 **Next Milestone: Test**
