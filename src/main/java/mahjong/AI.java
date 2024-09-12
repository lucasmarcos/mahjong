package mahjong;

import java.util.ArrayList;
import java.util.Collections;

enum Status {
    FREE, RICHI, WIN
}

public class AI extends Player {
    private static final int DRAW = 0;
    private static final int CHOW = 1;
    private static final int PONG = 2;
    private static final int RICHI = 6;
    private static final int RON = 7;
    private static final int HU = 8;

    private int exposed;
    private Status status;
    private Tile prevTile;
    private Action prevAct;

    public AI(String name) {
        super(name);
        reset();
    }

    private void reset() {
        exposed = 0;
        status = Status.FREE;
        prevTile = null;
        prevAct = null;
    }

    private boolean canChow(Tile tile) {
        if (hand.chowable(tile) == 0) return false;

        Hand tmp = new Hand(hand.getAll());
        tmp.add(tile);
        Collections.sort(tmp.getAll().get(tile.suit));
        removeShuns(tmp, tile.suit);

        return tmp.getAll().get(tile.suit).stream().noneMatch(t -> t.equals(tile));
    }

    private boolean canPong(Tile tile) {
        if (!hand.pongable(tile)) return false;

        Hand tmp = new Hand(hand.getAll());
        tmp.add(tile);
        Collections.sort(tmp.getAll().get(tile.suit));
        removeShuns(tmp, tile.suit);

        return tmp.getAll().get(tile.suit).stream()
                .filter(t -> t.equals(tile))
                .anyMatch(t -> t.getSize() >= 3);
    }

    private void removeShuns(Hand hand, int suit) {
        int size;
        do {
            size = hand.getAll().get(suit).size();
            for (int i = 0; i < size - 2; i++) {
                Tile a = hand.getAll().get(suit).get(i);
                Tile b = hand.getAll().get(suit).get(i + 1);
                Tile c = hand.getAll().get(suit).get(i + 2);
                if (a.index + 1 == b.index && b.index + 1 == c.index) {
                    hand.discard(a);
                    hand.discard(b);
                    hand.discard(c);
                    break;
                }
            }
        } while (hand.getAll().get(suit).size() < size);
    }

    private boolean canRichi(Tile tile) {
        return exposed == 0 && hand.tingable(tile) != null && !hand.tingable(tile).isEmpty();
    }

    private boolean canHu(Tile tile) {
        return hand.tingable(tile) == null;
    }

    private Tile decideDiscard(Hand _hand) {
        Hand tmp = new Hand(_hand.getAll());
        Tile res = getFirstTile(tmp);
        removeShuns(tmp, 0);
        removeTriplets(tmp);
        removePairs(tmp);
        return getFirstTile(tmp);
    }

    private Tile getFirstTile(Hand tmp) {
        return tmp.getAll().stream()
                .filter(list -> !list.isEmpty())
                .findFirst()
                .flatMap(list -> list.stream().findFirst())
                .orElse(null);
    }

    private void removeTriplets(Hand hand) {
        for (int suit = 0; suit <= 3; suit++) {
            Collections.sort(hand.getAll().get(suit));
            for (int i = 0; i < hand.getAll().get(suit).size(); ) {
                Tile a = hand.getAll().get(suit).get(i);
                if (a.getSize() >= 3) {
                    hand.discard(a);
                    hand.discard(a);
                    hand.discard(a);
                } else {
                    i++;
                }
            }
        }
    }

    private void removePairs(Hand hand) {
        for (int suit = 0; suit <= 3; suit++) {
            Collections.sort(hand.getAll().get(suit));
            for (int i = 0; i < hand.getAll().get(suit).size(); ) {
                Tile a = hand.getAll().get(suit).get(i);
                if (a.getSize() >= 2) {
                    hand.discard(a);
                    hand.discard(a);
                } else {
                    i++;
                }
            }
        }
    }

    private Action win(int actionType) {
        ArrayList<Tile> allTiles = new ArrayList<>();
        for (int i = 0; i <= 3; i++) {
            allTiles.addAll(hand.getAll().get(i));
        }
        status = Status.WIN;
        prevAct = new Action(actionType, allTiles);
        return prevAct;
    }

    public Action doSomething(int from, Tile tile) {
        if (from == 0) {
            if (canHu(tile)) {
                hand.add(tile);
                prevTile = tile.same();
                return win(HU);
            } else if (status == Status.RICHI) {
                prevTile = tile.same();
                return new Action(DRAW, new ArrayList<>(List.of(tile)));
            } else if (canRichi(tile)) {
                ArrayList<Tile> discardList = new ArrayList<>();
                Tile discardTile = hand.tingable(tile).get(0);
                hand.add(tile);
                prevTile = tile.same();
                discardList.add(discardTile);
                hand.discard(discardTile);
                status = Status.RICHI;
                prevAct = new Action(RICHI, discardList);
                return prevAct;
            } else {
                hand.add(tile);
                prevTile = tile.same();
                Tile discardTile = decideDiscard(hand);
                ArrayList<Tile> discardList = new ArrayList<>(List.of(discardTile));
                hand.discard(discardTile);
                prevAct = new Action(DRAW, discardList);
                return prevAct;
            }
        } else if (from == 3) {
            if (canHu(tile)) {
                hand.add(tile);
                prevTile = tile.same();
                return win(RON);
            } else if (status == Status.RICHI) {
                prevAct = null;
                return prevAct;
            } else if (canChow(tile)) {
                return performChow(tile);
            } else if (canPong(tile)) {
                return performPong(tile);
            } else {
                return null;
            }
        } else {
            if (canHu(tile)) {
                hand.add(tile);
                prevTile = tile.same();
                return win(RON);
            } else if (status == Status.RICHI) {
                prevAct = null;
                return prevAct;
            } else if (canPong(tile)) {
                return performPong(tile);
            } else {
                return null;
            }
        }
    }

    private Action performChow(Tile tile) {
        int flag = hand.chowable(tile);
        hand.add(tile);
        prevTile = tile.same();
        ArrayList<Tile> discardList = new ArrayList<>();
        Tile discardTile;

        if ((flag & 0b001) > 0) {
            discardTile = handleChow(tile.index - 2, tile.index - 1, tile.index);
        } else if ((flag & 0b010) > 0) {
            discardTile = handleChow(tile.index - 1, tile.index, tile.index + 1);
        } else {
            discardTile = handleChow(tile.index, tile.index + 1, tile.index + 2);
        }

        discardList.add(discardTile);
        prevAct = new Action(CHOW, discardList);
        return prevAct;
    }

    private Tile handleChow(int... indices) {
        for (int index : indices) {
            hand.discard(new Tile(index));
        }
        Tile discardTile = decideDiscard(hand);
        hand.discard(discardTile);
        return discardTile;
    }

    private Action performPong(Tile tile) {
        hand.add(tile);
        prevTile = tile.same();
        hand.discard(tile);
        hand.discard(tile);
        hand.discard(tile);
        exposed++;
        Tile discardTile = decideDiscard(hand);
        ArrayList<Tile> discardList = new ArrayList<>(List.of(discardTile, tile, tile, tile));
        hand.discard(discardTile);
        prevAct = new Action(PONG, discardList);
        return prevAct;
    }

    public void failed() {
        if (exposed > 0) exposed--;
        if (prevAct != null) {
            prevAct.tiles().forEach(hand::add);
            hand.discard(prevTile);
        }
    }

    public void gameOver(int type, int from) {
        reset();
    }
}
