package my.project.dto;

import java.time.Instant;
import java.util.UUID;
import my.project.type.Source;

import lombok.Data;

@Data
public class MyDataMessage {
    private final UUID id;
    private final Instant timestamp;
    private final Source source;
    private final MyData data;
}