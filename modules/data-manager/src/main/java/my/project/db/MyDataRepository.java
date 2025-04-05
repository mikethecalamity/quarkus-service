package my.project.db;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import my.project.dto.MyDataMessage;
import my.project.type.Source;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
@Transactional
public class MyDataRepository implements PanacheRepositoryBase<MyDataEntity, UUID> {

    public Uni<MyDataMessage> findLatest(final UUID id) {
        return find(MyDataEntity.FIND_LATEST_QUERY, Map.of("id", id)).firstResult().onItem()
                .transform(e -> new MyDataMessage(e.getId(), e.getTimestamp(), e.getSource(), e.getMyData()));
    }

    public Multi<MyDataMessage> findAllLatest() {
        final Uni<List<MyDataEntity>> result = find(MyDataEntity.FIND_ALL_LATEST_QUERY).list();
        return result.onItem().transformToMulti(list -> Multi.createFrom().iterable(list)).onItem()
                .transform(e -> new MyDataMessage(e.getId(), e.getTimestamp(), e.getSource(), e.getMyData()));
    }

    public Uni<MyDataMessage> persistAndFindLatest(final UUID id, final Instant timestamp, final Source source,
            final MyData data) {
        final MyDataEntity entity = new MyDataEntity(id, timestamp, source, data);
        return persist(entity).onItem().transformToUni(e -> findLatest(id));
    }
}