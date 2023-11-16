import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

interface PathingStrategy {
   /*
    * Returns a prefix of a path from the start point to a point within reach
    * of the end point.  This path is only valid ("clear") when returned, but
    * may be invalidated by movement of other entities.
    *
    * The prefix includes neither the start point nor the end point.
    */
   List<Point> computePath(Point start, Point end,
      Predicate<Node> canPassThrough,
      BiPredicate<Point, Point> withinReach,
      Function<Node, Stream<Node>> potentialNeighbors);

   static final Function<Node, Stream<Node>> CARDINAL_NEIGHBORS =
      node ->
         Stream.<Node>builder()
            .add(new Node(new Point(node.getPosition().getX(), node.getPosition().getY() - 1)))
            .add(new Node(new Point(node.getPosition().getX(), node.getPosition().getY() + 1)))
            .add(new Node(new Point(node.getPosition().getX() - 1, node.getPosition().getY())))
            .add(new Node(new Point(node.getPosition().getX() + 1, node.getPosition().getY())))
            .build();
}