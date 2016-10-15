package no.hiof.imagepr.examples;

import no.hiof.imagepr.*;
import no.hiof.imagepr.filters.HistEqualizer;
import no.hiof.imagepr.features.Histogram;
import no.hiof.imagepr.tools.MatrixTools;

import java.io.*;
import java.awt.Color;

/**
 * An example of histogram equalization of a test image.
 */
public class HistEqualizerTest2
{
    public static void main(String[] args)
    {
	// Create a test image.
	IntensityImage image = new IntensityImage(100, 400);
	short[][] data = image.getData();
	
	for (int row = 0; row < 100; row++)
	{
	    for (int col = 0; col < 100; col++)
	    {
		data[row][col] = 0;
		data[row][col + 100] = 25;
		data[row][col + 200] = 50;
		data[row][col + 300] = 75;
	    }
	}

	image.show("The original image");

	// Find the histogram
	Histogram hist = new Histogram(data);
	hist.drawHistogram(false).show("Histogram of original image");

	// Histogram equalize image
	HistEqualizer histeq = new HistEqualizer();
	IntensityImage histeqImage = (IntensityImage)histeq.filter(image);
	
	histeqImage.show("Histogram equalized image");

	// Histogram of equalized image.
	hist = new Histogram(histeqImage.getData());
	hist.drawHistogram(false).show("Histogram of equalized image");

	System.exit(0);
    }

}
