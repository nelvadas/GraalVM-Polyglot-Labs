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
