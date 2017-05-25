package shu.cms.dc.raw;

import java.io.*;
import javax.imageio.*;

import java.awt.image.*;

import org.apache.commons.collections.primitives.*;
import org.apache.commons.io.*;
import shu.math.*;
import shu.util.log.*;
import shu.math.array.DoubleArray;

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
public final class dcraw {

  public final static String DCRAW_EXECUTE = "dcrawMS.exe";
  public final static String DCRAW_DIR = "../lib/dcraw/";

  protected class ByteListener
      extends Thread {
    private InputStream is;
    private ArrayByteList buf;

    private Integer lock = new Integer(0);

    public byte[] getByteArray() {
      synchronized (lock) {
        return buf.toArray();
      }

    }

    protected ByteListener(InputStream is, ArrayByteList buf) {
      this.is = is;
      this.buf = buf;
//      buf.clear();
    }

//    private boolean stop = false;
    public void close() {
//      stop = true;
//      buf.clear();
      buf = null;
    }

    public void run() {
      try {
        synchronized (lock) {
          while (true) {
            int read = is.read();

            if (read == -1) {
              break;
            }
            buf.add( (byte) read);
          }
        }
      }
      catch (IOException ex) {
        Logger.log.error("", ex);
      }
    }

  }

  protected class StringListener
      extends Thread {
    private InputStream is;
    private StringBuilder buf = new StringBuilder();

    private Integer lock = new Integer(0);
    public String getString() {
      synchronized (lock) {
        return buf.toString();
      }
    }

    protected StringListener(InputStream is) {
      this.is = is;
    }

    private boolean stop = false;
    public void close() {
      stop = true;
    }

    public void run() {
      try {
        synchronized (lock) {
          while (true) {
            int read = is.read();
            if (read == -1 || stop) {
              break;
            }
            buf.append( (char) read);
          }
        }
      }
      catch (IOException ex) {
        Logger.log.error("", ex);
      }
    }

  }

  private byte[] imageByteArray;
//  private int bufferSize = 16777216;

  private String execdcrawVerbose(String arguments,
                                  boolean getImageAsByteArray) {

    String cmd = getCommand(arguments);
    Runtime rt = Runtime.getRuntime();
    try {
      Process p = rt.exec(cmd);
      StringListener listener = new StringListener(p.getErrorStream());
      listener.start();
      ByteListener imageListener = null;
      if (getImageAsByteArray) {
        imageListener = new ByteListener(p.getInputStream(), new ArrayByteList());
        imageListener.start();
      }
      int result = p.waitFor();
      if (getImageAsByteArray) {
        imageByteArray = imageListener.getByteArray();
        imageListener.close();
      }
      String string = listener.getString();
      return string;
    }
    catch (InterruptedException ex) {
      Logger.log.error("", ex);
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }

    return null;
  }

  protected final static String getCommand(String arguments) {
    String cmd = DCRAW_DIR + DCRAW_EXECUTE + " " + arguments;
    return cmd;
  }

//  protected int execdcraw(String arguments) {
//    String cmd = getCommand(arguments);
//    try {
//      return Utils.execAndWaitFor(cmd);
//    }
//    catch (InterruptedException ex) {
//      Logger.log.error("", ex);
//    }
//    catch (IOException ex) {
//      Logger.log.error("", ex);
//    }
//    return -1;
//  }

