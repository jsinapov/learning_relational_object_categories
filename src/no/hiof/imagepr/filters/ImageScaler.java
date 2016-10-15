package no.hiof.imagepr.filters;

import no.hiof.imagepr.*;

/**
 * <p>This class takes data-matrices and can perform scale
 * operations on them. One can choose either to use "Nearest
 * neighbour"-interpolation for to get a jaggy look or "Bi-linear
 * interpolation" for smoother scaling.
 *
 * <p>The class is adjusted to version 2 of the API by Per-Olav Rusås.
 *
 * @author Rune Andre Melgaard, Mads Hansen, Chao-Ching, Mai Burner.
 */
public class ImageScaler implements ImageFilter
{
    public static final int NEAREST = 1;
    public static final int BILINEAR = 2;

    private int interpolType;
    private double scaleVertical;
    private double scaleHorizontal;


    /**
     * Constructor for an ImageScaler. The parameter interpolType may
     * be either NEAREST or BILINEAR.
     * @param scaleVertical Vertical scale.
     * @param scaleHorizontal Horizontal scale.
     * @param interpolType Type of interpolation.
     */
    public ImageScaler(double scaleVertical, 
		       double scaleHorizontal, int interpolType)
    {
        this.scaleVertical = scaleVertical;
        this.scaleHorizontal = scaleHorizontal;
        this.interpolType = interpolType;
    }


    /**
     * Constructor for an ImageScaler. The parameter interpolType may
     * be either NEAREST or BILINEAR.
     * @param scale The scale in both vertical and horizontal directions.
     * @param interpolType Type of interpolation.
     */
    public ImageScaler(double scale, int interpolType)
    {
        this.scaleVertical = scale;
        this.scaleHorizontal = scale;
        this.interpolType = interpolType;
    }

    
    /**
     * Perform the scaling on a matrix of pixel values.
     * @param matrix The original matrix.
     * @return The scaled matrix.
     */
    public short[][] filter(short[][] matrix)
    {
        if (interpolType == NEAREST)
            return scaleMatrixNN(matrix);
        else if (interpolType == BILINEAR)
            return scaleMatrixBL(matrix);
        else
            return null;
    }


    /**
     * Perform the scaling on a an image, which may be a RGBImage,
     * IntensityImage or HSIImage. Scaling for BinaryImage is not
     * implemented.
     * @param image The original image.
     * @return The scaled image
     */
    public Image filter(Image image)
    {
        Image processedImage = null;

        if (image instanceof RGBImage)
        {
            RGBImage rgbimage = (RGBImage)image;
            short[][] red = rgbimage.getRed();
            short[][] green = rgbimage.getGreen();
            short[][] blue = rgbimage.getBlue();

            short[][] pRed = filter(red);
            short[][] pGreen = filter(green);
            short[][] pBlue = filter(blue);

            processedImage = new RGBImage(pRed, pGreen, pBlue);
        }
        else if (image instanceof IntensityImage)
	{
	    IntensityImage intensImage = (IntensityImage)image;
	    short[][] data = intensImage.getData();
	 
	    short[][] pData = filter(data);
	    processedImage = new IntensityImage(pData);
	}
	else if (image instanceof BinaryImage)
	{
	    System.out.println("Can't interpolate a binary image");
	    return null;
	}
	else if (image instanceof HSIImage)
	{
	    HSIImage hsiim = (HSIImage)image;
	    RGBImage scaledRgb =
		(RGBImage)filter(hsiim.makeRGBImage());
	    hsiim.fromRGBImage(scaledRgb);
	    processedImage = (Image)hsiim;
	}
	    
        return processedImage;
    }


    /**
     * Scale a matrix using bilinear interpolation.
     */
    private short[][] scaleMatrixNN(short[][] matrix)
    {

        int height = matrix.length;
        int width = matrix[0].length;

	double EPS = 1e-10;

        // The size of the scaled image
        int pheight = (int)(height * scaleVertical + EPS);
        int pwidth = (int)(width * scaleHorizontal + EPS);

        short[][] pMatrix = new short[pheight][pwidth];

	double sv = (pheight-1.0) / (height-1.0);
	double sh = (pwidth-1.0) / (width-1.0);

	int i, j;

        // Fills in the pMatrix using nearest neighbour interpolation
        for (int x = 0; x < pheight; x++)
        {
	    i = (int)Math.round(x/sv);

            for (int y = 0; y < pwidth; y++)
            {
		j = (int)Math.round(y/sh);
                pMatrix[x][y] = matrix[i][j];
            }

        }
        
        return pMatrix;
    }


    /**
     * Scale a matrix using bilinear interpolation
     */
    private short[][] scaleMatrixBL(short[][] matrix)
    {

        int height = matrix.length;
        int width = matrix[0].length;

	double EPS = 1e-10;

        // The size of the scaled image
        int pheight = (int)(height * scaleVertical + EPS);
        int pwidth = (int)(width * scaleHorizontal + EPS);

        short[][] pMatrix = new short[pheight][pwidth];

	double is, im;
	double js, jm;
	int i0, i0p1, j0, j0p1;
        short p1, p2, p3, p4;
	int i1, j1;
	short bilinValue;

	double sv = (pheight-1.0) / (height-1.0);
	double sh = (pwidth-1.0) / (width-1.0);

        // Fills in the pMatrix using bilinear interpolation
        for (int x = 0; x < pheight; x++)
        {
	    is = x / sv;
	    i0 = (int)is;
	    i0p1 = Math.min(i0 + 1, height - 1);
	    im = is - i0;

            for (int y = 0; y < pwidth; y++)
            {
		js = y / sh;
		j0 = (int)js;
		j0p1 = Math.min(j0 + 1, width - 1);
		jm = js - j0;

		p1 = matrix[i0][j0];
                p2 = matrix[i0p1][j0];
                p3 = matrix[i0p1][j0p1];
                p4 = matrix[i0][j0p1];
		
                bilinValue = (short) ((1-im) * (1-jm) * p1
				      + im * (1-jm) * p2
				      + im * jm * p3
				      + (1-im) * jm * p4);

                pMatrix[x][y] = bilinValue;
            }

        }
        
        return pMatrix;
    }

}
