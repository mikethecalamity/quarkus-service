package my.project.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import external.lib.MyData;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import my.project.db.MyDataRepository;
import my.project.dto.MyDataMessage;
import my.project.type.Source;

/**
 * Tests for {@link MyDataMessageProcessor}
 */
@QuarkusTest
@QuarkusTestResource(MyDataInMemoryConnectorLifecycleManager.class)
public class MyDataMessageProcessorTest {

    @Inject
    @Connector("smallrye-in-memory")
    private InMemoryConnector connector;

    @InjectMock
    private MyDataRepository dataRepository;

    @InjectMock
    private MyDataMessageProcessor processor;

    @AfterEach
    void after() {
        connector.sink(MyDataMessageProcessor.OUTGOING_CHANNEL).clear();
    }

    @ParameterizedTest
    @ArgumentsSource(ChannelSourceArgumentsProvider.class)
    void consumeTest(final String incomingChannel, final Source expectedSource) {
        final InMemorySource<MyDataMessage> incoming = connector.source(incomingChannel);
        final InMemorySink<MyDataMessage> outgoing = connector.sink(MyDataMessageProcessor.OUTGOING_CHANNEL);

        final UUID id = UUID.randomUUID();
        final Instant timestamp = Instant.now();
        final MyData data = new MyData("1743581148 5 10 15", "10 20 30 40");
        final MyDataMessage request = new MyDataMessage(id, timestamp, null, data);
        final MyDataMessage response = new MyDataMessage(id, timestamp, expectedSource, data);

        when(dataRepository.persistAndFindLatest(eq(id), eq(timestamp), eq(expectedSource), eq(data)))
                .thenReturn(Uni.createFrom().item(response));

        incoming.send(request);

        await().untilAsserted(() -> {
            assertThat(outgoing.received().get(0).getPayload()).isSameAs(response);
        });
    }

    static class ChannelSourceArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
            return Stream.of(Arguments.of(MyDataMessageProcessor.INCOMING1_CHANNEL, Source.SOURCE1),
                    Arguments.of(MyDataMessageProcessor.INCOMING2_CHANNEL, Source.SOURCE2));
        }
    }
}
