package shu.cms.lcd.calibrate.tester;

import shu.cms.colorspace.depend.*;

public final class CalibratedResult {
  public RGB[] modelResult;
  public RGB[] luminanceBasedResult;
  public RGB[] whiteBasedResult;
  public RGB[] greebBasedResult;
  public RGB[] luminanceBased2Result;

  public RGB[][] results;

  public final RGB[] getFinalResult() {
    return results[results.length - 1];
  }

  public CalibratedResult(RGB[] ...results) {
    this.results = results;
  }

  /**
   *
   * @param modelResult RGB[]
   */
  public CalibratedResult(RGB[] modelResult
      ) {
    this.modelResult = modelResult;
    this.results = new RGB[][] {
        modelResult};
  }

  /**
   *
   * @param modelResult RGB[]
   * @param luminanceBasedResult RGB[]
   * @param whiteBasedResult RGB[]
   * @param greebBasedResult RGB[]
   * @param luminanceBased2Result RGB[]
   * @deprecated
   */
  public CalibratedResult(RGB[] modelResult,
                          RGB[] luminanceBasedResult,
                          RGB[] whiteBasedResult,
                          RGB[] greebBasedResult,
                          RGB[] luminanceBased2Result
      ) {
    this.modelResult = modelResult;
    this.luminanceBasedResult = luminanceBasedResult;
    this.whiteBasedResult = whiteBasedResult;
    this.greebBasedResult = greebBasedResult;
    this.luminanceBased2Result = luminanceBased2Result;
  }

}
