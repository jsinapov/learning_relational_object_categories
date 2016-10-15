package no.hiof.imagepr.morphology;

import no.hiof.imagepr.BinaryImage;

/**
 * A StructElement object models a structure element used in
 * morphologic processes.
 *
 * @see Morphology
 * @author Per-Olav Rus&aring;s
 */
public class StructElement
{

   protected short[][] elementMatrix;
   protected int hCenter;
   protected int vCenter;

   /**
    * Constructs an empty structure element.
    */
   public StructElement ()
   {
   }

   /**
    * Constructs a structure element with a given element matrix and
    * center postition.
    *
    * @param elementMatrix The element matrix.
    * @param hCenter The horizontal position of the center.
    * @param vCenter The horizontal position of the center.
    */
   public StructElement (short[][] elementMatrix, int hCenter, int vCenter)
   {
      this.elementMatrix = elementMatrix;
      this.hCenter = hCenter;
      this.vCenter = vCenter;
   }


   /**
    * Gets the horizontal coordinate of the center of the structure
    * element.
    * @return The horizontal coordinate of the center.
    */
   public int getHCenter()
   {
      return hCenter;
   }


   /**
    * Gets the vertical coordinate of the center of the structure
    * element.
    * @return The vertical coordinate of the center.
    */
   public int getVCenter()
   {
      return vCenter;
   }


   /**
    * Gets the height of the element matrix.
    * @return The height of the structure element.
    */
   public int getHeight()
   {
      return elementMatrix.length;
   }


   /**
    * Gets the width of the element matrix.
    * @return The width of the structure element.
    */
   public int getWidth()
   {
      return elementMatrix[0].length;
   }


   /**
    * Gets the element matrix of the structure element.
    * @return The element matrix.
    */
   public short[][] getElementMatrix()
   {
      return elementMatrix;
   }


   /**
    * Creates the reflection of the structure element.
    * @return The reflection of the structure element.
    */
   public StructElement createReflectStruct()
   {
      int height = elementMatrix.length;
      int width = elementMatrix[0].length;

      short[][] rMatrix = new short[height][width];

      for (int row = 0; row < height; row++)
      {
         for (int col = 0; col < width; col++)
         {
            rMatrix[row][col] = elementMatrix[height-1-row][width-1-col];
         }
      }

      int rVCenter = height - 1 - vCenter;
      int rHCenter = width - 1 - hCenter;

      return new StructElement (rMatrix, rVCenter, rHCenter);
   }
}
      
