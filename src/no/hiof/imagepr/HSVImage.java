package no.hiof.imagepr;

import no.hiof.imagepr.tools.*;
import no.hiof.imagepr.filters.ImageScaler;

import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.Serializable;


/**
 * <p>An HSVImage is an image using the HSV colour model, which means
 * that each pixel has hue, saturation and value components. This
 * class contains methods for setting and getting the HSV components,
 * for transformation between HSV and RGB colour models and for
 * displaying the image and it's HSV components.
 *
 * <p>All of the components (hue, saturation and value) must be in
 * the interval 0 to 255.
 *
 * <p>Transforming formulas between the RGB and HSV color models are
 * obtained from <a
 * href="http://www.easyrgb.com/math.html">www.easyrgb.com/math.html</a>
 *
 * @see no.hiof.imagepr.RGBImage
 * @see no.hiof.imagepr.IntensityImage
 * @see no.hiof.imagepr.BinaryImage
 * @see no.hiof.imagepr.HSIImage
 * @author Per-Olav Rus&aring;s
 */
public class HSVImage implements no.hiof.imagepr.Image, Serializable
{
    /**
     * The maximum number of levels in each matrix.
     */
    public static final int L = 256;

    /**
     * The Matrix with the hue component of the image.
     */
    protected short[][] hue;

    /**
     * The Matrix with the saturation component of the image.
     */
    protected short[][] saturation;

    /**
     * The Matrix with the value component of the image.
     */
    protected short[][] value;

    /**
     * Creates an empty HSVImage object.
     */
    public HSVImage()
    {
    }

   
    /**
     * Creates an HSVImage by converting a RGBImage.
     * @param image RGBImage to be converted.
     */
    public HSVImage(RGBImage image)
    {
	this();
	fromRGBImage(image);
    }


    /**
     * A constructor that sets the HSV matrices
     * @param hue The hue component of the image.
     * @param saturation The saturation component of the image.
     * @param value The value component of the image.
     */
    public HSVImage(short[][] hue, short[][] saturation,
		    short[][] value)
    {
	this();
	this.hue = hue;
	this.saturation = saturation;
	this.value = value;
    }
    

    /**
     * A copy constructor which copies another HSVImage.
     * @param otherImage Another HSVImage.
     */
    public HSVImage(HSVImage otherImage)
    {
	hue = MatrixTools.copy(otherImage.hue);
	saturation = MatrixTools.copy(otherImage.saturation);
	value = MatrixTools.copy(otherImage.value);
    }


    /**
     * Gets the reference to the matrix with the hue component
     * of the image. This method does not make a copy of the matrix,
     * but returns the reference to the HSVImage's hue matrix. This
     * means that the returned reference can be used to manipulate the
     * hue of the HSVImage.
     * @return The hue matrix.
     */
    public short[][] getHue(){ return hue; }


    /**
     * Gets the reference to the matrix with the saturation
     * component of the image. This method does not make a copy of the
     * matrix, but returns the reference to the HSVImage's saturation
     * matrix. This means that the returned reference can be used to
     * manipulate the saturation of the HSVImage.
     * @return The saturation matrix.
     */
    public short[][] getSaturation(){ return saturation; }


    /**
     * Gets the reference to the matrix with the value
     * component of the image. This method does not make a copy of the
     * matrix, but returns the reference to the HSVImage's value
     * matrix. This means that the returned reference can be used to
     * manipulate the value of the HSVImage.
     * @return The value colour matrix.
     */
    public short[][] getValue(){ return value; }

  
    /**
     * Sets the hue component of the image.
     * @param hue Matrix with the hue values.
     */
    public void setHue(short[][] hue)
    {
	this.hue = hue;
    }


    /**
     * Sets the saturation component of the image.
     * @param saturation Matrix with the saturation values.
     */
    public void setSaturation(short[][] saturation)
    {
	this.saturation = saturation;
    }


    /**
     * Sets the value component of the image.
     * @param value Matrix with the value colour values.
     */
    public void setValue(short[][] value)
    {
	this.value = value;
    }
   

    /**
     * The width of the image. If some of the matrices are missing, the
     * method returns -1.
     * @return The width of the image if all the matrices exist. 
     */ 
    public int getWidth()
    {
	if (hue != null && saturation != null && value != null)
	    return hue[0].length;
	else
	    return -1;
    }


