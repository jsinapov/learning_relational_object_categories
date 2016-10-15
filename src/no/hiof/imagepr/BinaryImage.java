package no.hiof.imagepr;

import no.hiof.imagepr.filters.ImageScaler;

import java.util.BitSet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * <p>BinaryImage is a class for Binary images (black and white
 * images). The BinaryImage class contains a Bitset containing the
 * pixel values. There is one bit, which is 0 or 1 (or equally false
 * or true) for each pixel.
 * 
 * @author Per-Olav Rus&aring;s
 * 
 * @see no.hiof.imagepr.IntensityImage
 * @see no.hiof.imagepr.RGBImage
 * @see no.hiof.imagepr.HSIImage
 */
public class BinaryImage implements no.hiof.imagepr.Image, Serializable
{
    protected int width;
    protected int height;
    protected BitSet data;

    protected Color zeroColor;
    protected Color oneColor;


    /**
     * Constructor for an empty BinaryImage.
     */
    public BinaryImage()
    {
	zeroColor = new Color(0, 0, 0);
	oneColor = new Color(255, 255, 255);
	
    }


    /**
     * Constructs a BinaryImage with a given height and width.
     * @param height The height.
     * @param width The width.
     */
    public BinaryImage(int height, int width)
    {
	this();
	this.height = height;
	this.width = width;
	data = new BitSet(height*width);
    }
   

    /**
     * Construcs a BinaryImage by copying another BinaryImage.
     * @param otherImage Another BinaryImage.
     */
    public BinaryImage(BinaryImage otherImage)
    {
	this.width = otherImage.width;
	this.height = otherImage.height;
	this.data = (BitSet) otherImage.data.clone();
	this.zeroColor = otherImage.zeroColor;
	this.oneColor = otherImage.oneColor;
    }
   
    public int getBitSetSize(){
    	
    	return data.size();
    }
    
    public int getArea(){
    	
    	return data.cardinality();
    }

    /**
     * Construcs a BinaryImage with an IntensityImage as a basis. The
     * pixel value of the BinaryImage is set to true if the intensity
     * of the IntensityImage is greater or equal to a given
     * threshold. Otherwise the pixel value are set to false.
     * @param original The original IntensityImage
     * @param threshold The threshold
     */
    public BinaryImage(IntensityImage original, int threshold)
    {
	this();
	this.height = original.getHeight();
	this.width = original.getWidth();
	data = new BitSet(height*width);

	short[][] intensity = original.getData();

	for (int row = 0; row < height; row++)
	    for (int col = 0; col < width; col++)
		setValueAt(row, col, intensity[row][col] >= threshold);

    }
   

    /**
     * Gets the value at a given pixel. The row and column numbering
     * starts at zero.
     * @param row The row-number of the pixel.
     * @param col The column-number of the pixel.
     * @return true if the pixel's bit is 1 otherwhise false.
     */
    public boolean getValueAt(int row, int col)
    {
	int index = row*width + col;
	return data.get(index);
    }


    /**
     * Sets the value at a given pixel as true or false (representing 1 or 0).
     * @param row The row-number of the pixel.
     * @param col The column-number of the pixel.
     */
    public void setValueAt(int row, int col, boolean value)
    {
	int index = row*width + col;
	data.set(index, value);
    }


    /**
     * Shows the BinaryImage in a modal window.
     */
    public void show()
    {
	makeRGBImage().show();
    }


    /**
     * Shows the BinaryImage in a modal window.
     * @param title The title on top of the window.
     */
    public void show(String title)
    {
	makeRGBImage().show(title);
    }


    public void show(String title, double scale)
    {
	makeRGBImage().show(title, scale);
    }


    /**
     * Draw the image to a Graphics object at a given position.
     * @param g The Graphics object to draw on.
     * @param x The horizontal position of the image.
     * @param y The vertical position of the image.
     */
    public void draw(Graphics g, int x, int y)
    {
	makeRGBImage().draw(g, x, y);
    }

