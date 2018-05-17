package shu.cms.lcd;

import java.util.*;

import flanagan.math.*;
import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.math.*;
import shu.math.array.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 以最佳化的方式找到最合理的漏光值(flare)
 * Ref: Estimating Black-Level Emissions of computer-controlled Displays,
 * Roy S.Berns et al(2002).
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class FlareCalculator
    implements MinimisationFunction {

  protected double getVariance(RGBBase.Channel ch, CIEXYZ flare) {
    List<Patch> patchList = lcdTarget.filter.oneValueChannel(ch);
    int size = patchList.size();
    List<CIExyY> xyYList = new ArrayList<CIExyY> (size);
    double avex = 0, avey = 0;

    for (int x = 0; x < size; x++) {
      CIEXYZ XYZ = patchList.get(x).getXYZ();
      CIEXYZ pureXYZ = CIEXYZ.minus(XYZ, flare);
      CIExyY xyY = CIExyY.fromXYZ(pureXYZ);
      xyYList.add(xyY);
      avex += xyY.x;
      avey += xyY.y;
    }
    avex /= size;
    avey /= size;

    double[] distArray = new double[size];
    for (int x = 0; x < size; x++) {
      CIExyY xyY = xyYList.get(x);
      double d = Maths.RMSD(new double[] {xyY.x, xyY.y}, new double[] {avex,
                            avey});
      distArray[x] = d;
    }

    double aveDist = Maths.mean(distArray);
    double variance = 0;

    for (int x = 0; x < size; x++) {
      variance += Maths.sqr(distArray[x] - aveDist);
    }

    return variance / (size - 1);
  }

  public double function(double[] flareXYZValues) {
    CIEXYZ flare = new CIEXYZ(flareXYZValues);
    return getVariance(RGBBase.Channel.R, flare) +
        getVariance(RGBBase.Channel.G, flare) +
        getVariance(RGBBase.Channel.B, flare);

  }

  protected LCDTarget lcdTarget;

  public FlareCalculator(LCDTarget lcdTarget) {
    this.lcdTarget = lcdTarget;
  }

  public CIEXYZ getFlare() {
    //Create instance of Minimisation
    Minimisation min = new Minimisation();

//    CIEXYZ flare = lcdTarget.getDarkestPatch().getXYZ();
    CIEXYZ flare = lcdTarget.getBlackPatch().getXYZ();
    double[] flareValues = flare.getValues();

// initial estimates
    double[] start = flareValues;

// initial step sizes
    double[] step = DoubleArray.times(start, 1. / 2);

// convergence tolerance
    double ftol = 1e-15;

    min.addConstraint(0, -1, 0);
    min.addConstraint(1, -1, 0);
    min.addConstraint(2, -1, 0);

// Nelder and Mead minimisation procedure
    min.nelderMead(this, start, step, ftol);
//    min.nelderMead(this, start);

// get values of y and z at minimum
    double[] param = min.getParamValues();
    return new CIEXYZ(param, lcdTarget.getLuminance());
  }

  public static void main(String[] args) {
    LCDTarget.setRGBNormalize(false);
    LCDTarget target = LCDTarget.Instance.get("CPT_17inch_Demo2",
                                              LCDTarget.Source.CA210,
                                              LCDTarget.Room.Dark,
                                              LCDTarget.TargetIlluminant.
                                              Native,
                                              LCDTargetBase.Number.Ramp1792, null, null);

    FlareCalculator cal = new FlareCalculator(target);
    System.out.println("estimate: " + cal.getFlare());
    System.out.println("real: " + target.getDarkestPatch().getXYZ());

    FlareProportionCalculator propCal = new FlareProportionCalculator(target);
    System.out.println("flareProportionValue: " +
                       propCal.getFlareProportionValue());
  }
}
