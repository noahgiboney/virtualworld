final class Point
{
   public final int x;
   public final int y;

   public int g;

   public double f;
   public Point prior;

   public Point(int x, int y)   {
      this.x = x;
      this.y = y;
   }

   public int calcG(Point startPoint){
      return Math.abs(this.x - startPoint.x) + Math.abs(this.y - startPoint.y);
   }

   public double calcF(Point endPoint){
      return Math.sqrt(Math.pow(this.x - endPoint.x, 2) + Math.pow(this.y - endPoint.y, 2)) + this.calcG(endPoint);
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
