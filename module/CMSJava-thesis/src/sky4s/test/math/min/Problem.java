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



/*  class representing a PGSL problem
 *
 */
public abstract class Problem {
  public String title;

  public int numVars;
  public long maxNumEvaluations;
  public double threshold; /*  The minimum cost at which iterations stop  */

  /*  All variable axes  */
  public BGRADES.Axis[] axes;

  public double globalMinimum;
  public BGRADES.Point bestPoint;
  public BGRADES.Point currentPoint;
  public BGRADES.Point nextPoint;

  /*  The starting point for the gradient descent.  If this is null, a random point is used  */
  public BGRADES.Point startingPoint = null;

  public long numEvaluations; /* Current iteration  */

  public boolean doRestart = true; /*  Whether the algorithm should restart if it converges before finishing all iterations  */

  public double initialStepSize = 0.0; /*  If the initial step size is 0.0, it is taken as 1% of the diagonal of the domain  */

  protected double currentStepSize = 0.0;

  /*  This function has to be defined by the user.  It is the objective function.
   It is called by BGRADES algorithm to evaluate points that are generated.
          The argument is the point. The parameter parameter values are in the array point.x
   */
  public abstract double costFunction(BGRADES.Point point);

  /*  This function has to be defined by the user.  It is the derivative  of objective function.
          It is called by BGRADES to evaluate points that are generated.
          The argument is the point containing all parameter values.
          This method should populate the elements of the array point.gradient.
          Returns success or failure
   */
  public abstract boolean gradient(BGRADES.Point point);

  /*  constructor
   */
  public Problem(int numVars, long numEvaluations) {

    title = "";
    this.numVars = numVars;
    this.maxNumEvaluations = numEvaluations;
    this.numEvaluations = 0;
    if (numVars == 0) {
      return;
    }

    axes = new BGRADES.Axis[numVars];
    bestPoint = new BGRADES.Point(numVars);
    currentPoint = new BGRADES.Point(numVars);
    nextPoint = new BGRADES.Point(numVars);

  }

  /*  The following function generates value for each variable using the current PDF
   */
  public void generateRandomPoint(BGRADES.Point point) {
    double t;
    int j;

    for (j = 0; j < numVars; j++) {
      t = Math.random();
      point.x[j] = axes[j].min + t * (axes[j].max - axes[j].min);
    }
    point.y = costFunction(point);
  }

};
/*---------------------------------------------------------------------------*/
