package no.hiof.imagepr.morphology;

import no.hiof.imagepr.BinaryImage;
import no.hiof.imagepr.tools.Arithmetic;

/**
 * A HitOrMiss objects performs hit or miss transforms on a BinaryImage.
 *
 * @see StructElement
 * @author Per-Olav Rus&aring;s
 */
public class HitOrMiss
{
   
   private StructElement[] structElements;

   
   /**
    * Constructor which sets an array of structure elements to be used
    * by the hit of miss transform.
    * @param structElements An array of structure elements.
    */
   public HitOrMiss (StructElement[] structElements)
   {
      this.structElements = structElements;
   }


   /**
    * Constructor which sets a single structure elements to be used
    * by the hit of miss transform.
    * @param structEl The structure element.
    */
   public HitOrMiss (StructElement structEl)
   {
      this.structElements = new StructElement[1];
      structElements[0] = structEl;
   }

   /**
    * Produces a BinaryImage by a hit or miss transform of 
    * another BinaryImage. The source image is not altered.
    * @param image Source image for hit or miss transform.
    * @param structEl The structure element to be used.
    * @return The resulting image.
    */
   private BinaryImage hitOrMiss (BinaryImage image,
                                  StructElement structEl)
   {
      short[][] structMatrix = structEl.getElementMatrix();

      int elHeight = structEl.getHeight();
      int elWidth = structEl.getWidth();

      int vCenter = structEl.getVCenter();
      int hCenter = structEl.getHCenter();

      int height = image.getHeight();
      int width = image.getWidth();
      BinaryImage transformed = new BinaryImage (height, width);
      
      int indRow, indCol;
      boolean isHitMiss;

      for (int row = 0; row < height; row++)
      {
         for (int col = 0; col < width; col++)
         {
            isHitMiss = true;

            for (int i = 0; i < elHeight && isHitMiss; i++)
            {
               for (int j = 0; j < elWidth && isHitMiss; j++)
               {
                  indRow = row + i - vCenter;
                  indCol = col + j - hCenter;

                  if (indRow >= 0 && indRow < height &&
                      indCol >= 0 && indCol < width)
                  {
                     if (structMatrix[i][j] == 1)
                     {
                        isHitMiss = isHitMiss &&
                           image.getValueAt (indRow, indCol);
                     }
                     else if (structMatrix[i][j] == 0)
                     {
                        isHitMiss = isHitMiss &&
                           !image.getValueAt (indRow, indCol);
                     }
                  }
                  else
                  {
                     isHitMiss = false;
                  }
               }
            }

            transformed.setValueAt (row, col, isHitMiss);
            
         }
      }
      
      return transformed;
   }


   /**
    * Produces a BinaryImage by hit or miss transform. If there is
    * several structure elements registered, the result is the union
    * of several images produces by hit or miss transform.
    * @param image The source image for the transform.
    * @return The result of the hit or miss transform.
    */
   public BinaryImage transform (BinaryImage image)
   {
      Arithmetic ar = new Arithmetic();
      BinaryImage result = hitOrMiss (image, structElements[0]);
      for (int i = 1; i < structElements.length; i++)
      {
         result = ar.or(result, hitOrMiss (image, structElements[i]));
      }

      return result;
   }


   /**
    * Produces a BinaryImage by thinning with several structure elements.
    * @param image The source image for thinning.
    * @return The result of the thinning.
    */
   public BinaryImage thin (BinaryImage image)
   {
      Arithmetic ar = new Arithmetic();
      BinaryImage thinned = image;

      for (int i = 1; i < structElements.length; i++)
      {
         thinned = ar.andNot (thinned,
                              hitOrMiss (thinned, structElements[0]));
      }

      return thinned;
   }

}
