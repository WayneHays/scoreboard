package com.scoreboard.mapper;

import com.scoreboard.dto.NewMatchForm;
import com.scoreboard.validator.ValidationResult;

public class NewMatchFormMapper {

    public NewMatchForm map(String player1Input, String player2Input,
                            ValidationResult player1Result,
                            ValidationResult player2Result,
                            String generalError) {
        return NewMatchForm.builder()
                .player1Value(normalizeInput(player1Input))
                .player2Value(normalizeInput(player2Input))
                .player1Error(player1Result.errorMessage())
                .player2Error(player2Result.errorMessage())
                .generalError(generalError)
                .build();
    }

    private String normalizeInput(String input) {
        return input != null ? input : "";
    }
}
