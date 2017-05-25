package shu.cms.gma;

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
 * @todo M gma LightnessScaling
 */
public class LightnessScaling {

  protected static double produceKneeCompressGamma(double turnPoint,
      double outputLmin) {
    return Math.log(turnPoint - outputLmin) / Math.log(turnPoint);
  }

  protected static double produceShoulderCompressGamma(double turnPoint,
      double inputLmax,
      double outputLmax) {
    return Math.log(outputLmax - turnPoint) / Math.log(inputLmax - turnPoint);
  }

}