  protected String getOptions() {
    StringBuilder buf = new StringBuilder();
    if (verbose) {
      buf.append("-v ");
    }
    if (this.standardOutput) {
      buf.append("-c ");
    }
    if (this.cameraTimestamp) {
      buf.append("-z ");
    }
    if (whitebalance != null) {
      switch (whitebalance) {
        case Camera:
          buf.append("-w ");
          break;
        case Average:
          buf.append("-a ");
          break;
        case Custom:
          buf.append("-r " + DoubleArray.toString(this.customeWhitebalance) +
                     " ");
          break;
      }
    }
    if (waveletDenoise) {
      buf.append("-n " + this.denoiseNumber + " ");
    }
    if (!this.autoFlipImage) {
      buf.append("-t " + this.flipImageNumber + " ");
    }
    if (colorSpace != null) {
      switch (colorSpace) {
        case Raw:
          buf.append("-o 0 ");
          break;
        case sRGB:
          buf.append("-o 1 ");
          break;
        case AdobeRGB:
          buf.append("-o 2 ");
          break;
        case WideGamut:
          buf.append("-o 3 ");
          break;
        case ProPhoto:
          buf.append("-o 4 ");
          break;
        case XYZ:
          buf.append("-o 5 ");
          break;
      }
    }
    if (documentMode) {
      switch (document) {
        case WithScale:
          buf.append("-d ");
          break;
        case WithoutScale:
          buf.append("-D ");
          break;
      }
    }
    if (!autoBrighten) {
      buf.append("-W ");
    }
    if (this.demosaic != null) {
      switch (demosaic) {
        case Bilinear:
          buf.append("-q 0 ");
          break;
        case VNG:
          buf.append("-q 1 ");
          break;
        case PPG:
          buf.append("-q 2 ");
          break;
        case AHD:
          buf.append("-q 3 ");
          break;
      }
    }
    if (this.halfsize) {
      buf.append("-h ");
    }
    if (this.fourColors) {
      buf.append("-f ");
    }
    if (this.linear16bit) {
      buf.append("-4 ");
    }
    if (this.outputTiff) {
      buf.append("-T ");
    }
    return buf.toString();
  }

  private String verboseMessage;
//  private ArrayByteList buf = new ArrayByteList();

  public BufferedImage decodeAsBufferedImage(String filename) {
    this.standardOutput = true;
    _decode(filename, true);
    this.standardOutput = false;

    InputStream is = new ByteArrayInputStream(this.imageByteArray);
    try {
      BufferedImage img = ImageIO.read(is);
      return img;
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    return null;
  }

  private void _decode(String filename, boolean getImageAsByteArray) {
    File file = new File(filename);
    if (!file.exists()) {
      throw new IllegalStateException(filename + " is not exists!");
    }
    String options = getOptions();
    verboseMessage = execdcrawVerbose(options + " " + filename,
                                      getImageAsByteArray);
  }

  public String decode(String filename) {
    _decode(filename, false);
    String filenameonly = FilenameUtils.removeExtension(filename);
    if (outputTiff) {
      filenameonly = filenameonly + ".tiff";
    }
    else {
      filenameonly = filenameonly + ".ppm";
    }
    return filenameonly;
  }

  public dcraw() {

  }

  /**
   * Change file dates to camera timestamp
   * �N���ͤ��ɮת��̫�s���ɶ��]�w���Ӥ�����ک���ɶ��C
   * @param cameraTimestamp boolean
   */
  public void setCameraTimestamp(boolean cameraTimestamp) {
    this.cameraTimestamp = cameraTimestamp;
  }

  /**
   * It sets a custom user white balance. These 4 values are the multipliers
   *  that will scale linearly all levels found in the RGBG channels in that
   *  order. The white balance means a scaling of image levels therefore all
   *  levels will move from their original positions which could not be
   *  desirable in certain cases. If we are not to perform any white balance we
   *  will use the option -r 1 1 1 1. We will study this feature more deeply
   *  later as it is a very important concept.
   *
   * @param customeWhitebalance double[]
   */
  public void setCustomeWhitebalance(double[] customeWhitebalance) {
    this.customeWhitebalance = customeWhitebalance;
  }

  public void setDocumentMode(boolean documentMode) {
    this.documentMode = documentMode;
  }

  /**
   * It generates a linear 16-bit file instead of an 8-bit gamma corrected file
   *  which is the default. I always use this option.
   *
   * �b�i�椺���k�t��ɱN RGB ���|���C��C �p�G�Q�� VNG �άO AHD �����k�Ӳ��ͼv�����G�X
   * �{�檬���T���ܡA�Х[�W�o�ӿﶵ�Ӵ�����T�C
   * @param fourColors boolean
   */
  public void setFourColors(boolean fourColors) {
    this.fourColors = fourColors;
  }

  /**
   * ��X�v�������e��b�A�۹諸�ѽX�t�פ]�|�� -q 0�֤W�@���C
   * @param halfsize boolean
   */
  public void setHalfsize(boolean halfsize) {
    this.halfsize = halfsize;
  }

  /**
   * ��X 16-bit �u�ʮ榡���v���A���վ���ɪ� White Point �P Gamma ���u�ȡC
   * �P�ɦb���O�̭Y�O���ϥ� -b �Ѽƪ��ܡA�ϥ� -4 ��]�|���� .B -b �ȡC
   * @param linear16bit boolean
   */
  public void setLinear16bit(boolean linear16bit) {
    this.linear16bit = linear16bit;
  }

  /**
   * It outputs a TIFF image file instead of PPM.
   * ��X TIFF �榡�]��������ơ^���v���ɮסC
   * @param outputTiff boolean
   */
  public void setOutputTiff(boolean outputTiff) {
    this.outputTiff = outputTiff;
  }

  /**
   * Print verbose messages.
   * �b���Fĵ�i�P���~�T���~������B�~�������T�C
   * @param verbose boolean
   */
  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }

