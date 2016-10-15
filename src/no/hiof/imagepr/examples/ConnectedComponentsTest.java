package no.hiof.imagepr.examples;

import no.hiof.imagepr.features.*;
import no.hiof.imagepr.*;
import java.io.*; 
import java.awt.*;

public class ConnectedComponentsTest {

    private RGBImage colorImage; 
    private IntensityImage grayImage;
    private BinaryImage testImage;

    public ConnectedComponentsTest() {
	colorImage = null;

	String url = "http://www.ia.hiof.no/~por/imageprocAPI/version2/"
	    + "no/hiof/imagepr/pictures/robot1.jpg";
	// Reads data from a JPEG-file: 
	try 
	{
	    colorImage = new RGBImage(url);
	}
	catch (IOException ex )
	{
	    ex.printStackTrace(); 
	}
      
	// Creates an intensity image from a color image: 
	grayImage = new IntensityImage(colorImage);
	grayImage.show("Original image"); 
      
	// Thresholds the image
	testImage = new BinaryImage(grayImage, 220); 
	testImage.show("Thresholded image, T = 220"); 

	// Creates a ConnectedComponents object and gets the labeled image
	ConnectedComponents cc =
	    new ConnectedComponents(testImage, ConnectedComponents.N8,
				    ConnectedComponents.CLASSICAL);  

	cc.showAllComponents(Color.blue,Color.yellow); 

	// The labeled image. This is not used in this example, but
	// may be utilized to find features of each connected component.
	IntensityImage labeledImage = cc.getCompImage();

	System.out.println("Number of components: " + cc.getLabel()); 
            
	System.exit(0); 
    }


    public static void main (String [] args)  {
	ConnectedComponentsTest app = new ConnectedComponentsTest(); 
    }

   
}
