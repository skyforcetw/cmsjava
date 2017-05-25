package vv.cms.lcd.calibrate.measured.algo;

import shu.cms.colorspace.independ.*;
import vv.cms.measure.cp.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 * 實作部分AlogorithmAccessIF的功能, 依照algo的特性提供適當的建構式做初始化
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class AlogorithmAdapter
    implements AlogorithmAccessIF {

  /**
   *
   * @param white CIEXYZ
   * @param cpm CPCodeMeasurement
   * @param jndi JNDIInterface
   * @param quadrant DeltauvQuadrant
   * @param maxCode double
   * @param forceTrigger boolean
   * @deprecated
   */
  public AlogorithmAdapter(CIEXYZ white, CPCodeMeasurement cpm,
                           JNDIInterface jndi, DeltauvQuadrant quadrant,
                           double maxCode, boolean forceTrigger) {
    this.white = white;
    this.cpm = cpm;
    this.jndi = jndi;
    this.quadrant = quadrant;
    this.maxCode = maxCode;
    this.forceTrigger = forceTrigger;
    initAlogorithm();
  }

  public AlogorithmAdapter(CIEXYZ white, MeasureInterface mi,
                           JNDIInterface jndi, DeltauvQuadrant quadrant,
                           double maxCode, boolean forceTrigger) {
    this.white = white;
    this.mi = mi;
    this.jndi = jndi;
    this.quadrant = quadrant;
    this.maxCode = maxCode;
    this.forceTrigger = forceTrigger;
    initAlogorithm();
  }

  private boolean forceTrigger = false;
  private double maxCode = -1;
  private DeltauvQuadrant quadrant;

  public AlogorithmAdapter(CIEXYZ white, CPCodeMeasurement cpm,
                           JNDIInterface jndi, boolean forceTrigger) {
    this(white, cpm, jndi, null, -1, forceTrigger);
  }

  private void initAlogorithm() {
    deltaENearAlgo = new DeltaE00NearestAlgorithm(white, mi);
    lightnessNearAlgo = new LightnessNearestAlgorithm(white, mi, this.jndi);
    compoundNearAlogo = new CompoundNearestAlgorithm(white, mi, this.jndi,
        quadrant);
    uvNearAlgo = new CIEuv1960CompromiseAlgorithm(white, mi, quadrant);
    cubeNearAlgo = new CubeNearestAlgorithm(white, mi, this.jndi);

    stepAroundAlgo = maxCode != -1 ?
        new StepAroundAlgorithm(maxCode) : new StepAroundAlgorithm();
    chromaAroundAlgo = maxCode != -1 ? new
        ChromaticAroundAlgorithm(maxCode) : new ChromaticAroundAlgorithm();
    lightnessAroundAlgo = maxCode != -1 ? new
        LightnessAroundAlgorithm(maxCode) : new LightnessAroundAlgorithm();

    deltaENearAlgo.setForceTrigger(forceTrigger);
    lightnessNearAlgo.setForceTrigger(forceTrigger);
    compoundNearAlogo.setForceTrigger(forceTrigger);
    uvNearAlgo.setForceTrigger(forceTrigger);
    stepAroundAlgo.setForceTrigger(forceTrigger);
    chromaAroundAlgo.setForceTrigger(forceTrigger);
    lightnessAroundAlgo.setForceTrigger(forceTrigger);
  }

  protected CompoundNearestAlgorithm compoundNearAlogo;
  protected LightnessNearestAlgorithm lightnessNearAlgo;
  protected DeltaE00NearestAlgorithm deltaENearAlgo;
  protected CIEuv1960NearestAlgorithm uvNearAlgo;
  protected CubeNearestAlgorithm cubeNearAlgo;

  protected StepAroundAlgorithm stepAroundAlgo;
  protected ChromaticAroundAlgorithm chromaAroundAlgo;
  protected LightnessAroundAlgorithm lightnessAroundAlgo;

  private CIEXYZ white;
  private CPCodeMeasurement cpm;
  private MeasureInterface mi;
  private JNDIInterface jndi;

  /**
   * getDeltaE00NearestAlogorithm
   *
   * @return DeltaE00NearestAlogorithm
   */
  public DeltaE00NearestAlgorithm getDeltaE00NearestAlogorithm() {
    return deltaENearAlgo;
  }

  /**
   * getLightnessNearestAlogorithm
   *
   * @return LightnessNearestAlogorithm
   */
  public LightnessNearestAlgorithm getLightnessNearestAlogorithm() {
    return lightnessNearAlgo;
  }

  /**
   * getLightnessAroundAlgorithm
   *
   * @return LightnessAroundAlgorithm
   */
  public LightnessAroundAlgorithm getLightnessAroundAlgorithm() {
    return lightnessAroundAlgo;
  }

  /**
   * getCIEuv1960NearestAlogorithm
   *
   * @return CIEuv1960NearestAlogorithm
   */
  public CIEuv1960NearestAlgorithm getCIEuv1960NearestAlogorithm() {
    return uvNearAlgo;
  }

  /**
   * getChromaticAroundAlgorithm
   *
   * @return ChromaticAroundAlgorithm
   */
  public ChromaticAroundAlgorithm getChromaticAroundAlgorithm() {
    return chromaAroundAlgo;
  }

  /**
   * getCompoundNearestAlogorithm
   *
   * @return CompoundNearestAlogorithm
   */
  public CompoundNearestAlgorithm getCompoundNearestAlogorithm() {
    return compoundNearAlogo;
  }

  /**
   * getStepAroundAlgorithm
   *
   * @return StepAroundAlgorithm
   */
  public StepAroundAlgorithm getStepAroundAlgorithm() {
    return stepAroundAlgo;
  }

  public CubeNearestAlgorithm getCubeNearestAlgorithm() {
    return cubeNearAlgo;
  }

}
