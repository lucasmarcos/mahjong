package mahjong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class AITest {
    @Test
    void ai() {
        var ai = new AI("ai0", 0);
        assertEquals(0, ai.getScore());
    }
}
