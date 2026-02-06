package com.scoreboard.validation.result;

import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

public record PairValidationResult(
        List<String> commonErrors,
        List<String> firstNameErrors,
        List<String> secondNameErrors
) {

    public boolean hasErrors() {
        return isNotEmpty(commonErrors) || isNotEmpty(firstNameErrors) || isNotEmpty(secondNameErrors);
    }

    private boolean isNotEmpty(List<String> list) {
        return ObjectUtils.isNotEmpty(list);
    }
}
