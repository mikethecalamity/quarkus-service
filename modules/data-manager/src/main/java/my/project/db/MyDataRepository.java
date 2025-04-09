package my.project.db;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;

import external.lib.MyData;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import my.project.dto.MyDataMessage;
import my.project.type.Source;

@ApplicationScoped
@WithTransaction
public class MyDataRepository implements PanacheRepositoryBase<MyDataEntity, UUID> {

    public Uni<MyDataMessage> findLatest(final UUID id) {
        return find("#MyDataEntity.findLatestById", id).firstResult().onItem()
                .transform(e -> new MyDataMessage(e.getId(), e.getTimestamp(), e.getSource(), e.getData()));
    }

    public Uni<List<MyDataMessage>> findAllLatest() {
        final Uni<List<MyDataEntity>> result = find("#MyDataEntity.findAllLatest").list();
        return result.map(list -> list.stream()
                .map(e -> new MyDataMessage(e.getId(), e.getTimestamp(), e.getSource(), e.getData())).toList());
    }

    public Uni<MyDataMessage> persistAndFindLatest(final UUID id, final Instant timestamp, final Source source,
            final MyData data) {
        final MyDataEntity entity = new MyDataEntity(id, timestamp, source, data);
        return persist(entity).onItem().transformToUni(e -> findLatest(id));
    }
}