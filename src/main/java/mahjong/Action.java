package mahjong;

import java.util.ArrayList;

/*
 * 0:摸 {0: toque}
 * 1:吃 {1: comer}
 * 2:碰 {2: toque}
 * 3:槓 {3: Kong}
 * 4:加槓 {4: Adicione a barra}
 * 5:暗槓 {5: Barra escondida}
 * 6:立直 {6: Recupere-se}
 * 7:榮 {7: Rong}
 * 8:胡 {8: Hu}
 */

/* A placa considerará a 0ª carta como a carta a ser destruída, e o restante pode ser passado para a GUI para uso ou qualquer outra coisa (? */

public record Action(int type, ArrayList<Tile> tiles) {}
