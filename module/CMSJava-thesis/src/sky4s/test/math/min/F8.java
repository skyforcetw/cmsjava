package sky4s.test.math.min;

/*
 F8.java
 This file belongs to BGRADES version 1.

 Copyright (C) Benny Raphael



 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or  any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.


 */

import java.text.*;

//import BGRADES.*;

public class F8 {

  static DecimalFormat df = new DecimalFormat();

  /*  Here specify the number of variables  */
  static int numVariables = 10;

  public static class SampleProblem
      extends Problem {

    int numVariables;

    double bestcost = 1e100; // To store the best cost so far

    public SampleProblem(int numVars, long numEvaluations) {
      super(numVars, numEvaluations); // Call super class constructor

      this.numVariables = numVars;

      double min = -512;
      double max = 511;
      int i;

      for (i = 0; i < numVars; i++) {
        axes[i] = new BGRADES.Axis(min, max, 1e-4);
      }
      this.threshold = 1e-6;
    }

    /*  The objective function  */
    public double costFunction(BGRADES.Point point) {

      double costerm, total, x1;
      int i;

      total = 1;
      for (i = 0; i < numVariables; i++) {
        x1 = point.x[i];
        total += x1 * x1 / 4000.;
      }

      costerm = 1;
      for (i = 0; i < numVariables; i++) {
        x1 = point.x[i];
        costerm *= Math.cos(x1 / Math.sqrt(i + 1));
      }

      total -= costerm;

      if (total < bestcost) {
        bestcost = total;
        System.out.println("" + numEvaluations + "\t" + bestcost);
      }

      // System.out.println("" + numEvaluations + "\t" + total + "\t" + bestcost);

      return total;

    }

    /*  The objective function  */
    public double partialDerivative(BGRADES.Point point, int variable) {

      double costerm, total, x1;
      int i;

      total = 0;
      x1 = point.x[variable];
      total += 2 * x1 / 4000.;

      costerm = 1;
      for (i = 0; i < numVariables; i++) {
        if (i == variable) {
          continue;
        }
        x1 = point.x[i];
        costerm *= Math.cos(x1 / Math.sqrt(i + 1));
      }

      total -= costerm;
      return total;

    }

    /*  The objective function  */
    public boolean gradient(BGRADES.Point point) {

      int i;

      for (i = 0; i < numVariables; i++) {
        point.gradient[i] = partialDerivative(point, i);
      }

      return true;

    }

    //----------------------------------------------------------------------------
  }

  public static void main(String[] args) throws Exception {
    long numEvaluations = 500000L; // Maximum number of evaluations.
    if (numVariables > 50) {
      numEvaluations = 10000 * numVariables;
    }

    BGRADES BGRADES = new BGRADES();
    SampleProblem problem = new SampleProblem(numVariables, numEvaluations);
    BGRADES.findMinimum(problem);

    System.out.println();
    System.out.println("Solution");
    for (int i = 0; i < numVariables; i++) {
      System.out.println("\t" + i + "\t" + df.format(problem.bestPoint.x[i]));
    }
    System.out.println("= " + df.format(problem.bestPoint.y));
    System.out.println("Evaluations = " + problem.numEvaluations);
    System.out.println();

  }
  //----------------------------------------------------------------------------



}
