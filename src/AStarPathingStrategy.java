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

        //holds open points, sort the queue by first the fCost, then gCost
        PriorityQueue<Point> open = new PriorityQueue<>((p1, p2) -> {
            int fCompare = Integer.compare(p1.getfCost(), p2.getfCost());

            //if fCosts are the same, sort by gCost
            if (fCompare == 0) {
                return Integer.compare(p1.getgCost(), p2.getgCost());
            }
            else{
                return fCompare;
            }
        });

        HashSet<Point> closed = new HashSet<>(); //nodes that have been visited with the shortest path

        start.setgCost(0); //begin with g cost at zero
        start.setfCost(start.calcF(end)); //initialize first fCost
        open.add(start);

        while(!(open.isEmpty())){
            Point current = open.poll();// pop point with the shortest path (lowest f value)
            System.out.println("Processing node: " + current);

            //if we reached the end then backtrack through prior nodes to add the path
            Point temp = current;
            if(withinReach.test(temp, end)){
                System.out.println("Goal reached at " + current);
                while(temp != null && !(temp.equals(start))) {
                    path.add(0, temp);
                    temp = temp.getPrior();
                }
                return path;
            }

            potentialNeighbors.apply(current) //get the stream of potential neighbors of current point
                    .filter(canPassThrough) //make sure they are able to move through (non obstacles)
                    .filter(neighbor -> !(closed.contains(neighbor))) //make sure they are not in closed list
                    .forEach(neighbor -> { //iterate through valid neighbors
                        System.out.println("Checking neighbor: " + neighbor);

                        int currentG = current.calcDistanceFromStart(start) + neighbor.calcToAdjacent(current); //hold current g value to compare

                        if(open.contains(neighbor)) {//if neighbor is already in the open list, is the G cost better?
                            if( currentG < neighbor.getgCost()){

                                //if so, remove from list and replace its values with the lowest cost
                                open.remove(neighbor);

                                neighbor.setgCost(currentG);
                                neighbor.setfCost(neighbor.calcF(end));
                                neighbor.setPrior(current);
                                open.add(neighbor);
                            }
                        }
                        else{
                            //if not, then continue on to set values
                           // System.out.println("Adding new neighbor to open list: " + neighbor);

                            neighbor.setgCost(current.calcDistanceFromStart(start) + neighbor.calcToAdjacent(current));
                            neighbor.setfCost(neighbor.calcF(end));
                            neighbor.setPrior(current);
                            open.add(neighbor);
                        }
                    });
            //after all neighbors visited, add the that point to the closed list, so it is not visited anymore
            closed.add(current);
            System.out.println("Added to closed list: " + current);
        }
        return path; //empty path
    }
}