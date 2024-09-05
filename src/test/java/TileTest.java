import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import mahjong.Tile;

class TileTest {
    @Test
    void string0() {
        var tile0 = new Tile(0);
        assertEquals("一萬", tile0.toString());
    }

    @Test
    void string1() {
        var tile1 = new Tile(1);
        assertEquals("二萬", tile1.toString());
    }
}
