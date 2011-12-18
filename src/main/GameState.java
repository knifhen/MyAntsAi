import java.util.*;

/**
 * Holds all game data and current game state.
 */
public class GameState {
    /** Maximum map size. */
    static final int MAX_MAP_SIZE = 256 * 2;
    final int loadTime;
    final int turnTime;
    final int rows;
    final int cols;
    final int turns;
    final int viewRadius2;
    final int attackRadius2;
    final int spawnRadius2;
    final boolean visible[][];
    final Set<Tile> visionOffsets;
    long turnStartTime;
    final Ilk map[][];
    
    final Set<Ant> myAnts = new HashSet<Ant>();
    final Set<Ant> antsWithOrders = new HashSet<Ant>();
    final List<Ant> antsWithoutOrders = new LinkedList<Ant>();
    
    final Set<Tile> enemyAnts = new HashSet<Tile>();
    final Set<Tile> myHills = new HashSet<Tile>();
    final Set<Tile> enemyHills = new HashSet<Tile>();
    final Set<Tile> foodTiles = new HashSet<Tile>();
    
    final Set<Order> orders = new HashSet<Order>();
    final Set<Tile> issuedOrders = new TreeSet<Tile>();

    private HashMap<Tile, Ant> tilesToMyAnts = new HashMap<Tile, Ant>();

    /**
     * Creates new {@link GameState} object.
     * 
     * @param loadTime timeout for initializing and setting up the bot on turn 0
     * @param turnTime timeout for a single game turn, starting with turn 1
     * @param rows game map height
     * @param cols game map width
     * @param turns maximum number of turns the game will be played
     * @param viewRadius2 squared view radius of each ant
     * @param attackRadius2 squared attack radius of each ant
     * @param spawnRadius2 squared spawn radius of each ant
     */
    public GameState(int loadTime, int turnTime, int rows, int cols, int turns, int viewRadius2,
            int attackRadius2, int spawnRadius2) {
        this.loadTime = loadTime;
        this.turnTime = turnTime;
        this.rows = rows;
        this.cols = cols;
        this.turns = turns;
        this.viewRadius2 = viewRadius2;
        this.attackRadius2 = attackRadius2;
        this.spawnRadius2 = spawnRadius2;
        map = new Ilk[rows][cols];
        for (Ilk[] row : map) {
            Arrays.fill(row, Ilk.LAND);
        }
        visible = new boolean[rows][cols];
        for (boolean[] row : visible) {
            Arrays.fill(row, false);
        }
        // calc vision offsets
        visionOffsets = new HashSet<Tile>();
        int mx = (int)Math.sqrt(viewRadius2);
        for (int row = -mx; row <= mx; ++row) {
            for (int col = -mx; col <= mx; ++col) {
                int d = row * row + col * col;
                if (d <= viewRadius2) {
                    visionOffsets.add(new Tile(row, col));
                }
            }
        }
    }

    /**
     * Returns how much time the bot has still has to take its turn before timing out.
     * 
     * @return how much time the bot has still has to take its turn before timing out
     */
    public int getTimeRemaining() {
        return turnTime - (int)(System.currentTimeMillis() - turnStartTime);
    }

    /**
     * Returns ilk at the specified location.
     * 
     * @param tile location on the game map
     * 
     * @return ilk at the <cod>tile</code>
     */
    public Ilk getIlk(Tile tile) {
        return map[tile.row][tile.col];
    }

    /**
     * Sets ilk at the specified location.
     * 
     * @param tile location on the game map
     * @param ilk ilk to be set at <code>tile</code>
     */
    public void setIlk(Tile tile, Ilk ilk) {
        map[tile.row][tile.col] = ilk;
    }

    /**
     * Returns ilk at the location in the specified direction from the specified location.
     * 
     * @param tile location on the game map
     * @param direction direction to look up
     * 
     * @return ilk at the location in <code>direction</code> from <cod>tile</code>
     */
    public Ilk getIlk(Tile tile, Aim direction) {
        Tile newTile = getTile(tile, direction);
        return map[newTile.row][newTile.col];
    }

    /**
     * Returns location in the specified direction from the specified location.
     * 
     * @param tile location on the game map
     * @param direction direction to look up
     * 
     * @return location in <code>direction</code> from <cod>tile</code>
     */
    public Tile getTile(Tile tile, Aim direction) {
        int row = (tile.row + direction.getRowDelta()) % rows;
        if (row < 0) {
            row += rows;
        }
        int col = (tile.col + direction.getColDelta()) % cols;
        if (col < 0) {
            col += cols;
        }
        return new Tile(row, col);
    }

    /**
     * Returns location with the specified offset from the specified location.
     * 
     * @param ant location on the game map
     * @param offset offset to look up
     * 
     * @return location with <code>offset</code> from <cod>tile</code>
     */
    public Tile getTile(Ant ant, Tile offset) {
        int row = (ant.tile.row + offset.row) % rows;
        if (row < 0) {
            row += rows;
        }
        int col = (ant.tile.col + offset.col) % cols;
        if (col < 0) {
            col += cols;
        }
        return new Tile(row, col);
    }

    /**
     * Calculates distance between two locations on the game map.
     * 
     * @param t1 one location on the game map
     * @param t2 another location on the game map
     * 
     * @return distance between <code>t1</code> and <code>t2</code>
     */
    public int getDistance(Tile t1, Tile t2) {
        int rowDelta = Math.abs(t1.row - t2.row);
        int colDelta = Math.abs(t1.col - t2.col);
        rowDelta = Math.min(rowDelta, rows - rowDelta);
        colDelta = Math.min(colDelta, cols - colDelta);
        return rowDelta * rowDelta + colDelta * colDelta;
    }

