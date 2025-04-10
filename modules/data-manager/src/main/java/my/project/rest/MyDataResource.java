package my.project.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestStreamElementType;

import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Multi;
import my.project.dto.MyDataMessage;
import my.project.messaging.MyDataMessageProcessor;
import my.project.type.Source;

@Startup
@Path("data1")
@IfBuildProfile("dev")
public class MyDataResource {

    @Inject
    @Channel(MyDataMessageProcessor.INCOMING1_CHANNEL)
    Emitter<MyDataMessage> incomingSource1Channel;

    @Inject
    @Channel(MyDataMessageProcessor.INCOMING2_CHANNEL)
    Emitter<MyDataMessage> incomingSource2Channel;

    @Inject
    @Channel(MyDataMessageProcessor.OUTGOING_CHANNEL)
    Multi<MyDataMessage> outgoingChannel;

    @POST
    @Path("consume/{source}")
    @Consumes(MediaType.APPLICATION_JSON)
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<MyDataMessage> consume(@RestPath final Source source, final MyDataMessage message) {
        if (source == Source.SOURCE1) {
            incomingSource1Channel.send(message);
        }
        else if (source == Source.SOURCE2) {
            incomingSource2Channel.send(message);
        }

        return outgoingChannel;
    }
}
