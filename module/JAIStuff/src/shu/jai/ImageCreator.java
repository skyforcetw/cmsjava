package shu.jai;

import javax.media.jai.*;

import java.awt.*;
import java.awt.image.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class ImageCreator {
  public static TiledImage createGrayImage(float[] imageData, int width,
                                           int height) {
//    int width = imageData[0].length;
//    int height = imageData.length;

    javax.media.jai.DataBufferFloat dbuffer =
        new javax.media.jai.DataBufferFloat(imageData, width * height);
    // Create a float data sample model.
    SampleModel sampleModel =
        RasterFactory.createBandedSampleModel(DataBuffer.TYPE_FLOAT,
                                              width, height, 1);
    // Create a compatible ColorModel.
    ColorModel colorModel = PlanarImage.createColorModel(sampleModel);
    // Create a WritableRaster.
    Raster raster = RasterFactory.createWritableRaster(sampleModel, dbuffer,
        new Point(0, 0));
    // Create a TiledImage using the float SampleModel.
    TiledImage tiledImage = new TiledImage(0, 0, width, height, 0, 0,
                                           sampleModel, colorModel);
    // Set the data of the tiled image to be the raster.
    tiledImage.setData(raster);
    return tiledImage;
  }

  public TiledImage createGrayImage(byte[] imageData, int width, int height) {
//    int width = imageData[0].length;
//    int height = imageData.length;

    DataBufferByte dbuffer = new DataBufferByte(imageData, width * height);
    // Create a float data sample model.
    SampleModel sampleModel =
        RasterFactory.createBandedSampleModel(DataBuffer.TYPE_BYTE,
                                              width, height, 1);
    // Create a compatible ColorModel.
    ColorModel colorModel = PlanarImage.createColorModel(sampleModel);
    // Create a WritableRaster.
    Raster raster = RasterFactory.createWritableRaster(sampleModel, dbuffer,
        new Point(0, 0));
    // Create a TiledImage using the float SampleModel.
    TiledImage tiledImage = new TiledImage(0, 0, width, height, 0, 0,
                                           sampleModel, colorModel);
    // Set the data of the tiled image to be the raster.
    tiledImage.setData(raster);
    return tiledImage;
  }
}
