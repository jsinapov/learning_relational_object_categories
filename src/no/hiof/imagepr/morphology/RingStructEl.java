package no.hiof.imagepr.morphology;

import no.hiof.imagepr.BinaryImage;

/**
 * A RingStructEl models a ring shaped structure element with a given
 * radius and a thickness of one.
 * 
 * @see Morphology
 * @author Per-Olav Rus&aring;s
 */
public class RingStructEl extends StructElement
{
   private int radius;

   /**
    * Constructor which sets the radius of the ring shaped structure element.
    * @param radius The radius of the ring.
    */
   public RingStructEl (int radius)
   {
      this.radius = radius;
      int height = 2*radius + 1;
      int width = 2*radius + 1;

      this.elementMatrix = new short[height][width];
      this.vCenter = radius;
      this.hCenter = radius;

      int r2;
      
      for (int row = 0; row < height; row++)
      {
         for (int col = 0; col < width; col++)
         {
            r2 = (row - vCenter)*(row - vCenter) +
               (col - hCenter)*(col - hCenter);

            if (Math.abs(Math.sqrt(r2) - radius) <= 1)
               elementMatrix[row][col] = 1;
            else
               elementMatrix[row][col] = -1;

         }
      }

   }

}
      
