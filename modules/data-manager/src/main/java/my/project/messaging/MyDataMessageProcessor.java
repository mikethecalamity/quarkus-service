package my.project.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.mutiny.Multi;
import lombok.RequiredArgsConstructor;
import my.project.db.MyDataRepository;
import my.project.dto.MyDataMessage;
import my.project.type.Source;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MyDataMessageProcessor {

    public static final String INCOMING1_CHANNEL = "incoming-source1-data1";
    public static final String INCOMING2_CHANNEL = "incoming-source2-data1";
    public static final String OUTGOING_CHANNEL = "outgoing-data1";

    private final MyDataRepository dataRepository;

    @Incoming(INCOMING1_CHANNEL)
    @Outgoing(OUTGOING_CHANNEL)
    public Multi<MyDataMessage> consumeSource1(final Multi<MyDataMessage> message) {
        return message.onItem().transformToUniAndMerge(
                m -> dataRepository.persistAndFindLatest(m.getId(), m.getTimestamp(), Source.SOURCE1, m.getData()));
    }

    @Incoming(INCOMING2_CHANNEL)
    @Outgoing(OUTGOING_CHANNEL)
    public Multi<MyDataMessage> consumeSource2(final Multi<MyDataMessage> message) {
        return message.onItem().transformToUniAndMerge(
                m -> dataRepository.persistAndFindLatest(m.getId(), m.getTimestamp(), Source.SOURCE2, m.getData()));
    }
}
