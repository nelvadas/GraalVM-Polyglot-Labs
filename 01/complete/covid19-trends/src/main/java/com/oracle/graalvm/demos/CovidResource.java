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
            this.polyglot = Context.newBuilder().allowAllAccess(true).build();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @return
     */
    @Path("/help")
    @GET
    @Produces(MediaType.APPLICATION_JSON)

    public Response help() {

        String welcome = polyglot.eval("js", "print('{\"Welcome to GraalVM Polyglot EMEA HOL!\"}');").asString();
        String welcome1 = polyglot.eval("js", "'Welcome to GraalVM Polyglot EMEA HOL!\\n';").asString();
        return Response.ok(welcome).build();


    }


}
