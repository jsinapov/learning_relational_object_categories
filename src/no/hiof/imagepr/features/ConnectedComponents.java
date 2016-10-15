package no.hiof.imagepr.features;

import no.hiof.imagepr.*;
import no.hiof.imagepr.tools.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * <p>ConnectedComponents is a class for labeling connected components
 * of a binary image, with respect to the 4-neighborhood (default),
 * the or the 8-neighborhood definition. This class offers two
 * algorithms, a recursive one and the so called classical
 * algorithm. See "Computer Vision" chapter 3.4 by Linda G. Shapiro
 * and George C. Stockman, for a description of the algorithms.
 *
 * <p>The classical algorithm was described by Rosenfeld and Pfaltz. In
 * this class this is combined with a union-find structure as given in
 * "Computer Vision" chapter 3.4 by Linda G. Shapiro and George
 * C. Stockman.
 *
 * <p>The recursive algorithm will fail for pictures with large connected
 * components due to the limited recursive depth possible in Java.
 *
 * @author Mari-Ann Akerjord
 */
public class ConnectedComponents
{
    
    /** Option for finding 4-connected components. */
    public static final int N4 = 1;
    
    // /** Option for finding D-connected components.*/
    //public static final int ND = 2;
    
    /** Option for finding 8-connected components. */
    public static final int N8 = 3;

    /** Option for the classical algorithm */
    public static final int CLASSICAL = 4;

    /** Option for the recursive algorithm */
    public static final int RECURSIVE = 5;
    
    private int maxLabels;
    private BinaryImage bImage;
    private IntensityImage compImage;
    private int numRows ;
    private int numCols;
    private int label;
    private int nbhood;
    private int algorithm;
    
    private int[] parent;
    
    /**
     * Constructor which sets the BinaryImage to process. By default
     * the generated instance of ConnectedComponents will find
     * four-connected components.
     * @param bImage Image to process
     */
    public ConnectedComponents(BinaryImage bImage)
    {
        this(bImage, N4, CLASSICAL);
    }
    
    /**
     * Constructor wich sets the BinaryImage to process and the type
     * of connection to use. The parameter nbhood should be
     * either N4 or N8.
     * @param bImage Image to process
     * @param nbhood Type of neighbourhood
     */
    public ConnectedComponents(BinaryImage bImage, int nbhood)
    {
        this(bImage, nbhood, CLASSICAL);
    }
    
    
    /**
     * Constructor which sets the BinaryImage to process and the type
     * of connection to use. Set nbhood to N4 of N8 and algorithm to
     * CLASSICAL or RECURSIVE
     * @param bImage Image to process
     * @param nbhood Type of connection
     * @param algorithm Type of algorithm
     */
    public ConnectedComponents(BinaryImage bImage, int nbhood, int algorithm)
    {
        this.bImage = bImage;
        this.nbhood = nbhood;
        this.algorithm = algorithm;
        compImage = bImage.makeIntensityImage();
        numRows = compImage.getHeight();
        numCols = compImage.getWidth();
        label = 0;
        setMaxLabels(10000);
        
        if (algorithm == RECURSIVE)
            recursiveFindComponents();
        else if (algorithm == CLASSICAL)
            classicFindComponents();
        else
            throw new RuntimeException(
	      "Type of algorithm must be either CLASSICAL or RECURSIVE");
    }
    
    
    /**
     * Set maximum number of labels used with the classical
     * algorithm. I this method is not called, the number is set to
     * 10000. Note that the number of labels must be set much larger
     * than the maximum number of components, as the algorithm uses a
     * temporary array of labels that may be much larger than the
     * final number of components.
     * @param max Maximum number of labels
     */
    public void setMaxLabels(int max)
    {
        maxLabels = max;
    }
    
    
    /**
     * Returns the labeled image in which the value of each pixel is
     * the label of its connected component. All background pixels have
     * value zero.
     * @return The labeled image
     */
    public IntensityImage getCompImage() {return compImage;}
    

