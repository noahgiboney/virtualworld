import java.util.Objects;

public class Node {

    private int gCost;
    private int fCost;

    private Point position;

    private Node prior;

    public Node(Point position){
        this.position = position;
    }

    public int calcDistanceFromStart(Point startPoint){
        return Math.abs(position.x - startPoint.x) + Math.abs(position.y - startPoint.y);
    }

    public int calcToAdjacent(Point current){
        return Math.abs(position.x - current.x) + Math.abs(position.y - current.y);
    }

    public int calcF(Point endPoint){
        return Math.abs(position.x - endPoint.x) + Math.abs(position.y - endPoint.y) + getgCost();
    }

    public int getgCost() {
        return gCost;
    }

    public int getfCost() {
        return fCost;
    }

    public Point getPosition() {
        return position;
    }

    public Node getPrior() {
        return prior;
    }

    public void setgCost(int gCost) {
        this.gCost = gCost;
    }

    public void setfCost(int fCost) {
        this.fCost = fCost;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void setPrior(Node prior) {
        this.prior = prior;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Node other = (Node) obj;
        return Objects.equals(position, other.position); // This uses Point's equals method
    }

    @Override
    public int hashCode() {
        return Objects.hash(position); // This uses Point's hashCode method
    }
}
