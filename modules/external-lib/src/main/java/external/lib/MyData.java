package external.lib;

import lombok.Data;

@Data
public class MyData {
    private final String line1;
    private final String line2;

    private final long epoch;
    private final int value2;
    private final int value3;
    private final int value4;
    private final int value5;
    private final int value6;

    public MyData(String line1, String line2) {
        this.line1 = line1;
        this.line2 = line2;

        final String[] parts1 = line1.split(" ");
        this.epoch = Long.valueOf(parts1[0]);
        this.value2 = Integer.valueOf(parts1[1]);
        this.value3 = Integer.valueOf(parts1[2]);
        final String[] parts2 = line2.split(" ");
        this.value4 = Integer.valueOf(parts2[0]);
        this.value5 = Integer.valueOf(parts2[1]);
        this.value6 = Integer.valueOf(parts2[2]);
    }
}