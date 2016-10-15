package no.hiof.imagepr;

import java.awt.image.BufferedImage;
import java.awt.Graphics;

public interface Image
{
    // Constants
    public static final int RED = 3;
    public static final int GREEN = 2;
    public static final int BLUE = 1;

    public abstract int getHeight();
    public abstract int getWidth();

    public abstract BufferedImage makeBufferedImage();

    public abstract Image createScaledImage(double scale, int interpolType);

    public abstract void show(String title);
    public abstract void show(String title, double scale);

    public abstract void draw(Graphics g, int x, int y);
    public abstract void draw(Graphics g, int x, int y, double scaleX,
			      double scaleY, double rotateAngle);

}
