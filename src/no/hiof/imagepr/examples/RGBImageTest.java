package no.hiof.imagepr.examples;

import no.hiof.imagepr.*;
import java.io.*;

public class RGBImageTest
{
    public static void main(String[] args)
    {

	RGBImage original = null;
	RGBImage changed;

        String url = "http://www.ia.hiof.no/~por/perolavr.jpg";

	// Reads an image
	try
	{
	    original = new RGBImage(url);
	}
	catch(IOException ex)
	{
	    System.out.println("Can't read image");
	}

	original.show("Original");

	// Red, green and blue matrices
	short[][] red = original.getRed();
	short[][] green = original.getGreen();
	short[][] blue = original.getBlue();

	int rows = original.getHeight();
	int cols = original.getWidth();
	
	// Mark red where blue is the dominating color
	for (int row = 0; row < rows; row++)
	{
	    for (int col = 0; col < cols; col++)
	    {
		if (blue[row][col] > red[row][col] &&
		    blue[row][col] > green[row][col])
		{
		    red[row][col] = 255;
		    blue[row][col] = 0;
		    green[row][col] = 0;
		}
	    }
	}

	original.show();

	System.exit(0);
    }
}
