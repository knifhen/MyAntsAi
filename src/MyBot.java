import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
    
    /**
     * For every ant check every direction in fixed order (N, E, S, W) and move it if the tile is
     * passable.
     */
    @Override
    public void doTurn() {
        explore();
    }

    
    
	private void explore() {
		Collections.rotate(directions, new Random().nextInt(4));
		for(Ant ant: getMyAnts()) {
            for (Aim direction : directions) {
                if (isPassable(ant, direction)) {
                    ant.orders.add(direction);
                    ant.orders.add(direction);
                    issueOrder(ant);
                    break;
                }
            }
        }
	}
}
