package mahjong;

import java.util.ArrayList;

public class Board {
    public static final int initScore = 25000;
    public static final int games = 1;
    public static int wind = 0;
    public static int game = 0;
    public static String[] actionString = {
        "",
        "comer",
        "ressalto",
        "barra",
        "Adicionar uma barra",
        "barra escondida",
        "fique em linha reta",
        "glória",
        "Hu",
    };
    public static int dealer = 0;
    private static Shuffler shuffler;
    private static ComGUI GUI;
    private static Player[] players = new Player[4];
    private static ArrayList<ArrayList<Tile>> allTiles = new ArrayList<>();
    private static ArrayList<ArrayList<Tile>> table = new ArrayList<>();
    private static int[] left = { 0, 0, 0, 0 };

    ///
    public static void main(String[] args) {
        initGame(); // Inicializa o jogo (configurações, variáveis, etc.)

        while (true) { // Loop principal do jogo
            startNewRound(); // Inicia uma nova rodada do jogo

            if (playRound() || endGameConditionMet()) break;
            // Chama o método 'playRound' para jogar a rodada. Se 'playRound' retornar 'true' (ou seja, o jogo terminou)
            // ou se a condição de fim de jogo ('endGameConditionMet') for satisfeita, o loop termina e o jogo acaba.
        }
    }

    private static boolean playRound() {
        int gameOver = 0; // Variável para checar se o jogo acabou
        int current = (dealer + game) % 4; // Determina qual jogador é o atual baseado no dealer e no estado do jogo
        Tile tile = shuffler.getNext(); // Pega a próxima peça do embaralhador (shuffler)
        Action action = players[current].doSomething(0, tile); // O jogador faz uma ação baseada na peça que recebeu

        while (gameOver == 0) { // Loop até que a variável 'gameOver' seja alterada
            debugRound(current, action); // Chama o método 'debugRound' para imprimir as informações de depuração
            gameOver = handlePlayerAction(current, action); // Processa a ação do jogador atual e verifica se o jogo acabou

            if (gameOver == 1) return true; // Se o jogo acabou (gameOver == 1), a rodada termina e o método retorna 'true'

            tile = shuffler.getNext(); // Pega a próxima peça
            if (tile == null) return handleDrawSituation() == 1; // Se não houver mais peças (tile == null), trata a situação de empate e retorna 'true' se o jogo acabou

            action = players[current].doSomething(0, tile); // O jogador faz outra ação com a nova peça
        }
        return false; // Se o loop terminou sem game over, a rodada não resultou no fim do jogo
    }

    private static void debugRound(int current, Action action) {
        System.out.println(
            "DEBUG: wind: " +
            wind +
            " game: " +
            game +
            players[current] +
            actionString[action.type()] +
            "."
        );
    }

    ///

    private static void initGame() {
        shuffler = new Shuffler();
        GUI = new ComGUI();
        GUI.initPlayerGUI("PlayerGUI", GUI);
        players[0] = GUI.player;

        for (int i = 1; i < 4; i++) {
            players[i] = new AI("PlayerAI" + i);
        }

        for (int i = 0; i < 4; i++) {
            allTiles.add(new ArrayList<>());
            table.add(new ArrayList<>());
        }
        table.add(new ArrayList<>()); // Adiciona o fundo do rio
    }

    private static void startNewRound() {
        table.get(0).clear(); // Limpa o fundo do rio

        for (int i = 0; i < 4; i++) {
            table.get(i + 1).clear(); // Limpa os efeitos colaterais
            left[i] = 13; // 13 cartas na mão
            if (i > 0) GUI.assignHandNum(i + 1, left[i]);

            allTiles.forEach(ArrayList::clear); // Limpa todas as tiles
            for (int j = 0; j < 13; j++) {
                Tile tmpTile = shuffler.getNext();
                allTiles.get(tmpTile.suit).add(tmpTile);
            }

            players[i].initHand(allTiles);
        }

        GUI.showWind(wind, game + 1);
        GUI.renewGUI();
        GUI.showGUI();
    }

    private static int handlePlayerAction(int current, Action action) {
        switch (action.type()) {
            case 0:
            case 1:
            case 2:
            case 6: // Ações: tocar, comer, ressalto, linha reta
                return handleStandardAction(current, action);
            case 3:
            case 4:
            case 5: // Ações: barra, adicionar barra, barra escondida
                return handleKongAction(current, action);
            case 7:
            case 8: // Ações: glória, toque-se
                return handleGameOverAction(current, action);
            default:
                System.out.println(
                    "ERROR: " +
                    players[current] +
                    " unknown action " +
                    action.type() +
                    "."
                );
                System.exit(1);
                return 0;
        }
    }

    private static int handleStandardAction(int current, Action action) {
        if (current > 0) {
            left[current] -= (action.tiles().size() - 1);
            GUI.assignHandNum(current + 1, left[current]);
        }

        for (int i = 1; i < action.tiles().size(); i++) {
            table.get(current + 1).add(action.tiles().get(i));
        }

        GUI.assignTile(table);
        GUI.renewGUI();
        Tile tile = action.tiles().get(0);
        int selectPlayer = findNextActionPlayer(current, tile);

        if (selectPlayer != -1) {
            return 0; // Continues to next action
        } else {
            table.get(0).add(tile);
            GUI.assignTile(table);
            GUI.renewGUI();
            return 0;
        }
    }

    private static int handleKongAction(int current, Action action) {
        if (current > 0) {
            left[current] -= action.tiles().size();
            GUI.assignHandNum(current + 1, left[current]);
        }

        for (Tile tile : action.tiles()) {
            table.get(current + 1).add(tile);
        }

        GUI.assignTile(table);
        GUI.renewGUI();
        shuffler.ackKong();
        return 0;
    }

    private static int handleGameOverAction(int current, Action action) {
        printTiles(action.tiles());

        if (current != (dealer + game) % 4) {
            game++;
        }

        shuffler.permuteIndex();
        if (current > 0) {
            GUI.flipTile(current - 1, action.tiles());
        }

        for (int i = 0; i < 4; i++) {
            players[i].GameOver(
                    action.type() == 7 ? 1 : 2,
                    (current - i + 4) % 4
                );
        }

        return 1;
    }

    private static int handleDrawSituation() {
        for (int i = 0; i < 4; i++) {
            players[i].GameOver(0, i); // Notifica sobre a situação de empate
        }
        return 1;
    }

    private static boolean endGameConditionMet() {
        if (game == 4) {
            wind++;
            game = 0;
        }

        if (wind == games) {
            if (!GUI.showWind(wind, -1)) {
                return true;
            }
            game = 0;
            wind = 0;
        }

        return false;
    }

    private static int findNextActionPlayer(int current, Tile tile) {
        Action selectAction = null;
        int selectPlayer = -1;

        for (int i = 1; i < 4; i++) {
            int p = (current + i) % 4;
            System.out.println("wait " + p + " " + tile + " " + tile.getSize());
            Action action = players[p].doSomething(4 - i, tile);

            if (action == null) continue;

            System.out.println(p + " " + actionString[action.type()]);
            if (selectPlayer == -1 || action.type() > selectAction.type()) {
                if (selectPlayer != -1) players[selectPlayer].failed();
                selectAction = action;
                selectPlayer = p;
            } else {
                players[p].failed();
            }
        }

        if (selectAction != null) {
            current = selectPlayer;
        }

        return selectPlayer;
    }

    public static void printTiles(ArrayList<Tile> tiles) {
        for (Tile t : tiles) {
            System.out.print(t.toString() + t.getSize() + ",");
        }
    }
}