  /**
   * Set threshold for wavelet denoising
   * ���T�����{�ɭ�
   * �Q�ηL�i�k�Ӯ������T�P�ɫO�s�v���Ӹ`�C���T�����{�ɭȧڭ̫�ĳ�ϥ� 100 �� 1000 ��������
   * �ȡC
   * @param denoiseNumber double
   */
  public void setDenoiseNumber(double denoiseNumber) {
    this.denoiseNumber = denoiseNumber;
  }

  public void setWaveletDenoise(boolean waveletDenoise) {
    this.waveletDenoise = waveletDenoise;
  }

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * Sets the quality of the Bayer demosaicing algorithm employed. The more
   *  quality the more complex the algorithm will be and thus less quick,
   *  however DCRAW is very fast in all of them. Possible values are:
   *  0=bilinear, 1=VNG, 2=PPG, 3=AHD. I always use the last value which is an
   *  adaptive algorithm providing very good results, although according to the
   *  author DCRAW will use by default the best algorithm for each camera model.
   *  In this way for Fuji cameras the method -q 2 is better than -q 3.
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  public static enum Whitebalance {
    /**
     * Use camera white balance, if possible.
     * If DCRAW manages to find it, it will use the white balance that was
     *  adjusted in the camera at shooting.
     * �ϥά۾��ҫ��w���ե��ŭȡC�p�G�b�ɮפ��䤣�즹����ơA���ĵ�i�T���ç�Ψ�L�覡�վ�
     * �ե��šC
     */
    Camera,
    /**
     * Average the whole image for white balance.
     * Performs an automatic white balance calculation over the whole image.
     * �Q�ξ�Ӽv���������Ȩӭp��ե��šC
     */
    Average,
    /**
     * Set custom white balance
     */
    Custom
  }

  private Whitebalance whitebalance;
  /**
   * Set custom white balance
   */
  private double[] customeWhitebalance = new double[] {
      1, 1, 1, 1};
  private boolean verbose = false;
  private boolean linear16bit = false;
  private boolean outputTiff = true;
  private boolean cameraTimestamp = false;
  private double denoiseNumber = 100;
  private boolean waveletDenoise = false;

  public static enum Document {
    /**
     * Extracts an image with the pure RAW data without any demosaicing or
     *  scaling applied. It is very useful to analyse the real captured levels
     *  in the sensor's native range of 12, 14 or 16 bits.
     * �N��l�v���ɮפ��e�H�Ƕ��覡�ѽX�A���ϥΤ����k�ӧﵽ�v���~��C�o�ӼҦ��̾A�X½����
     */
    WithScale,
    /**
     * As the previous command, it does not perform any demosaicing but goes one
     *  step ahead in the development process since it adjusts black and
     *  saturation points, white balance and rescales to the output 16-bit
     *  range. It is very interesting to get linearized (for substracting the
     *  black point) undemosaiced data in a 16-bit scale with
     *  dcraw -d -r 1 1 1 1.
     * ���Ҧ��P -d�@�˷|��X�Ƕ��v���A ���O�v���ѽX�L�{�������g�L����B�z�C
     */
    WithoutScale
  }

