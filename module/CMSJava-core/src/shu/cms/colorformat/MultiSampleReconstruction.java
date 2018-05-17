package shu.cms.colorformat;

import java.io.*;
import java.util.*;
import javax.xml.bind.*;

import shu.cms.colorformat.cxf.*;
import shu.cms.colorformat.trans.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 將多次取樣的SpectraWinASCII檔案還原
 * 還原的方式為採用平均的方式
 * 還原的檔案為.CxF
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class MultiSampleReconstruction
    extends TransformTask {
  protected int samplingTimes;
  protected SpectraWinAsciiFile2CxF transfer = null;

  public MultiSampleReconstruction(int samplingTimes) {
    this.samplingTimes = samplingTimes;
  }

  public int getLengthOfTask() {
    if (transfer != null) {
      return transfer.getLengthOfTask();
    }
    else {
      return 0;
    }
  }

  public int getCurrent() {
    if (transfer != null) {
      return transfer.getCurrent();
    }
    else {
      return 0;
    }
  }

  public void transforming(String dir, String outputFilename) throws
      Exception {
    if (!isFileCountRight(dir, samplingTimes)) {
      throw new IllegalArgumentException(
          "sampleDir's file counts isn't match");
    }

    transfer = new SpectraWinAsciiFile2CxF();
    File file = File.createTempFile("reconstruction", null);
    transfer.transforming(dir,
                          file.getAbsolutePath());

    JAXBContext jc = JAXBContext.newInstance(
        "shu.cms.colorformat.cxf");
    Unmarshaller um = jc.createUnmarshaller();
    CXF cxf = (CXF) um.unmarshal(file);

    SampleSet sampleSet = cxf.getSampleSet().get(0);
    List<Sample> samples = sampleSet.getSample();
    List<Sample> ave = averageSamples(samples, samplingTimes);
    sampleSet.getSample().clear();
    sampleSet.getSample().addAll(ave);

    Marshaller m = jc.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                  Boolean.TRUE);

    m.marshal(cxf, new BufferedWriter(new FileWriter(outputFilename)));

    //結束
    current = lengthOfTask;
    done = true;
  }

  protected static List<Sample> averageSamples(List<Sample> samples,
      int aveCount) {
    List<Sample> ave = new ArrayList<Sample> ();
    int samplesIndex = 1, outputIndex = 0;
    List<Value> valTmp = null;

    for (Sample sample : samples) {
      List<Value> values = sample.getSampleAttribute().get(0).getSpectrum().
          getValue();

      if (valTmp == null) {
        //如果為null,代表做過一次平均,並且輸出資料了
        //因此一切重頭開始
        valTmp = values;
      }
      else {
        //加總
        int size = values.size();
        for (int x = 0; x < size; x++) {
          Value val = valTmp.get(x);
          double dVal1 = Double.parseDouble(val.getvalue());
          double dVal2 = Double.parseDouble(values.get(x).getvalue());
          dVal1 += dVal2;
          val.setvalue(Double.toString(dVal1));
        }
      }

      if (samplesIndex % aveCount == 0 && valTmp != null) {
        for (Value val : valTmp) {
          double dVal = Double.parseDouble(val.getvalue());
          val.setvalue(Double.toString(dVal / aveCount));
        }
        List<Value> list = sample.getSampleAttribute().get(0).getSpectrum().
            getValue();
        list.clear();
        list.addAll(valTmp);
        sample.setName("A" + outputIndex++);
        ave.add(sample);

        valTmp = null;
      }
      samplesIndex++;
    }
    return ave;
  }

  protected static boolean isFileCountRight(String dir, int samplingTimes) {
    File dirFile = new File(dir);
    int fileCount = dirFile.list().length;

    double intVal = fileCount / samplingTimes;
    double doubleVal = ( (double) fileCount) / samplingTimes;
    if (doubleVal != intVal) {
      return false;
    }
    return true;
  }

  public static void main(String[] args) throws Exception {
    MultiSampleReconstruction multisamplereconstruction = new
        MultiSampleReconstruction(3);
    multisamplereconstruction.transforming(
        "Measurement Files\\Calibration\\pr650\\Training_127 3x",
        "Training_127 3x.cxf");
  }
}
