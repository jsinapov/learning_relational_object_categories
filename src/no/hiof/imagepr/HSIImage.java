package no.hiof.imagepr;

import no.hiof.imagepr.tools.*;
import no.hiof.imagepr.filters.ImageScaler;

import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.Serializable;


/**
 * <p>An HSIImage is an image using the HSI colour model, which means
 * that each pixel has hue, saturation and intensity components. This
 * class contains methods for setting and getting the HSI components,
 * for transformation between HSI and RGB colour models and for
 * displaying the image and it's HSI components.
 *
 * <p>All of the components (hue, saturation and intensity) must be in
 * the interval 0 to 255. The hue component is circular, which means
 * that the hue value for 0 and 255 is identical.
 *
 * @see no.hiof.imagepr.RGBImage
 * @see no.hiof.imagepr.IntensityImage
 * @see no.hiof.imagepr.BinaryImage
 * @author Per-Olav Rus&aring;s
 */
public class HSIImage implements no.hiof.imagepr.Image, Serializable
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
     * The Matrix with the intensity component of the image.
     */
    protected short[][] intensity;


    /**
     * Creates an empty HSIImage object.
     */
    public HSIImage()
    {
    }

   
    /**
     * Creates an HSIImage by converting a RGBImage.
     * @param image RGBImage to be converted.
     */
    public HSIImage(RGBImage image)
    {
	fromRGBImage(image);
    }


    /**
     * A constructor that sets the HSI matrices
     * @param hue The hue component of the image.
     * @param saturation The saturation component of the image.
     * @param intensity The intensity component of the image.
     */
    public HSIImage(short[][] hue, short[][] saturation,
		    short[][] intensity)
    {
	this.hue = hue;
	this.saturation = saturation;
	this.intensity = intensity;
    }
    

    /**
     * A copy constructor which copies another HSIImage.
     * @param otherImage Another HSIImage.
     */
    public HSIImage(HSIImage otherImage)
    {
	hue = MatrixTools.copy(otherImage.hue);
	saturation = MatrixTools.copy(otherImage.saturation);
	intensity = MatrixTools.copy(otherImage.intensity);
    }


    /**
     * Gets the reference to the matrix with the hue component
     * of the image. This method does not make a copy of the matrix,
     * but returns the reference to the HSIImage's hue matrix. This
     * means that the returned reference can be used to manipulate the
     * hue of the HSIImage.
     * @return The hue matrix.
     */
    public short[][] getHue(){ return hue; }


    /**
     * Gets the reference to the matrix with the saturation
     * component of the image. This method does not make a copy of the
     * matrix, but returns the reference to the HSIImage's saturation
     * matrix. This means that the returned reference can be used to
     * manipulate the saturation of the HSIImage.
     * @return The saturation matrix.
     */
    public short[][] getSaturation(){ return saturation; }


    /**
     * Gets the reference to the matrix with the intensity
     * component of the image. This method does not make a copy of the
     * matrix, but returns the reference to the HSIImage's intensity
     * matrix. This means that the returned reference can be used to
     * manipulate the intensity of the HSIImage.
     * @return The intensity colour matrix.
     */
    public short[][] getIntensity(){ return intensity; }

  
    /**
     * Sets the hue component of the image.
     * @param hue Matrix with the hue values.
     */
    public void setHue (short[][] hue)
    {
	this.hue = hue;
    }


    /**
     * Sets the saturation component of the image.
     * @param saturation Matrix with the saturation values.
     */
    public void setSaturation (short[][] saturation)
    {
	this.saturation = saturation;
    }


    /**
     * Sets the intensity component of the image.
     * @param intensity Matrix with the intensity colour values.
     */
    public void setIntensity (short[][] intensity)
    {
	this.intensity = intensity;
    }
   

    /**
     * The width of the image. If some of the matrices are missing, the
     * method returns -1.
     * @return The width of the image if all the matrices exist. 
     */ 
    public int getWidth()
    {
	if (hue != null && saturation != null && intensity != null)
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
	if (hue != null && saturation != null && intensity != null)
	    return hue.length;
	else
	    return -1;
    }

   
    /**
     * calculats the HSI components from the RGB components of a
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
	this.intensity = new short[rows][cols];
      
	double H, S, I;
	short sector, r, g, b, h, t;
	double sum, t1, t2, theta, minrgb;

	for (int row = 0; row < rows; row++)
	{
	    for (int col = 0; col < cols; col++)
	    {
		r = red[row][col];
		g = green[row][col];
		b = blue[row][col];
		sum = r + g + b;

		if (r == g && r == b)
		{
		    // black, gray or white
		    hue[row][col] = (short)0;
		    saturation[row][col] = (short)0;
		    intensity[row][col] = (short)((r + g + b)/3);
		}
		else
		{
               
		    if (g == 0 && b == 0)
		    {
			// only red
			t = 0;
		    }
		    else if ((r == 0 && b == 0) || (b == 0 && g == 0))
		    {
			// only green or blue
			t = (L-1)/3;
		    }
		    else
		    {
			t1 = 0.5 * ((r-g) + (r-b));
			t2 = Math.sqrt((r-g)*(r-g) + (r-b)*(g-b));
			theta = Math.acos (t1/t2);
			t = (short)((L-1) * theta/(2*Math.PI));
		    }

		    if (b <= g)
			hue[row][col] = t;
		    else
			hue[row][col] = (short)((L-1) - t);

		    minrgb = r;
		    if (g < minrgb)
			minrgb = g;
		    if (b < minrgb)
			minrgb = b;
                              
		    saturation[row][col] = (short)
			((L-1)*(1 - minrgb*3.0/sum));
		    intensity[row][col] = (short)(sum / 3.0);
		}
	    }
	}

    }

   
    /**
     * Makes a RGBImage of the HSIImage.
     * @return A HSIImage converted into a RGBImage
     */
    public RGBImage makeRGBImage()
    {
	int rows = getHeight();
	int cols = getWidth();
      
	short[][] red = new short[rows][cols]; 
	short[][] green = new short[rows][cols]; 
	short[][] blue = new short[getHeight()][getWidth()]; 
      
	double H, S, I;
	short v1, v2, v3, h, h0, sat, intens;
	int sector;

	for (int row = 0; row < rows; row++)
	{
	    for (int col = 0; col < cols; col++)
	    {

		if (saturation[row][col] == 0)
		{
		    red[row][col] = intensity[row][col];
		    green[row][col] = intensity[row][col];
		    blue[row][col] = intensity[row][col];
		}
		else
		{
		    h0 = hue[row][col];

		    if (h0 <= (L-1)/3)
		    {
			h = (short)h0;
			sector = 1;
		    }
		    else if (h0 <= 2*(L-1)/3)
		    {
			h = (short)(h0 - (L-1)/3);
			sector = 2;
		    }
		    else
		    {
			h = (short)(h0 - 2*(L-1)/3);
			sector = 3;
		    }

		    H = h * 2 * Math.PI / (L-1);

		    sat = saturation[row][col];
		    S = (double)sat / (L-1);

		    intens = intensity[row][col];               
		    I = (double)intens / (L-1);

		    v1 = (short)Math.round(intens * (1 - S));
		    v1 = checkInterval (v1);

		    v2 = (short)Math.round(intens * (1 + S * Math.cos(H)/
						     Math.cos(Math.PI/3 - H) ));
		    v2 = checkInterval (v2);
               
		    v3 = (short)Math.round(3 * intens - (v1 + v2));
		    v3 = checkInterval (v3);
                              
		    if (sector == 1)
		    {
			blue[row][col] = v1;
			red[row][col] = v2;
			green[row][col] = v3;
		    }
		    else if (sector == 2)
		    {
			red[row][col] = v1;
			green[row][col] = v2;
			blue[row][col] = v3;
		    }
		    else
		    {
			green[row][col] = v1;
			blue[row][col] = v2;
			red[row][col] = v3;
		    }
		}
	    }
	}
            
	return new RGBImage (red, green, blue);
    }

   
    private short checkInterval (short x)
    {
	if (x < 0)
	    x = 0;
	else if (x > L-1)
	    x = L-1;

	return x;
    }
   

    /**
     * Shows the HSIImage in a modal window.
     * @param title The title on top of the window.
     */
    public void show(String title)
    {
	makeRGBImage().show (title);
    }

   
    /**
     * Shows the HSIImage in a modal window.
     */
    public void show()
    {
	show("");
    }


    /**
     * Shows the HSIImage scaled with use of nearest neighbor
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
     * Shows the HSIImage scaled with use of bilinear
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
     * Shows the hue component as an intensity image.
     */
    public void showHue()
    {
	showHue("Hue");
    }


    /**
     * Shows the saturation component as an intensity image.
     */
    public void showSaturation()
    {
	showSaturation("Saturation");
    }


    /**
     * Shows the Intensity component as an intensity image.
     */
    public void showIntensity()
    {
	showIntensity("Intensity");
    }

    /**
     * Shows the hue component as an intensity image.
     */
    public void showHue(String title)
    {
	IntensityImage image = new IntensityImage(hue);
	//image.setColormap(IntensityImage.HSI);
	image.show(title);
    }


    /**
     * Shows the saturation component as an intensity image.
     */
    public void showSaturation(String title)
    {
	IntensityImage image = new IntensityImage(saturation);
	image.show(title);
    }


    /**
     * Shows the Intensity component as an intensity image.
     */
    public void showIntensity (String title)
    {
	IntensityImage image = new IntensityImage (intensity);
	image.show (title);
    }


    /**
     * Creates a scaled image. This method utilizes an ImageScaler to
     * perform the scaling. The parameter interpolType may be one of
     * the constants RGBImage.NN_INTERPOL for nearest neighbor
     * interpolation or RGBImage.BL_INTERPOL for bilinear interpolation.
     * @param scale The scale.
     * @param interpolType The type of interpolation
     */    
    public Image createScaledImage (double scale, int interpolType)
    {
	ImageScaler scaler = new ImageScaler(scale, interpolType);
	return (HSIImage)scaler.filter(this);
    }


    /**
     * Draw the image to a Graphics object at a given position.
     * @param g The Graphics object to draw on.
     * @param x The horizontal position of the image.
     * @param y The vertical position of the image.
     */
    public void draw (Graphics g, int x, int y)
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
    public void draw (Graphics g, int x, int y, double scaleX, double scaleY,
		      double rotateAngle)
    {
	makeRGBImage().draw (g, x, y, scaleX, scaleY, rotateAngle);
    }


    /**
     * Creates a bufferedImage of the HSIImage.
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
	return "HSIImage-object:" +
	    "\n\tWidth:  " + getWidth() +
	    "\n\tHeight: " + getHeight();
    }


    /**
     * Find the maximum intensity possible given a hue and saturation,
     * all in the interval [0,1].
     * @param hue Hue
     * @param sat Saturation
     * @return Maximum intensity
     */
    public static double maxIntensity(double hue, double sat)
    {
	double H = (hue % (1.0/3.0)) * Math.PI;
	double pi3 = Math.PI/3.0;
	return 1.0/(1.0 + sat*Math.cos(H)/Math.cos(pi3 - H));
    } 


    /**
     * Adjust the intensity, so that it doesn't have impossible
     * values. May be called after adjusting hue or saturation. If the
     * intensity of a point is to high, the intensity is adjusted to
     * the highest possible value while maintaining the hue and
     * saturation.
     */
    public void adjustIntensity()
    {
	int rows = getHeight();
	int cols = getWidth();
	double dbL = (double)(L-1);
	short maxI;
	for (int row = 0; row < rows; row++)
	{
	    for (int col = 0; col < cols; col++)
	    {
		maxI = (short)Math.min(L-1, 
		     Math.round((L-1) * maxIntensity(hue[row][col]/dbL,
		     saturation[row][col]/dbL)));
		intensity[row][col] =
		    (short)Math.min(intensity[row][col], maxI);
	    }
	}
    }

    
    /**
     * Set the intensity to its maximum possible value while
     * maintaining hue and saturation
     */
    public void maximizeIntensity()
    {
	int rows = getHeight();
	int cols = getWidth();
	double dbL = (double)(L-1);
	for (int row = 0; row < rows; row++)
	{
	    for (int col = 0; col < cols; col++)
	    {
		intensity[row][col] = (short)Math.min(L-1, 
		     Math.round((L-1) * maxIntensity(hue[row][col]/dbL,
		     saturation[row][col]/dbL)));
	    }
	}
    }
}
