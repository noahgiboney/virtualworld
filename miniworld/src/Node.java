public class Node {

    private int gCost;
    private int fCost;

    Point position;

    public Node(Point position){
        this.position = position;
    }

    public int calcDistanceFromStart(Point startPoint){
        return Math.abs(position.x - startPoint.x) + Math.abs(position.y - startPoint.y);
    }

    public int calcToAdjacent(Point current){
        return Math.abs(position.x - current.x) + Math.abs(position.y - current.y);
    }

    public double calcF(Point endPoint){
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

    public void setgCost(int gCost) {
        this.gCost = gCost;
    }

    public void setfCost(int hCost) {
        this.fCost = fCost;
    }

    public void setPosition(Point position) {
        this.position = position;
    }
}
