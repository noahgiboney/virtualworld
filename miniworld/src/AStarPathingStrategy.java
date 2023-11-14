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

        List<Point> path = new LinkedList<>();

        PriorityQueue<Point> open = new PriorityQueue<>(Comparator.comparing(point -> point.f)); //nodes we can expand
        HashSet<Point> closed = new HashSet<>(); //nodes that have been visited with the shortest path

        open.add(start);



        while(!(open.isEmpty())){
            Point current = open.poll();// pop node with the shortest path (lowest f value)

            //backtrack to get the path if the end is within reach
            Point temp = current;
            if(withinReach.test(temp, end)){
                while(temp != null) {
                    path.add(0, temp);
                    temp = temp.prior;
                }
                return path;
            }

            potentialNeighbors.apply(current) //get the stream of potential neighbors of current point
                              .filter(canPassThrough) //make sure they are obstacles
                              .filter(neighbor -> !closed.contains(neighbor)) //make sure they are not in closed list
                              .forEach(neighbor -> { //iterate through valid neighbors

                                  neighbor.g = neighbor.calcG(start); //calculate g value for the point

                                  if(open.contains(neighbor)) {
                                      if(current.calcG(start) < neighbor.g){
                                          neighbor.g = current.calcG(start);
                                          neighbor.f = neighbor.calcF(end);
                                          neighbor.prior = current;

                                          open.remove(neighbor);
                                          open.add(neighbor);
                                      }
                                  }
                                  else{
                                      neighbor.f = neighbor.calcF(end); //calculate f value for point
                                      neighbor.prior = current; //save the prior node
                                      open.add(neighbor); //add point to open list
                                  }
                              });
            closed.add(current); //add visited node to current list
        }
        return path; //empty path
    }
}

