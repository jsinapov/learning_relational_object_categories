package no.hiof.imagepr.examples;

import no.hiof.imagepr.*;
import java.io.*;
import java.awt.Color;

/**
 * <p>In this example an X-ray image is red from the internet and
 * inverted. The image is represented by an IntensityImage, which has
 * a matrix (two dimentional array) of short values with the
 * intensities in each pixel. The short type has 16 bits, but only the
 * lowest 8 bits (values 0-255) will have effect when visualizing the
 * image.
 * 
 * <p>The image is visualized using different colormaps.
 */
public class IntensityImageTest
{
    public static void main(String[] args)
    {
       
	IntensityImage image = null;

	// Reads an image published on 
	// http://moon.ouhsc.edu/kfung/JTY1/Com/Com306-2-Diss.htm
	String url =
	    "http://moon.ouhsc.edu/kfung/JTY1/Com/ComImage/Com306-2-X-ray.gif";

	try
	{
	    // First a RGBImage is created and then the IntensityImage
	    // is created by taking the mean of the red, green and
	    // blue component of the RGBImage.
	    image = new IntensityImage(new RGBImage(url));
	}
	catch(Exception ex)
	{
	    System.out.println("Can't find the image");
	    System.exit(0);
	}

	// Show the image
	image.show("An X-ray of teeth");

	// Using other colormaps than the gray default map, to map the
	// intensity into RGB colors.

	// 1. A "temperature" colormap. High intensity gives red color
	//    and low intensity blue.
	image.setColormap(IntensityImage.TEMP);
	image.show("Temperature colormap");

	// 2. A yellow colormap. High intensity gives yellow color and
	//    low intensity black.
	image.setColormap(Color.black, Color.yellow);
	image.show("Yellow colormap");

	// Invert the image
	short[][] intensity = image.getData();
	for (int row = 0; row < image.getHeight(); row++)
	    for (int col = 0; col < image.getWidth(); col++)
		intensity[row][col] = (short)(255 - intensity[row][col]);

	// Show the inverted image
	image.show("Inverted X-ray image with yellow colormap");

	System.exit(0);
    }
}