    /**
     * Returns one or two orthogonal directions from one location to the another.
     * 
     * @param t1 one location on the game map
     * @param t2 another location on the game map
     * 
     * @return orthogonal directions from <code>t1</code> to <code>t2</code>
     */
    public List<Aim> getDirections(Tile t1, Tile t2) {
        List<Aim> directions = new ArrayList<Aim>();
        if (t1.row < t2.row) {
            if (t2.row - t1.row >= rows / 2) {
                directions.add(Aim.NORTH);
            } else {
                directions.add(Aim.SOUTH);
            }
        } else {
            if (t1.row > t2.row) {
                if (t1.row - t2.row >= rows / 2) {
                    directions.add(Aim.SOUTH);
                } else {
                    directions.add(Aim.NORTH);
                }
            }
        }
        if (t1.col < t2.col) {
            if (t2.col - t1.col >= cols / 2) {
                directions.add(Aim.WEST);
            } else {
                directions.add(Aim.EAST);
            }
        } else {
            if (t1.col > t2.col) {
                if (t1.col - t2.col >= cols / 2) {
                    directions.add(Aim.EAST);
                } else {
                    directions.add(Aim.WEST);
                }
            }
        }
        return directions;
    }

    /**
     * Clears game state information about my ants locations.
     */
    public void clearMyAnts() {
        for (Ant ant : myAnts) {
        	Tile tile = ant.previousTile != null ? ant.previousTile : ant.tile;
            map[tile.row][tile.col] = Ilk.LAND;
            ant.previousTile = ant.tile;
        }
    }

    /**
     * Clears game state information about enemy ants locations.
     */
    public void clearEnemyAnts() {
        for (Tile enemyAnt : enemyAnts) {
            map[enemyAnt.row][enemyAnt.col] = Ilk.LAND;
        }
        enemyAnts.clear();
    }

    /**
     * Clears game state information about food locations.
     */
    public void clearFood() {
        for (Tile food : foodTiles) {
            map[food.row][food.col] = Ilk.LAND;
        }
        foodTiles.clear();
    }

    /**
     * Clears game state information about my hills locations.
     */
    public void clearMyHills() {
        myHills.clear();
    }

    /**
     * Clears game state information about enemy hills locations.
     */
    public void clearEnemyHills() {
        enemyHills.clear();
    }

    /**
     * Clears game state information about dead ants locations.
     */
    public void clearDeadAnts() {
        //currently we do not have list of dead ants, so iterate over all map
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (map[row][col] == Ilk.DEAD) {
                    map[row][col] = Ilk.LAND;
                }
            }
        }
    }

    /**
     * Clears visible information
     */
    public void clearVision() {
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                visible[row][col] = false;
            }
        }
    }

    /**
     * Calculates visible information
     */
    public void setVision() {
        for (Ant antLoc : myAnts) {
            for (Tile locOffset : visionOffsets) {
                Tile newLoc = getTile(antLoc, locOffset);
                visible[newLoc.row][newLoc.col] = true;
            }
        }
    }

    /**
     * Updates game state information about new ants and food locations.
     * 
     * @param ilk ilk to be updated
     * @param tile location on the game map to be updated
     */
    public void update(Ilk ilk, Tile tile) {
        map[tile.row][tile.col] = ilk;
        switch (ilk) {
            case FOOD:
                foodTiles.add(tile);
            break;
            case MY_ANT:
                updateAnt(tile);
            break;
            case ENEMY_ANT:
                enemyAnts.add(tile);
            break;
            case DEAD:
                removeAnt(tile);
            break;
        }
    }

    private void removeAnt(Tile tile) {
        if(tilesToMyAnts.containsKey(tile)) {
            Ant ant = tilesToMyAnts.get(tile);
            myAnts.remove(ant);
            tilesToMyAnts.remove(tile);
        }
    }

    private void updateAnt(Tile tile) {
        Ant ant = new Ant(tile);

        if(isNewAnt(ant)) {
            myAnts.add(ant);
            antsWithoutOrders.add(ant);
            tilesToMyAnts.put(tile, ant);
        }
    }

    private boolean isNewAnt(Ant ant) {
        return !tilesToMyAnts.containsKey(ant.tile);
    }

    /**
     * Updates game state information about hills locations.
     *
     * @param owner owner of hill
     * @param tile location on the game map to be updated
     */
    public void updateHills(int owner, Tile tile) {
        if (owner > 0)
            enemyHills.add(tile);
        else
            myHills.add(tile);
    }

    public void issueOrder(Ant ant, Aim direction) {
    	Tile tile = getTile(ant.tile, direction);
    	
    	publishOrder(ant, direction);
        saveIssuedOrder(tile);
        updateAntLocation(ant, tile);
    }

	private void publishOrder(Ant myAnt, Aim direction) {
		Order order = new Order(myAnt.tile, direction);
        orders.add(order);
        System.out.println(order);
	}

	private void saveIssuedOrder(Tile tile) {
		issuedOrders.add(tile);
	}

	private void updateAntLocation(Ant myAnt, Tile tile) {
		tilesToMyAnts.remove(myAnt.tile);
        myAnt.previousTile = myAnt.tile;
        myAnt.tile = tile;
        tilesToMyAnts.put(myAnt.tile, myAnt);
	}

	public void clearOrders() {
		orders.clear();
        issuedOrders.clear();
	}

	public void antHasOrder(Ant ant) {
		if(antsWithOrders.contains(ant)) {
			return;
		}
		antsWithOrders.add(ant);
    	antsWithoutOrders.remove(ant);
	}
}
