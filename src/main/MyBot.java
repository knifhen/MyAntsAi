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
        astar = new AStar(getAnts());
        Iterator<Ant> ants = getMyAnts().iterator();
        findFood(ants);
        explore(ants);
        executeOrders(getMyAnts());
    }

    private void executeOrders(Set<Ant> myAnts) {
        for(Ant ant : myAnts) {
            issueOrder(ant);
        }
    }

    private void findFood(Iterator<Ant> ants) {
        Set<Tile> foodTiles = getAnts().getFoodTiles();
        for(Tile food : foodTiles) {
            if(ants.hasNext()) {
                Ant ant = ants.next();
                if(ant.orders.isEmpty()) {
                    List<Tile> path = astar.findPath(ant.tile, food);
                    if(!path.isEmpty()) {
                        ant.orders.addAll(path);
                    }
                }
            } else {
                break;
            }
        }
    }


    private void explore(Iterator<Ant> ants) {
		Collections.rotate(directions, new Random().nextInt(4));
		while(ants.hasNext()) {
            Ant ant = ants.next();
            if(ant.orders.isEmpty()) {
                for (Aim direction : directions) {
                    Tile tile = getAnts().getTile(ant.tile, direction);
                    if (isPassable(tile)) {
                        ant.orders.add(tile);
                        break;
                    }
                }
            }
        }
	}
}
