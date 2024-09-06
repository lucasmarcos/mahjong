import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import mahjong.Hand;
import mahjong.Tile;

class HandTest {
    @Test
    void all() {
        var list = new ArrayList<ArrayList<Tile>>();

        list.add(new ArrayList<Tile>());
        list.add(new ArrayList<Tile>());
        list.add(new ArrayList<Tile>());
        list.add(new ArrayList<Tile>());

        var hand = new Hand(list);

        var all = hand.getAll();

        assertEquals(all, list);
    }

    @Test
    void string() {
        var list = new ArrayList<ArrayList<Tile>>();

        list.add(new ArrayList<Tile>());
        list.add(new ArrayList<Tile>());
        list.add(new ArrayList<Tile>());
        list.add(new ArrayList<Tile>());

        var hand = new Hand(list);

        var string = hand.toString();

        assertEquals("", string);
    }
}
