package my.project.messaging;

import java.util.List;

import my.project.test.InMemoryConnectorLifecycleManager;

/**
 * Test utility to create in-memory connectors for {@link MyDataMessageProcessor} messaging tests
 */
public class MyDataInMemoryConnectorLifecycleManager extends InMemoryConnectorLifecycleManager {

    @Override
    protected List<String> getIncomingChannels() {
        return List.of(MyDataMessageProcessor.INCOMING1_CHANNEL, MyDataMessageProcessor.INCOMING2_CHANNEL);
    }

    @Override
    protected List<String> getOutgoingChannels() {
        return List.of(MyDataMessageProcessor.OUTGOING_CHANNEL);
    }

}
