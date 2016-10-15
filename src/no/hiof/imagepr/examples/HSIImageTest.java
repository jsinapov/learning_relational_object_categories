package no.hiof.imagepr.examples;

import no.hiof.imagepr.*;
import no.hiof.imagepr.filters.ImageScaler;
import java.io.*;

public class HSIImageTest
{
    public static void main(String[] args)
    {

	// Creates a color spectrum
	short[][] data = new short[50][256];
	IntensityImage intImage = new IntensityImage(data);
	for (int row = 0; row < 50; row++)
	{
	    for (int col = 0; col < 256; col++)
	    {
		data[row][col] = (short)col;
	    }
	}

	// Set the colormap to HSI
	intImage.setColormap(IntensityImage.HSI);
	intImage.show("The color spectrum");
	

	// Find the HSI components of an image of a robot
	HSIImage image = null;
	String url = "http://www.ia.hiof.no/bildeb/bilder/robot2.jpg";

	// Reads an image
	try
	{
	    image = new HSIImage(new RGBImage(url));
	}
	catch(IOException ex)
	{
	    System.out.println("Can't read image");
	}

	ImageScaler scaler = new ImageScaler(0.5, ImageScaler.NEAREST);
	image = (HSIImage)scaler.filter(image);

	image.show("A robot");

	// Hue, saturation and intensity matrices
	short[][] hue = image.getHue();
	short[][] saturation = image.getSaturation();
	short[][] intensity = image.getIntensity();

	int rows = image.getHeight();
	int cols = image.getWidth();
	
	image.showHue("Hue component");
	image.showSaturation("Saturation component");
	image.showIntensity("Intensity component");

	// Half the saturation
	for (int row = 0; row < rows; row++)
	{
	    for (int col = 0; col < cols; col++)
	    {
		saturation[row][col] = (short)(saturation[row][col]/2);
	    }
	}

	// Adjust intensity so that it doesn't exceed maximum possible
	// value. Is strictly not necessary when adjusting saturation
	// down, as in this case.
	image.adjustIntensity();

	image.show("Half of original saturation");

	System.exit(0);
    }
}
