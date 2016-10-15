package no.hiof.imagepr.tools;

import no.hiof.imagepr.*;

import java.util.BitSet;

/** 
 * <p>Arithmetic is a class for enhancement of images using arithmetic
 * and logic operations. 
 * 
 * <p>The Arithemtic class contains methods for performing the logic
 * operations (AND and OR) and arithmetic operations (substraction,
 * addition and multiplication) between two images, and methods for
 * performing the logical operation NOT on separate images.
 *
 * @author Mari-Ann Akerjord
 */
public class Arithmetic {
   
   /** Option for no scaling */
   public static final int DONT_SCALE = 0; 

   /** Option for changing negative pixel values to zero*/
   public static final int CUT_LOWER = 1;

   /** Option for changing pixel values above 255 to 255*/
   public static final int CUT_UPPER = 2; 

   /** Option for changing negative pixel values to their absolute value.*/ 
   public static final int ABS = 4; 

   /** Option for changing pixel values (p) above 255 to 2*255 -p */ 
   public static final int WRAP_UPPER = 8;

   /** Option for substracting the minimum pixel value in the image
    * from all pixels in the image.*/ 
   public static final int SUB_MIN = 16; 

   /** Option for multiplying each pixel in the image by the quantity
    * 255/Max, where Max is the maximum pixel value in the image. */ 
   public static final int SCALE_MAX = 32; 


   private static final int L = 256; 
   private int option; 

   /** 
    * Constructs an Arithmetic-object. No scaling will be performed on
    * images created by the methods for performing arithmetic or
    * logical operations on spesific images.
    */
   public Arithmetic () {
      option = DONT_SCALE;       
   }

   
   /** 
    * Constructs an Arithmetic-object with a spesific
    * scaling-option. Images created by the methods for performing
    * arithmetic or logical operations on spesific images will be
    * scaled. Scaling method to be used is determined by the value of
    * the parameter option.
    * @param option Option for scaling. Possible values: DONT_SCALE,
    * CUT_LOWER, CUT_UPPER, ABS, WRAP_UPPER, SUB_MIN AND SCALE_MAX. If
    * more than one scaling method is wanted, the sum of two or more
    * of these values should be used.
    */
   public Arithmetic(int option) {
      this.option = option; 
   }


   /**
    * Performs the logical  operator NOT on a BinaryImage. 
    * @param im The BinaryImage.
    * @return The resulting BinaryImage.
    */
   public BinaryImage not (BinaryImage im) 
   {
      BinaryImage newImage = new BinaryImage(im); 
      BitSet bits = newImage.getData();
      bits.flip(0, bits.size()-1);
      return newImage;
   }


   /**
    * Performs the logical operator NOT on an IntensityImage.
    * @param im The IntensityImage.
    * @return The resulting IntensityImage.
    */
   public IntensityImage not (IntensityImage im) 
   {
      return new IntensityImage( not(im.getData()) ); 
   }


   /**
    * Performs the logical operator NOT on a short matrix. The
    * operation is performed only on the eight lowest bits. The eight
    * upper bits are set to zero.
    * @param matrix The short matrix.
    * @return The resulting matrix.
    */
   public short[][] not (short[][] matrix)
   {
      short [][] newMatrix = MatrixTools.copy(matrix);
      int height = matrix.length;
      int width = matrix[0].length;

      for (int i=0; i<height; i++) 
         for (int j=0; j<width; j++)   
            newMatrix[i][j] = (short)((~newMatrix[i][j])&255);

      return newMatrix; 
   }


   /** 
    * Performs the logical operator AND between two BinaryImages. The
    * two images must be of equal size.
    * @param im1 A BinaryImage. 
    * @param im2 Another  BinaryImage. 
    * @return The resulting BinaryImage. 
    */ 
   public BinaryImage and (BinaryImage im1, BinaryImage im2) 
   {
      BinaryImage newImage = new BinaryImage(im1);
      BitSet bits1 = newImage.getData();
      BitSet bits2 = im2.getData();
      bits1.and (bits2);

      return newImage;
   }


   /** 
    * Performs the logical operator AND between an IntensityImage and
    * a BinaryImage. The two images must be of equal size. 
    * @param im1 The intensityImage 
    * @param im2 The BinaryImage 
    * @return The resulting IntensityImage. 
    */
   public IntensityImage and (IntensityImage im1, BinaryImage im2) 
   {
      short[][] im1Matrix = im1.getData();
      short[][] im2Matrix = im2.makeIntensityImage().getData(); 

      return new IntensityImage( and(im1Matrix, im2Matrix) );
   }


