package my.project.db;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import external.lib.MyData;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import my.project.type.Source;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "data", name = "data1")
@NamedQueries({
    @NamedQuery(name = MyDataEntity.FIND_LATEST_QUERY,
            query = "SELECT DISTINCT ON (e.key.id) e FROM MyDataEntity e WHERE e.key.id = :id "
                    + "ORDER BY position(e.key.source IN ('SOURCE2', 'SOURCE1')), e.key.timestamp DESC"),
    @NamedQuery(name = MyDataEntity.FIND_ALL_LATEST_QUERY,
            query = "SELECT DISTINCT ON (e.key.id) e FROM MyDataEntity e "
                    + "ORDER BY position(e.key.source IN ('SOURCE2', 'SOURCE1')), e.key.timestamp DESC")
})
class MyDataEntity implements Serializable  {

    static final String FIND_LATEST_QUERY = "MyDataEntity.findLatestById";
    static final String FIND_ALL_LATEST_QUERY = "MyDataEntity.findAllLatest";

    @EmbeddedId
    @Getter(AccessLevel.PROTECTED)
    private Key key;

    @Column(name = "data", columnDefinition = "JSONB", nullable = false, updatable = false)
    @JdbcTypeCode(SqlTypes.JSON_ARRAY)
    @Convert(converter = MyDataConverter.class)
    private MyData data;

    public MyDataEntity(final UUID id, final Instant timestamp, final Source source, final MyData data) {
        this.key = new Key(id, timestamp, source);
        this.data = data;
    }

    public UUID getId() {
        return key.getId();
    }

    public Instant getTimestamp() {
        return key.getTimestamp();
    }

    public Source getSource() {
        return key.getSource();
    }

    static class MyDataConverter implements AttributeConverter<MyData, String[]> {

        @Override
        public String[] convertToDatabaseColumn(final MyData data) {
            return new String[] { data.getLine1(), data.getLine2() };
        }

        @Override
        public MyData convertToEntityAttribute(final String[] lines) {
            return new MyData(lines[0], lines[1]);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    static class Key implements Serializable {
        @Column(name = "id", columnDefinition = "UUID", nullable = false, updatable = false)
        private UUID id;

        @Column(name = "timestamp", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false, updatable = false)
        private Instant timestamp;

        @Column(name = "source", columnDefinition = "STRING", nullable = false, updatable = false)
        @Enumerated(EnumType.STRING)
        private Source source;
    }
}