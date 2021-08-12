
# 02 : Accessing Guest Language resources from Java 

<div class="inline-container">
<img src="../images/noun_Stopwatch_14262_100.png">
<strong>
  Estimated time: 20 minutes
</strong>
</div>


# Objective
* Access Guest Languages Directly from Java  
* Define Guest Language Functions as Java Values 
 


# Todo 
In the folowing lab, you will have to :
*  Create a new Java/Python polyglot REST endpoint `/covid19/fr/department/{departmentId}`
*  The new endpoint returns a department name based on its number `eg.  75 => Paris`
*  The endpoint relies on an existing Python script to retreive departement names. ( direct member access)
*  The python script is made available as a resource in the project.
*  Define a python lambda function as Java Value. 



## Instructions 


![User Input](../images/noun_Computer_3477192_100.png)
![Shell Script](../images/noun_SH_File_272740_100.png)

```bash
# Create a `scripts` folder besides your project

mkdir scripts
cd scripts
```


```bash
#Download the python script
$ wget https://raw.githubusercontent.com/nelvadas/helidon-polyglot-demo/master/scripts/department.py

```


![User Input](../images/noun_Computer_3477192_100.png)
![Java](../images/noun_java_825609_100.png)

## Application Configuration

 Edit the `src/main/resources/META-INF/microprofile-config.properties`
```bash

# Application properties. This is the default greeting
app.greeting=covid19-trends

# Microprofile server properties
server.port=8080
server.host=0.0.0.0

# Turn on support for REST.request SimpleTimers for all JAX-RS endpoints
metrics.rest-request.enabled=true

# Add the python script location
app.covid.pyscript=~/Projects/Workshops/EMEA-HOL-GraalVMPolyglot/GraalVM-Polyglot-Labs/02/scripts/department.py
```



#  Calling Python function from Java

Edit the controller  `src/main/java/com/oracle/graalvm/demos/Covid19Controller.java` 
 with the following code 



 *  Add a private instance `pythonScriptFile` to hold a reference on the Python script
 *  `Function<String, String> getDepartmentNameByIdFunc;` hold a reference on the function we want to call in order to retreive department names
 *  update the `CovidResource` constructor , include a configResource for the python script path.
 *

```bash 
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
    private String pythonScriptFile;
    Function<String, String> getDepartmentNameByIdFunc;

    private Context polyglot;


    @Inject
    public CovidResource(@ConfigProperty(name = "app.covid.pyscript") String pythonScriptFile)
    {
        try {
            this.polyglot = Context.newBuilder().allowAllAccess(true).build();
            this.pythonScriptFile = pythonScriptFile;
            this.getDepartmentNameByIdFunc = getPythonDeptFunction();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


     @Path("/help")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response help() {
        String welcome = polyglot.eval("js", "'Welcome to GraalVM Polyglot EMEA HOL!\\n';").asString();
        return Response.ok(welcome).build();
    }

    @Path("/department/{departmentId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDepartmentName(@PathParam("departmentId") String departmentId) {

        try {
            String dname = getDepartmentNameByIdFunc.apply(departmentId);
            logger.info(String.format("Departement 1%s=%s", departmentId, dname));
            return Response.ok(dname).build();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

    }

    /**
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


}

  ````

The `getDepartmentNameByIdFunc` java object refers to a python function  

```python
# department.py

@polyglot.export_value
def getDepartmentNameById(deptId):
  if deptId in dnames:
      return dnames[deptId]
  else: 
    return "-"
  ```


Run the application from your  browser/Terminal 
if the helidon Dev loop is not enabled, 
Build and start the application using 
```shell
# build and run 
mvn clean install 
java -jar target/covid19-trends.jar
```

Testing from Curl/Httpie
```bash
# get the department with id 75
http http://localhost:8080/covid19/fr/department/75
HTTP/1.1 200 OK
Content-Type: application/json
Date: Wed, 11 Aug 2021 14:42:45 +0200
connection: keep-alive
content-length: 5

PARIS

```


## Quiz

Suppose you have an inline python lamda function that convert a USD amount in EUR `lambda x: x*1.1733"` 
Ho would you invoke this function from Java 
<details><summary>Solution</summary>
<p>

```java
  Value function = polyglot.eval("python", "lambda x: x*1.1733");
  Double xeuro = function.execute(100).asDouble();
```
</p>
</details>

## Summary
In this labs, you build and run Polyglot Application running Java, Javascript and Python
You used various mechanisms to access functions from Guest languages as Java Values
Use the  `execute`and `apply` methods to invoke guest functions.

