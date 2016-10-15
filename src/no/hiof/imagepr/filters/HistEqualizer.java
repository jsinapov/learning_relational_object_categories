package no.hiof.imagepr.filters;

import no.hiof.imagepr.*;
import no.hiof.imagepr.features.Histogram;

/**
 * A HistEqualizer performs histogram equalization. This class is
 * adopted to version 2 of the API by Per-Olav Rusås
 *
 * @author David Ellingsen, Roar Hauger, Bjørnar Henriksen, Nils-Edvard Lileng Holene, Ola Oddvar Myhren Juliussen
 */
public class HistEqualizer implements ImageFilter
{
    
    /**
     * Constructor for HistEqualizer.
     */
    public HistEqualizer()
    {
    }
    
    
    /**
     * Performs histogram equalization on a matrix with values between
     * 0 and 255.
     * @param matrix The matrix.
     */
    public short[][] filter(short[][] matrix)
    {

	Histogram histogram = new Histogram(matrix);
	double[] cumNormHist = histogram.calcCumNormHistogram();
	int depth = cumNormHist.length;
	
	int rows = matrix.length;
	int cols = matrix[0].length;

	short[][] equalizedMatrix = new short[rows][cols];
	
	for (int row = 0; row < rows; row++)
	    for (int col = 0; col < cols; col++)
		equalizedMatrix[row][col] = (short)
		    ((depth-1)*cumNormHist[matrix[row][col]] + 0.5);
	
	return equalizedMatrix;
    }

    
    /**
     * Performs histogram equalization on an IntensityImage.
     * @param image An IntensityImage.
     * @return The equalized IntensityImage.
     */
    public Image filter(Image image)
    {
	Image processedImage = null;
	
	if (image instanceof IntensityImage)
	{
	    IntensityImage intensImage = (IntensityImage)image;
	    short[][] matrix = intensImage.getData();
	    short[][] equalizedMatrix = filter(matrix);
	    processedImage = new IntensityImage(equalizedMatrix);
	}
	else
	{
	    System.out.println("Histogram equalization is only "
			       + "implemented for instances of "
			       + "IntensityImage.");
	    processedImage = null;
	}

	return processedImage;
    }

}
