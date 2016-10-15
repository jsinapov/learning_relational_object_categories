package no.hiof.imagepr.tools;

/**
 * The class MatrixTools contains some static methods performing some
 * useful operations on matrices.
 *
 * @author Per-Olav Rus;&aring;s
 */
public class MatrixTools
{
      
   /**
    * Prints the contens of a short matrix to standard out. Useful for
    * debugging purposes on small matrices.
    * @param matrix A matrix to be printed.
    */
   public static void print (short[][] matrix)
   {
      for (int row = 0; row < matrix.length; row++)
      {
         System.out.println();
         for (int col = 0; col < matrix[0].length; col++)
         {
            System.out.print(matrix[row][col] + "\t");
         }
      }
      System.out.println();
   }


   /**
    * Fills the matrix with a value.
    * @param matrix A matrix.
    * @param value A value.
    */
   public static void fill (short[][] matrix, short value)
   {

      for (int row = 0; row < matrix.length; row++)
      {
         for (int col = 0; col < matrix[0].length; col++)
         {
            matrix[row][col] = value;
         }
      }

   }


   /**
    * Creates a copy of a short matrix.
    * @param matrix A matrix to be copied.
    * @return The copy.
    */
   public static short[][] copy (short[][] matrix)
   {
      short[][] copiedMatrix = new short[matrix.length][matrix[0].length];

      for (int row = 0; row < matrix.length; row++)
      {
         for (int col = 0; col < matrix[0].length; col++)
         {
            copiedMatrix[row][col] = matrix[row][col];
         }
      }

      return copiedMatrix;
   }

   /**
    * Creates a copy of a double matrix.
    * @param matrix A matrix to be copied.
    * @return The copy.
    */
   public static double[][] copy (double[][] matrix)
   {
      double[][] copiedMatrix = new double[matrix.length][matrix[0].length];

      for (int row = 0; row < matrix.length; row++)
      {
         for (int col = 0; col < matrix[0].length; col++)
         {
            copiedMatrix[row][col] = matrix[row][col];
         }
      }

      return copiedMatrix;
   }


   /**
    * Find the mean value of the elements in a matrix.
    * @param matrix A matrix.
    * @return the mean value.
    */
   public static double mean (short[][] matrix)
   {
      double sum = 0;
      for (int row = 0; row < matrix.length; row++)
      {
         for (int col = 0; col < matrix[0].length; col++)
         {
            sum += matrix[row][col];
         }
      }
     
      return sum/(matrix.length*matrix[0].length);
   }


    public static short[][] double2short(double[][] matrix, double factor)
    {
	int rows = matrix.length;
	int cols = matrix[1].length;
	short[][] sMat = new short[rows][cols];
	for (int row = 0; row < rows; row++)
	{
	    for (int col = 0; col < cols; col++)
	    {
		sMat[row][col] = (short)(matrix[row][col] * factor + 0.5);
	    }
	}

	return sMat;
    }


    public static short[][] subMatrix(short[][] matrix, int fromRow, int toRow,
				      int fromCol, int toCol)
    {
	int rows = toRow - fromRow + 1;
	int cols = toCol - fromCol + 1;

	short[][] subMat = new short[rows][cols];

	for (int row = 0; row < rows; row++)
	    for (int col = 0; col < cols; col++)
		subMat[row][col] = matrix[fromRow + row][fromCol + col];

	return subMat;
    }
}