    private void classicFindComponents()
    {
	int rows = bImage.getHeight();
        int cols = bImage.getWidth();
        short[][] labeledMat = compImage.getData();        

        parent = new int[maxLabels];
        int noInd;
        int[][] neighbourInd = new int[2][4];
        int m = 0;
        int[] nbLabels = new int[4];
        int noLabels;
        
        label = 0;
        
        for (int row = 0; row < rows; row++)
	{
            for (int col = 0; col < cols; col++)
	    {
                if (bImage.getValueAt(row,col))
		{
                    noInd = priorNeighbours(neighbourInd, row, col);
                    
                    // If no prior neighbours, start with a new label
                    if (noInd == 0)
                        m = ++label;
                    else
		    {
                        noLabels = labels(nbLabels, labeledMat,
                                          neighbourInd, noInd);
                        
                        //System.out.println("Neighbour labels, "
                        //    + row + ", " + col + "; ");
                        //printTab(nbLabels, noLabels);
                        
                        // Choose the minimum label of the prior neighbours
                        m = minimum(nbLabels, noLabels);
                        unionAll(m, nbLabels, noLabels);
                    }
                    
                    labeledMat[row][col] = (short)m;
                }
            }
        }
        
        //System.out.println("After pass 1");
        //MatrixTools.print(labeledMat);

        // Pass 2
	int[] usedLabels = new int[maxLabels];
	m = 0;
	int parLabel;

        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                if (bImage.getValueAt(row,col))
		{
		    // The original algorithm leaves empty labels,
		    // this does not
		    parLabel = findLabelParent(labeledMat[row][col]);
		    if (usedLabels[parLabel] == 0)
			usedLabels[parLabel] = ++m;

                    labeledMat[row][col] = (short)usedLabels[parLabel];
		}

	// Largest label
	label = m;
    }
    
    
    private int findLabelParent(short lab)
    {
        int j = lab;
        while (parent[j] != 0)
            j = (int)parent[j];
        
        return j;
    }
    

    /** For debugging */
    private void printTab(int[] tab, int n)
    {
        for (int i = 0; i < n; i++)
        {
            System.out.print("  " + tab[i]);
        }
    }
    
