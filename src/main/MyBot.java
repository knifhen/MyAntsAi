import java.io.IOException;
import java.util.*;

/**
 * Starter bot implementation.
 */
public class MyBot extends Bot {
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
        astar = new AStar(getGameState());
        Iterator<Ant> ants = getMyAnts().iterator();
        findFood(ants);
        explore(ants);
        issueOrders(getMyAnts());
    }

    private void issueOrders(Set<Ant> myAnts) {
        for(Ant ant : myAnts) {
            issueOrder(ant);
        }
    }

    private void findFood(Iterator<Ant> ants) {
        Set<Tile> foodTiles = getGameState().foodTiles;
        for(Tile food : foodTiles) {
            if(ants.hasNext()) {
                Ant ant = ants.next();
                if(!ant.hasOrder()) {
                    List<Tile> path = astar.findPath(ant.tile, food);
                    if(foundPath(path)) {
                    	ant.giveOrder(path);
                    }
                }
            } else {
                break;
            }
        }
    }

	private boolean foundPath(List<Tile> path) {
		return !path.isEmpty();
	}


    private void explore(Iterator<Ant> ants) {
		Collections.rotate(directions, new Random().nextInt(4));
		while(ants.hasNext()) {
            Ant ant = ants.next();
            if(ant.orders.isEmpty()) {
                for (Aim direction : directions) {
                    Tile tile = getGameState().getTile(ant.tile, direction);
                    if (isPassable(tile)) {
                        ant.orders.add(tile);
                        break;
                    }
                }
            }
        }
	}
}
