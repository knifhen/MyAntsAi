import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

public class Ant {
    public Tile tile;
    public List<Tile> orders = new LinkedList<Tile>();
	public Tile previousTile;

    public Ant(Tile tile) {
        this.tile = tile;
    }
}
