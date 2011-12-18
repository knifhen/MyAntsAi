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
	private GameState gameState;
	private Tile primaryHill;
    
    @Override
    public void doTurn() {
        gameState = getGameState();
		astar = new AStar(gameState);
		if(!gameState.myHills.isEmpty())
			primaryHill = gameState.myHills.iterator().next();
        
        int size = gameState.antsWithoutOrders.size();
		int nAntsPerTask = size / NUMBER_OF_TASKS + size % NUMBER_OF_TASKS;

		List<Ant> gatherAnts  = new LinkedList<Ant>(gameState.antsWithoutOrders.subList(0, nAntsPerTask));
        findFood(gatherAnts);
        
        LinkedList<Ant> soldierAnts = new LinkedList<Ant>(gameState.antsWithoutOrders);
        if(shouldAttack()) {
        	attack(soldierAnts);
        } else {
        	explore(soldierAnts);
        }
      
        issueOrders(getMyAnts());
    }

	private boolean shouldAttack() {
		return gameState.myAnts.size() > 20 && gameState.enemyHills.size() > 0;
	}

    private void attack(List<Ant> ants) {
    	
    	Tile bestHill = null;
    	int shortestDistance = Integer.MAX_VALUE;
    	for(Tile hill : gameState.enemyHills) {
    		int distance = gameState.getDistance(primaryHill, hill);
			if(bestHill == null || distance < shortestDistance) {
    			bestHill = hill;
    			shortestDistance = distance;
    		}
    	}
    	for (Ant ant : ants) {
    		List<Tile> path = astar.findPath(ant.tile, bestHill);
    		ant.giveOrder(path);
    		gameState.antHasOrder(ant);
		}
    	
	}

	private void issueOrders(Set<Ant> myAnts) {
        for(Ant ant : myAnts) {
            issueOrder(ant);
        }
    }

    private void findFood(List<Ant> ants) {
    	List<Tile> foodTiles = new LinkedList<Tile>(getGameState().foodTiles);
    	
    	if(foodTiles.isEmpty() || ants.isEmpty()) {
    		return;
    	}
    	
    	Collections.sort(foodTiles, new Comparator<Tile>() {
			@Override
			public int compare(Tile o1, Tile o2) {
				int distance1 = gameState.getDistance(primaryHill, o1);
				int distance2 = gameState.getDistance(primaryHill, o2);
				return distance1 - distance2;
			}}
    	);
    	
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


    private void explore(List<Ant> exploreAnts) {
		Collections.rotate(directions, new Random().nextInt(4));
		for (Ant ant : exploreAnts) {
			for (Aim direction : directions) {
                Tile tile = getGameState().getTile(ant.tile, direction);
                if (isPassable(tile)) {
                    ant.giveOrder(tile);
                    gameState.antHasOrder(ant);
                    break;
                }
            }
		}
	}
}
