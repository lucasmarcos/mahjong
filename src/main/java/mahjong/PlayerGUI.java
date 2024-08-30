package mahjong;

import java.awt.*;
import java.util.ArrayList;

public class PlayerGUI extends Player {
    public ArrayList<Tile> myHand = new ArrayList<Tile>();
    private ComGUI c;
    private ArrayList<Tile> discardTile;
    private ArrayList<Tile> pushTile;
    private boolean[] choice;
    private int action;

    private final MainGUI frame;

    public PlayerGUI(String name, int score, MainGUI frame) {
        super(name, score);
        myHand = new ArrayList<Tile>();
        this.frame = frame;
    }

    public void setCom(ComGUI c) {
        this.c = c;
    }

    public void failed() {
        frame.actionFail();
        for (int i = discardTile.size() - 1; i > 0; i--)
            hand.add(discardTile.get(i));
        c.renewGUI();
    }

    @Override
    public void initHand(ArrayList<ArrayList<Tile>> allTiles) {
        frame.setFlip(-1, new ArrayList<Tile>());
        //myHand = new ArrayList<Tile>();
        hand = new Hand(allTiles);
        getHand();
    }

    public Action doSomething(int from, Tile tile) {
        frame.resetChoice();
        action = -1;

        //boolean[] b;
        //private Hand hand;
        Tile newTile = tile.same();
        doSelect(from, newTile);

        //frame.changeEnable(false);
        if (action == -1)
            return null;
        return new Action(action, discardTile);
    }

    private void doSelect(int from, Tile newTile) {
        //boolean[] b = {true, true, true, true, true};
        boolean[] b = {false, false, false, false, false}; /*可做 吃, 碰, 槓, 聽, 胡*/ //{Pode comer, tocar, tocar, ouvir, bagunçar}

        int tempType = hand.chowable(newTile);
        ArrayList<Tile> temp = hand.tingable(newTile);

        if (tempType != 0 && from == 3) {
            b[0] = true;
            frame.setChowOption(tempType, getChewChoice(tempType, newTile));
        }

        if (from != 0)
            b[1] = hand.pongable(newTile);

        b[2] = hand.kongable(newTile);

        if (temp == null)
            b[4] = true;
        else if (temp.size() != 0 && from == 0) {
            //b[3] = true;
        }

        if (from == 0) {
            //myHand.add(tile);
            hand.add(newTile);
        }
        frame.setThrower(from, newTile);
			/*System.out.println("AAA " + from +" "+ newTile);
			for(int i = 0 ; i < 5; i++)
				if(b[i])
					System.out.println("Choice " + i);*/
        //b[0] = true;
        //tempType = 7;
        //frame.setChowOption(tempType, getChewChoice(tempType, newTile));
        if (b[0] || b[1] || b[2] || b[3] || b[4]) {
            frame.setSelect(b);
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        frame.frameOpen();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            waitOK();
        }

        choice = frame.getChoice();
        selectProcess(tempType, newTile, from);
    }

    private void waitOK() {
        while (frame.ok == false) {
        }
        pushTile = new ArrayList<Tile>();
        for (Tile t : frame.push)
            pushTile.add(t.same());

			/*for(int i = 0; i < 6; i++)
				if(choice[i])
					System.out.println(i);*/
			/*for(Tile t : frame.push)
				System.out.println(t);*/
        frame.ok = false;
        frame.push = new ArrayList<Tile>();
        //frame.changeEnable(false);
    }
		/*private boolean remove(Tile t)
		{
			boolean b = false;
			for(Tile temp : myHand)
				if(temp.equals(t)){
					myHand.remove(temp);
					b = true;
				}
			return b;
		}*/