   /** 
    * Performs the logical operator AND between an two IntensityImages
    * The two images must be of equal size.
    * @param im1 An IntensityImage.
    * @param im2 Another IntensityImage.
    * @return The resulting IntensityImage. 
    */
   public IntensityImage and (IntensityImage im1, IntensityImage im2) 
   {
      return new IntensityImage( and(im1.getData(), im2.getData()) );
   }


   /**
    * Performs the logical operator AND between two short matrices.
    * The operation is performed only on the eight
    * lowest bits. The eight upper bits are set to zero.
    * @param matrix1 A short matrix.
    * @param matrix2 Another short matrix.
    */
   public short[][] and (short[][] matrix1, short[][] matrix2)
   {
      int height = matrix1.length;
      int width = matrix1[0].length;
      short [][] newMatrix = new short[height][width];

      for (int i=0; i<height; i++) 
         for (int j=0; j<width; j++)   
            newMatrix[i][j] = (short)((matrix1[i][j]&matrix2[i][j])&255);

      return newMatrix; 
   }



   /** 
    * Performs the logical operator OR between two BinaryImages. The
    * two images must be of equal size.
    * @param im1 A BinaryImage. 
    * @param im2 Another  BinaryImage. 
    * @return The resulting BinaryImage. 
    */ 
   public BinaryImage or (BinaryImage im1, BinaryImage im2) 
   {
      BinaryImage newImage = new BinaryImage(im1);
      BitSet bits1 = newImage.getData();
      BitSet bits2 = im2.getData();
      bits1.or (bits2);

      return newImage; 
   }

      
   /** 
    * Performs the logical operator OR between an IntensityImage and
    * a BinaryImage. The two images must be of equal size. 
    * @param im1 The intensityImage 
    * @param im2 The BinaryImage 
    * @return The resulting IntensityImage. 
    */
   public IntensityImage or (IntensityImage im1, BinaryImage im2) 
   {
      short[][] im1Matrix = im1.getData();
      short[][] im2Matrix = im2.makeIntensityImage().getData(); 

      return new IntensityImage( or(im1Matrix, im2Matrix) );
   }


   /** 
    * Performs the logical operator OR between two
    * IntensityImages. The two images must be of equal size.
    * @param im1 An IntensityImage.
    * @param im2 Another IntensityImage.
    * @return The resulting IntensityImage. 
    */
   public IntensityImage or (IntensityImage im1, IntensityImage im2) 
   {
      short[][] im1Matrix = im1.getData();
      short[][] im2Matrix = im2.getData(); 

      return new IntensityImage( or(im1Matrix, im2Matrix) );
   }


   /** 
    * Performs the logical operator OR between an IntensityImage and
    * a BinaryImage. The two images must be of equal size. 
    * @param matrix1 The intensityImage 
    * @param matrix2 The BinaryImage 
    * @return The resulting IntensityImage. 
    */
   public short[][] or (short[][] matrix1, short[][] matrix2)
   {
      int height = matrix1.length;
      int width = matrix1[0].length;
      
      short [][] newMatrix = new short[height][width]; 
 
      for (int i=0; i<height; i++) 
         for (int j=0; j<width; j++) 
            newMatrix[i][j] = (short) ((matrix1[i][j]|matrix2[i][j])&255); 
      
      return newMatrix; 
   }


   /**
    * Computes the sum of two IntensityImages on a pixel-by-pixel
    * basis. Each pixel in the resulting image is the sum of the
    * pixels in the same location in the two images beeing added. The
    * resulting image is scaled. 
    * @param im1 An IntensityImage
    * @param im2 Another IntensityImage
    * @return The resulting IntensityImage. 
    */
   public IntensityImage add (IntensityImage im1, IntensityImage im2) 
   {
      short [][] im1Matrix = im1.getData(); 
      short [][] im2Matrix = im2.getData(); 
      return new IntensityImage( add(im1Matrix, im2Matrix) ); 
   }
   
   
   /**
    * Computes the sum of two IntensityImages on a pixel-by-pixel
    * basis. Each pixel in the resulting image is the sum of the
    * pixels in the same location in the two images beeing added. The
    * resulting image is scaled. 
    * @param matrix1 A matrix
    * @param matrix2 Another matrix
    * @return The resulting IntensityImage. 
    */
   public short[][] add (short[][] matrix1, short[][] matrix2) 
   {
      int height = matrix1.length;
      int width = matrix1[0].length;
      short [][] newMatrix = MatrixTools.copy (matrix1);

      for (int i=0; i<height; i++) 
         for (int j=0; j<width; j++) 
            newMatrix[i][j] += matrix2[i][j]; 

      scale(newMatrix); 

      return newMatrix; 
   }

   
   /**
    * Adds a value to each pixel of an IntensityImage. The
    * resulting image is scaled.
    * @param image An IntensityImage.
    * @param c The value that is added to the image.
    * @return The resulting IntensityImage.
    */
   public IntensityImage add (IntensityImage image, short c) 
   {
      return new IntensityImage (add (image.getData(), c));
   }


