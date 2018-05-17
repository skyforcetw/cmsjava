package auo.cms.hsv.saturation;

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
public class FastIntegerSaturationFormula
    implements SaturationFormula {
  public static void main(String[] args) {
    int turnPoint = 7;
    IntegerSaturationFormula f1 = new
        IntegerSaturationFormula( (byte) turnPoint, 4);
    FastIntegerSaturationFormula f2 = new
        FastIntegerSaturationFormula( (byte) turnPoint);
    for (int x = 0; x < 1000; x++) {
      double s = Math.random();
      double adj = Math.random();
      short saturation = (short) (1023 * s);
      short adjustValue = (short) (128 * adj - 64);
      double f1v = f1.getSaturartion(saturation, adjustValue);
      double f2v = f2.getSaturartion(saturation, adjustValue);
      if (f1v != f2v) {
        System.out.print(saturation + " " + adjustValue);
        System.out.println(" " + f1v + "/" + f2v);
      }

    }
//    f1.getSaturartion()

  }

  /**
   *
   * @param originalSaturation double 0~100%
   * @param adjustValue double
   * @return double
   * @deprecated
   */
  public double getSaturartion(double originalSaturation, double adjustValue) {
    return 0.0;
  }

  /**
   *
   * @param originalSaturation double 0~100%
   * @param newSaturation double 0~100%
   * @return double
   * @deprecated
   */
  public double getAdjustValue(double originalSaturation, double newSaturation) {
    return 0.0;
  }

  public static short getShortTurnPoint(byte turnPoint,
                                        int bit) {
    int shiftBit = 10 - bit;
    short shotTurnPoint = (short) ( (turnPoint << shiftBit) - 1);

    return shotTurnPoint;
  }

//  private byte turnPoint;
  private short shotTurnPoint;
  private int turnPointBit;
  private int newEndSaturation;
  public FastIntegerSaturationFormula(byte turnPoint) {
    turnPoint++;
//    this.turnPoint = turnPoint;
    turnPointBit = 4;
    shotTurnPoint = getShortTurnPoint(turnPoint, turnPointBit);

    boolean complement = true;
    int bitG2 = 11;
    int bitgain = 12;
    int bitOutputSaturation = 10;
    newEndSaturation = (1 << bitOutputSaturation) + (complement ? -1 : 0);
    n = 1;
    n2 = 1. / (Math.pow(2, bitG2) - 1);
    intg1g2Shift = bitG2 - 3 + (12 - bitgain);
    offsetShift = 10 + (bitgain - 12) + (3 - turnPointBit); //乘出來的offset做縮減

    g2InSelect = n / (turnPoint); // 3bit
    g2n2InSelect = g2InSelect / n2;
    int turnPointMax = (int) Math.pow(2, turnPointBit);
    g2NotInSelect = n / (turnPointMax - turnPoint);
    g2n2NotInSelect = g2NotInSelect / n2;
  }

  private int intg1g2Shift, offsetShift;
  private double n, n2;
  private double g2InSelect, g2n2InSelect, g2NotInSelect, g2n2NotInSelect;

  public short getSaturartion(short saturation, short adjustValue) {
    short result = -1;
    int intg1 = adjustValue; //9bit

    boolean select = false;
//    turnPoint++; //turnPoint +1

    select = saturation < shotTurnPoint;
    if (select) {
      //======================================================================
      // S' = S + LUT * (S/T)
      //    = S + (LUT/T) * S = S * ( 1+ (LUT/T) )
      //======================================================================
      int intg2_turnpoint = (int) g2n2InSelect; //9bit register
      int ints = 0;
      int originalIntg1g2 = saturation * intg2_turnpoint;
      boolean negative = intg1 < 0;

      int intg1g2 = originalIntg1g2 >> intg1g2Shift; //9+9=18 18-5=13
      int saturationOffset = intg1 * intg1g2; // 12 + 13 = 23 (22?)
      saturationOffset = Math.abs(saturationOffset);
      int saturationOffset2 = saturationOffset >> offsetShift; //22 - 10 = 12
      ints = negative ? saturation - saturationOffset2 :
          saturation + saturationOffset2; //12 + 12

      result = (short) ints;

    }
    else { //saturation >= shotTurnPoint
      //======================================================================
      // S' = S + LUT / (S-100%)/(100%-T)
      //    = S + LUT / (100%-T) * (S-100%)
      //    = S + (LUT/n) * (n/(100%-T) * (100%-S)
      //======================================================================
      int intg2_turnpoint = (int) g2n2NotInSelect; //9bit register

      int ints = 0;
      int originalIntg1g2 = (newEndSaturation - saturation) * intg2_turnpoint;
      boolean negative = intg1 < 0;

      int intg1g2 = originalIntg1g2 >> intg1g2Shift;
      int saturationOffset = intg1 * intg1g2;
      saturationOffset = Math.abs(saturationOffset);
      int saturationOffset2 = saturationOffset >> offsetShift;
      ints = negative ? saturation - saturationOffset2 :
          saturation + saturationOffset2; //12 + 12

      result = (short) ints;
    }
    return result;

  }

  /**
   * getName
   *
   * @return String
   */
  public String getName() {
    return "";
  }

}
