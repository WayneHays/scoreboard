package com.scoreboard.mapper;

public interface Mapper<S, T> {
    T map(S source);
}