    private void unionAll(int m, int[] nbLabels, int noLabels)
    {
        int lab;
        for (int k = 0; k < noLabels; k++)
        {
            lab = nbLabels[k];
            
            if (lab != m)
                union(m, lab, parent);
        }
    }
    
    
    private void union(int label1, int label2, int[] parent)
    {
        int j = label1;
        int k = label2;
        
        // Propagete until the root are found for both labels
        while(parent[j] != 0)
            j = parent[j];
            
        while(parent[k] != 0)
            k = parent[k];
        
        if (j != k)
            parent[k] = j;
    }
    
    
    private static int minimum(int[] tab, int n)
    {
        int min = tab[0];
        for (int i = 1; i < n; i++)
            if (tab[i] < min)
                min = tab[i];
        
        return min;
    }
    
    
    private int labels(int[] nbLabels, short[][] labeledMat,
    int[][] neighbourInd, int noInd)
    {
        boolean found;
        int lab;
        int noLabels = 0;
        
        for (int i = 0; i < noInd; i++)
	{
            lab = labeledMat[neighbourInd[0][i]][neighbourInd[1][i]];
            
            found = false;
            for (int j = 0; j < noLabels && !found; j++)
	    {
                found = (lab == nbLabels[j]);
            }
            
            if (!found)
                nbLabels[noLabels++] = lab;
        }
        
        return noLabels;
    }
    
    
    private int priorNeighbours(int[][] ind, int row, int col)
    {
        int noInd = 0;
        
        if (row > 0 && bImage.getValueAt(row-1, col))
	{
            ind[0][noInd] = row-1;
            ind[1][noInd] = col;
            ++noInd;
        }
        if (col > 0 && bImage.getValueAt(row, col-1))
	{
            ind[0][noInd] = row;
            ind[1][noInd] = col-1;
            ++noInd;
        }
        
        if (nbhood == N8)
	{
            if (row > 0 && col > 0 &&
                    bImage.getValueAt(row-1, col-1)) {
                ind[0][noInd] = row-1;
                ind[1][noInd] = col-1;
                ++noInd;
            }
            if (row > 0 && col < bImage.getWidth()-1 &&
                    bImage.getValueAt(row-1, col+1)) {
                ind[0][noInd] = row-1;
                ind[1][noInd] = col+1;
                ++noInd;
            }
        }
        
        return noInd;
    }
    
    
    /**
     * Returns the value of the label of the last connected component
     * found in the image. This is the same as the number of connected
     * components of the image.
     * @return The label of the last connected component
     */
    public int getLabel() {return label;}
    
    
    /**
     * Returns an image with only one of the connected components.
     * @param label The label of the component.
     * @return A BinaryImage containing the spesific connected component.
     */
    public BinaryImage getCompNo(int label)
    {
        BinaryImage bin = new BinaryImage(numRows,numCols);
        
        short [][] data = compImage.getData();
        
        for (int i= 0; i<numRows; i++)
            for (int j=0; j<numCols; j++)
                if (data[i][j] == label)
                    bin.setValueAt(i,j,true);
        return bin;
    }
    
    
    /**
     * Show a component in a window.
     * @param label The label of the component.
     */
    public void showCompNo(int label)
    {
        BinaryImage bin = getCompNo(label);
        bin.setOneColor(Color.yellow);
        bin.show();
    }
    
    
    /**
     * Show all components in a window, with a unique colour for each
     * component.
     */
    public void showAllComponents()
    {
        showAllComponents(Color.red,Color.yellow);
    }
    
    
    /**
     * Show all components in a window, with a unique colour for each
     * component.
     * @param mincolor The color of the first component.
     * @param maxcolor The color of the last component.
     */
    public void showAllComponents(Color mincolor, Color maxcolor)
    {
        
        short [][] labelmap = new short [255][3];
        short [][] cmap = IntensityImage.makeColormap(mincolor,maxcolor);
        
        
        if (label > 255)
	{
            labelmap = cmap;
            labelmap[0][0] = 0;labelmap[0][1] = 0; labelmap[0][2] = 0;
            
            IntensityImage copy = new IntensityImage(compImage);
            short [][] data = copy.getData();
            
            for (int i=0; i<numRows; i++)
                for (int j=0; j<numCols; j++)
                    if (data[i][j] > 255)
                        data[i][j] = 255;
            copy.setColormap(labelmap);
            copy.show();
        }
        else
	{
            labelmap[1] = cmap[0];
            labelmap[label] =cmap[255];
            
            int step = (int)(255.0/(label-1));
            for (int i=1; i<label-1;i++)
                labelmap[i+1] = cmap[i*step];
            compImage.setColormap(labelmap);
            
            compImage.show();
        }
        
    }
    
    
    private void recursiveFindComponents()
    {
        negate();
        short [][] data = compImage.getData();
        
        for (int i=0; i<numRows; i++)
            for (int j=0; j<numCols; j++)
                if ( data[i][j] == -255) {
                    label++;
                    
                    try {
                        search(label,i,j);
                    }
                    
                    catch(StackOverflowError e ) {
                        System.out.println(e);
                        System.out.println("\nError: Component too large."
                            + "Use the CLASSICAL method instead of "
                            + "the recursive.");
                        return;
                    }
                }
    }
    
    
    
    private void search(int label, int r, int c) throws StackOverflowError
    {
        short [][] data = compImage.getData();
        int [] nb = {r,c};
        ArrayList nbList = findNeighbours(nb);
        
        data[r][c] = (short)label;
        
        
        for (int i=0; i<nbList.size(); i++)
	{
            int [] pos  = (int [])nbList.get(i);
            if (data[pos[0]][pos[1]] == -255)
                search(label,pos[0],pos[1]);
        }
    }
    
    
    private void  negate() {
        Arithmetic ar = new Arithmetic();
        compImage = ar.multiply(compImage,-1);
    }
    
    
    private ArrayList findNeighbours(int [] pos)
    {
        ArrayList nbList ;
        
        //if (nbhood == ND)
            //nbList = findDNeighbours(pos);
        
        if (nbhood == N8)
            nbList = find8Neighbours(pos);
        else if (nbhood == N4)
            nbList = find4Neighbours(pos);
        else
            throw new RuntimeException("Use either N4 or N8 neighbours");
            
        return nbList;
    }
    
    
    private ArrayList  find4Neighbours(int [] pos)
    {
        ArrayList nbList = new ArrayList();
        
        if (pos[0] > 0)
            addNeighbour(pos[0]-1,pos[1],nbList);
        if (pos[1] > 0)
            addNeighbour(pos[0],pos[1]-1,nbList);
        if (pos[1] < numCols -1)
            addNeighbour(pos[0],pos[1]+1,nbList);
        if (pos[0] < numRows -1 )
            addNeighbour(pos[0]+1,pos[1],nbList);
        
        return nbList;
    }
    
