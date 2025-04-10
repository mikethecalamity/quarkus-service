package my.project.db;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.project.type.Source;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public  class DataId implements Serializable {
    @Column(name = "id", columnDefinition = "UUID", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "timestamp", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false, updatable = false)
    private Instant timestamp;

    @Column(name = "source", columnDefinition = "INT", nullable = false, updatable = false)
    @Convert(converter = SourceConverter.class)
    private Source source;

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
}
