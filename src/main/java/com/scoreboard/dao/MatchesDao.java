package com.scoreboard.dao;

import com.scoreboard.entity.Match;
import com.scoreboard.exception.DaoException;
import com.scoreboard.infrastructure.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MatchesDao {
    private static final String DB_ID_COLUMN_NAME = "id";
    private static final String DB_PLAYER1_COLUMN_NAME = "player1";
    private static final String DB_PLAYER2_COLUMN_NAME = "player2";
    private static final String DB_WINNER_COLUMN_NAME = "winner";

    private static final String SAVE_SQL = "INSERT INTO Matches (player1, player2, winner) VALUES (?,?,?)";
    private static final String FAIL_MESSAGE = "Failed to save match";

    public Match save(Match match) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL)) {
            statement.setLong(1, match.getFirstPlayerId());
            statement.setLong(2, match.getSecondPlayerId());
            statement.setLong(3, match.getWinnerId());
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                throw new DaoException(FAIL_MESSAGE);
            }

            return new Match(
                    resultSet.getLong(DB_ID_COLUMN_NAME),
                    resultSet.getLong(DB_PLAYER1_COLUMN_NAME),
                    resultSet.getLong(DB_PLAYER2_COLUMN_NAME),
                    resultSet.getLong(DB_WINNER_COLUMN_NAME)
            );
        } catch (SQLException e) {
            throw new DaoException(FAIL_MESSAGE);
        }
    }
}
