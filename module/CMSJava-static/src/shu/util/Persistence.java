package shu.util;

import java.io.*;
import java.util.zip.*;

import org.math.io.files.*;
import com.thoughtworks.xstream.*;
import com.thoughtworks.xstream.io.xml.*;
import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public final class Persistence {
  public final static Object readObject(String filename) {
    try {
      FileInputStream fis = new FileInputStream(filename);
      BufferedInputStream bis = new BufferedInputStream(fis);
      ObjectInputStream ois = new ObjectInputStream(bis);
      Object obj = ois.readObject();
      ois.close();
      bis.close();
      fis.close();
      return obj;
    }
    catch (FileNotFoundException ex) {
      Logger.log.error("", ex);
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    catch (ClassNotFoundException ex) {
      Logger.log.error("", ex);
    }
    return null;
  }

  public final static Object readObjectAsXML(String filename) {
    XStream xstream = new XStream(new DomDriver());
    FileReader reader = null;
    try {
      reader = new FileReader(filename);
    }
    catch (FileNotFoundException ex) {
      return null;
    }
    return xstream.fromXML(reader);
  }

  public final static Object readObjectAsXMLString(String xmlString) {
    XStream xstream = new XStream(new DomDriver());
    return xstream.fromXML(xmlString);
  }

  public final static byte[] decompress(byte[] compressedData) {
    // Create the decompressor and give it the data to compress
    Inflater decompressor = new Inflater();
    decompressor.setInput(compressedData);

    // Create an expandable byte array to hold the decompressed data
    ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedData.length);

    // Decompress the data
    byte[] buf = new byte[1024];
    while (!decompressor.finished()) {
      try {
        int count = decompressor.inflate(buf);
        bos.write(buf, 0, count);
      }
      catch (DataFormatException ex) {
        Logger.log.error("", ex);
      }
    }
    try {
      bos.close();
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }

    // Get the decompressed data
    byte[] decompressedData = bos.toByteArray();
    return decompressedData;
  }

  public final static byte[] compress(byte[] input) {
    // Create the compressor with highest level of compression
    Deflater compressor = new Deflater();
    compressor.setLevel(Deflater.BEST_COMPRESSION);

    // Give the compressor the data to compress
    compressor.setInput(input);
    compressor.finish();

    // Create an expandable byte array to hold the compressed data.
    // You cannot use an array that's the same size as the orginal because
    // there is no guarantee that the compressed data will be smaller than
    // the uncompressed data.
    ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);

    // Compress the data
    byte[] buf = new byte[1024];
    while (!compressor.finished()) {
      int count = compressor.deflate(buf);
      bos.write(buf, 0, count);
    }
    try {
      bos.close();
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }

    // Get the compressed data
    byte[] compressedData = bos.toByteArray();
    return compressedData;
  }

  public final static Object readCompressObject(String filename) {
    try {
      FileInputStream fis = new FileInputStream(filename);
      BufferedInputStream bis = new BufferedInputStream(fis);

      byte[] compressed = new byte[bis.available()];
      bis.read(compressed);
      byte[] byteArray = decompress(compressed);

      ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
      BufferedInputStream bis2 = new BufferedInputStream(bais);
      ObjectInputStream ois = new ObjectInputStream(bis2);
      Object obj = ois.readObject();

      ois.close();
      bis2.close();
      bais.close();

      bis.close();
      fis.close();
      return obj;
    }
    catch (FileNotFoundException ex) {
      Logger.log.error("", ex);
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    catch (ClassNotFoundException ex) {
      Logger.log.error("", ex);
    }
    return null;

  }

  public final static void writeCompressObject(Object obj, String filename) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    BufferedOutputStream bos = new BufferedOutputStream(baos);
    try {
      ObjectOutputStream oos = new ObjectOutputStream(bos);
      oos.writeObject(obj);
      oos.flush();
      byte[] byteArray = baos.toByteArray();
      byte[] compressed = compress(byteArray);

      FileOutputStream fos = new FileOutputStream(filename);
      BufferedOutputStream bos2 = new BufferedOutputStream(fos);
      bos2.write(compressed);
      bos2.flush();
      bos2.close();
      fos.close();

      oos.close();
      bos.close();
      baos.close();
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
  }

  public final static void writeObject(Object obj, String filename) {
    try {
      FileOutputStream fos = new FileOutputStream(filename);
      BufferedOutputStream bos = new BufferedOutputStream(fos);
      ObjectOutputStream oos = new ObjectOutputStream(bos);
      oos.writeObject(obj);
      oos.close();
      bos.close();
      fos.close();
    }
    catch (FileNotFoundException ex) {
      Logger.log.error("", ex);
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
  }

  public final static String writeObjectAsXML(Object obj) {
    XStream xstream = new XStream();
    String xmlString = xstream.toXML(obj);
    return xmlString;
  }

  public static void main(String[] args) {
    String objfilename = "cpm.buf";
    Object obj = Persistence.readObject(objfilename);
    writeObjectAsXML(obj, objfilename + ".xml");
  }

  public final static void writeObjectAsXML(Object obj, String filename) {
    String xmlString = writeObjectAsXML(obj);
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
      writer.write(xmlString);
      writer.flush();
      writer.close();
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
  }

}
