import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AStarPathingStrategy implements PathingStrategy {


    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {

        List<Point> path = new LinkedList<>(); //list to store path

        PriorityQueue<Point> open = new PriorityQueue<>(Comparator.comparing(point -> point.f)); //nodes we can expand
        HashSet<Point> closed = new HashSet<>(); //nodes that have been visited with the shortest path

        start.g = 0; //begin with g cost at zero
        open.add(start);

        while(!(open.isEmpty())){
            Point current = open.poll();// pop node with the shortest path (lowest f value)

            //if we reached the end then backtrack to add the path
            Point temp = current;
            if(withinReach.test(temp, end)){
                while(temp != null) {
                    path.add(0, temp);
                    temp = temp.prior;
                }
                return path;
            }

            potentialNeighbors.apply(current) //get the stream of potential neighbors of current point
                              .filter(canPassThrough) //make sure they are able to move through (non obstacles)
                              .filter(neighbor -> !(closed.contains(neighbor))) //make sure they are not in closed list
                              .forEach(neighbor -> { //iterate through valid neighbors
                                  int currentG = current.calcDistanceFromStart(start) + neighbor.calcToAdjacent(current); //hold current g value to compare

                                  if(open.contains(neighbor)) {//if neighbor is already in the open list, is the G cost better?
                                      if( currentG < neighbor.g){
                                          //if so, remove from list and replace its values with the lowest cost

                                          open.remove(neighbor);

                                          neighbor.g = current.calcDistanceFromStart(start) + neighbor.calcToAdjacent(current);
                                          neighbor.f = neighbor.calcF(end);
                                          neighbor.prior = current;
                                          open.add(neighbor); //add point to open list
                                      }
                                  }
                                  else{
                                      //if not, then continue on to set values

                                      neighbor.g = current.calcDistanceFromStart(start) + neighbor.calcToAdjacent(current);
                                      neighbor.f = neighbor.calcF(end);
                                      neighbor.prior = current; //save the prior node
                                      open.add(neighbor); //add point to open list
                                  }
                              });
            //after all neighbors visited, add the that point to the closed list
            closed.add(current);
        }
        return path; //empty path
    }
}

