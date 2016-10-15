package no.hiof.imagepr.tools;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.io.*;
import javax.imageio.*;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;


/**
 * ImageIOTools is a class which includes a static method for saving
 * JPEG images.
 *
 * @author Per-Olav Rus&aring;s
 */ 
public class ImageIOTools
{

   /**
    * Saves a BufferedImage in a JPEG-file. A low value of the quality
    * value results in a small file. An often used value for the
    * quality is 0.75.
    * @param quality The quality of the image from 0 to 1.
    */
   public static void saveAsJPEG (BufferedImage bufim, 
                                  String filename, float quality)
      throws IOException
   {
      ImageWriter jpgWriter = null;
      
      Iterator it = ImageIO.getImageWritersByFormatName ("jpeg");
      if (it.hasNext())
      {
         jpgWriter = (ImageWriter)it.next();
      }
      else
      {
         throw new IOException("Can't find an ImageWriter for JPEG files.");
      }
      
      ImageWriteParam jpgParam = (ImageWriteParam)
         jpgWriter.getDefaultWriteParam();
      
      jpgParam.setCompressionMode (jpgParam.MODE_EXPLICIT);
      jpgParam.setCompressionQuality (quality);
      
      FileImageOutputStream imout = new FileImageOutputStream
         (new File (filename));
      
      jpgWriter.setOutput (imout);        
      jpgWriter.write (null, new IIOImage (bufim, null, null), jpgParam);      
   }
}
