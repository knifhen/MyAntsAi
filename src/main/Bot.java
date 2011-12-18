import java.util.Set;

/**
 * Provides basic game state handling.
 */
public abstract class Bot extends AbstractSystemInputParser {
    private GameState gameState;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setup(int loadTime, int turnTime, int rows, int cols, int turns, int viewRadius2,
            int attackRadius2, int spawnRadius2) {
        setGameState(new GameState(loadTime, turnTime, rows, cols, turns, viewRadius2, attackRadius2,
            spawnRadius2));
    }
    
    public GameState getGameState() {
        return gameState;
    }
    
    protected void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
    

    @Override
    public void beforeUpdate() {
        gameState.turnStartTime = System.currentTimeMillis();
        gameState.clearMyAnts();
        gameState.clearEnemyAnts();
        gameState.clearMyHills();
        gameState.clearEnemyHills();
        gameState.clearFood();
        gameState.clearDeadAnts();
        gameState.clearOrders();
        gameState.clearVision();
    }
    
    @Override
    public void addWater(int row, int col) {
        gameState.update(Ilk.WATER, new Tile(row, col));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addAnt(int row, int col, int owner) {
        gameState.update(owner > 0 ? Ilk.ENEMY_ANT : Ilk.MY_ANT, new Tile(row, col));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addFood(int row, int col) {
        gameState.update(Ilk.FOOD, new Tile(row, col));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAnt(int row, int col, int owner) {
        gameState.update(Ilk.DEAD, new Tile(row, col));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addHill(int row, int col, int owner) {
        gameState.updateHills(owner, new Tile(row, col));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void afterUpdate() {
        gameState.setVision();
    }

	protected Set<Ant> getMyAnts() {
		return getGameState().myAnts;
	}

	protected void issueOrder(Ant ant) {
        if(!ant.hasOrder()) {
            return;
        }
        Tile tile = ant.orders.get(0);

        if(orderIsValid(tile)) {
            Aim direction = getGameState().getDirections(ant.tile, tile).get(0);
            getGameState().issueOrder(ant, direction);
            ant.orders.remove(0);
        } else  {
        	ant.orders.clear();
        }
        if(!ant.hasOrder() && gameState.antsWithOrders.contains(ant)) {
        	gameState.antsWithoutOrders.add(ant);
        	gameState.antsWithOrders.remove(ant);
        }
	}

    private boolean orderIsValid(Tile tile) {
        return isPassable(tile) && !tile.willBeOccupiedNextTurn(gameState) && tile.isUnoccupied(gameState);
    }

    protected boolean isPassable(Tile tile) {
        return tile.isPassableIlk(gameState);
	}

}
