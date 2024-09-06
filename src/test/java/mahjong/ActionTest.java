import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import mahjong.Action;

class ActionTest {
    @Test
    void actionsType() {
        var actions = new Action(0, new ArrayList<>());
        assertEquals(0, actions.type());
    }

    @Test
    void actionsTiles() {
        var actions = new Action(0, new ArrayList<>());
        assertEquals(new ArrayList<>(), actions.tiles());
    }
}
