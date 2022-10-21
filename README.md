# weather-api

## Background
This application was written in Scala using the Play framework. The Play framework is used in particular for HTTP routing and JSON serialization/deserialization. No form of user authentication was implemented.  This application only has one API endpoint and that endpoint requires the use of query parameters as outlined below.

## Requirements
This application was built with sbt and Java 11. To ensure compatibility, the same version of Java should be installed. And sbt should be installed but the version does not have to be the same as the version used by the application (1.7.2).

## Testing
To run unit tests, navigate to the project’s root directory and run the command `sbt test`.

## Running:
To run the application, navigate to the project’s root directory and run the command `sbt run`. The project will compile and run after the address http://localhost:9000/?latitude=36.1627&longitude=-86.7816 is opened via browser or curl GET request.
