# Corneast
Corneast is a distributed token middleware.  

## Features
- **Decoupling with Business**
- **High Performance**
- **Scalability**

## Building
Run `mvn clean install` from the root directory of the project.  

The built jar files will be located in the `target` directory of each module.  

## Running
```java
java -jar corneast-<module>-<version>[-exec].jar
```

## Usage
Deploy example: [corneast-deploy/README.md](corneast-deploy/README.md)  

Request object generating: Use `CorneastRequest` or `CorneastRequestBuilder` in `corneast-client` to construct request objects.  

Request file generating example: generate while testing in `ProtobufRequestGeneratorTests.java`.  

## Reporting Issues
Use Github Issues.  

## Contributing
Contributions to Corneast are welcome!  
