package no.hiof.imagepr.morphology;

import no.hiof.imagepr.BinaryImage;

/**
 * <p>A Morphology object can perform dilation and erotion of a
 * BinaryImage. These are the basic morphological operations.  The
 * structure element are modelled by an instance of StructElement, or
 * by an instance of a subclass of StructElement.
 *
 * @see StructElement
 * @author Per-Olav Rus&aring;s
*/

public class Morphology
{

   protected StructElement structEl;


   /**
    * A constructor which sets the structure element to be used during
    * dilation and erotion.
    * @param structEl The structure element.
    */
   public Morphology (StructElement structEl)
   {
      this.structEl = structEl;
   }

   
   /**
    * Produces a Binary Image by dilating a source image. The source
    * image is not altered.
    * @param image Source image for dilation.
    * @return The dilated image.
    */
   public BinaryImage dilate (BinaryImage image)
   {

      // Reflection of structure element
      StructElement rStruct = structEl.createReflectStruct();
      int vCenter = rStruct.getVCenter();
      int hCenter = rStruct.getHCenter();

      short[][] rStructMatrix = rStruct.getElementMatrix();

      int elHeight = rStruct.getHeight();
      int elWidth = rStruct.getWidth();

      int height = image.getHeight();
      int width = image.getWidth();
      BinaryImage dilatedImage = new BinaryImage (height, width);
      
      int indRow, indCol;

      for (int row = 0; row < height; row++)
      {
         for (int col = 0; col < width; col++)
         {

            if (image.getValueAt (row, col))
            {

               for (int i = 0; i < elHeight; i++)
               {
                  for (int j = 0; j < elWidth; j++)
                  {
                     indRow = row + i - vCenter;
                     indCol = col + j - hCenter;

                     if (rStructMatrix[i][j] == 1 &&
                         indRow >= 0 && indRow < height &&
                         indCol >= 0 && indCol < width)
                     {
                        dilatedImage.setValueAt (indRow, indCol, true);
                     }
                  }
               }

            }// end if

         }
      }
      
      return dilatedImage;
   }


   /**
    * Produces a Binary Image by eroding a source image. The source
    * image is not altered.
    * @param image Source image for erotion.
    * @return The dilated image.
    */
   public BinaryImage erode (BinaryImage image)
   {

      int vCenter = structEl.getVCenter();
      int hCenter = structEl.getHCenter();

      short[][] structElMatrix = structEl.getElementMatrix();

      int elHeight = structEl.getHeight();
      int elWidth = structEl.getWidth();

      int height = image.getHeight();
      int width = image.getWidth();
      BinaryImage erodedImage = new BinaryImage (height, width);
      
      int indRow, indCol;
      boolean allOne;


      for (int row = 0; row < height; row++)
      {
         for (int col = 0; col < width; col++)
         {

            allOne = true;

            for (int i = 0; i < elHeight && allOne; i++)
            {
               for (int j = 0; j < elWidth && allOne; j++)
               {
                  indRow = row + i - vCenter;
                  indCol = col + j - hCenter;
                  
                  if (structElMatrix[i][j] == 1 &&
                      indRow >= 0 && indRow < height &&
                      indCol >= 0 && indCol < width &&
                      !image.getValueAt (indRow, indCol))
                  {
                     allOne = false;
                  }
               }
            }

            erodedImage.setValueAt (row, col, allOne);
         }
         
      }
      
      return erodedImage;
   }

}
