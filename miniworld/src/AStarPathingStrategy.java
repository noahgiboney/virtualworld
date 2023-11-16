import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AStarPathingStrategy implements PathingStrategy {


    public List<Point> computePath(Point start, Point end,
                                   Predicate<Node> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Node, Stream<Node>> potentialNeighbors) {

        List<Point> path = new LinkedList<>(); //list to store path

        System.out.println("Starting pathfinding from " + start + " to " + end);

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparing(Node::getfCost)); //nodes we can expand, sort based on lowest f cost
        HashSet<Node> closed = new HashSet<>(); //nodes that have been visited with the shortest path

        //initialize starting point as first node, set g & f cost as zero and add to queue
        Node startNode = new Node(start);
        startNode.setgCost(0);
        startNode.setfCost(0);
        open.add(startNode);

        while(!(open.isEmpty())){
            Node current = open.poll();// pop node with the shortest path (lowest f value)
            //System.out.println("Processing node: " + current.getPosition());

            //if we reached the end then backtrack to add the path
            Node temp = current;
            if(withinReach.test(temp.getPosition(), end)){
                System.out.println("Goal reached at " + temp.getPosition());
                while(temp != null) {
                    path.add(0, temp.getPosition());
                    temp = temp.getPrior();
                }
                System.out.println("Path found: " + path);
                return path;
            }

            potentialNeighbors.apply(current) //get the stream of potential neighbors of current node
                              .filter(canPassThrough) //make sure they are able to move through (non obstacles)
                              .filter(neighbor -> !(closed.contains(neighbor))) //make sure they are not in closed list
                              .forEach(neighbor -> { //iterate through valid neighbors
                                  System.out.println("Checking neighbor: " + neighbor.getPosition());
                                  //hold current g value to compare
                                  int currentG = current.calcDistanceFromStart(start) + neighbor.calcToAdjacent(current.getPosition());

                                  if(open.contains(neighbor)) {//if neighbor is already in the open list, is the G cost better?
                                      if( currentG < neighbor.getgCost()){

                                          //if so, remove from list and replace its values with the lowest cost
                                          open.remove(neighbor);

                                          neighbor.setgCost(current.calcDistanceFromStart(start) + neighbor.calcToAdjacent(current.getPosition()));
                                          neighbor.setfCost(neighbor.calcF(end));
                                          neighbor.setPrior(current);
                                          open.add(neighbor); //add point to open list
                                      }
                                  }
                                  else{
                                      //if not, then continue on to set values

                                      neighbor.setgCost(current.calcDistanceFromStart(start) + neighbor.calcToAdjacent(current.getPosition()));
                                      neighbor.setfCost(neighbor.calcF(end));
                                      neighbor.setPrior(current);
                                      open.add(neighbor); //add point to open list
                                  }
                              });
            //after all neighbors visited, add the that point to the closed list
            closed.add(current);
            System.out.println("Added to closed list: " + current.getPosition());
        }
        System.out.println("No path found.");
        return path; //empty path
    }
}

