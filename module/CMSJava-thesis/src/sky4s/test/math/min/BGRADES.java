package sky4s.test.math.min;

/*
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

public class BGRADES {

  static DecimalFormat df = new DecimalFormat();
  public boolean BGRADES_verbose = false;

  /*  Turn this on to generate a log file  statistics.txt */
  public int PGSL_Generate_Log = 0;

  /* Structure to represent a point
   */
  public static class Point {
    public int numVars;
    /*  VAlues of variables  */
    public double[] x;
    /*  Evaluation of the point  */
    public double y;

    /*  gradient vector at this point */
    public double[] gradient;

    public Point(int numVars) {
      this.numVars = numVars;
      if (numVars == 0) {
        return;
      }
      x = new double[numVars];
      gradient = new double[numVars];
    }

    public void copy(Point to) {
      int i;
      if (to.numVars != numVars) {
        to.numVars = numVars;
        to.x = new double[numVars];
        to.gradient = new double[numVars];
      }
      for (i = 0; i < numVars; i++) {
        to.x[i] = x[i];
        to.gradient[i] = gradient[i];
      }
      to.y = y;
    }

  };
  /*----------------------------------------------------------------*/

  /*  Represents an axis of a variable
   *
   */
  public static class Axis {

    public double min, max; /*  Minimum and maximum values  */
    public double precision; /*  Half the precision of the variable  */

    public Axis(double min, double max, double precision) {

      this.min = min;
      this.max = max;
      this.precision = precision;
    }

  };

  /*   Selects a new random starting point - this is called after a convergence
   */
  void doRestart(Problem problem) {

//if (BGRADES_verbose)
    System.out.println("Restarting");

    problem.generateRandomPoint(problem.currentPoint);
    problem.numEvaluations++;
    problem.currentStepSize = problem.initialStepSize;

  }

  /*---------------------------------------------------------------------------*/



  /*  Check if the problem has converged  */
  public boolean checkConverged(Problem problem) {

    for (int j = 0; j < problem.numVars; j++) {
      double dx = Math.abs(problem.nextPoint.x[j] - problem.currentPoint.x[j]);
      //System.out.println("dx = " + dx);
      if (dx >= problem.axes[j].precision) {
        return false;
      }
    }
    return true;
  }

  public double calculateDiagonal(Problem problem) {

    double sum = 0;

    for (int j = 0; j < problem.numVars; j++) {
      double dx = problem.axes[j].max - problem.axes[j].min;
      sum += dx * dx;
    }
    sum = Math.sqrt(sum);
    return sum;
  }

  public double calculateNorm(int numVars, double[] x) {

    double sum = 0;

    for (int j = 0; j < numVars; j++) {
      sum += x[j] * x[j];
    }
    sum = Math.sqrt(sum);
    return sum;
  }

  public void nextStep(Problem problem) {

    problem.gradient(problem.currentPoint);
    double norm = calculateNorm(problem.numVars, problem.currentPoint.gradient);
    if (norm <= Double.MIN_VALUE) {
      norm = 2 * Double.MIN_VALUE;
    }

    for (int j = 0; j < problem.numVars; j++) {
      problem.nextPoint.x[j] = problem.currentPoint.x[j] -
          problem.currentStepSize * problem.currentPoint.gradient[j] / norm;
      if (problem.nextPoint.x[j] < problem.axes[j].min) {
        problem.nextPoint.x[j] = problem.axes[j].min;
      }
      if (problem.nextPoint.x[j] > problem.axes[j].max) {
        problem.nextPoint.x[j] = problem.axes[j].max;
      }
    }
    problem.nextPoint.y = problem.costFunction(problem.nextPoint);
    problem.numEvaluations++;
  }

  /*  This is the main optimisation routine that uses gradient descent
   */
  public double findMinimum(Problem problem) {

    if (problem.initialStepSize == 0.0) {
      problem.initialStepSize = 0.01 * calculateDiagonal(problem);
    }
    problem.currentStepSize = problem.initialStepSize;

    /*  Initial best point  */
    if (problem.startingPoint == null) {
      problem.generateRandomPoint(problem.bestPoint);
    }
    else {
      problem.startingPoint.copy(problem.bestPoint);
    }

    problem.bestPoint.copy(problem.currentPoint);

    while (problem.numEvaluations < problem.maxNumEvaluations) {
      nextStep(problem); // Computes the gradient and calculates the next point with the current step size
      if (problem.nextPoint.y < problem.currentPoint.y) {
        problem.nextPoint.copy(problem.currentPoint);
        problem.currentStepSize *= 1.1; // This is my secret..
      }
      else {
        problem.currentStepSize /= 2.;
        if (checkConverged(problem)) {
          if (problem.doRestart == false) {
            break;
          }
          doRestart(problem);
        }
      }
      if (problem.currentPoint.y < problem.bestPoint.y) {
        problem.currentPoint.copy(problem.bestPoint);
      }
      if (problem.bestPoint.y < problem.threshold) {
        break;
      }
    }
    return problem.bestPoint.y;

  }
  /*----------------------------------------------------------------*/

}
/*----------------------------------------------------------------*/
