final class Point
{
   public final int x;
   public final int y;

   private int gCost;
   private int fCost;
   private Point prior;

   public Point(int x, int y)   {
      this.x = x;
      this.y = y;
   }

   public int calcDistanceFromStart(Point startPoint){
      return Math.abs(this.x - startPoint.x) + Math.abs(this.y - startPoint.y);
   }

   public int calcToAdjacent(Point current){
      return Math.abs(this.x - current.x) + Math.abs(this.y - current.y);
   }

   public int calcF(Point endPoint){
      return Math.abs(this.x - endPoint.x) + Math.abs(this.y - endPoint.y) + getgCost();
   }

   public int getgCost() {
      return gCost;
   }

   public int getfCost() {
      return fCost;
   }

   public Point getPrior() {
      return prior;
   }

   public void setgCost(int gCost) {
      this.gCost = gCost;
   }

   public void setfCost(int fCost) {
      this.fCost = fCost;
   }

   public void setPrior(Point prior) {
      this.prior = prior;
   }

   public String toString()   {
      return "(" + x + "," + y + ")";
   }

   public boolean equals(Object other)   {
      return other instanceof Point &&
         ((Point)other).x == this.x &&
         ((Point)other).y == this.y;
   }

   public int hashCode()   {
      int result = 17;
      result = result * 31 + x;
      result = result * 31 + y;
      return result;
   }

   public boolean adjacent(Point p)   {
      return (x == p.x && Math.abs(y - p.y) == 1) ||
              (y == p.y && Math.abs(x - p.x) == 1);
   }
}
