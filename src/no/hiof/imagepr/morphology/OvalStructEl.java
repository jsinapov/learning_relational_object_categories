package no.hiof.imagepr.morphology;

import no.hiof.imagepr.BinaryImage;

/**
 * An OvalStructEl models an oval shaped structure element with a
 * given half width in horizontal and vertical direction.
 *
 * @see Morphology
 * @author Per-Olav Rus&aring;s
 */
public class OvalStructEl extends StructElement
{


   /**
    * A constructor which sets the half height and half width of the
    * oval structure element.
    * @param halfWidth The half width.
    * @param halfHeight The half height.
    */
   public OvalStructEl (int halfHeight, int halfWidth)
   {
      int height = 2*halfHeight + 1;
      int width = 2*halfWidth + 1;

      double toleranse = 0.01;
      this.elementMatrix = new short[height][width];
      this.vCenter = halfHeight;
      this.hCenter = halfWidth;

      double r=0, rmax, cosVinkel, sinVinkel, hp, vp;
      int r2;
      
      for (int row = 0; row < height; row++)
      {
         for (int col = 0; col < width; col++)
         {
            r2 = (row - vCenter)*(row - vCenter) +
               (col - hCenter)*(col - hCenter);

            if (r2 == 0)
               rmax = halfWidth;
            else
            {
               r = Math.sqrt(r2);
               sinVinkel = (row - vCenter) / r;
               cosVinkel = (col - hCenter) / r;
               hp = halfWidth * cosVinkel;
               vp = halfHeight * sinVinkel;
               rmax = Math.sqrt (hp*hp + vp*vp);
            }

            if (r - rmax < toleranse)
               elementMatrix[row][col] = 1;
            else
               elementMatrix[row][col] = -1;

         }
      }

   }

}
      