    /**
     * Draw the image to a Graphics object at a given position with a
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
	makeRGBImage().draw(g, x, y, scaleX, scaleY, rotateAngle);
    }

    /**
     * Make a RGBImage of the BinaryImage
     * @return a RGBImage representation of the BinaryImage
     */     
    public RGBImage makeRGBImage()
    {

	short[][] red = new short[height][width];
	short[][] green = new short[height][width];
	short[][] blue = new short[height][width];

	int index;
	short[] oneColors = {(short)oneColor.getRed(),
			     (short)oneColor.getGreen(),
			     (short)oneColor.getBlue()};
	short[] zeroColors = {(short)zeroColor.getRed(),
			      (short)zeroColor.getGreen(),
			      (short)zeroColor.getBlue()};

	short[] c;

	for (int row = 0; row < height; row++)
	{
	    for (int col = 0; col < width; col++)
	    {
		index = row*width + col;

		if (data.get(index))
		    c = oneColors;
		else
		    c = zeroColors;

		red[row][col] = c[0];
		green[row][col] = c[1];
		blue[row][col] = c[2];
	    }
	}
         
	return new RGBImage (red, green, blue);
    }


    /**
     * Make a IntensityImage of the BinaryImage
     * @return An IntensityImage representation of the BinaryImage
     */     
    public IntensityImage makeIntensityImage()
    {
	return makeIntensityImage((short)0, (short)255);
    }

      
    /**
     * Make a IntensityImage of the BinaryImage
     * @param zeroValue Value in IntensityImage corresponding to binary 0.
     * @param oneValue Value in IntensityImage corresponding to binary 1.
     * @return An IntensityImage representation of the BinaryImage
     */     
    public IntensityImage makeIntensityImage(short zeroValue, short oneValue)
    {

	short[][] grayData = new short[height][width];

	int index;

	for (int row = 0; row < height; row++)
	{
	    for (int col = 0; col < width; col++)
	    {
		index = row*width + col;

		if (data.get(index))
		    grayData[row][col] = oneValue;
		else
		    grayData[row][col] = zeroValue;
	    }
	}


	IntensityImage intensIm = new IntensityImage(grayData);
	intensIm.setColormap(zeroColor, oneColor);
         
	return intensIm;
    }
      
   
    /**
     * Sets the color of the pixels whith value zero.
     * @param c The color.
     */
    public void setZeroColor(Color c)
    {
	zeroColor = c;
    }


    /**
     * Sets the color of the pixels whith value one.
     * @param c The color.
     */
    public void setOneColor(Color c)
    {
	oneColor = c;
    }


    /**
     * Sets the BitSet of the BinaryImage
     * @param data The BitSet.
     */
    public void setData(BitSet data)
    {
	this.data = data;
    }


    /**
     * Get the width of the BinaryImage.
     * @return The width.
     */
    public int getWidth()
    {
	return width;
    }

   
    /**
     * Get the height of the BinaryImage.
     * @return height.
     */
    public int getHeight()
    {
	return height;
    }

   
    /**
     * Saves the image to a binary file.
     * @param filename The filename.
     */
    public void save(String filename) throws IOException
    {
      
	ObjectOutputStream objWriter =
	    new ObjectOutputStream(new FileOutputStream (filename));
    
	objWriter.writeObject(this);
	objWriter.close();
    }


    /**
     * Loads the image from a file. The format of the file must be as
     * produced by the save-method of this class.
     * @param filename The filename.
     */
    public void load(String filename) throws IOException
    {
	ObjectInputStream objReader =
	    new ObjectInputStream(new FileInputStream (filename));

	BinaryImage otherImage = null;

	try
	{
	    otherImage = (BinaryImage)objReader.readObject();
	}
	catch (ClassNotFoundException ex)
	{
	    ex.printStackTrace();
	    return;
	}
      
	objReader.close();
      
	this.width = otherImage.width;
	this.height = otherImage.height;
	this.data = otherImage.data;
	this.zeroColor = otherImage.zeroColor;
	this.oneColor = otherImage.oneColor;
    }

   
    /**
     * Gets the BitSet width the image data.
     * @return The image data.
     */
    public BitSet getData()
    {
	return data;
    }


    public BufferedImage makeBufferedImage()
    {
	return makeRGBImage().makeBufferedImage();
    }


    /**
     * Returns a description of the image.
     * @return A description.
     */
    public String toString()
    {
	return "BinaryImage-object:" +
	    "\n\tWidth:  " + getWidth() +
	    "\n\tHeight: " + getHeight();
    }


    /**
     * Creates a scaled image. This method utilizes an ImageScaler to
     * perform the scaling. The parameter interpolType is unused for
     * this type of image. Nearest neighbour interpolation is the only
     * possibility.
     * @param scale The scale
     * @param interpolType The type of interpolation (unused)
     */    
    public Image createScaledImage(double scale, int interpolType)
    {
	ImageScaler scaler = new ImageScaler(scale, interpolType);
	return (BinaryImage)scaler.filter(this);
    }


}
   