  public void setDocument(Document document) {
    this.document = document;
  }

  /**
   * Automatically brighten the image
   * @param autoBrighten boolean
   */
  public void setAutoBrighten(boolean autoBrighten) {
    this.autoBrighten = autoBrighten;
  }

  public void setAutoFlipImage(boolean autoFlipImage) {
    this.autoFlipImage = autoFlipImage;
  }

  public void setFlipImageNumber(int flipImageNumber) {
    this.flipImageNumber = flipImageNumber;
  }

  public void setWhitebalance(shu.cms.dc.raw.dcraw.Whitebalance whitebalance) {
    this.whitebalance = whitebalance;
  }

  public void setColorSpace(shu.cms.dc.raw.dcraw.ColorSpace colorSpace) {
    this.colorSpace = colorSpace;
  }

  public void setDemosaic(shu.cms.dc.raw.dcraw.Demosaic demosaic) {
    this.demosaic = demosaic;
  }

  private Document document = Document.WithScale;
  private boolean documentMode = false;

  private boolean halfsize = false;
  private boolean fourColors = false;
  private boolean autoFlipImage = true;
  private int flipImageNumber = 0;
  private boolean autoBrighten = true;
  private Demosaic demosaic = null;
  private boolean standardOutput = false;

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * �p�G�z�S���ϥ� -p �ﶵ���ܡA�z�i�H�ϥ� -o [0-5] �Ѽƨӫ��w��X�ɮת� colorspace�C���ѼƭȪ��w�q�p�U�G
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  public static enum ColorSpace {
    Raw, sRGB, AdobeRGB, WideGamut, ProPhoto, XYZ
  }

  private ColorSpace colorSpace = null;

  /**
   *
   * <p>Title: Colour Management System</p>
   *
   * <p>Description: a Colour Management System by Java</p>
   * Sets the output colour profile being possible the values: 0=none
   *  (no colour management), 1=sRGB, 2=AdobeRGB, 3=WideGamut, 4=ProPhoto,
   *  5=XYZ. To convert to a colour space means a matrix transformation of the
   *  levels of the image and in some cases this could not be desirably. Not to
   *  perform any transformation we will use the option -o 0.
   *
   * <p>Copyright: Copyright (c) 2008</p>
   *
   * <p>Company: skygroup</p>
   *
   * @author skyforce
   * @version 1.0
   */
  public static enum Demosaic {
    /**
     * �ϥΫ~�貤���A���t�׸��֪����u�� (bilinear) �����k�Ӷi��v�����ѽX�C
     */
    Bilinear,
    /**
     * �ϥ��ܼƺ��h (Variable Number of Gradients, VNG) �����k�Ӷi��v�����ѽX�C
     */
    VNG,
    /**
     * �ϥι����ϼ˲ոs (Patterned Pixel Grouping, PPG) �����k�C
     */
    PPG,
    /**
     * �ϥ� Adaptive Homogeneity-Directed (AHD) �����k�Ӷi��v�����ѽX�C
     */
    AHD
  }

  public static void main(String[] args) throws IOException {
    String filename = "DSC_3831.NEF";
    dcraw dcraw = new dcraw();
//    dcraw.setVerbose(true);
//    dcraw.setHalfsize(true);
    long start = System.currentTimeMillis();

    start = System.currentTimeMillis();
    for (int x = 0; x < 5; x++) {
      dcraw.decode(filename);
      System.out.println(x);
    }
    System.out.println(System.currentTimeMillis() - start);

    start = System.currentTimeMillis();
    for (int x = 0; x < 5; x++) {
      BufferedImage img = dcraw.decodeAsBufferedImage(filename);
      System.out.println(x);
    }
    System.out.println(System.currentTimeMillis() - start);
  }

  public String getVerboseMessage() {
    return verboseMessage;
  }
}
