The Squad Maker Application

#Requirements
Application Requirements can be found at:
https://github.com/darryl-mccool/squad-maker

This application already has default data that provided list of players.
Application has embedded tomcat server, hence user has the ability to run application jar
from a command line. Please see section "Running JAR" below.

#Port 8090
Default Port to access this application:

#VM Args(Optional)
Optional VM Arguments:
-Dservice.resturl=<REST URL>
-Djson.file.location=<Absolute path of json file>

#Build
To build the following are required:
- Maven
- Java 8
- Dependent JAR's

##Running Locally using IDE
To run the application in an IDE, simply open the project in IDE, and as long as
you have java 8, mvn and dependency jar (can be downloaded), you will be able execute it
like a regular java application. Once running, use http://localhost:8080 to get to home page.

You can provide optional arguments from IDE (VM Arguments)

## Running JAR
To run the application from command line:
Maven build will create a demo-0.0.1-SNAPSHOT under the target folder.

maven commands to use:

mvn clean

mvn compile

mvn package

##Default json file

Project has a default json file that it uses.
 
If optional arguments are not provided, then the application will use file "json/players.json"
located under the Resource folder as Default Data.

User has the ability to provide their own data by one of the following 
two ways:
##Provide your own json file
Use the following command to run app from command line:
java -jar -Djson.file.location=Absolute path of json file demo-0.0.1-SNAPSHOT.jar

##Provide restURL
Use the following command to run app from command line:
java -jar -Dservice.resturl=REST URL demo-0.0.1-SNAPSHOT.jar
