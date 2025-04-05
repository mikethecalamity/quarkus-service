package my.project.dto;

import java.time.Instant;
import java.util.UUID;

import external.lib.MyData;
import lombok.Data;
import my.project.type.Source;

@Data
public class MyDataMessage {
    private final UUID id;
    private final Instant timestamp;
    private final Source source;
    private final MyData data;
}