package com.scoreboard.dao;

import com.scoreboard.entity.Player;
import com.scoreboard.exception.DaoException;
import com.scoreboard.exception.EntityExistsException;
import com.scoreboard.infrastructure.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayersDao {
    private static final String DB_ID_COLUMN_NAME = "id";
    private static final String DB_NAME_COLUMN_NAME = "name";
    private static final int DB_DUPLICATE_ERROR_CODE = 23505;

    private static final String SAVE_SQL = "INSERT INTO Players (name) VALUES (?)";
    private static final String FAIL_MESSAGE = "Failed to save player with name %s";
    private static final String DUPLICATE_MESSAGE = "Player with name %s already exists";


    public Player save(Player player) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL)) {
            statement.setString(1, player.getName());
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                throw new DaoException(FAIL_MESSAGE.formatted(player.getName()));
            }

            return new Player(
                    resultSet.getLong(DB_ID_COLUMN_NAME),
                    resultSet.getString(DB_NAME_COLUMN_NAME));
        } catch (SQLException e) {
            if (isDuplicate(e.getErrorCode())) {
                throw new EntityExistsException(DUPLICATE_MESSAGE.formatted(player.getName()));
            }
            throw new DaoException(FAIL_MESSAGE.formatted(player.getName()));
        }
    }

    private boolean isDuplicate(int errorCode) {
        return errorCode == DB_DUPLICATE_ERROR_CODE;
    }
}
