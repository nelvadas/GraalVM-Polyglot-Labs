
# 02 : Accessing Guest Language resources from Java 

<div class="inline-container">
<img src="../images/noun_Stopwatch_14262_100.png">
<strong>
  Estimated time: 20 minutes
</strong>
</div>


# Objective
* Access Guest languages directly from Java Code
* Define guest language functions as Java Values 
 


# Todo 
In the folowing lab, you will have to :
*  Create a new Java/Python polyglot REST endpoint `/covid19/fr/department/{departmentId}`
*  The new endpoint returns a department name based on its number `eg.  75 => Paris`
*  The endpoint relies on an existing `Python` script to retreive departement names. ( direct member access)
*  The python script is made available as a resource in the project.
*  Define a python lambda function as Java Value. 



## Instructions 


![User Input](../images/noun_Computer_3477192_100.png)
![Shell Script](../images/noun_SH_File_272740_100.png)

```shell
# Create a `scripts` folder besides your project
$ mkdir scripts
$ cd scripts
```


```shell
#Download the python script
$ wget https://raw.githubusercontent.com/nelvadas/helidon-polyglot-demo/master/scripts/department.py

```

The python scripts has two main parts:
* a dictionnary global vairable `dnames` containing department ids( key) and names ( values)
* a function `getDepartmentNameById` that we intend to call in the Java Controller


```python
# department.py
import polyglot

dnames={
"01":  "AIN",
"02":  "AISNE",
"03":  "ALLIER",
"05":  "HAUTES-ALPES",
"04":  "ALPES-DE-HAUTE-PROVENCE",
"06":  "ALPES-MARITIMES",
"07":  "ARDÈCHE",
"08":  "ARDENNES",
"09":  "ARIÈGE",
#...
"972":  "MARTINIQUE",
"974":  "RÉUNION"
}

#Make the  getDepartmentNameById function available as a Member of the polyglot context.
@polyglot.export_value
def getDepartmentNameById(deptId):
  if deptId in dnames:
      return dnames[deptId]
  else: 
    return "-"
  ```

The `polyglot.export_value` decorator on top of the `getDepartmentNameById`function is used to export this fonction with the name `getDepartmentNameById` in our polyglot context so others applications can have access to it


![User Input](../images/noun_Computer_3477192_100.png)
![Java](../images/noun_java_825609_100.png)

## Application Configuration

 Edit the `src/main/resources/META-INF/microprofile-config.properties` to add a new config property `app.covid.pyscript`  pointing to the location of the python script you want to use in your Java Endpoint 
<pre><code>

# Application properties. This is the default greeting
app.greeting=covid19-trends

# Microprofile server properties
server.port=8080
server.host=0.0.0.0

# Turn on support for REST.request SimpleTimers for all JAX-RS endpoints
metrics.rest-request.enabled=true

# Add the python script location
<b>app.covid.pyscript=~/Projects/Workshops/EMEA-HOL-GraalVMPolyglot/GraalVM-Polyglot-Labs/02/complete/scripts/department.py</b>
</code></pre>

As soon as the python script is avilable in your workspace, 
you can call
Now we are going to edit the controller to show how you can call Python from Java
to retreive the department names based on departement id.
The controller should received the department id in java and shared this parameter with the python script.

#  Calling Python function from Java

Edit the controller  `src/main/java/com/oracle/graalvm/demos/Covid19Controller.java` 
 with the following code 



 *  Add a private instance `pythonScriptFile` to hold a reference on the Python script
 *  `Function<String, String> getDepartmentNameByIdFunc;` hold a reference on the function we want to call in order to retreive department names
 *  update the `CovidResource` constructor , include a configResource for the python script path.
 *  Load a reference to the python Function `getDepartmentNameById` and keep it in a Java Property

