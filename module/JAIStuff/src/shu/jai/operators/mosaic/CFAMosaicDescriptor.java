package shu.jai.operators.mosaic;

import javax.media.jai.*;
import javax.media.jai.registry.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class CFAMosaicDescriptor
    extends OperationDescriptorImpl {
  // A map-like array of strings with resources information.
  private static final String[][] resources = {
      {
      "GlobalName", "CFAMosaic"}, {
      "LocalName", "CFAMosaic"}, {
      "Vendor", "shu.jai"}, {
      "Description", "CFA Mosaic image"}, {
      "DocURL", "http://"}, {
      "Version", "1.0"}, {
      "arg0Desc", "NorthWest Pixel Channel"}, {
      "arg1Desc", "NorthEast Pixel Channel"}, {
      "arg2Desc", "SouthWest Pixel Channel"}, {
      "arg3Desc", "SouthEast Pixel Channel"}
  };
  // An array of strings with the supported modes for this operator.
  private static final String[] supportedModes = {
      "rendered"};
  // An array of strings with the parameter names for this operator.
  private static final String[] paramNames = {
      "NorthWest Pixel Channel", "NorthEast Pixel Channel",
      "SouthWest Pixel Channel", "SouthEast Pixel Channel"};
  // An array of Classes with the parameters' classes for this operator.
  private static final Class[] paramClasses = {
      Character.class, Character.class, Character.class, Character.class, };
  // An array of Objects with the parameters' default values.
  private static final Object[] paramDefaults = {
      'G', 'R', 'B', 'G'};
//  private static final Range[] validParamValues = {
//      new Range(Character.class, // 1st parameter
//                new Character(Character., new Integer(Integer.MAX_VALUE)),
//      new Range(Integer.class, // 2nd parameter
//                new Integer(Integer.MIN_VALUE), new Integer(Integer.MAX_VALUE))
//  };
  // The number of sources required for this operator.
  private static final int numSources = 1;
  // A flag that indicates whether the operator is already registered.
  private static boolean registered = false;

  /**
   * The constructor for this descriptor, which just calls the constructor
   * for its ancestral class (OperationDescriptorImpl).
   */
  public CFAMosaicDescriptor() {
//    super(resources, numSources, paramClasses, paramNames, paramDefaults);
    super(resources, supportedModes, numSources, paramNames,
          paramClasses, paramDefaults, null);
  }

  /**
   * A method to register this operator with the OperationRegistry and
   * RIFRegistry.
   */
  public static void register() {
    if (!registered) {
      // Get the OperationRegistry.
      OperationRegistry op = JAI.getDefaultInstance().getOperationRegistry();
      // Register the operator's descriptor.
      CFAMosaicDescriptor desc =
          new CFAMosaicDescriptor();
      op.registerDescriptor(desc);
      // Register the operators's RIF.
      CFAMosaicRIF rif = new CFAMosaicRIF();
      RIFRegistry.register(op, "cfamosaic", "shu.jai", rif);
      registered = true;
    }
  }

}