    private void selectProcess(int chewType, Tile newTile, int from) {
        discardTile = new ArrayList<Tile>();
        discardTile.add(new Tile(0));
        if (choice[0]) {
            action = 1;
            ArrayList<ArrayList<Tile>> chowOption = getChewChoice(chewType, newTile);
            if (chowOption.size() == 1) {
                for (int i = 0; i < 3; i++)
                    discardTile.add(chowOption.get(0).get(i));
            } else {
                for (int i = 0; i < 3; i++)
                    discardTile.add(pushTile.get(i).same());
            }
        } else if (choice[1]) {
            action = 2;
            for (int i = 0; i < 3; i++)
                discardTile.add(newTile.same());
        } else if (choice[2]) {
            action = 3;
            if (from == 0)
                action = 5;
            for (int i = 0; i < 4; i++)
                discardTile.add(newTile.same());
        } else if (choice[3]) {
            action = 6;
            discardTile.set(0, pushTile.get(0));
            return;
        } else if (choice[4]) {

            if (from == 0)
                action = 8;
            else {
                action = 7;
                hand.add(newTile);
            }
            getHand();
            //discardTile.set(0, myHand.get(0));
            discardTile.remove(0);
            for (int i = 0; i < myHand.size(); i++)
                discardTile.add(myHand.get(i));

				/*for(int i = 0; i < discardTile.size(); i++)
					hand.discard(discardTile.get(i));*/
            return;
        } else {
            if (from == 0)
                action = 0;
            else {
                action = -1;
                return;
            }
        }

        for (int i = 1; i < discardTile.size() - 1; i++)
            hand.discard(discardTile.get(i));
        c.renewGUI();

        if (choice[2]) {
            discardTile.remove(0);
        } else {
            frame.showThrowTile(true);
            //frame.changeEnable(true);
            waitOK();
            discardTile.set(0, pushTile.get(0));
            hand.discard(discardTile.get(0));
        }

        for (Tile t : discardTile) {
            System.out.println(t);
        }

        frame.resetChoice();
        c.renewGUI();
    }

    private ArrayList<ArrayList<Tile>> getChewChoice(int flag, Tile newTile) {
        ArrayList<ArrayList<Tile>> temp = new ArrayList<ArrayList<Tile>>();

        if ((flag & 0b001) > 0) {
            ArrayList<Tile> temp1 = new ArrayList<Tile>();
            temp1.add(newTile.same(-2));
            temp1.add(newTile.same(-1));
            temp1.add(newTile.same());
            temp.add(temp1);
        }

        if ((flag & 0b010) > 0) {
            ArrayList<Tile> temp1 = new ArrayList<Tile>();
            temp1.add(newTile.same(-1));
            temp1.add(newTile.same(1));
            temp1.add(newTile.same());
            temp.add(temp1);
        }

        if ((flag & 0b100) > 0) {
            ArrayList<Tile> temp1 = new ArrayList<Tile>();
            temp1.add(newTile.same(1));
            temp1.add(newTile.same(2));
            temp1.add(newTile.same());
            temp.add(temp1);
        }

        return temp;
    }

    public void getHand() {
        int length = myHand.size();

        for (int i = 0; i < length; i++) {
            myHand.remove(0);
        }

        for (ArrayList<Tile> temp : hand.getAll()) {
            for (Tile t : temp) {
                for (int i = 0; i < t.getSize(); i++) {
                    myHand.add(new Tile(t.index));
                }
            }
        }
    }

    public void GameOver(int type, int from) {
			/*if(action == 7 || action == 8){
				frame.hu(type, from);
			}
			else
				frame.hu(false);*/
        frame.hu(type, from);
    }

		/*public boolean doDraw(Tile tile, ArrayList<ArrayList<Tile>> currentTable)
		{
			newTile = tile.same();
			doSelect(newTile);
			return true;
		}*/
		/*public boolean doChow(Tile tile, ArrayList<ArrayList<Tile>> currentTable){return choice[0];}
		public boolean doPong(Tile tile, ArrayList<ArrayList<Tile>> currentTable){return choice[1];}
		public boolean doKong(Tile tile, ArrayList<ArrayList<Tile>> currentTable){return choice[2];}
		public boolean doReach(Tile tile, ArrayList<ArrayList<Tile>> currentTable){return choice[3];}
		public boolean doHu(Tile tile, ArrayList<ArrayList<Tile>> currentTable){return choice[4];}*/

    //public Tile replace(Tile tile, ArrayList<ArrayList<Tile>> currentTable){return new Tile(0);}
    //public Tile kong(ArrayList<ArrayList<Tile>> currentTable){return new Tile(0);}
}
