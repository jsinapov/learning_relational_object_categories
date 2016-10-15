package no.hiof.imagepr;

import no.hiof.imagepr.filters.ImageScaler;

import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.*;

/**
 * <p>IntensityImage is a class for intensity images (gray scale
 * images) which is supposed to be easier to use than the classes for
 * image processing in the Java SDK API. The IntensityImage class
 * contains one two-dimensional short array for the intensity values.
 * 
 * <p>In order to be viewed properly, the intensity values must be set
 * in the interval from 0 to 255. As default an IntensityImage is
 * shown with a gray scale colormap, which means that an intensity of
 * 0 is shown as black and an intensity of 255 is shown as white. It
 * is possible to change which colormap to use. Several predifined
 * colormaps are defined as static matrices.
 *
 * @author Per-Olav Rus&aring;s
 * 
 * @see no.hiof.imagepr.RGBImage
 * @see no.hiof.imagepr.BinaryImage
 * @see no.hiof.imagepr.HSIImage
 */
public class IntensityImage implements no.hiof.imagepr.Image, Serializable
{

    /** The matrix with pixel values */
    protected short[][] data;

    /** The current mapping between intensity values and RGB colors */
    protected short[][] colormap;

    /** Colormap which shows the image as gray. */
    public static short[][] GRAY;

    /** Colormap which shows the image as an inverted gray image. */
    public static short[][] INVGRAY;

    /** Colormap which shows the image in shades of red. */
    public static short[][] RED;

    /** Colormap which shows the image in shades of green. */
    public static short[][] GREEN;

    /** Colormap which shows the image in shades of blue. */
    public static short[][] BLUE;

    /** Colormap which uses the HSI color model. */
    public static short[][] HSI;
    
    /**
     * Colormap which shows an intensity of 0 as blue and an intensity
     * of 255 as red.
     */
    public static short[][] TEMP;

    /**
     * Colormap which shows an intensity of 0 as blue and an intensity
     * of 255 as white.
     */
    public static short[][] COOL;
   
    static
    {
	GRAY = makeColormap(new Color (0, 0, 0), new Color (255, 255, 255));
	INVGRAY = makeColormap(new Color (255, 255, 255), new Color (0, 0, 0));
	RED = makeColormap(new Color (0, 0, 0), new Color (255, 0, 0));
	GREEN = makeColormap(new Color (0, 0, 0), new Color (0, 255, 0));
	BLUE = makeColormap(new Color (0, 0, 0), new Color (0, 0, 255));
	TEMP = makeColormap(new Color (0, 0, 255), new Color (255, 0, 0));
	COOL = makeColormap(new Color (0, 0, 255), new Color (255, 255, 255));
	HSI = makeHSImap();
    }


    /**
     * Constructor that creates an empty IntensityImage.
     */
    public IntensityImage()
    {
	setColormap(GRAY);
    }


    /**
     * Constructor that creates an IntensityImage with given intensity
     * data.
     * @param data Matrix of intensity data.
     */
    public IntensityImage(short[][] data)
    {
	this();
	this.data = data;
    }


    /**
     * Constructor that creates an IntensityImage with all pixel
     * values set to zero.
     * @param height The height of the image
     * @param width The width of the image
     */
    public IntensityImage(int height, int width)
    {
	this();
	data = new short[height][width];
    }
    
    /**
     * Returns the value of the corresponding pixel
     * @param height_index The height index
     * @param width_index The width index
     */
    public short getValueAt(int height_index, int width_index){
    	return data[height_index][width_index];
    }


    /**
     * Constructor that creates an IntensityImage by reading data from
     * a file. The format of the file must be as produced by the
     * save-method of this class.
     * @param filename The name of the file.
     */
    public IntensityImage(String filename) throws IOException
    {
	this();
	load(filename);
    }


    /**
     * Constructor that copies another IntensityImage.
     * @param otherImage Original image.
     */
    public IntensityImage(IntensityImage otherImage)
    {
	this.data = otherImage.data;
	this.colormap = otherImage.colormap;
    }


    /**
     * A constructor that creates an IntensityImage from a RGBImage by
     * letting the gray scale intensity be the average of the red,
     * green and blue intensities.
     * @param rgbImage Original image.
     */
    public IntensityImage(RGBImage rgbImage)
    {
	this();

	int rows = rgbImage.getHeight();
	int cols = rgbImage.getWidth();

	data = new short[rows][cols];

	short[][] red = rgbImage.getRed();
	short[][] green = rgbImage.getGreen();
	short[][] blue = rgbImage.getBlue();

	int m;
	for (int row = 0; row < rows; row++)
	{
	    for (int col = 0; col < cols; col++)
	    {
		m = (red[row][col] + green[row][col] + blue[row][col])/3;
		data[row][col] = (short) m;
	    }
	}

    }


