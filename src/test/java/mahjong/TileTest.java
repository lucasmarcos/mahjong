package mahjong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

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

    @Test
    void compareEq() {
        var tile1 = new Tile(0);
        var tile2 = new Tile(0);
        var res = tile1.compareTo(tile2);
        assertEquals(0, res);
    }

    @Test
    void compareDiff1() {
        var tile1 = new Tile(0);
        var tile2 = new Tile(1);
        var res = tile1.compareTo(tile2);
        assertEquals(-1, res);
    }

    @Test
    void compareDiff2() {
        var tile1 = new Tile(1);
        var tile2 = new Tile(0);
        var res = tile1.compareTo(tile2);
        assertEquals(1, res);
    }

    @Test
    void same() {
        var tile = new Tile(0);
        var tileSame = tile.same();
        var res = tile.compareTo(tileSame);
        assertEquals(0, res);
    }

    @Test
    void getSize() {
        var tile = new Tile(0);
        var res = tile.getSize();
        assertEquals(1, res);
    }

    @Test
    void setSize() {
        var tile = new Tile(0);
        tile.setSize(1);
        var res = tile.getSize();
        assertEquals(1, res);
    }

    @Test
    void addSize() {
        var tile = new Tile(0);
        tile.addSize(1);
        var res = tile.getSize();
        assertEquals(2, res);
    }

    @Test
    void equals() {
    }

    @Test
    void sameInt() {
    }
}
