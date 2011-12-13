import java.util.*;

public class AStar {

    private Ants ants;

    public AStar(Ants ants) {
        this.ants = ants;
    }


    public List<Tile> findPath(Tile start, Tile goal) {
        try {
            if(start.equals(goal)) {
                return new LinkedList<Tile>();
            }

            List<Tile> closedSet = new LinkedList<Tile>();
            Map<Tile, Tile> cameFrom = new HashMap<Tile, Tile>();

            List<Tile> openSet = new LinkedList<Tile>();
            openSet.add(start);

            while(!openSet.isEmpty()) {
                Tile openTile = openSet.remove(0);
                closedSet.add(openTile);
                if(openTile.equals(goal)) {
                    return reconstructPath(cameFrom, openTile);
                }

                List<Tile> neighbors = findNonVisitedNeighbors(closedSet, openTile, goal);
                for(Tile neighbor : neighbors) {
                    openSet.add(0, neighbor);
                    cameFrom.put(neighbor, openTile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new LinkedList<Tile>();
    }

    private List<Tile> findNonVisitedNeighbors(List<Tile> closedSet, Tile openTile, final Tile goal) {
        List<Tile> neighbors = new LinkedList<Tile>();
        for(Aim direction : Aim.values()) {
            Tile neighbor = ants.getTile(openTile, direction);
            if(!closedSet.contains(neighbor) && neighbor.isPassableIlk(ants)) {
                neighbors.add(neighbor);
            }
        }
        Collections.sort(neighbors, new Comparator<Tile>() {
            @Override
            public int compare(Tile tile1, Tile tile2) {
                int distance1 = ants.getDistance(tile1, goal);
                int distance2 = ants.getDistance(tile2, goal);

                return distance2 - distance1;
            }
        });
        
        return neighbors;
    }

    private List<Tile> reconstructPath(Map<Tile, Tile> cameFrom, Tile tile) {
        List<Tile> path = new LinkedList<Tile>();
        while(cameFrom.containsKey(tile)) {
            path.add(0, tile);
            tile = cameFrom.get(tile);
        }
        return path;
    }

}
