import java.util.LinkedList;
import java.util.List;

public class Ant {
    public Tile tile;
    public Tile previousTile;

    public List<Tile> orders = new LinkedList<Tile>();

    public Ant(Tile tile) {
        this.tile = tile;
    }

	public boolean hasOrder() {
		return !orders.isEmpty();
	}

	public void giveOrder(List<Tile> path) {
		orders.addAll(path);
	}
}
