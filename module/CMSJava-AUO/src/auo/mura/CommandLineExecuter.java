package auo.mura;

import java.io.FileNotFoundException;
import java.io.IOException;
import auo.mura.verify.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CommandLineExecuter {

  public static void main(String[] args) throws FileNotFoundException,
      IOException {
    if (args.length == 0) {
      System.out.println(
          "Argument format is: parameter_filename lut_filename input_image output_directory");
      return;
    }
    if (args.length != 4) {
      System.err.println("Argument number is not correct!");
    }
    String parameterFilename = args[0];
    String lutfilename = args[1];
    String imageFilename = args[2];
    String outputdir = args[3];

    DeMuraParameter parameter = new DeMuraParameter(parameterFilename);
    CorrectionData correctiondata = new CorrectionData(lutfilename, parameter);

//    correctiondata.storeToFlashFormat("demura/flash.hex", 256, 192);
//    MuraCompensationExecuter.execute(correctiondata, imageFilename, outputdir);

  }
}
