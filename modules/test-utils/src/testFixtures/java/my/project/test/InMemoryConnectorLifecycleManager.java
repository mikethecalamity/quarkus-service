package my.project.test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.logging.Logger;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;

/**
 * Test utility to create in-memory connectors for messaging tests
 */
public abstract class InMemoryConnectorLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private static final Logger LOGGER = Logger.getLogger(InMemoryConnectorLifecycleManager.class);

    protected abstract List<String> getIncomingChannels();

    protected abstract List<String> getOutgoingChannels();

    @Override
    public Map<String, String> start() {
        Map<String, String> map1 = Stream
                .concat(getIncomingChannels().stream().map(InMemoryConnector::switchIncomingChannelsToInMemory),
                        getOutgoingChannels().stream().map(InMemoryConnector::switchOutgoingChannelsToInMemory))
                .map(Map::entrySet).flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2));
        LOGGER.info(map1);
        return map1;
    }

    @Override
    public void stop() {
        InMemoryConnector.clear();
    }
}
