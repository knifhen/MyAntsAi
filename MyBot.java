import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
    
    /**
     * For every ant check every direction in fixed order (N, E, S, W) and move it if the tile is
     * passable.
     */
    @Override
    public void doTurn() {
        Ants ants = getAnts();
        Iterator<Tile> myAnts = ants.getMyAnts().iterator();
        for(Tile food: ants.getFoodTiles()) {
        	if(!myAnts.hasNext()) {
        		break;
        	}
        	Tile ant = myAnts.next();
        	List<Aim> directions = ants.getDirections(ant, food);
        	for(Aim direction: directions) {
            	if(ants.getIlk(ant, direction).isPassable()) {
            		ants.issueOrder(ant, direction);
            		break;
            	}        		
        	}
        }
        while(myAnts.hasNext()) {
        	Tile myAnt = myAnts.next();
            for (Aim direction : Aim.values()) {
                if (ants.getIlk(myAnt, direction).isPassable()) {
                    ants.issueOrder(myAnt, direction);
                    break;
                }
            }
        }
    }
}
