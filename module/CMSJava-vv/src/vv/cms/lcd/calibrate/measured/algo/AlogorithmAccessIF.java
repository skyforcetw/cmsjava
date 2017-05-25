package vv.cms.lcd.calibrate.measured.algo;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 將演算法初始化且提供一個統一存取的介面
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public interface AlogorithmAccessIF {
  public DeltaE00NearestAlgorithm getDeltaE00NearestAlogorithm();

  public LightnessNearestAlgorithm getLightnessNearestAlogorithm();

  public LightnessAroundAlgorithm getLightnessAroundAlgorithm();

  public CIEuv1960NearestAlgorithm getCIEuv1960NearestAlogorithm();

  public ChromaticAroundAlgorithm getChromaticAroundAlgorithm();

  public CompoundNearestAlgorithm getCompoundNearestAlogorithm();

  public StepAroundAlgorithm getStepAroundAlgorithm();

  public CubeNearestAlgorithm getCubeNearestAlgorithm();
}