```java

package com.oracle.graalvm.demos;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;
import java.util.logging.Logger;


@Path("/covid19/fr/")
@RequestScoped
public class CovidResource {

    private static Logger logger = Logger.getLogger(CovidResource.class.getName());
    //reference the python script file
    private String pythonScriptFile;
    // Create a java Object to hold a reference on the python function getDepartmentNameById
    Function<String, String> getDepartmentNameByIdFunc;

    // Polyglot context to run code with guest languages
    private Context polyglot;


    // Automatically inject configuration properties from src/main/resources/META-INF/microprofile-config.properties  when The Controller instance is created

    @Inject
    public CovidResource(@ConfigProperty(name = "app.covid.pyscript") String pythonScriptFile)
    {
        try {

          // Context provides an execution environment for guest languages. 
          // you can pass a list of expected language for this context in the newBuilder Method 
          // R language requires the allowAllAccess flag to be set to true to run .

            this.polyglot = Context.newBuilder().allowAllAccess(true).build();
            // keep the python script location
            this.pythonScriptFile = pythonScriptFile;

            //******** Intialize the Python function as a Java Property. *******
            this.getDepartmentNameByIdFunc = getPythonDeptFunction();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


   /**
     * This function uses the polyglot contexte to load the python script first
     * then keep a reference on the getDepartmentNameById 
     * Load the PYTHON script that provide a function to retrieve department names
     * from their Ids.
     *
     * @return
     * @throws IOException
     */
    private Function<String, String> getPythonDeptFunction() throws IOException {
        Source source = Source.newBuilder("python", new File(pythonScriptFile)).build();
        Value pyPart = polyglot.eval(source);
        Function<String, String> getDepartmentNameByIdFunc =
                pyPart.getContext().getPolyglotBindings().getMember("getDepartmentNameById").as(Function.class);
        return getDepartmentNameByIdFunc;
    }


    @Path("/help")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response help() {
        String welcome = polyglot.eval("js", "'Welcome to GraalVM Polyglot EMEA HOL!\\n';").asString();
        return Response.ok(welcome).build();
    }


    // The new Endpoint to retreive department 's name from departmentId
    @Path("/department/{departmentId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDepartmentName(@PathParam("departmentId") String departmentId) {

        try {
            // Call the Pyhton function  from the getDepartmentNameByIdFunc variables with the deparmentId as parameter
            String dname = getDepartmentNameByIdFunc.apply(departmentId);
            logger.info(String.format("Departement 1%s=%s", departmentId, dname));
            return Response.ok(dname).build();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

    }

   


}

  ```

The `getDepartmentNameByIdFunc` java object refers to a python function  

```python
# department.py

#Make the  getDepartmentNameById function available as a Member of the polyglot context.
@polyglot.export_value
def getDepartmentNameById(deptId):
  if deptId in dnames:
      return dnames[deptId]
  else: 
    return "-"
  ```


Run the application from your  browser/terminal 
if the helidon Dev loop is not enabled, 
Build and start the application using 
```shell
# build and run 
$ mvn clean install 
$ java -jar target/covid19-trends.jar
```

Testing from curl
```shell
# get the department with id 75
$ curl -v http://localhost:8080/covid19/fr/department/75
HTTP/1.1 200 OK
Content-Type: application/json
Date: Wed, 11 Aug 2021 14:42:45 +0200
connection: keep-alive
content-length: 5

PARIS

```


## Quiz

1. Suppose you have an inline python lamda function that convert a USD amount in EUR `lambda x: x*1.1733"` 
Ho would you invoke this function from Java ?
2. What is the purpose of `@polyglot.export_value` annotation in the python script?
3. What happen when you edit the python script with the Java program  running?
<details><summary>Solution</summary>
<p>
 1. Lamdda 

```java
  Value function = polyglot.eval("python", "lambda x: x*1.1733");
  Double xeuro = function.execute(100).asDouble();
```
2. Make the annotated function available in the polyglot context.
3. Python updates are automatically incorporated in the next java calls .
  if you changed the python code whitout restarting the Java controller, the python updates are availables in the next calls on the endpoint.
  You can swap two departments id and names to confirm the behaviour.

</p>
</details>

## Summary
In this labs, you built and run a polyglot application running Java, Javascript and Python
You used various mechanisms to access functions from Guest languages as Java Values
Use the  `execute`and `apply` methods to invoke guest functions.
Congratulation for your :2nd_place_medal:	

