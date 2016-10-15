package no.hiof.imagepr.filters;

import no.hiof.imagepr.Image;

/**
 * ImageFilter is an interface for filters, which are operations who
 * takes an image or an image matrix and return an image or an matrix
 * after doing an operation on the image or image matrix.
 */
public interface ImageFilter
{
    public abstract Image filter(Image image);
    public abstract short[][] filter(short[][] matrix);
}
