# excel-import

The content of this project realizes a [leanIX](https://www.leanix.net/) metrics job as a console application. The main purpose is to import demo data for metrics from Microsoft Office excel files.

The excel file to read must contain the demo data in a specific structure. See [how to create demo data](HowToCreateDemoData.md) for more information.

It uses [Apache POI](https://poi.apache.org/) to extract needed data from an excel file and the [leanIX metrics api](https://dev.leanix.net/docs/api-overview) to store them as points. The project depends on [Java 8](http://www.oracle.com/technetwork/java/javase/overview/index.html) (OpenJDK 8 is not tested).
 
## Table of Contents
 
- [Project setup](#project-setup)
- [Release artifacts](#release-artifacts)
 
## Project setup
 
This project uses [Apache Maven](https://maven.apache.org/) as build tool. Please use the latest version.
 
Initialization: `mvn package` in project directory
 
## Release artifacts
 
In the `target` directory of this project, you can find (after a successful packaging):
 
- `excel-import-release.tar.gz`
- `excel-import-release.zip`

You can use one of them to deploy this metrics job. Use `java -jar excel-import.jar -help` to get a clue how to use it.