import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.junit.matchers.JUnitMatchers.hasItems;

public class AstarTest extends TestCase {

    private Ants ants;

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
        ants = new Ants(loadTime, turnTime, rows, cols, turns, viewRadius2,
            attackRadius2, spawnRadius2);
    }

    @Test
    public void testFindPath() {
        AStar astar = new AStar(ants);
        Tile start = new Tile(0,0);
        Tile goal = new Tile(2,3);
        List<Tile> path = astar.findPath(start, goal);
        assertThat(path, hasItems(any(Tile.class)));
    }
    
    public void testFindPathAroundObstacle() {
        AStar astar = new AStar(ants);
        ants.setIlk(new Tile(1,1), Ilk.WATER);
        
        Tile start = new Tile(0,0);
        Tile goal = new Tile(2,3);
       
        List<Tile> path = astar.findPath(start, goal);
        assertThat(path, hasItems(any(Tile.class)));
        assertThat(path, not(hasItem(new Tile(1,1))));
    }

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

//    public void testFindNoPathAroundAnts() {
//        AStar astar = new AStar(ants);
//
//        ants.setIlk(new Tile(0,1), Ilk.MY_ANT);
//        ants.setIlk(new Tile(1,0), Ilk.MY_ANT);
//        ants.setIlk(new Tile(1,2), Ilk.MY_ANT);
//        ants.setIlk(new Tile(2,1), Ilk.MY_ANT);
//
//        Tile start = new Tile(1,1);
//        Tile goal = new Tile(2,3);
//
//        List<Tile> path = astar.findPath(start, goal);
//        assertThat(path, not(hasItems(any(Tile.class))));
//    }
}
