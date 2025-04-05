package my.project.db;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import external.lib.MyData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.project.type.Source;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "data", name = "data1")
@NamedQueries({
    @NamedQuery(
        name = MyDataEntity.FIND_LATEST_QUERY,
        query = "SELECT DISTINCT ON (e.id) e FROM MyDataEntity e WHERE id = :id ORDER BY position(e.source IN ('SOURCE2', 'SOURCE1')), e.timestamp DESC"
    ),
    @NamedQuery(
        name = MyDataEntity.FIND_ALL_LATEST_QUERY,
        query = "SELECT DISTINCT ON (e.id) e FROM MyDataEntity e ORDER BY position(e.source IN ('SOURCE2', 'SOURCE1')), e.timestamp DESC"
    )
})
class MyDataEntity {

    static final String FIND_LATEST_QUERY = "MyDataEntity.findLatestById";
    static final String FIND_ALL_LATEST_QUERY = "MyDataEntity.findAllLatest";

    @Id
    @Column(name = "id", columnDefinition = "UUID", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "timestamp", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false, updatable = false)
    private Instant timestamp;

    @Column(name = "source", columnDefinition = "STRING", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private Source source;

    @Column(name = "data", columnDefinition = "JSONB", nullable = false, updatable = false)
    @JdbcTypeCode(SqlTypes.JSON_ARRAY)
    @Convert(converter = MyDataConverter.class)
    private MyData data;

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
}