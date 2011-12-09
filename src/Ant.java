import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: andreas.knifh
 * Date: 12/9/11
 * Time: 8:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class Ant {
    public Tile tile;
    public List<Aim> orders = new LinkedList<Aim>();

    public Ant(Tile tile) {
        this.tile = tile;
    }
}