   /**
    * Adds a value to each element of a short matrix. The
    * resulting matrix is scaled.
    * @param matrix1 A matrix
    * @param c The value that is added to the image.
    * @return The resulting IntensityImage.
    */
   public short[][] add (short[][] matrix1, short c) 
   {
      int height = matrix1.length;
      int width = matrix1[0].length;
      short [][] newMatrix = MatrixTools.copy (matrix1);

      for (int i=0; i<height; i++) 
         for (int j=0; j<width; j++) 
            newMatrix[i][j] += c; 

      scale(newMatrix); 

      return newMatrix; 
   }
   
   
   /** 
    * Multiplies each pixel value in an IntensityImage with a constant value.
    * @param im The IntensityImage
    * @param c  The constant. 
    * @return The resulting IntensityImage. 
    */
   public IntensityImage multiply (IntensityImage im, int c) 
   {
      return new IntensityImage ( multiply(im.getData(), c) );
   }
    

   /** 
    * Multiplies each element of a short matrix with a constant value.
    * @param matrix The matrix. 
    * @param c The constant. 
    * @return The resulting matrix.
    */
   public short[][] multiply (short[][] matrix, int c) 
   {
      int height = matrix.length;
      int width = matrix[0].length;
      
      short [][] newMatrix = MatrixTools.copy(matrix); 

      for (int i=0; i<height; i++) 
         for (int j=0; j<width; j++) 
            newMatrix[i][j] = (short) (newMatrix[i][j]*c); 

      return newMatrix; 
   }

   
   /**
    * Multiplies two IntensityImages on a pixel-by-pixel basis. Each
    * pixel in the resulting image is the product  of the pixels in the
    * same location in the two images beeing multiplied. The resulting
    * image is scaled.
    * @param im1 An IntensityImage
    * @param im2 Another IntensityImage
    * @return The resulting IntensityImage. 
    */
   public IntensityImage multiply (IntensityImage im1, IntensityImage im2) 
   {
      return new IntensityImage(multiply (im1.getData(), im2.getData())); 
   }


   /**
    * Multiplies two short matrices on a pixel-by-pixel basis. Each
    * pixel in the resulting matrix is the product  of the pixels in the
    * same location in the two matrices beeing multiplied. The resulting
    * matrix is scaled.
    * @param matrix1 A short matrix
    * @param matrix2 Another short matrix
    * @return The resulting matrix. 
    */
   public short[][] multiply (short[][] matrix1, short[][] matrix2) 
   {
      short [][] newMatrix = MatrixTools.copy(matrix1); 
      int height = matrix1.length;
      int width = matrix1[0].length;

      for (int i=0; i<height; i++) 
         for (int j=0; j<width; j++) 
            newMatrix[i][j] *= matrix2[i][j]; 

      scale(newMatrix);
 
      return newMatrix;
   }


   /**
    * Takes the and operation between a binary image and of the
    * inversion of another binary image.  This means that the
    * resulting image is the result of setting the pixels of the first
    * image to zero where the pixel of the second image is one.
    * @param im1 A binary image.
    * @param im2 A binary image specifying which pixels should be set to zero.
    * @return The resulting image.
    */
   public BinaryImage andNot (BinaryImage im1, BinaryImage im2) 
   {
      BinaryImage newImage = new BinaryImage (im1);
      BitSet bits1 = newImage.getData();
      BitSet bits2 = im2.getData();
      bits1.andNot(bits2);
      return newImage;
   }

   
   /**
    * Takes the xor operation between to binary images.
    * @param im1 A binary image.
    * @param im2 Another binary image.
    * @return The resulting image.
    */
   public BinaryImage xor (BinaryImage im1, BinaryImage im2) 
   {
      BinaryImage newImage = new BinaryImage (im1);
      BitSet bits1 = newImage.getData();
      BitSet bits2 = im2.getData();
      bits1.xor (bits2);
      return newImage;
   }


