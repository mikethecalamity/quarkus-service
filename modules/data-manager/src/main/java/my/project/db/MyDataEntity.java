package my.project.db;

import java.io.Serializable;
import java.io.StringReader;
import java.time.Instant;
import java.util.UUID;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonReader;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

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
@Table(schema = "data", name = "data1", indexes = @Index(columnList = "id, "))
@NamedQueries({
    @NamedQuery(name = MyDataEntity.FIND_LATEST_QUERY,
            query = """
                    SELECT e FROM MyDataEntity e
                    WHERE e.key.id = ?1
                    ORDER BY e.key.source ASC, e.key.timestamp DESC
                    FETCH FIRST 1 ROW ONLY
                    """),
    @NamedQuery(name = MyDataEntity.FIND_ALL_LATEST_QUERY,
            query = """
                    SELECT e FROM MyDataEntity e WHERE NOT EXISTS (
                      SELECT 1 FROM MyDataEntity e2
                      WHERE e.key.id = e2.key.id
                      AND (
                        e.key.source > e2.key.source OR (
                          e.key.source = e2.key.source AND e.key.timestamp < e2.key.timestamp
                        )
                      )
                    )
                    """)
})
class MyDataEntity implements Serializable  {

    static final String FIND_LATEST_QUERY = "MyDataEntity.findLatestById";
    static final String FIND_ALL_LATEST_QUERY = "MyDataEntity.findAllLatest";

    @EmbeddedId
    @Getter(AccessLevel.PROTECTED)
    private Key key;

    @Column(name = "data", columnDefinition = "JSONB", nullable = false, updatable = false)
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

    static class SourceConverter implements AttributeConverter<Source, Integer> {

        @Override
        public Integer convertToDatabaseColumn(final Source value) {
            return value.getValue();
        }

        @Override
        public Source convertToEntityAttribute(final Integer value) {
            return Source.fromValue(value);
        }
    }

    static class MyDataConverter implements AttributeConverter<MyData, String> {

        @Override
        public String convertToDatabaseColumn(final MyData data) {
            return Json.createArrayBuilder().add(data.getLine1()).add(data.getLine2()).toString();
        }

        @Override
        public MyData convertToEntityAttribute(final String data) {
            try (JsonReader jsonReader = Json.createReader(new StringReader(data))) {
                final JsonArray json = jsonReader.readArray();
                return new MyData(json.getString(0), json.getString(1));
            }
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

        @Column(name = "source", columnDefinition = "INT", nullable = false, updatable = false)
        @Convert(converter = SourceConverter.class)
        private Source source;
    }
}