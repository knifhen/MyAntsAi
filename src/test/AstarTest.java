import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.junit.matchers.JUnitMatchers.hasItems;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class AstarTest extends TestCase {

    private GameState ants;

    @Before
    public void setUp() {
        int loadTime = 0;
        int turnTime = 0;
        int rows = 10;
        int cols = 10;
        int turns = 0;
        int viewRadius2 = 0;
        int attackRadius2 = 0;
        int spawnRadius2 = 0;
        ants = new GameState(loadTime, turnTime, rows, cols, turns, viewRadius2,
            attackRadius2, spawnRadius2);
    }

    @SuppressWarnings("unchecked")
	@Test
    public void testFindPath() {
        AStar astar = new AStar(ants);
        Tile start = new Tile(0,0);
        Tile goal = new Tile(2,3);
        List<Tile> path = astar.findPath(start, goal);
        assertThat(path, hasItems(any(Tile.class)));
    }
    
    @SuppressWarnings("unchecked")
	public void testFindPathAroundObstacle() {
        AStar astar = new AStar(ants);
        ants.setIlk(new Tile(1,1), Ilk.WATER);
        
        Tile start = new Tile(0,0);
        Tile goal = new Tile(2,3);
       
        List<Tile> path = astar.findPath(start, goal);
        assertThat(path, hasItems(any(Tile.class)));
        assertThat(path, not(hasItem(new Tile(1,1))));
    }

    @SuppressWarnings("unchecked")
	public void testFindNoPathAroundObstacle() {
        AStar astar = new AStar(ants);

        ants.setIlk(new Tile(0,1), Ilk.WATER);
        ants.setIlk(new Tile(1,0), Ilk.WATER);
        ants.setIlk(new Tile(1,2), Ilk.WATER);
        ants.setIlk(new Tile(2,1), Ilk.WATER);

        Tile start = new Tile(1,1);
        Tile goal = new Tile(2,3);

        List<Tile> path = astar.findPath(start, goal);
        assertThat(path, not(hasItems(any(Tile.class))));
    }

}
