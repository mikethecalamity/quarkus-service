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
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import external.lib.MyData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.project.type.Source;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "data", name = "data1", indexes = @Index(columnList = "id, "))
@NamedQueries({
    @NamedQuery(name = MyDataEntity.FIND_LATEST_QUERY, query = """
        SELECT e FROM MyDataEntity e
        WHERE e.id.id = ?1
        ORDER BY e.id.source ASC, e.id.timestamp DESC
        FETCH FIRST 1 ROW ONLY
        """),
    @NamedQuery(name = MyDataEntity.FIND_ALL_LATEST_QUERY, query = """
        SELECT e FROM MyDataEntity e WHERE NOT EXISTS (
          SELECT 1 FROM MyDataEntity e2
          WHERE e.id.id = e2.id.id
          AND (
            e.id.source > e2.id.source OR (
              e.id.source = e2.id.source AND e.id.timestamp < e2.id.timestamp
            )
          )
        )
        """)
})
class MyDataEntity implements Serializable {

    static final String FIND_LATEST_QUERY = "MyDataEntity.findLatestById";
    static final String FIND_ALL_LATEST_QUERY = "MyDataEntity.findAllLatest";

    @EmbeddedId
    private DataId id;

    @Column(name = "data", columnDefinition = "JSONB", nullable = false, updatable = false)
    @Convert(converter = MyDataConverter.class)
    private MyData data;

    public MyDataEntity(final UUID id, final Instant timestamp, final Source source, final MyData data) {
        this.id = new DataId(id, timestamp, source);
        this.data = data;
    }

    public UUID getId() {
        return id.getId();
    }

    public Instant getTimestamp() {
        return id.getTimestamp();
    }

    public Source getSource() {
        return id.getSource();
    }

    static class MyDataConverter implements AttributeConverter<MyData, String> {

        @Override
        public String convertToDatabaseColumn(final MyData data) {
            return Json.createArrayBuilder().add(data.getLine1()).add(data.getLine2()).build().toString();
        }

        @Override
        public MyData convertToEntityAttribute(final String data) {
            try (JsonReader jsonReader = Json.createReader(new StringReader(data))) {
                final JsonArray json = jsonReader.readArray();
                return new MyData(json.getString(0), json.getString(1));
            }
        }
    }
}