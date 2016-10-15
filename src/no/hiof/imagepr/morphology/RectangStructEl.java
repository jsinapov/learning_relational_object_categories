package no.hiof.imagepr.morphology;

import no.hiof.imagepr.BinaryImage;

/**
 * A RectangStructEl models a rectangular structur element.
 *
 * @see Morphology
 * @author Per-Olav Rus&aring;s
 */
public class RectangStructEl extends StructElement
{

   /**
    * Constructs a rectangular structure element with given size and center.
    * @param height The height of the structure element.
    * @param width The width of the structure element.
    * @param vCenter The vertical coordinate of the elements center.
    * @param hCenter The horizontal coordinate of the elements center.
    */
   public RectangStructEl (int height, int width, int vCenter, int hCenter)
   {
      this.elementMatrix = new short[height][width];
      this.vCenter = vCenter;
      this.hCenter = hCenter;
      
      for (int row = 0; row < height; row++)
      {
         for (int col = 0; col < width; col++)
         {
            elementMatrix[row][col] = 1;
         }
      }

   }

}
      
