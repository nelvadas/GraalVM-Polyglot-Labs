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
    Function<String, String> getDepartmentNameByIdFunc;
    private String pythonScriptFile;
    private Source rSource;
    private String rScriptFile;
    private String csvLocalFilePath;

    private Context polyglot;


    @Inject
    public CovidResource(@ConfigProperty(name = "app.covid.pyscript") String pythonScriptFile,
                         @ConfigProperty(name = "app.covid.rscript") String rScriptUrl,
                         @ConfigProperty(name = "app.covid.data.download.csvfullpath") String csvLocalFilePath) {

        this.pythonScriptFile = pythonScriptFile;
        this.rScriptFile = rScriptUrl;
        this.csvLocalFilePath = csvLocalFilePath;
        try {
            this.polyglot = Context.newBuilder().allowAllAccess(true).build();
            this.getDepartmentNameByIdFunc = getPythonDeptFunction();
            this.rSource = Source.newBuilder("R", new File(rScriptFile)).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Path("/trends/{departmentId}")
    @GET
    @Produces({"image/svg+xml"})
    public Response getCovidHospitalisationGraphic(@PathParam("departmentId") String departmentId) {
        // Get the department Name from Python script
        String departmentName = getDepartmentNameByIdFunc.apply(departmentId);
        // Display the covid graph in R for the selected department
        CovidDtoTable.CovidDto[] datas = {new CovidDtoTable.CovidDto(departmentId, csvLocalFilePath, departmentName)};
        CovidDtoTable dataTable = new CovidDtoTable(datas);
        Function<CovidDtoTable, String> rplotFunc = polyglot.eval(rSource).as(Function.class);
        String svgData = rplotFunc.apply(dataTable);
        return Response.ok(svgData).build();
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
