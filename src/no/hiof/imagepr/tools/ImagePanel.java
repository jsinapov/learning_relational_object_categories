package no.hiof.imagepr.tools;

import no.hiof.imagepr.Image;

import java.awt.Graphics;
import java.awt.Dimension;

import javax.swing.JPanel;

/**
 * <p>An ImagePanel is a JPanel which contains an Image. This means
 * that an ImagePanel may be used the same way as a JPanel in a
 * graphical user interface.
 * 
 * <p>The classes RGBImage, IntensityImage, BinaryImage all implements
 * the Image interface, such that an ImagePanel may contain an object
 * of any of these classes.
 *
 * @author Per-Olav Rus&aring;s
 */ 
public class ImagePanel extends JPanel
{
   private Image image;
   private double xScale;
   private double yScale;


   /**
    * A constructor which sets the ImagePanel's RGBImage.
    * @param image An RGBImage.
    */
   public ImagePanel(Image image)
   {
      this.image = image;
      
      this.xScale = 1.0;
      this.yScale = 1.0;
      setPreferredSize (
         new Dimension (image.getWidth(), image.getHeight()));
   }

   /**
    * A constructor which sets the ImagePanelæs RGBImage with scaling.
    * @param image The RGBImage
    * @param xScale The scaling in the x-direction. 1 means no scaling.
    * @param yScale The scaling in the y-direction. 1 means no scaling.
    */
   public ImagePanel(Image image, double xScale,
		     double yScale)
   {
      this.image = image;
      this.xScale = xScale;
      this.yScale = yScale;

      setPreferredSize (
         new Dimension ((int) (image.getWidth()*xScale),
                        (int) (image.getHeight()*yScale)));
    }


   /**
    * Sets the image which is included in the ImagePanel. This method
    * also repaint the ImagePanel.
    * @param image The image to be included in the panel
    */
   public void setImage(Image image)
   {
      this.image = image;
      repaint();
   }


   /**
    * Paints the ImagePanel. This is called automatically whenever the
    * ImagePanel is repainted.
    * @param g The Graphics object to paint on
    */
   public void paintComponent (Graphics g)
   {
      image.draw(g, 0, 0, xScale, yScale, 0);
   }
}
      
