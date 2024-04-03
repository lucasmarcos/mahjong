import java.util.*;

public class Action{
	public int type;
	/*
	 * 0:摸
	 * 1:吃
	 * 2:碰
	 * 3:槓
	 * 4:加槓
	 * 5:暗槓
	 * 6:立直
	 * 7:榮
	 * 8:胡
	 */
	public ArrayList<Tile> tiles;	//board會把第0張當作是要打掉的牌，其他看要放吃碰槓的傳給GUI用還是幹嘛都可以(?
	public Action(int i, ArrayList<Tile> t){
		type = i;
		tiles = t;
	}

}
