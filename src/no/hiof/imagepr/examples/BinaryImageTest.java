package no.hiof.imagepr.examples;

import no.hiof.imagepr.*;
import java.io.*;
import java.awt.Color;

/**
 * In this example an X-ray image is read from the internet and
 * thresholded. The thresholded image is represented by a
 * BinaryImage. See also IntensityImageTest.
 */
public class BinaryImageTest
{
    public static void main(String[] args)
    {
       
        BinaryImage binImage = null;
	IntensityImage intensImage = null;

	// Reads an image published on 
	// http://moon.ouhsc.edu/kfung/JTY1/Com/Com306-2-Diss.htm
	String url =
	    "http://moon.ouhsc.edu/kfung/JTY1/Com/ComImage/Com306-2-X-ray.gif";

	try
	{
	    // First a RGBImage is created and then the IntensityImage
	    // is created by taking the mean of the red, green and
	    // blue component of the RGBImage.
	    intensImage = new IntensityImage(new RGBImage(url));
	}
	catch(Exception ex)
	{
	    System.out.println("Can't find the image");
	    System.exit(0);
	}

	// Constructs a BinaryImage by thresholding the IntensityImage.
	int t = 210;
	binImage = new BinaryImage(intensImage, t);

	// Show the image
	binImage.show("Thresholded X-ray of teeth");

	System.exit(0);
    }
}
