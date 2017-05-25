package sky4s.test;

//package /*modify this according to your package*/;

import java.io.*;

/**
 * Captures the error or output messages from the console.
 */
class MessageCapturer
    extends Thread {
  private InputStream is_;
  private String type_;
  private String message_;

  public MessageCapturer(InputStream _is, String _type) {
    super();
    this.is_ = _is;
    this.type_ = _type;
    this.message_ = _type + ": ";
    this.start();
  }

  public String getMessage() {
    return this.message_;
  }

  public void run() {
    try {
      InputStreamReader isr = new InputStreamReader(this.is_);
      BufferedReader br = new BufferedReader(isr);
      String line = null;
      while ( (line = br.readLine()) != null) {
        //System.out.println(this.type_ +  ":" + line);
        this.message_ += line + "\n";
      }
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
}

/**
 * A Java class to call command-line python program. Python interpreter must be installed.
 *
 * Usage:
 *		PythonCaller pc = new PythonCaller();
 *		String cxf = pc.	("/../xxx.txt");
 *		//parse cxf as XML fragment.
 */
public class PythonCaller {
  private String errormessage_;
  private String outputmessage_;

  public PythonCaller() {
  }

  public String getErrorMessage() {
    return this.errormessage_;
  }

  public String getOutputMessage() {
    return this.outputmessage_;
  }

  /**
   * Convert the given text file to cxf by calling txt2cxf.py.
   * It returns the result filename or null if the conversion failed.
   *
   * @param txtFilePath - txt file path and name.
   * @return String as the filename of cxf file.
   */
  public String getCxfByPython(String txtFilePath) {
    try {
      String cmd = "C:/Python25/python txt2cxf.py " + txtFilePath + " " +
          txtFilePath + ".cxf";
      //String cmd = "dir";
      String[] params = cmd.split(" ");

      Runtime rt = Runtime.getRuntime();
      Process proc = rt.exec(params);

      MessageCapturer errcapturer = new MessageCapturer(proc.getErrorStream(),
          "ERROR");
      MessageCapturer outcapturer = new MessageCapturer(proc.getInputStream(),
          "OUTPUT");

      int exitValue = proc.waitFor();

      this.errormessage_ = errcapturer.getMessage();
      this.outputmessage_ = outcapturer.getMessage();

      return txtFilePath + ".cxf";
      //System.out.println("Exit value = "+exitValue);
    }
    catch (Throwable t) {
      t.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args) {
    PythonCaller pc = new PythonCaller();
    String outputfile = pc.getCxfByPython("data/EP510toISO_spectral.txt");
    if (outputfile == null) {
      System.out.println("Failed to convert!");
    }
    else {
      System.out.println("CXF is saved in file '" + outputfile + "'");
    }
  }
}
