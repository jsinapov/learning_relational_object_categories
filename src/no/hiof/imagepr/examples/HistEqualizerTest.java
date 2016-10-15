package no.hiof.imagepr.examples;

import no.hiof.imagepr.*;
import no.hiof.imagepr.filters.HistEqualizer;
import no.hiof.imagepr.features.Histogram;
import no.hiof.imagepr.tools.MatrixTools;

import java.io.*;
import java.awt.Color;

/**
 * An example which shows how to utilze an instance of HistEqualizer
 * to perform histogram equalization on an IntensityImage.
 */
public class HistEqualizerTest
{
    public static void main(String[] args)
    {
       
	IntensityImage image = null;
	IntensityImage equalizedImage;

	// Reads an image published on 
	// http://moon.ouhsc.edu/kfung/JTY1/Com/Com306-2-Diss.htm
	String url =
	    "http://moon.ouhsc.edu/kfung/JTY1/Com/ComImage/Com306-2-X-ray.gif";

	try
	{
	    image = new IntensityImage(new RGBImage(url));
	}
	catch(Exception ex)
	{
	    System.out.println("Can't find the image");
	    System.exit(0);
	}

	// Show the image
	image.show("An X-ray of teeth");

	// A sub image
	short[][] subMat = MatrixTools.subMatrix(image.getData(),
						120, 300, 270, 450);
	image = new IntensityImage(subMat);
	image.show("A sub image of the X-ray");

	// A histogram
	Histogram hist = new Histogram(image.getData());
	hist.drawHistogram(false).show("Histogram");
	
	// Histogram equalization
	HistEqualizer equalizer = new HistEqualizer();
	equalizedImage = (IntensityImage)equalizer.filter(image);
	equalizedImage.show("Equalized image");

	// Histogram of the histogram equalized image. Try to change
	// "false" to "true" and observe the cumulative histogram of
	// the equalized image. What do you observe?
	Histogram histEqualized = new Histogram(equalizedImage.getData());
	histEqualized.drawHistogram(false).
	    show("Histogram of equalized image");

	System.exit(0);
    }

}
