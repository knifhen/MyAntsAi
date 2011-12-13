import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Provides basic game state handling.
 */
public abstract class Bot extends AbstractSystemInputParser {
    private Ants ants;
	private Set<Tile> issuedOrders = new TreeSet<Tile>();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setup(int loadTime, int turnTime, int rows, int cols, int turns, int viewRadius2,
            int attackRadius2, int spawnRadius2) {
        setAnts(new Ants(loadTime, turnTime, rows, cols, turns, viewRadius2, attackRadius2,
            spawnRadius2));
    }
    
    /**
     * Returns game state information.
     * 
     * @return game state information
     */
    public Ants getAnts() {
        return ants;
    }
    
    /**
     * Sets game state information.
     * 
     * @param ants game state information to be set
     */
    protected void setAnts(Ants ants) {
        this.ants = ants;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeUpdate() {
        ants.setTurnStartTime(System.currentTimeMillis());
        ants.clearMyAnts();
        ants.clearEnemyAnts();
        ants.clearMyHills();
        ants.clearEnemyHills();
        ants.clearFood();
        ants.clearDeadAnts();
        ants.getOrders().clear();
        ants.clearVision();
        issuedOrders.clear();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addWater(int row, int col) {
        ants.update(Ilk.WATER, new Tile(row, col));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addAnt(int row, int col, int owner) {
        ants.update(owner > 0 ? Ilk.ENEMY_ANT : Ilk.MY_ANT, new Tile(row, col));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addFood(int row, int col) {
        ants.update(Ilk.FOOD, new Tile(row, col));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAnt(int row, int col, int owner) {
        ants.update(Ilk.DEAD, new Tile(row, col));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addHill(int row, int col, int owner) {
        ants.updateHills(owner, new Tile(row, col));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void afterUpdate() {
        ants.setVision();
    }

	protected Set<Ant> getMyAnts() {
		return getAnts().getMyAnts();
	}

	protected void issueOrder(Ant myAnt) {
        if(myAnt.orders.isEmpty()) {
            return;
        }
        Tile tile = myAnt.orders.remove(0);

        if(orderIsValid(tile)) {
            Aim direction = getAnts().getDirections(myAnt.tile, tile).get(0);
            getAnts().issueOrder(myAnt, direction);
            issuedOrders.add(tile);
        } else {
            myAnt.orders.clear();
        }
	}

    private boolean orderIsValid(Tile tile) {
        return isPassable(tile);
    }

    protected boolean isPassable(Tile tile) {
        return tile.isPassableIlk(getAnts()) && tile.willBeOccupiedNextTurn(issuedOrders);
	}

}
