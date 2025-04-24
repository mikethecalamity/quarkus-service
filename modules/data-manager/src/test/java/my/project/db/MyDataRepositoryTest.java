package my.project.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import external.lib.MyData;
import io.quarkus.test.TestReactiveTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.UniAsserter;
import my.project.type.Source;

/**
 * Tests for {@link MyDataRepository}
 */
@Disabled
@QuarkusTest
//@QuarkusTestResource(H2DatabaseTestResource.class)
@TestReactiveTransaction
public class MyDataRepositoryTest {

    private static final UUID ID1 = UUID.randomUUID();
    private static final UUID ID2 = UUID.randomUUID();

    private static final MyData DATA1 = new MyData("1743581148 5 10 15", "10 20 30 40");
    private static final MyData DATA2 = new MyData("1743681148 6 11 16", "11 21 31 41");
    private static final MyData DATA3 = new MyData("1743781148 7 12 17", "12 22 32 42");
    private static final MyData DATA4 = new MyData("1743881148 8 13 18", "13 23 33 43");
    private static final MyData DATA5 = new MyData("1743981148 9 14 19", "14 24 34 44");

    private final MyDataEntity entity1 =
            new MyDataEntity(ID1, Instant.ofEpochSecond(DATA1.getEpoch()), Source.SOURCE1, DATA1);
    private final MyDataEntity entity2 =
            new MyDataEntity(ID1, Instant.ofEpochSecond(DATA2.getEpoch()), Source.SOURCE2, DATA2);
    private final MyDataEntity entity3 =
            new MyDataEntity(ID1, Instant.ofEpochSecond(DATA3.getEpoch()), Source.SOURCE1, DATA3);
    private final MyDataEntity entity4 =
            new MyDataEntity(ID1, Instant.ofEpochSecond(DATA4.getEpoch()), Source.SOURCE2, DATA4);
    private final MyDataEntity entity5 =
            new MyDataEntity(ID2, Instant.ofEpochSecond(DATA5.getEpoch()), Source.SOURCE2, DATA5);
    @Inject
    private MyDataRepository dataRepository;

    @Test
    void findLatestTest(final UniAsserter asserter) {
        asserter.assertEquals(() -> dataRepository.persist(entity1, entity2, entity3, entity4, entity5)
                .chain(() -> dataRepository.count()), 5L);

        asserter.assertThat(() -> dataRepository.findLatest(ID1), m -> {
            assertThat(m.getId()).isEqualTo(ID1);
            assertThat(m.getTimestamp()).isEqualTo(Instant.ofEpochSecond(DATA3.getEpoch()));
            assertThat(m.getSource()).isEqualTo(Source.SOURCE1);
            assertThat(m.getData()).isEqualTo(DATA3);
        });
    }

    @Test
    void findAllLatestTest(final UniAsserter asserter) {
        asserter.assertEquals(() -> dataRepository.persist(entity1, entity2, entity3, entity4, entity5)
                .chain(() -> dataRepository.count()), 5L);

        asserter.assertThat(() -> dataRepository.findAllLatest(), results -> {
            assertThat(results).satisfiesExactlyInAnyOrder(m -> {
                assertThat(m.getId()).isEqualTo(ID1);
                assertThat(m.getTimestamp()).isEqualTo(Instant.ofEpochSecond(DATA3.getEpoch()));
                assertThat(m.getSource()).isEqualTo(Source.SOURCE1);
                assertThat(m.getData()).isEqualTo(DATA3);
            }, m -> {
                assertThat(m.getId()).isEqualTo(ID2);
                assertThat(m.getTimestamp()).isEqualTo(Instant.ofEpochSecond(DATA5.getEpoch()));
                assertThat(m.getSource()).isEqualTo(Source.SOURCE2);
                assertThat(m.getData()).isEqualTo(DATA5);
            });
        });
    }

    @Test
    void persistAndFindLatestTest(final UniAsserter asserter) {
        // Add the data and check that the same data is returned (since there is no other data)
        final UUID id = UUID.randomUUID();
        final Instant time1 = Instant.ofEpochSecond(2);
        asserter.assertThat(() -> dataRepository.persistAndFindLatest(id, time1, Source.SOURCE2, DATA1), m -> {
            assertThat(m.getId()).isEqualTo(id);
            assertThat(m.getTimestamp()).isEqualTo(time1);
            assertThat(m.getSource()).isEqualTo(Source.SOURCE2);
            assertThat(m.getData()).isEqualTo(DATA1);
        });

        // Add the data and check that the same data is returned
        // (since SOURCE1 is prioritized, even though its timestamp is older)
        final Instant time2 = Instant.ofEpochSecond(1);
        asserter.assertThat(() -> dataRepository.persistAndFindLatest(id, time2, Source.SOURCE1, DATA2), m -> {
            assertThat(m.getId()).isEqualTo(id);
            assertThat(m.getTimestamp()).isEqualTo(time2);
            assertThat(m.getSource()).isEqualTo(Source.SOURCE1);
            assertThat(m.getData()).isEqualTo(DATA2);
        });

        // Add the data and check that the second set of data is returned (since SOURCE1 is prioritized)
        final Instant time3 = Instant.ofEpochSecond(3);
        asserter.assertThat(() -> dataRepository.persistAndFindLatest(id, time3, Source.SOURCE2, DATA3), m -> {
            assertThat(m.getId()).isEqualTo(id);
            assertThat(m.getTimestamp()).isEqualTo(time2);
            assertThat(m.getSource()).isEqualTo(Source.SOURCE1);
            assertThat(m.getData()).isEqualTo(DATA2);
        });

        // Add the data and check that the same data is returned
        // (since it has a newer timestamp than the other SOURCE1 data)
        final Instant time4 = Instant.ofEpochSecond(3);
        asserter.assertThat(() -> dataRepository.persistAndFindLatest(id, time4, Source.SOURCE1, DATA4), m -> {
            assertThat(m.getId()).isEqualTo(id);
            assertThat(m.getTimestamp()).isEqualTo(time3);
            assertThat(m.getSource()).isEqualTo(Source.SOURCE1);
            assertThat(m.getData()).isEqualTo(DATA4);
        });
    }
}
