package my.project.rest;

import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("data")
public class MyDataResource {

    @Inject
    ConnectionFactory connectionFactory;

    @GET
    @Path("test")
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "TEST";
    }
}
