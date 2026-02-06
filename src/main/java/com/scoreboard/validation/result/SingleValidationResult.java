package com.scoreboard.validation.result;

import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

public record SingleValidationResult(
        List<String> errors
) {

    public boolean hasErrors() {
        return ObjectUtils.isNotEmpty(errors);
    }
}
