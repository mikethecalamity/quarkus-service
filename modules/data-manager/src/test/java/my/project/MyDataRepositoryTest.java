package my.project.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import my.project.dto.MyDataMessage;
import my.project.type.Source;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

/**
 * Tests for {@link MyDataRepository}
 */
@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
public class MyDataRepositoryTest {

    private static final MyData DATA1 = new MyData("1743581148 5 10 15", "10 20 30 40");
    private static final MyData DATA2 = new MyData("1743681148 6 11 16", "11 22 33 44");
    private static final MyData DATA3 = new MyData("1743781148 7 12 17", "11 22 33 44");
    private static final MyData DATA4 = new MyData("1743881148 8 13 18", "11 22 33 44");

    @InjectMock
    private MyDataRepository dataRepository;

    @Test
    void findLatestTest() {
        final UUID id = UUID.randomUUID();

        final MyDataEntity entity1 = new MyDataEntity(id, Instant.from(DATA1.getEpoch()), Source.SOURCE2, DATA1);
        final MyDataEntity entity2 = new MyDataEntity(id, Instant.from(DATA2.getEpoch()), Source.SOURCE1, DATA2);
        final MyDataEntity entity3 = new MyDataEntity(id, Instant.from(DATA3.getEpoch()), Source.SOURCE2, DATA3);
        final MyDataEntity entity4 = new MyDataEntity(id, Instant.from(DATA4.getEpoch()), Source.SOURCE1, DATA4);
        dataRepository.persist(entity1, entity2, entity3, entity4);

        final MyDataMessage result = dataRepository.findLatest(id).subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertCompleted().getItem();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getTimestamp()).isEqualTo(Instant.from(DATA3.getEpoch()));
        assertThat(result.getSource()).isEqualTo(Source.SOURCE2);
        assertThat(result.getMyData()).isEqualTo(DATA3);
    }
}
