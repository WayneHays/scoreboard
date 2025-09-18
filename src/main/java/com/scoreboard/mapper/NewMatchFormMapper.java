package com.scoreboard.mapper;

import com.scoreboard.dto.NewMatchForm;
import com.scoreboard.validator.ValidationResult;

public class NewMatchFormMapper {

    public NewMatchForm map(String player1Input, String player2Input,
                            ValidationResult player1Result,
                            ValidationResult player2Result,
                            String generalError) {
        return new NewMatchForm(
                checkInput(player1Input),
                checkInput(player2Input),
                player1Result.errorMessage(),
                player2Result.errorMessage(),
                generalError
        );
    }

    private String checkInput(String input) {
        return input != null ? input : "";
    }
}
