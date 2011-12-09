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
        Aim direction = myAnt.orders.remove(0);
        getAnts().issueOrder(myAnt, direction);
		issuedOrders.add(getAnts().getTile(myAnt.tile, direction));
	}

	protected boolean isPassable(Ant myAnt, Aim direction) {
		return isPassableIlk(myAnt.tile, direction) && haveNoOrders(myAnt.tile, direction);
	}

	private boolean haveNoOrders(Tile myAnt, Aim direction) {
		Tile tile = getAnts().getTile(myAnt, direction);
		return !issuedOrders.contains(tile);
	}

	private boolean isPassableIlk(Tile myAnt, Aim direction) {
		return getAnts().getIlk(myAnt, direction).isPassable();
	}
}