    /**
     * The height of the image. If some of the matrixes are missing,
     * the method returns -1.
     * @return The height of the image if all the matrices exist. 
     */ 
    public int getHeight()
    {
	if (hue != null && saturation != null && value != null)
	    return hue.length;
	else
	    return -1;
    }

   
    /**
     * calculats the HSV components from the RGB components of a
     * RGBImage.
     * @param image The RGB source image.
     */
    public void fromRGBImage(RGBImage image)
    {
	short[][] red = image.getRed();
	short[][] green = image.getGreen();
	short[][] blue = image.getBlue();

	int rows = image.getHeight();
	int cols = image.getWidth();

	this.hue = new short[rows][cols];
	this.saturation = new short[rows][cols];
	this.value = new short[rows][cols];
      
	short r, g, b;
	int rgbmax, rgbmin, dmax;
	double h, s, dr, dg, db;

	double[] hsv;

	for (int row = 0; row < rows; row++)
	{
	    for (int col = 0; col < cols; col++)
	    {
		hsv = rgb2hsv(red[row][col], green[row][col],
			      blue[row][col], L);
		hue[row][col] = (short)Math.round(hsv[0]*(L-1));
		saturation[row][col] = (short)Math.round(hsv[1]*(L-1));
		value[row][col] = (short)Math.round(hsv[2]*(L-1));
	    }
	}
    }


    public static double[] rgb2hsv(int red, int green, int blue, int depth)
    {
	int rgbmin = Math.min(red, Math.min(green, blue));
	int rgbmax = Math.max(red, Math.max(green, blue));
	int dmax = rgbmax - rgbmin;
	double h, s, v;
	double dr, dg, db;

	v = (double)rgbmax/(depth-1);

	if (dmax == 0)
	{
	    // Gray
	    h = 0;
	    s = 0;
	}
	else
	{
	    // Color
	    s = (double)dmax / rgbmax;
	    
	    dr = (rgbmax - red)/(6.0*dmax) + 0.5;
	    dg = (rgbmax - green)/(6.0*dmax) + 0.5;
	    db = (rgbmax - blue)/(6.0*dmax) + 0.5;

	    if (red == rgbmax)
		h = db - dg;
	    else if (green == rgbmax)
		h = 1.0/3.0 + dr - db;
	    else
		h = 2.0/3.0 + dg - dr;

	    /*h = (h + 10) % 1;*/

	    double eps = 0.25/depth;

	    if (h < -eps)
	    {
		h += 1-eps;
	    }
	    else if (h > 1+eps)
	    {
		h -= 1-eps;
	    }
	}

	double[] hsv = new double[3];
	hsv[0] = h;
	hsv[1] = s;
	hsv[2] = v;

	return hsv;
    }

   
    /**
     * Makes a RGBImage of the HSVImage.
     * @return A HSVImage converted into a RGBImage
     */
    public RGBImage makeRGBImage()
    {
	int rows = getHeight();
	int cols = getWidth();
      
	short[][] red = new short[rows][cols]; 
	short[][] green = new short[rows][cols]; 
	short[][] blue = new short[rows][cols]; 

	int[] rgb;

	for (int row = 0; row < rows; row++)
	{
	    for (int col = 0; col < cols; col++)
	    {
		rgb = hsv2rgb((double)hue[row][col]/(L-1),
			      (double)saturation[row][col]/(L-1),
			      (double)value[row][col]/(L-1), L);
		red[row][col] = (short)Math.round(rgb[0]);
		green[row][col] = (short)Math.round(rgb[1]);
		blue[row][col] = (short)Math.round(rgb[2]);
	    }
	}

	return new RGBImage(red, green, blue);
    }