    /**
     * Creates a bufferedImage of the intensityImage using the current
     * colormap.
     * @return a BufferedImage version of the Image
     */
    public BufferedImage makeBufferedImage()
    {
	return makeRGBImage().makeBufferedImage();
    }


    /**
     * Shows the IntensityImage in a modal window.
     * @param title The title on top of the window.
     */
    public void show(String title)
    {
	makeRGBImage().show(title);
    }

   
    /**
     * Shows the HSIImage in a modal window.
     */
    public void show()
    {
	show("");
    }


    /**
     * Shows the IntensityImage scaled with use of nearest neighbor
     * interpolation.  This method utilizes an ImageScaler to perform
     * the scaling.
     * @param scale The scale.
     * @param title The title on top of the window.
     */
    public void show(String title, double scale)
    {
	makeRGBImage().createScaledImage(scale,
					 ImageScaler.NEAREST).show(title);
    }


    /**
     * Shows the IntensityImage scaled with use of bilinear
     * interpolation. This method utilizes an ImageScaler to perform
     * the scaling.
     * @param scale The scale.
     * @param title The title on top of the window.
     */
    public void showBL(double scale, String title)
    {
	makeRGBImage().createScaledImage (scale, ImageScaler.BILINEAR).
	    show(title);
    }


    /**
     * Draws the image to a Graphics object at a given position.
     * @param g The Graphics object to draw on.
     * @param x The horizontal position of the image.
     * @param y The vertical position of the image.
     */
    public void draw(Graphics g, int x, int y)
    {
	makeRGBImage().draw (g, x, y);
    }


    /**
     * Draws the image to a Graphics object at a given position with a
     * given scale and rotation.
     * @param g The Graphics object to draw on.
     * @param x The horizontal position of the image.
     * @param y The vertical position of the image.
     * @param scaleX The scale in x-direction. 1.0 means unscaled.
     * @param scaleY The scale in y-direction. 1.0 means unscaled.
     * @param rotateAngle Rotation of the image in degrees (0-360).
     */
    public void draw(Graphics g, int x, int y, double scaleX, double scaleY,
		      double rotateAngle)
    {
	makeRGBImage().draw (g, x, y, scaleX, scaleY, rotateAngle);
    }


    /**
     * Makes a RGBImage of the IntensityImage using the current
     * colormap.
     * @return A RGBImage representation of the IntensityImage.
     */      
    public RGBImage makeRGBImage()
    {
	int rows = data.length;
	int cols = data[0].length;

	short[][] red = new short[rows][cols];
	short[][] green = new short[rows][cols];
	short[][] blue = new short[rows][cols];

	for (int row = 0; row < rows; row++)
	{
	    for (int col = 0; col < cols; col++)
	    {
		red[row][col] = colormap[data[row][col]%256][0];
		green[row][col] = colormap[data[row][col]%256][1];
		blue[row][col] = colormap[data[row][col]%256][2];
	    }
	}

	RGBImage rgbImage = new RGBImage (red, green, blue);
	//	rgbImage.setAlwaysColorMapping(withColors());
      
	return rgbImage;
    }


    /**
     * Sets the current colormap of the IntensityImage.
     * @param colormap The colormap.
     */
    public void setColormap(short[][] colormap)
    {
	this.colormap = colormap;
    }

   
    /**
     * Calculates and sets the colormap of the IntensityImage.
     * @param minColor Color which represent 0.
     * @param maxColor Color which represent 255.
     */
    public void setColormap(Color minColor, Color maxColor)
    {
	setColormap(makeColormap (minColor, maxColor));
    }


    /**
     * Calculates a linear colormap.
     * @param minColor Color which represent 0.
     * @param maxColor Color which represent 255.
     * @return The colormap.
     */ 
    public static short[][] makeColormap(Color minColor, Color maxColor)
    {
	short[][] map = new short[256][3]; 

	map[0][0] = (short)minColor.getRed();
	map[0][1] = (short)minColor.getGreen();
	map[0][2] = (short)minColor.getBlue();

	map[255][0] = (short)maxColor.getRed();
	map[255][1] = (short)maxColor.getGreen();
	map[255][2] = (short)maxColor.getBlue();

	for (int i = 1; i < 255; i++)
	{
	    map[i][0] = (short)(map[0][0] +
				(map[255][0] - map[0][0]) * i/255.0);
	    map[i][1] = (short)(map[0][1] +
				(map[255][1] - map[0][1]) * i/255.0);
	    map[i][2] = (short)(map[0][2] +
				(map[255][2] - map[0][2]) * i/255.0);
	}

	return map;
    }


