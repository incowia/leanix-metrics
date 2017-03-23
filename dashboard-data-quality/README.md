# dashboard-data-quality
 
The content of this project realizes a [leanIX](https://www.leanix.net/) metrics job as a console application. It can be included in any cronjob-like environment.

It uses the [leanIX rest api](https://dev.leanix.net/docs/api-overview) to query needed data and the [leanIX metrics api](https://dev.leanix.net/docs/api-overview) to store them as points. The project depends on [Java 8](http://www.oracle.com/technetwork/java/javase/overview/index.html) (OpenJDK 8 is not tested).
 
## Table of Contents
 
- [Project setup](#project-setup)
- [Release artifacts](#release-artifacts)
 
## Project setup
 
This project uses [Apache Maven](https://maven.apache.org/) as build tool. Please use the latest version.
 
Initialization: `mvn package` in project directory
 
## Release artifacts
 
In the `target` directory of this project, you can find (after a successful packaging):
 
- `dashbord-data-quality-release.tar.gz`
- `dashbord-data-quality-release.zip`

You can use one of them to deploy this metrics job. Use `java -jar dashboard-data-quality.jar -help` to get a clue how to use it.