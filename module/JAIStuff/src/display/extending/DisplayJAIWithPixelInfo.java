/*
 * Part of the Java Image Processing Cookbook, please see
 * http://www.lac.inpe.br/~rafael.santos/JIPCookbook.jsp
 * for information on usage and distribution.
 * Rafael Santos (rafael.santos@lac.inpe.br)
 */
package display.extending;

import java.awt.event.MouseEvent;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;

import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;

import com.sun.media.jai.widget.DisplayJAI;
import java.awt.Graphics;

/**
 * This class shows how to extend the DisplayJAI class. We'll override the
 * mouseMoved method of DisplayJAI so when the mouse is moved, some information
 * about the pixel beneath the mouse will be stored (but not displayed by this class).
 */
public class DisplayJAIWithPixelInfo extends DisplayJAI {
    private StringBuffer pixelInfo; // The pixel information (formatted in a StringBuffer).
    private double[] dpixel; // The pixel information as an array of doubles.
    private int[] ipixel; // The pixel information as an array of integers.
    private boolean isDoubleType; // Indicates which of the above arrays we will use.
    private RandomIter readIterator; // A RandomIter that allow us to get the data on a single pixel.
    private boolean isIndexed; // True if the image has a indexed color model.
    private short[][] lutData; // Will contain the look-up table data if isIndexed is true.
    protected int width, height; // The dimensions of the image

    /**
     * The constructor of the class, which creates the arrays and instances needed
     * to obtain the image data and registers the class to listen to mouse motion events.
     * @param image a RenderedImage for display
     */
    public DisplayJAIWithPixelInfo(RenderedImage image) {
        super(image); // Calls the constructor for DisplayJAI.
        readIterator = RandomIterFactory.create(image, null); // Creates the iterator.
        // Get some data about the image
        width = image.getWidth();
        height = image.getHeight();
        int dataType = image.getSampleModel().getDataType(); // Gets the data type
        switch (dataType) {
        case DataBuffer.TYPE_BYTE:
        case DataBuffer.TYPE_SHORT:
        case DataBuffer.TYPE_USHORT:
        case DataBuffer.TYPE_INT:
            isDoubleType = false;
            break;
        case DataBuffer.TYPE_FLOAT:
        case DataBuffer.TYPE_DOUBLE:
            isDoubleType = true;
            break;
        }
        // Depending on the image data type, allocate the double or the int array.
        if (isDoubleType) {
            dpixel = new double[image.getSampleModel().getNumBands()];
        } else {
            ipixel = new int[image.getSampleModel().getNumBands()];
        }
        // Is the image color model indexed?
        isIndexed = (image.getColorModel() instanceof IndexColorModel);
        if (isIndexed) {
            // Retrieve the index color model of the image.
            IndexColorModel icm = (IndexColorModel) image.getColorModel();
            // Get the number of elements in each band of the colormap.
            int mapSize = icm.getMapSize();
            // Allocate an array for the lookup table data.
            byte[][] templutData = new byte[3][mapSize];
            // Load the lookup table data from the IndexColorModel.
            icm.getReds(templutData[0]);
            icm.getGreens(templutData[1]);
            icm.getBlues(templutData[2]);
            // Load the lookup table data into a short array to avoid negative numbers.
            lutData = new short[3][mapSize];
            for (int entry = 0; entry < mapSize; entry++) {
                lutData[0][entry] = templutData[0][entry] > 0 ?
                                    templutData[0][entry] :
                                    (short) (templutData[0][entry] + 256);
                lutData[1][entry] = templutData[1][entry] > 0 ?
                                    templutData[1][entry] :
                                    (short) (templutData[1][entry] + 256);
                lutData[2][entry] = templutData[2][entry] > 0 ?
                                    templutData[2][entry] :
                                    (short) (templutData[2][entry] + 256);
            }
        } // end if indexed
        // Registers the mouse motion listener.
        addMouseMotionListener(this);
//        addMouseListener(this);
//        this.setDoubleBuffered(true);
        // Create the StringBuffer instance for the pixel information.
        pixelInfo = new StringBuffer(50);
    }


    /**
     * This method will be called when the mouse is moved over the image being
     * displayed.
     * @param me the mouse event that caused the execution of this method.
     */
    public void mouseMoved(MouseEvent me) {
        pixelInfo.setLength(0); // Clear the StringBuffer
        int x = me.getX();
        int y = me.getY();
        if ((x >= width) || (y >= height)) {
            pixelInfo.append("No data!");
            return;
        }
        if (isDoubleType) { // Process the pixel as an array of double values
            pixelInfo.append("(floating-point data) ");
            readIterator.getPixel(me.getX(), me.getY(), dpixel); // Read the pixel's values
            for (int b = 0; b < dpixel.length; b++) {
                pixelInfo.append(dpixel[b] + ","); // Append to the StringBuffer
            }
            pixelInfo = pixelInfo.deleteCharAt(pixelInfo.length() - 1); // Erase last comma
        } else { // Pixel type is not floating point, will be processed as integers.
            if (isIndexed) { // If color model is indexed
                pixelInfo.append("(integer data with colormap) ");
                readIterator.getPixel(me.getX(), me.getY(), ipixel); // Read the pixel's values
                // Assume ipixel.length = 1
                pixelInfo.append("Index: " + ipixel[0]);
                // Add also the RGB entry from the LUT.
                pixelInfo.append(" RGB:" + lutData[0][ipixel[0]] + "," +
                                 lutData[1][ipixel[0]] + "," +
                                 lutData[2][ipixel[0]]);
            } else
            // Pixels are of integer type, but not indexed
            {
                pixelInfo.append("(RGB) ");
                readIterator.getPixel(me.getX(), me.getY(), ipixel); // Read the pixel's values
                for (int b = 0; b < ipixel.length; b++) {
                    pixelInfo.append(ipixel[b] + ","); // Append to the StringBuffer
                }
                pixelInfo = pixelInfo.deleteCharAt(pixelInfo.length() - 1); // Erase last comma
            }
        } // Pixel is integer type
    } // end of method mouseMoved

    /**
     * This method allows access to the pixel info which was obtained in the mouseMoved method.
     * @return the pixel information, formatted as a string
     */
    public String getPixelInfo() {
        return pixelInfo.toString();
    }

}
