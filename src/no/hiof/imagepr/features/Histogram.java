package no.hiof.imagepr.features;

import no.hiof.imagepr.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

/**
 * <p>A Histogram represents the histogram of an IntensityImage.  The
 * histogram of an image contains the amount of pixels of each value
 * in the interval 0-255. (This class could easily be extended to
 * accept other intervals of pixel values).
 * 
 * <p> This class is adopted to version 2 of the API by Per-Olav Rusås
 *
 * @author David Ellingsen, Roar Hauger, Bjørnar Henriksen, Nils-Edvard Lileng Holene, Ola Oddvar Myhren Juliussen
 */
public class Histogram
{
    private int depth; 
    private int[] histogram;
    private int sum;

    private int height;
    private int space;
    private int border;
    private double xScale;
    private double yScale;
    

    /**
     * Set the matrix with pixel values and calculate the histogram.
     * @param matrix The matrix
     */
    public Histogram(short[][] matrix)
    {
	depth = 256;
	calcHistogram(matrix);

	// The sum of the elements in the histogram equals the number
	// of elements in the matrix
	sum = matrix.length * matrix[0].length;
    }


    /**
     * Get the histogram as an integer array. Each element contains
     * the number of pixels in the image of the value equal to the
     * index of the element.
     * @return The histogram
     */
    public int[] getHistogram()
    {
	return histogram;
    }


    private void calcHistogram(short[][] matrix)
    {
	histogram = new int[depth];
	int rows = matrix.length;
	int cols = matrix[0].length;

	int v;
	//find the histogram
	for (int row = 0; row < rows; row++)
	{
	    for (int col = 0; col < cols; col++)
	    {
		v = matrix[row][col];
		if (v >= 0 && v < depth)
		    ++histogram[v];
	    }
	}
    }


    /**
     * Calculate and return the normalized histogram for the
     * image. The normalized histogram is represented by an array of
     * 256 doubles. Each element gives the relative amount of pixels
     * in the image of the value equal to the index of the
     * element.
     *
     * @return      The normalized histogram.
     */
    public double[] calcNormHistogram()
    {
	double [] normHistogram = new double[histogram.length];
	
	//Find the normalized histogram by dividing each element of
	//the histogram by sum
	for( int n = 0; n < histogram.length; n++ )
	    normHistogram[n] = (double)histogram[n]/sum;
	
	return normHistogram;
    }


    /**
     * Calculate the cumulative normalized histogram. In this
     * histogram element with index i gives the relative number of
     * pixels having value less than or equal to i.
     * @return The cumulative normalized histogram.
     */
    public double[] calcCumNormHistogram()
    {
	double[] normHist = calcNormHistogram();
	double[] cumNormHist = new double[normHist.length];

	cumNormHist[0] = normHist[0];
	for (int i = 1; i < normHist.length; i++)
	{
	    cumNormHist[i] = cumNormHist[i-1] + normHist[i];
	}

	return cumNormHist;
    }

    
    /**
     * Draw the histogram of the image on an IntensityImage.
     * @param cumulative Set to true for cumulative histogram.
     */
    public IntensityImage drawHistogram(boolean cumulative)
    {
	return drawHistogram(cumulative, 200, 1);
    }


    /**
     * Draw the histogram of the image on an IntensityImage with a
     * given height and spacing between the bars.
     *
     * @param cumulative Set to true for cumulative histogram.
     * @param  height The heigth of the histogram.
     * @param  space  The space between the bars of the histogram.
     * @return An IntensityImage containing the image of the histogram.
     */
    public IntensityImage drawHistogram(boolean cumulative,
					int height, int space)
    {
	border = 30;
	int width = 255 * (1 + space) + 2*border;
	RGBImage image = new RGBImage(height, width);
	BufferedImage bim = image.makeBufferedImage();
	Graphics g = bim.getGraphics();
	
	draw(cumulative, g, 0, 0, height, space);

	image.setBImage(bim);
	
	return new IntensityImage(image);
    }

    
    /**
     * Draw the histogram of the image.
     *
     * @param  g      The Graphics object to draw the histogram on.
     * @param  x0     The horizonal coordinate of the histogram.
     * @param  y0     The vertical coordinate of the histogram.
     * @param  height The heigth of the histogram.
     * @param  space  The space between the bars of the histogram.
     */
    public void draw(boolean cumulative, Graphics g, int x0, int y0,
		     int height, int space)
    {
	double[] normHistogram;
	if (cumulative)
	    normHistogram = calcCumNormHistogram();
	else
	    normHistogram = calcNormHistogram();

	//find max value in histogram
	double max = 0;
	for( int j = 0; j < 256; j++ )
	    if( normHistogram[j] > max )
		max = normHistogram[j];
	
	this.height = height;
	this.space = space;
	this.border = 30;
	this.xScale = 1;
	this.yScale = (height - 2*border) / max;
	int width = 256 + 255*space + 2*border;

	// Draw a white background
	g.setColor(Color.white);
	g.fillRect(0, 0, width, height);
	
	g.setColor(Color.gray);
	g.setFont(new Font("Arial", Font.PLAIN, 9));
	
	// Draw vertical axis
	line(g, 0, 0, 0, max);
	// Draw horizontal axis
	line(g, 0, 0, 255*(1 + space), 0);

	// Text on axes
	DecimalFormat df = new DecimalFormat("0.0");
	g.drawString(df.format(max*100) + "%", xm(-border), ym(max));
	g.drawString("0", xm(0), ym(0) + 20);
	g.drawString("255", xm(256 + 255*space), ym(0) + 20);
	
	//draw bars
	g.setColor(Color.black);
	int x;
	for (int i = 0; i < depth; i++)
	{
	    x = i * (1 + space);
	    if (normHistogram[i] > 0)
		line(g, x, 0, x, normHistogram[i]); 
	}
    }
    

    private void line(Graphics g, double x0, double y0, double x1, double y1)
    {
	g.drawLine(xm(x0), ym(y0), xm(x1), ym(y1));
    }


    private int xm(double x)
    {
	return (int)(border + x * xScale + 0.5);
    }


    private int ym(double y)
    {
	return (int)(height - border - y * yScale + 0.5);
    }

}
