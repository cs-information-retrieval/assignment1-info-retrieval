Information Retrieval - Assignment 1 Part 1

# Setup
1. Download [Java JDK](https://www.oracle.com/technetwork/java/javase/downloads/index.html). Make sure it is JDK 8 or higher.
2. Download [Maven](https://maven.apache.org/download.cgi).

# Change from Multi-Thread to Single-Thread
To use either SingleThread or MultiThread class, go into `main/MainFrame.java` and look around line 168. Please comment out which `Crawler` class you do not want to use.

# How to Compile
1. Make sure you are in the folder with the file "pom.xml"
2. Type into the terminal:
```
mvn clean compile assembly:single
```

# How to Run
1. Make sure you are in the folder with the file "pom.xml"
2. Type into the terminal:
```
java -jar target/info_retrieval_part1-1.0-SNAPSHOT-jar-with-dependencies.jar
```