    private ArrayList findDNeighbours(int [] pos)
    {
        ArrayList nbList = new ArrayList();
        
        if ( (pos[0] > 0) && (pos[1] > 0))
            addNeighbour(pos[0]-1,pos[1]-1,nbList);
        if ( (pos[0] > 0) && (pos[1] < numCols -1 ) )
            addNeighbour(pos[0]-1,pos[1] +1,nbList);
        if ( (pos[0] < numRows -1) && (pos[1] > 0))
            addNeighbour(pos[0]+1,pos[1]-1,nbList);
        if ((pos[0] < numRows -1) && (pos[1] <numCols -1))
            addNeighbour(pos[0]+1, pos[1] +1 ,nbList);
        
        
        
        return nbList;
    }
    
    
    private ArrayList find8Neighbours(int [] pos)
    {
        ArrayList nbList = new ArrayList();
        
        if ( (pos[0] > 0) && (pos[1] > 0))
            addNeighbour(pos[0]-1,pos[1]-1,nbList);
        if (pos[0] > 0)
            addNeighbour(pos[0]-1,pos[1],nbList);
        if ( (pos[0] > 0) && (pos[1] < numCols -1 ) )
            addNeighbour(pos[0]-1,pos[1] +1,nbList);
        if (pos[1] > 0)
            addNeighbour(pos[0],pos[1]-1,nbList);
        if (pos[1] < numCols -1)
            addNeighbour(pos[0],pos[1]+1,nbList);
        if ( (pos[0] < numRows -1) && (pos[1] > 0))
            addNeighbour(pos[0]+1,pos[1]-1,nbList);
        if (pos[0] < numRows -1 )
            addNeighbour(pos[0]+1,pos[1],nbList);
        if ((pos[0] < numRows -1) && (pos[1] <numCols -1))
            addNeighbour(pos[0]+1, pos[1] +1 ,nbList);
        
        return nbList;
    }
    
    
    private void addNeighbour(int r, int c, ArrayList list)
    {
        int [] nb = {r,c};
        list.add(nb);
    }
    
    
    private void printData(short [][] data)
    {
        for (int i=0; i<data.length; i++)
	{
            System.out.println("");
            for (int j=0; j<data[0].length; j++)
                System.out.print(" "+data[i][j]);
        }
    }
    
    // Testing
//     public static void main(String [] args )
//     {
//         short [][] data =
// 	    {{1, 1, 0, 1, 1, 1, 0, 1},
// 	     {1, 1, 0, 1, 0, 1, 0, 1},
// 	     {1, 1, 1, 1, 0, 0, 0, 1},
// 	     {0, 0, 0, 0, 0, 0, 0, 1},
// 	     {1, 1, 1, 1, 0, 1, 0, 1},
// 	     {0, 0, 0, 1, 0, 1, 0, 1},
// 	     {1, 1, 0, 1, 0, 0, 0, 1},
// 	     {1, 1, 0, 1, 0, 1, 1, 1},
// 	     {0, 0, 1, 0, 0, 0, 0, 0},
// 	     {0, 1, 0, 1, 0, 0, 0, 0}};
        
//         IntensityImage testImage = new IntensityImage(data);
//         BinaryImage bImage = new BinaryImage(testImage, 1);
        
//         bImage.show("Test image", 20);
        
//         ConnectedComponents cc =
//         new ConnectedComponents(bImage, ConnectedComponents.N8,
//         ConnectedComponents.CLASSICAL);
//         IntensityImage ccImage = cc.getCompImage();
        
//         System.out.println("Original matrix");
//         MatrixTools.print(data);
        
//         System.out.println("After labeling");
//         MatrixTools.print(ccImage.getData());
//         System.exit(0);
        
//     }
}
