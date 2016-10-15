package no.hiof.imagepr.examples;

import no.hiof.imagepr.morphology.*;
import no.hiof.imagepr.BinaryImage;

public class MorphologyTest
{

   public static void main (String[] args)
   {
      // A rectangular structure element of size 3x2
      StructElement se = new RectangStructEl(3, 3, 1, 1);

      // Create a Morphology instance which uses the given
      // structural element
      Morphology mp = new Morphology(se);

      // Create a small test image
      BinaryImage binImage = new BinaryImage (6, 6);
      for (int i = 0; i < 2; i++)
      {
         for (int j = 0; j < 2; j++)
         { 
            binImage.setValueAt (2 + i, 2 + j, true);
         }
      }

      binImage.show("Test image scaled by a factor 20", 20);

      // Dilates the image
      binImage = mp.dilate(binImage);
      binImage.show("Dilated image", 20);

      // Erodes the image
      binImage = mp.erode(binImage);
      binImage.show("Eroded image", 20);

      System.exit (0);
   }
}
