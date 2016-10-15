package no.hiof.imagepr.examples;

import no.hiof.imagepr.morphology.*;
import no.hiof.imagepr.BinaryImage;

public class HitOrMissTest
{
   public static void main (String[] args)
   {

      // Makes a test image
      BinaryImage image = new BinaryImage (300, 300);
      for (int i = 0; i < 100; i++)
      {
         for (int j = 0; j < 100; j++)
         {
            image.setValueAt (100 + i, 100 + j, true);
         }
      }
      for (int i = 0; i < 50; i++)
      {
         for (int j = 0; j < 50; j++)
         {
            image.setValueAt (125 + i, 50 + j, true);
         }
      }

      image.show ("Test image");

      // Creates Four structure elements for detection of outer corners.
      short[][] seMat1 = {{1, 0},
                          {0, 0}};
      StructElement se1 = new StructElement (seMat1, 0, 0);

      short[][] seMat2 = {{0, 1},
                          {0, 0}};
      StructElement se2 = new StructElement (seMat2, 0, 0);

      short[][] seMat3 = {{0, 0},
                          {1, 0}};
      StructElement se3 = new StructElement (seMat3, 0, 0);

      short[][] seMat4 = {{0, 0},
                          {0, 1}};
      StructElement se4 = new StructElement (seMat4, 0, 0);

      StructElement[] elements = {se1, se2, se3, se4}; 
      HitOrMiss hm = new HitOrMiss (elements);

      BinaryImage outerCorners = hm.transform(image);
      outerCorners.show("All outer corners marked with a white dot");

      System.exit (0);
   }
}