    /**
     * Returns the matrix of intensity data.
     * @return The intensity matrix.
     */
    public short[][] getData()
    {
	return data;
    }


    /**
     * Sets the matrix of intensity data. Does not copy the data.
     * @param data The matrix of intensity data.
     */
    public void setData(short[][] data)
    {
	this.data = data;
    }


    /**
     * The width of the image.
     * @return The width.
     */
    public int getWidth()
    {
	return data[0].length;
    }

   
    /**
     * The height of the image.
     * @return The width.
     */
    public int getHeight()
    {
	return data.length;
    }

   
    /**
     * Saves the image to a binary file.
     * @param filename The filename.
     */
    public void save(String filename) throws IOException
    {
      
	DataOutputStream dataWriter =
	    new DataOutputStream (new FileOutputStream (filename));
    
	int rows = getHeight();
	int cols = getWidth();

	dataWriter.writeShort ((short)rows);
	dataWriter.writeShort ((short)cols);

	byte b;

	for (int row = 0; row < rows; ++row)
	{
	    for (int col = 0; col < cols; ++col)
	    {
		dataWriter.writeByte ((byte) (data[row][col]-128));
	    }
	}
      
	dataWriter.close();
    }


    /**
     * Loads the image from a file. The format of the file must be as
     * produced by the save-method of this class.
     * @param filename The filename.
     */ 
    public void load(String filename) throws IOException
    {
	DataInputStream dataReader =
	    new DataInputStream (new FileInputStream (filename));

	int rows = dataReader.readShort();
	int cols = dataReader.readShort();

	data = new short[rows][cols];

	for (int row = 0; row < rows; ++row)
	{
	    for (int col = 0; col < cols; ++col)
	    {
		data[row][col] = (short) (128+dataReader.readByte());
	    }
	}
      
	dataReader.close();
    }


    /**
     * Returns true if the image uses a colormap which uses other
     * colors than gray.
     * @return Shown with colors.
     */
    public boolean withColors()
    {
	int sumDiff = 0;

	for (int i = 0; i < colormap.length; i++)
	{
	    sumDiff += Math.abs (colormap[i][0] - colormap[i][1]) +
		Math.abs (colormap[i][0] - colormap[i][2]);
	}

	return (sumDiff != 0);
    }


    /**
     * Returns a description of the image.
     * @return A description.
     */
    public String toString()
    {
	return "IntensityImage-object:" +
	    "\n\tWidth:  " + getWidth() +
	    "\n\tHeight: " + getHeight();
    }


    /**
     * Creates a scaled image. This method utilizes an ImageScaler to
     * perform the scaling. The parameter interpolType may be one of
     * the constants Image.NEAREST for nearest neighbour
     * interpolation or Image.BILINEAR for bilinear interpolation.
     * @param scale The scale
     * @param interpolType The type of interpolation
     */    
    public Image createScaledImage(double scale, int interpolType)
    {
	ImageScaler scaler = new ImageScaler(scale, interpolType);
	return (IntensityImage)scaler.filter(this);
    }

    
    private static short[][] makeHSImap()
    {
	short[][] hue = new short[256][1];
	short[][] sat = new short[256][1];
	short[][] intens = new short[256][1];
	for (int i = 0; i < 256; i++)
	{
	    hue[i][0] = (short)i;
	    sat[i][0] = 255;
	    intens[i][0] =
		(short)Math.round(255 * HSIImage.maxIntensity(i/255.0, 1.0)); 
	}
	
	HSIImage hsiim = new HSIImage(hue, sat, intens);
	RGBImage rgbim = hsiim.makeRGBImage();
	short[][] red = rgbim.getRed();
	short[][] green = rgbim.getGreen();
	short[][] blue = rgbim.getBlue();
	
	short[][] colormap = new short[256][3];
	for (int i = 0; i < 256; i++)
	{
	    colormap[i][0] = red[i][0];
	    colormap[i][1] = green[i][0];
	    colormap[i][2] = blue[i][0];
	}

	return colormap;
    }

}
   
