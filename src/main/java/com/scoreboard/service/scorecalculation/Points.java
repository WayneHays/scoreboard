package com.scoreboard.service.scorecalculation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Points {
    ZERO("0"),
    FIFTEEN("15"),
    THIRTY("30"),
    FORTY("40"),
    ADVANTAGE("AD");

    private final String value;

    public Points next() {
        return switch (this) {
            case ZERO -> FIFTEEN;
            case FIFTEEN -> THIRTY;
            case THIRTY -> FORTY;
            case FORTY -> ADVANTAGE;
            case ADVANTAGE -> throw new IllegalStateException(
                    "Cannot advance beyond ADVANTAGE. Game should be finished.");
        };
    }

    public boolean canAdvance() {
        return this != ADVANTAGE;
    }
}
