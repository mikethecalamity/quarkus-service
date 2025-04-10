package my.project.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The source of the data
 */
@RequiredArgsConstructor
@Getter
public enum Source {
    /**
     * From source 1
     */
    SOURCE1(0),
    /**
     * From source 2
     */
    SOURCE2(1);

    private final int value;

    public static Source fromValue(final int value) {
        for (Source source : values()) {
            if (source.getValue() == value) {
                return source;
            }
        }
        throw new IllegalArgumentException("Unknown source value: " + value);
    }
}