package my.project.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestPath;

import io.quarkus.arc.profile.IfBuildProfile;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import my.project.dto.MyDataMessage;
import my.project.messaging.MyDataMessageProcessor;
import my.project.type.Source;

@Path("source1")
@IfBuildProfile("dev")
public class MyDataResource {

    private final InMemorySource<MyDataMessage> incomingSource1Channel;
    private final InMemorySource<MyDataMessage> incomingSource2Channel;
    private final InMemorySink<MyDataMessage> outgoingChannel;

    @Inject
    MyDataResource(final InMemoryConnector connector) {
        InMemoryConnector.switchIncomingChannelsToInMemory(MyDataMessageProcessor.INCOMING1_CHANNEL);
        InMemoryConnector.switchIncomingChannelsToInMemory(MyDataMessageProcessor.INCOMING2_CHANNEL);
        InMemoryConnector.switchOutgoingChannelsToInMemory(MyDataMessageProcessor.OUTGOING_CHANNEL);
        incomingSource1Channel = connector.source(MyDataMessageProcessor.INCOMING1_CHANNEL);
        incomingSource2Channel = connector.source(MyDataMessageProcessor.INCOMING2_CHANNEL);
        outgoingChannel = connector.sink(MyDataMessageProcessor.OUTGOING_CHANNEL);
    }

    @POST
    @Path("consume/{source}")
    @Consumes(MediaType.APPLICATION_JSON)
    MyDataMessage consume(@RestPath final Source source, final MyDataMessage message) {
        if (source == Source.SOURCE1) {
            incomingSource1Channel.send(message);
        }
        else if (source == Source.SOURCE2) {
            incomingSource2Channel.send(message);
        }

        while (!outgoingChannel.hasCompleted() && !outgoingChannel.hasFailed()) {

        }

        if (outgoingChannel.hasCompleted()) {
            return outgoingChannel.received().get(0).getPayload();
        }
        else if (outgoingChannel.hasFailed()) {
            throw new WebApplicationException(outgoingChannel.getFailure());
        }
        throw new WebApplicationException("Unexpected outcome, request never completed or failed");
    }
}
