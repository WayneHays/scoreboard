package scorecalculationservice_test;

import com.scoreboard.model.entity.Player;

import java.lang.reflect.Field;

public class TestPlayerFactory {
    public static Player createWithId(String name, Long id) throws Exception {
        Player player = new Player(name);
        Field idField = Player.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(player, id);
        return player;
    }
}