    /**
    * Computes the difference of two IntensityImages on a
    * pixel-by-pixel basis. Each pixel in the resulting image is the
    * difference of the pixels in the same location in the two images
    * beeing subtracted. The resulting image is scaled.
    * @param im1 An IntensityImage
    * @param im2 Another IntensityImage
    * @return The resulting IntensityImage. 
    */
   public IntensityImage subtract(IntensityImage im1, IntensityImage im2) 
   {
      return new IntensityImage (subtract(im1.getData(), im2.getData()));
   }


    /**
    * Computes the difference of two short matrices on a element by
    * element basis. Each element in the resulting matrix is the
    * difference of the elements in the same location in the two
    * matrices beeing subtracted. The resulting matrix is scaled.
    * @param matrix1 A short matrix.
    * @param matrix2 Another short matrix.
    * @return The resulting matrix. 
    */
   public short[][] subtract (short[][] matrix1, short[][] matrix2) 
   {
      matrix2 = multiply(matrix2, -1); 

      return add(matrix1, matrix2); 
   }

      
   /** 
    * Scales a matrix of image data. 
    * @param matrix The matrix containing the image data. 
    */
   public void  scale (short[][] matrix)
   {
      if ((option & CUT_LOWER) == CUT_LOWER)
         cutLower (matrix);
      if ((option & CUT_UPPER) == CUT_UPPER)
         cutUpper(matrix);
      if ((option & ABS) == ABS)
         abs(matrix); 
      if ((option & WRAP_UPPER) == WRAP_UPPER) 
         wrapUpper(matrix); 
      if ((option & SUB_MIN) == SUB_MIN)
         subMin(matrix);
      if ((option & SCALE_MAX) == SCALE_MAX)
         scaleMax(matrix); 
   }

   private void  cutLower(short [][] matrix) 
   {
      for (int i = 0; i<matrix.length; i++)
         for (int j=0; j<matrix[0].length; j++) 
            if (matrix[i][j] <0)
               matrix[i][j]=0;
   }

   private void  cutUpper(short [][] matrix) 
   {
       for (int i = 0; i<matrix.length; i++)
         for (int j=0; j<matrix[0].length; j++) 
            if (matrix[i][j] > (L-1))
               matrix[i][j]=(L-1);
   }

   private void  abs(short [][] matrix) 
   {
       for (int i = 0; i<matrix.length; i++)
         for (int j=0; j<matrix[0].length; j++) 
            matrix[i][j] = (short) Math.abs(matrix[i][j]); 
   }

   private void  wrapUpper(short [][] matrix) 
   {
       for (int i = 0; i<matrix.length; i++)
         for (int j=0; j<matrix[0].length; j++) 
            matrix[i][j] = (short)(2*(L-1) - matrix[i][j]);
   }

   private void  subMin(short [][] matrix) 
   {
      short min = findMinimum(matrix); 

      for (int i = 0; i<matrix.length; i++)
         for (int j=0; j<matrix[0].length; j++) 
            matrix[i][j] = (short)(matrix[i][j] - min); 
   }

   private void  scaleMax(short [][] matrix) 
   {
      short max = findMaximum(matrix); 

      for (int i = 0; i<matrix.length; i++)
         for (int j=0; j<matrix[0].length; j++) 
            matrix[i][j] = (short)(matrix[i][j]*(double)(L-1)/max);
   }


   /**
    * Find the minimum of the elements in a short matrix.
    * @param matrix A matrix.
    * @return The minimum.
    */
   public  short findMinimum(short [][] matrix) 
   {
      short min = matrix[0][0]; 
      
      for (int i=0; i < matrix.length; i++) 
         for (int j=0; j<matrix[0].length; j++) 
            if (matrix[i][j] < min) 
               min = matrix[i][j]; 

      return min; 
   }


   /**
    * Find the maximum of the elements in a short matrix.
    * @param matrix A matrix.
    * @return The maximum.
    */
   public short findMaximum(short [][] matrix) 
   {
      short max = matrix[0][0]; 
      
      for (int i=0; i < matrix.length; i++) 
         for (int j=0; j<matrix[0].length; j++) 
            if (matrix[i][j] > max) 
               max = matrix[i][j]; 

      return max; 
   }
    
}
