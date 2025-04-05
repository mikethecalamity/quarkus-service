package my.project;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import my.project.db.MyDataRepository;
import my.project.dto.MyDataMessage;
import my.project.type.Source;

import io.smallrye.mutiny.Multi;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MyDataMessageProcessor {

    private final MyDataRepository dataRepository;

    @Incoming("incoming-source1-data1")
    @Outgoing("outgoing-data1")
    public Multi<MyDataMessage> consumeExternal(final Multi<MyDataMessage> message) {
        return message.onItem().transformToUniAndMerge(
                m -> dataRepository.persistAndFindLatest(m.getId(), m.getTimestamp(), Source.SOURCE1, m.getMyData()));
    }

    @Incoming("incoming-source2-data1")
    @Outgoing("outgoing-data1")
    public Multi<MyDataMessage> consumeOD(final Multi<MyDataMessage> message) {
        return message.onItem().transformToUniAndMerge(
                m -> dataRepository.persistAndFindLatest(m.getId(), m.getTimestamp(), Source.SOURCE2, m.getMyData()));
    }
}
