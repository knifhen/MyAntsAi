import java.io.IOException;
import java.util.*;

/**
 * Starter bot implementation.
 */
public class MyBot extends Bot {
    private static final int NUMBER_OF_TASKS = 2;

	/**
     * Main method executed by the game engine for starting the bot.
     * 
     * @param args command line arguments
     * 
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        new MyBot().readSystemInput();
    }



	private List<Aim> directions = Arrays.asList(Aim.NORTH, Aim.EAST, Aim.SOUTH, Aim.WEST);
    private AStar astar;
    
    /**
     * For every ant check every direction in fixed order (N, E, S, W) and move it if the tile is
     * passable.
     */
    @Override
    public void doTurn() {
        GameState gameState = getGameState();
		astar = new AStar(gameState);
//        Iterator<Ant> ants = gameState.myAnts.iterator();
        
        int size = gameState.antsWithoutOrders.size();
		System.err.println("Ants without orders: " + size);
		int nAntsPerTask = size / NUMBER_OF_TASKS + size % NUMBER_OF_TASKS;
		List<Ant> foodAnts  = new LinkedList<Ant>(gameState.antsWithoutOrders.subList(0, nAntsPerTask));
		List<Ant> exploreAnts  = new LinkedList<Ant>(gameState.antsWithoutOrders.subList(nAntsPerTask, Math.min(nAntsPerTask * 2, size)));
		
        
        findFood(gameState, foodAnts);
        explore(gameState, exploreAnts);
        issueOrders(getMyAnts());
    }

    private void issueOrders(Set<Ant> myAnts) {
        for(Ant ant : myAnts) {
            issueOrder(ant);
        }
    }

    private void findFood(GameState gameState, List<Ant> ants) {
    	Set<Tile> foodTiles = getGameState().foodTiles;
    	if(foodTiles.isEmpty() || ants.isEmpty()) {
    		return;
    	}
    	    	
    	Iterator<Tile> iterator = foodTiles.iterator();
    	for (Ant ant : ants) {
    		if(!iterator.hasNext()) {
    			iterator = foodTiles.iterator();
    		}
    		Tile food = iterator.next();
    		List<Tile> path = astar.findPath(ant.tile, food);
    		if(foundPath(path)) {
            	ant.giveOrder(path);
            	gameState.antHasOrder(ant);
            }
		}
    }

	private boolean foundPath(List<Tile> path) {
		return !path.isEmpty();
	}


    private void explore(GameState gameState, List<Ant> exploreAnts) {
		Collections.rotate(directions, new Random().nextInt(4));
		for (Ant ant : exploreAnts) {
			for (Aim direction : directions) {
                Tile tile = getGameState().getTile(ant.tile, direction);
                if (isPassable(tile)) {
                    ant.orders.add(tile);
                    gameState.antHasOrder(ant);
                    break;
                }
            }
		}
	}
}
