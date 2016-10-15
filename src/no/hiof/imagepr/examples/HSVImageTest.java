package no.hiof.imagepr.examples;

import no.hiof.imagepr.*;
import no.hiof.imagepr.filters.HistEqualizer;
import java.io.*;

public class HSVImageTest
{
    public static void main(String[] args)
    {
	HSVImage image = null;
	String url = "http://www.ia.hiof.no/~por/imageprocAPI/version2/"
	    + "/no/hiof/imagepr/pictures/julebilde.jpg";

	// Read an image
	try
	{
	    image = new HSVImage(new RGBImage(url));
	}
	catch(IOException ex)
	{
	    System.out.println("Can't read image");
	}

	image.show("Original");

	// Perform a gamma transformation of the value component
	double gamma = 0.5;
	short[][] value = image.getValue();
	int rows = image.getHeight();
	int cols = image.getWidth();
	double v;
	
	for (int row = 0; row < rows; row++)
	{
	    for (int col = 0; col < cols; col++)
	    {
		v = value[row][col]/255.0;
		value[row][col] = (short)Math.round(255*Math.pow(v, gamma));
	    }
	}
	
	image.show("After gamma transformation, gamma=0.5");

	System.exit(0);
    }
}