    public static int[] hsv2rgb(double hue, double saturation,
				double value, int depth)
    {
	int red, green, blue;
	double vh, v1, v2, v3, vr, vg, vb;
	int vi;
	if (saturation == 0)
	{
	    // Gray
	    red = (int)Math.round(value*(depth-1));
	    green = (int)Math.round(value*(depth-1));
	    blue = (int)Math.round(value*(depth-1));
	}
	else
	{
	    // Color
	    vh = 6*hue;
	    vi = (int) vh;
	    v1 = value * (1 - saturation);
	    v2 = value * (1 - saturation * (vh - vi));
	    v3 = value * (1 - saturation * (1 - (vh - vi)));
	    
	    if (vi == 0)
	    {
		vr = value;
		vg = v3;
		vb = v1;
	    }
	    else if (vi == 1)
	    {
		vr = v2;
		vg = value;
		vb = v1;
	    }
	    else if (vi == 2)
	    {
		vr = v1;
		vg = value;
		vb = v3;
	    }
	    else if (vi == 3)
	    {
		vr = v1;
		vg = v2;
		vb = value;
	    }
	    else if (vi == 4)
	    {
		vr = v3;
		vg = v1;
		vb = value;
	    }
	    else
	    {
		vr = value;
		vg = v1;
		vb = v2;
	    }
	    
	    red = (int)Math.round(vr * (depth-1));
	    green = (int)Math.round(vg * (depth-1));
	    blue = (int)Math.round(vb * (depth-1));
	}

	int[] rgb = new int[3];
	rgb[0] = red;
	rgb[1] = green;
	rgb[2] = blue;
	
	return rgb;
    }

   
    private short checkInterval(short x)
    {
	if (x < 0)
	    x = 0;
	else if (x > L-1)
	    x = L-1;

	return x;
    }
   

    /**
     * Shows the HSVImage in a modal window.
     * @param title The title on top of the window.
     */
    public void show(String title)
    {
	makeRGBImage().show (title);
    }

   
    /**
     * Shows the HSVImage in a modal window.
     */
    public void show()
    {
	show("");
    }


    /**
     * Shows the HSVImage scaled with use of nearest neighbor
     * interpolation.  This method utilizes an ImageScaler to perform
     * the scaling.
     * @param title The title on top of the window.
     * @param scale The scale.
     */
    public void show(String title, double scale)
    {
	makeRGBImage().createScaledImage(scale,
					 ImageScaler.NEAREST).show(title);
    }


    /**
     * Shows the HSVImage scaled with use of bilinear
     * interpolation. This method utilizes an ImageScaler to perform
     * the scaling.
     * @param scale The scale.
     * @param title The title on top of the window.
     */
    public void showBL (double scale, String title)
    {
	makeRGBImage().createScaledImage (scale, ImageScaler.BILINEAR).
	    show(title);
    }


    /**
     * Shows the hue component as an value image.
     */
    public void showHue()
    {
	showHue("Hue");
    }


    /**
     * Shows the saturation component as an value image.
     */
    public void showSaturation()
    {
	showSaturation("Saturation");
    }


    /**
     * Shows the Value component as an value image.
     */
    public void showValue()
    {
	showValue("Value");
    }

    /**
     * Shows the hue component as an value image.
     */
    public void showHue(String title)
    {
	IntensityImage image = new IntensityImage(hue);
	image.show(title);
    }


    /**
     * Shows the saturation component as an value image.
     */
    public void showSaturation(String title)
    {
	IntensityImage image = new IntensityImage(saturation);
	image.show(title);
    }


    /**
     * Shows the Value component as an value image.
     */
    public void showValue(String title)
    {
	IntensityImage image = new IntensityImage(value);
	image.show(title);
    }


    /**
     * Creates a scaled image. This method utilizes an ImageScaler to
     * perform the scaling. The parameter interpolType may be one of
     * the constants RGBImage.NN_INTERPOL for nearest neighbor
     * interpolation or RGBImage.BL_INTERPOL for bilinear interpolation.
     * @param scale The scale.
     * @param interpolType The type of interpolation
     */    
    public Image createScaledImage(double scale, int interpolType)
    {
	ImageScaler scaler = new ImageScaler(scale, interpolType);
	return (HSVImage)scaler.filter(this);
    }


    /**
     * Draw the image to a Graphics object at a given position.
     * @param g The Graphics object to draw on.
     * @param x The horizontal position of the image.
     * @param y The vertical position of the image.
     */
    public void draw(Graphics g, int x, int y)
    {
	makeRGBImage().draw (g, x, y);
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
	makeRGBImage().draw (g, x, y, scaleX, scaleY, rotateAngle);
    }


    /**
     * Creates a bufferedImage of the HSVImage.
     * @return a BufferedImage version of the Image
     */
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
	return "HSVImage-object:" +
	    "\n\tWidth:  " + getWidth() +
	    "\n\tHeight: " + getHeight();
    }

}
