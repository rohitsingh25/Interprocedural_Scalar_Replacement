# JavaScalarReplacement

## Description

This project implements **scalar replacement** for Java objects using advanced static analysis techniques, including:

- Control Flow Analysis
- Points-To Analysis
- Interprocedural Optimization
- Three Address Code (3AC) Generation

Scalar replacement is an optimization that breaks down heap-allocated objects into individual fields (scalars) when it is safe to do so, improving performance by reducing memory allocation and enhancing CPU cache efficiency.

---

## Features

- Parses Java programs and builds control flow graphs
- Identifies scalar-replaceable object allocations
- Uses points-to analysis to detect object aliasing
- Performs interprocedural analysis across methods
- Outputs detailed logs showing the optimization process
- Includes test cases and example inputs for verification

---

## Project Structure

JavaScalarReplacement-main/
├── MainClass.java # Main entry point
├── pom.xml # Maven build file
├── README.md # Project overview (you are here)
├── .gitignore
├── LICENSE
├── .github/
│ └── dependabot.yml # Dependency config
├── src/
│ ├── main/
│ │ └── java/
│ │ ├── com/laamella/javacfa/ # Control flow analyzer
│ │ ├── com/scalarreplacement/ # Scalar replacement logic
│ │ ├── example/ # Sample input: example3.java
│ │ └── example_new/ # Sample output: example3.java
│ └── test/
│ ├── jar_files/ # all utility jar files 
│ └── resources/
│ └── compilation_unit/ # Test Java programs



---

## Requirements

- Java 8 or higher
- Maven 3 or higher

---

## Setup Instructions

### 1. Clone the repository
bash : git clone https://github.com/rohitsingh25/JavaScalarReplacement.git
cd JavaScalarReplacement

### 2. Build the project using Maven
bash : mvn clean install

### 3. Run the main class
bash : java -cp target/JavaScalarReplacement-1.0-SNAPSHOT.jar MainClass

- You may edit MainClass.java to change input file paths or test new files.


### Input / Output
Input : src/main/java/example/example3.java

Output : src/main/java/example_new/example3.java

#### How to Add Custom Input
Create a new .java file under src/main/java/example/.


