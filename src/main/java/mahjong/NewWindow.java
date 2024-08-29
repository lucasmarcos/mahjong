package mahjong;

import javax.swing.*;

public class NewWindow extends JFrame {
    public NewWindow() {
        this.setTitle("Mahjong");

        addMenu();

        this.setSize(640 * 2, 360 * 2);
        this.setVisible(true);
    }

    private void addMenu() {
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        JMenu menuJogo = new JMenu("Jogo");
        menuBar.add(menuJogo);

        JMenuItem menuItemTutorial = new JMenuItem("Tutorial");
        menuJogo.add(menuItemTutorial);

        JMenuItem menuItemSair = new JMenuItem("Sair");
        menuJogo.add(menuItemSair);
    }
}
