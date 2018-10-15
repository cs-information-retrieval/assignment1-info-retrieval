Information Retrieval - Assignment 1 Part 1

# Pre-compiled Binaries
If you want to just run the pre-compiled binaries, please go into the `bin` folder. You'll find the JAR files for Single-Threaded as well as Multi-Threaded crawlers.

**NOTE:** For Multi-Threaded Crawler, the input file MUST follow this example:
`protocol://url, number of pages, url`
i.e. the seed must have a protocol (http/https) while the domain restricton must NOT have a protocol.

This restriction does NOT apply to the Single-Threaded Crawler.

To run a respective crawler, go into the `bin` folder and type into a terminal:
```
java -jar SingleThreadedCrawler.jar
```

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
