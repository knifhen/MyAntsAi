import java.util.Set;

/**
 * Represents a tile of the game map.
 */
public class Tile implements Comparable<Tile> {
    public final int row;
    public final int col;

    /**
     * Creates new {@link Tile} object.
     * 
     * @param row row index
     * @param col column index
     */
    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Tile o) {
        return hashCode() - o.hashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return row * Ants.MAX_MAP_SIZE + col;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof Tile) {
            Tile tile = (Tile)o;
            result = row == tile.row && col == tile.col;
        }
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return row + " " + col;
    }

    public boolean isPassableIlk(Ants ants) {
        return ants.getIlk(this).isPassable();
    }

    public boolean willBeOccupiedNextTurn(Set<Tile> issuedOrders) {
        return !issuedOrders.contains(this);
    }

    public boolean isUnoccupied(Ants ants) {
        return ants.getIlk(this).isUnoccupied();
    }
}
