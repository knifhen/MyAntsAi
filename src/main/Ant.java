import java.util.LinkedList;
import java.util.List;

public class Ant {
    public Tile tile;
    public Tile previousTile;

    private List<Tile> orders = new LinkedList<Tile>();

    public Ant(Tile tile) {
        this.tile = tile;
    }

	public boolean hasOrder() {
		return !orders.isEmpty();
	}

	public void giveOrder(List<Tile> path) {
		orders.addAll(path);
	}

	public void giveOrder(Tile tile) {
		orders.add(tile);
	}

	public Tile getFirstOrder() {
		return orders.get(0);
	}

	public void removeFirstOrder() {
		orders.remove(0);
	}

	public void clearOrders() {
		orders.clear();
	}
}
