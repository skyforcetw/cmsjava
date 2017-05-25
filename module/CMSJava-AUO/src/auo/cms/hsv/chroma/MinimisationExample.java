package auo.cms.hsv.chroma;

class MinimFunct
    implements MinimisationFunction {

  private double a = 0.0D;

  // evaluation function
  public double function(double[] x) {
    double z = a + x[0] * x[0] + 3.0D * Math.pow(x[1], 4);
    return z;
  }

  // Method to set a
  public void setA(double a) {
    this.a = a;
  }
}

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
// Class to evaluate the function z = a + x^2 + 3y^4
// where a is fixed and the values of x and y
// (x[0] and x[1] in this method) are the
// current values in the minimisation method.


// Class to demonstrate minimisation method, Minimisation nelderMead
public class MinimisationExample {

  public static void main(String[] args) {

    //Create instance of Minimisation
    MinimisationEdited min = new MinimisationEdited();

    // Create instace of class holding function to be minimised
    MinimFunct funct = new MinimFunct();

    // Set value of the constant a to 5
    funct.setA(5.0D);

    // initial estimates
    double[] start = {
        1.0D, 3.0D};

    // initial step sizes
    double[] step = {
        0.2D, 0.6D};

    // convergence tolerance
    double ftol = 1e-15;

    // Nelder and Mead minimisation procedure
    min.nelderMead(funct, start, step, ftol);

    // get the minimum value
//    double minimum = min.getMinimum();

    // get values of y and z at minimum
    double[] param = min.getParamValues();

    // Print results to a text file
//        min.print("MinimExampleOutput.txt");

    // Output the results to screen
    System.out.println("Minimum = " + min.getMinimum());
    System.out.println("Value of x at the minimum = " + param[0]);
    System.out.println("Value of y at the minimum = " + param[1]);
    System.out.println(min.getMinTest());

  }
}
