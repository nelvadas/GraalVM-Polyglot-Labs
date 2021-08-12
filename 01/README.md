
# 01 : Building a Simple GraalVM Polyglot Application

<div class="inline-container">
<img src="../images/noun_Stopwatch_14262_100.png">
<strong>
  Estimated time: 20 minutes
</strong>
</div>

## Content
* [Introduction](./#installations)



## Introduction

Creating polyglot Apps is very simple from GraalVM Perspective.
The purpose of this lab is to build a simple JAX-RS  Controller that will run some very simple Javascript code.
To do so, You will have to create an Helidon project from the Helidon CLI first.

## My first Polyglot REST Endpoint 

![User Input](../images/noun_Computer_3477192_100.png)
![Shell Script](../images/noun_SH_File_272740_100.png)


Create your Helidon covid19-trends `Microprofile` REST Application from your terminal
  
```bash
# Follow the conversation 
$  helidon init
Using Helidon version 2.3.2
Helidon flavor
  (1) SE
  (2) MP
Enter selection (Default: 1): 2
Select archetype
  (1) bare | Minimal Helidon MP project suitable to start from scratch
  (2) quickstart | Sample Helidon MP project that includes multiple REST operations
  (3) database | Helidon MP application that uses JPA with an in-memory H2 database
Enter selection (Default: 1): 2
Project name (Default: quickstart-mp): covid19-trends
Project groupId (Default: me.nono-helidon): com.oracle.graalvm.demos
Project artifactId (Default: quickstart-mp): covid19-trends
Project version (Default: 1.0-SNAPSHOT):
Java package name (Default: me.nono.mp.quickstart): com.oracle.graalvm.demos

```

Start the Helidon Dev loop to preview the changes
```
$ helidon dev
helidon dev starting ...
| building
| build completed (9,0 seconds)
| covid19-trends starting
```


![User Input](../images/noun_Computer_3477192_100.png)
![Java](../images/noun_java_825609_100.png)

## Add GraalVM SDK dependency
Add the Graal SDK [https://mvnrepository.com/artifact/org.graalvm.sdk/graal-sdk/21.2.0](https://mvnrepository.com/artifact/org.graalvm.sdk/graal-sdk/21.2.0) to your maven pom.xml or Gradle Build file.


## Create the Covid19Resource Controller 

Rename the the `src/main/java/com/oracle/graalvm/demos/GreetingController.java` file to 
`src/main/java/com/oracle/graalvm/demos/Covid19Controller.java` and edit it with the following content

```java 
package com.oracle.graalvm.demos;

import org.graalvm.polyglot.Context;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/covid19/fr/")
@RequestScoped
public class CovidResource {

    private Context polyglot;

    @Inject
    public CovidResource() {
        try {
          // First create a polyglot contexte to run our code
          // Context provides an execution environment for guest languages. 
          // you can pass a list of expected language for this context in the newBuilder Method 
          // R language requires the allowAllAccess flag to be set to true to run .

            this.polyglot = Context.newBuilder().allowAllAccess(true).build();
      } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Path("/help")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response help() {
       // Evaluate a polyglott expression
       // language = js
       // Code to execute as the second parameter.
       // convert the Value returned asString
        String welcome = polyglot.eval("js", "print('{\"Welcome to GraalVM Polyglot EMEA HOL!\"}');").asString();
        return Response.ok(welcome).build();

    }

}
```

Update the Main Unit test file `src/test/java/com/oracle/graalvm/demos/MainTest.java` 

```java
//Ensure the /covid19/fr/help return an HTTP 200
@Test
    void testHelloWorld() {
        Client client = ClientBuilder.newClient();


        Response r = client
                .target(serverUrl)
                .path("/covid19/fr/help")
                .request()
                .get();
       Assertions.assertEquals(200, r.getStatus(), "GET health status code");


    }
  ````

Run the application from your  browser/Terminal 
if the helidon Dev loop is not enabled, 
Build and start the application using 
```shell
# build and run 
mvn clean install 
java -jar target/covid19-trends.jar
```

Testing from Curl 
```
curl http://localhost:8080//covid19/fr/help

```
From the application logs you can see the printed message 
````
2021.08.11 11:55:11 INFO io.helidon.common.HelidonFeatures Thread[features-thread,5,main]: Helidon MP 2.3.2 features: [CDI, Config, Fault Tolerance, Health, JAX-RS, Metrics, Open API, REST Client, Security, Server, Tracing]
{"Welcome to GraalVM Polyglot EMEA HOL!"}
````


## Quiz

From the previous curl command, we have no output in the console/Browser. Why? Ho to fix that? 

<details><summary>Solution</summary>
<p>
Change the Javascript instruction to return the expected string instead of printing it to the console

```java
  String welcome = polyglot.eval("js", "'Welcome to GraalVM Polyglot EMEA HOL!\\n';").asString();
```
</p>
</details>

## Summary
In this labs, you build and run a simple Polyglot Application.
Building Polyglot applications require the same tooling as those required for traditionnal Apps.
