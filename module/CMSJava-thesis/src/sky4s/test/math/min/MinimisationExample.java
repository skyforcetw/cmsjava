package sky4s.test.math.min;

import flanagan.math.*;

// Class to evaluate the function z = a + x^2 + 3y^4
// where a is fixed and the values of x and y
// (x[0] and x[1] in this method) are the
// current values in the minimisation method.
class MinimFunct
    implements MinimisationFunction {

  // evaluation function
  public double function(double[] x) {
//    System.out.println(Arrays.toString(x));
//    double z = a + x[0] * x[0] + 3.0D * Math.pow(x[1], 4);
    double z = Math.tan(x[0]) - (Math.PI / 2. + x[0]);
    return z;
  }

}

// Class to demonstrate minimisation method, Minimisation nelderMead
public class MinimisationExample {

  public static void main(String[] args) {

    //Create instance of Minimisation
    Minimisation min = new Minimisation();

    // Create instace of class holding function to be minimised
    MinimFunct funct = new MinimFunct();

    // Set value of the constant a to 5
//    funct.setA(5.0D);

    // initial estimates
    double[] start = {
        0};

    // initial step sizes
    double[] step = {
        0.1D};

    // convergence tolerance
    double ftol = 1e-15;

//    min.addConstraint(0, 1, 1.);
//        min.addConstraint(0, -1, .5);
//    min.addConstraint(1, -1, 0.);
    min.addConstraint(0, -1, 0);
    min.addConstraint(0, 1, Math.PI / 2.);

    // Nelder and Mead minimisation procedure
    min.nelderMead(funct, start, step, ftol);

    // get the minimum value
    double minimum = min.getMinimum();

    // get values of y and z at minimum
    double[] param = min.getParamValues();

    // Print results to a text file
    min.print("MinimExampleOutput.txt");

    // Output the results to screen
    System.out.println("Minimum = " + min.getMinimum());
    System.out.println("Value of x at the minimum = " + param[0]);
//    System.out.println("Value of y at the minimum = " + param[1]);

  }
}
