package com.scoreboard.domain.model.state;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
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

    public boolean isLessThanForty() {
        return this.ordinal() < FORTY.ordinal();
    }

    public boolean isForty() {
        return this == FORTY;
    }

    public boolean isAdvantageState() {
        return this == ADVANTAGE;
    }
}
