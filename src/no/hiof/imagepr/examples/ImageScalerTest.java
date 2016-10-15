package no.hiof.imagepr.examples;

import no.hiof.imagepr.*;
import no.hiof.imagepr.filters.ImageScaler;
import java.io.*;

public class ImageScalerTest
{
    public static void main(String[] args)
    {

	RGBImage original = null;
	RGBImage small;
	RGBImage large;
	RGBImage stretched;

	// Reads an image
	try
	{
	    original = new RGBImage(
		 "/home/users/jsinapov/brevel/breveIDE_2.4/toolDB/D1/Trial0.0.png");
	}
	catch(IOException ex)
	{
	    System.out.println("Can't read image");
	}

	original.show("Full size");

	// Scale the image
	ImageScaler scaler = new ImageScaler(0.2, ImageScaler.NEAREST);
	small = (RGBImage) scaler.filter(original);

	small.show("Scaled to 20% of original size");

	// Magnifying the small image using nearest neighbour interpolation
	scaler = new ImageScaler(5, ImageScaler.NEAREST);
	large = (RGBImage) scaler.filter(small);
	
	large.show("Magnified using nearest neighbour interpolation");

	scaler = new ImageScaler(5, ImageScaler.BILINEAR);
	large = (RGBImage) scaler.filter(small);
	
	large.show("Magnified using bilinear interpolation");
	
	// Stretching in one direction
	scaler = new ImageScaler(1, 2, ImageScaler.BILINEAR);
	stretched = (RGBImage) scaler.filter(original);
	
	stretched.show("Stretched a factor 2 in horizontal direction");

	System.exit(0);
    }
}
