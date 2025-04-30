# Functional Specification: J2Arch

## 1. Overview

J2Arch is a comprehensive tool designed to analyze Java source code and generate various outputs, including UML diagrams, HTML documentation, and JavaDoc. It provides a unified platform for developers, architects, and project managers to visualize, document, and understand the structure and behavior of Java applications. J2Arch supports both default configurations and user-defined configurations, making it flexible and easy to use for projects of all sizes.

---

## 2. Product Features

### 2.1 Core Features

#### Java-to-UML Diagram Generation:
- Parse Java source code to extract class structures, methods, fields, and relationships.
- Generate UML diagrams, including class diagrams and sequence diagrams.
- Export diagrams in multiple formats: PNG, SVG, and PlantUML.

#### PUML to HTML Documentation Generation:
- Convert PlantUML files into HTML documentation.
- Generate HTML pages with embedded diagrams and navigation.

#### JavaDoc Generation:
- Generate JavaDoc documentation for Java source code.
- Include custom templates and styles for enhanced documentation.

#### Diagram Image Preview:
- Preview generated UML diagrams and HTML documentation.
- Provide an interactive UI for viewing and managing diagrams.

#### Configuration Management:
- Support default configurations for quick setup.
- Allow user-defined configurations for custom input/output directories and settings.

#### Hybrid Storage Options:
- Process data in-memory for small projects.
- Use persistent storage (JSON, SQLite/PostgreSQL) for large projects.
- Support cloud storage for distributed teams and backups.

#### Integration with CI/CD Pipelines:
- Automate UML diagram and documentation generation during build and deployment processes.

#### Interactive User Interfaces:
- Command-Line Interface (CLI) for advanced users.
- Graphical User Interface (GUI) for interactive diagram visualization and management.

---

## 3. Module-Wise Functional Specification

### 3.1 Java-to-UML Diagram Generation

#### Purpose
Analyze Java source code and generate UML diagrams to visualize class structures, relationships, and method interactions.

#### Features
- Parse Java source files to extract:
  - Classes and interfaces.
  - Methods, fields, and annotations.
  - Relationships such as inheritance, associations, and method calls.
- Generate UML diagrams:
  - **Class Diagrams**: Visualize class structures and relationships.
  - **Sequence Diagrams**: Represent method interactions and call flows.
- Export diagrams in the following formats:
  - **PNG**: For easy sharing and embedding.
  - **SVG**: For scalable and high-quality visuals.
  - **PlantUML**: For further customization and editing.
- Incremental updates:
  - Track changes in the source code and update diagrams dynamically.
- Support for annotations:
  - Recognize and include Java annotations in UML diagrams.

#### Default Configuration
- **Input Directory**: `./input`
- **Output Directories**:
  - UML Diagrams: `./umldoc`
  - PUML Files: `./umldoc/puml`
  - Images: `./umldoc/images`

---

### 3.2 PUML to HTML Documentation Generation

#### Purpose
Convert PlantUML files into HTML documentation for easy sharing and navigation.

#### Features
- Parse PlantUML files and generate HTML pages with:
  - Embedded UML diagrams.
  - Navigation links for easy browsing.
- Support for custom templates:
  - Use predefined or user-defined HTML templates for documentation.
- Export HTML documentation to the following directory:
  - `./htmldoc`
- Include stylesheets for consistent formatting.

#### Default Configuration
- **Input Directory**: `./umldoc/puml`
- **Output Directory**: `./htmldoc`

---

### 3.3 JavaDoc Generation

#### Purpose
Generate JavaDoc documentation for Java source code with enhanced templates and styles.

#### Features
- Generate JavaDoc for:
  - Classes and interfaces.
  - Methods and fields.
  - Annotations and relationships.
- Support for custom templates:
  - Use predefined or user-defined templates for JavaDoc generation.
- Export JavaDoc to the following directory:
  - `./javadoc`
- Include stylesheets for consistent formatting.

#### Default Configuration
- **Input Directory**: `./input`
- **Output Directory**: `./javadoc`

---

### 3.4 Diagram Image Preview

#### Purpose
Provide an interactive interface for previewing and managing generated UML diagrams and HTML documentation.

#### Features
- Preview UML diagrams:
  - Class diagrams.
  - Sequence diagrams.
- Preview HTML documentation:
  - Navigate through generated HTML pages.
  - View embedded diagrams.
- Interactive UI:
  - Zoom, pan, and navigate diagrams.
  - Export diagrams directly from the preview interface.

---

## 4. Configuration Management

### Default Configuration
- **Input Directory**: `./input`
- **Output Directories**:
  - UML Diagrams: `./umldoc`
  - PUML Files: `./umldoc/puml`
  - Images: `./umldoc/images`
  - HTML Documentation: `./htmldoc`
  - JavaDoc: `./javadoc`

### User-Defined Configuration
- Users can specify custom configurations in a `config` directory at the root of the application.
- Example configuration file:
  ```application.properties
  