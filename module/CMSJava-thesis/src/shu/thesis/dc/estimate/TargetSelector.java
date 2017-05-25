package shu.thesis.dc.estimate;

import java.util.*;

import flanagan.analysis.*;
import shu.cms.*;
import shu.cms.dc.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * �ɪ��ܾ�
 * ��q�ҶǤJ���ɪ�,�C�X�Ҧ��i�઺���X.
 * �Ԩ� selectTarget
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class TargetSelector {

  protected final static int possibleSetTotal(int m) {
    int total = 0;
    for (int x = 1; x <= m; x++) {
      total += Stat.binomialCoeff(m, x);
    }
    return total;
  }

  protected final static int[][] possibleSet(int m) {
    int total = possibleSetTotal(m);
    int[][] set = new int[total][];
    int index = 0;

    int[] digit = new int[m];
    for (int i = 0; i < digit.length; i++) {
      digit[i] = 0;
    }

    while (true) { // ��Ĥ@��0�A�ñN���e�Ҹg�L�������ܬ�0
      int i;
      for (i = 0; i < digit.length && digit[i] == 1; digit[i] = 0, i++) {
        ;
      }
      if (i == digit.length) { // �䤣��0
        break;
      }
      else { // �N�Ĥ@�ӧ�쪺0�ܬ�1
        digit[i] = 1; // ��Ĥ@��1�A�ðO��������m
      }
      for (i = 0; i < digit.length && digit[i] == 0; i++) {
        ;
      }
      ArrayList<Integer> arrayList = new ArrayList<Integer> ();
      arrayList.add(i);
      for (int j = i + 1; j < digit.length; j++) {
        if (digit[j] == 1) {
          arrayList.add(j);
        }
      }
      set[index++] = toIntArray(arrayList.toArray());
    }
//    Integer[] array=(Integer[])arrayList.toArray();
//    toIntArray((Integer[])arrayList.toArray());
    return set;
  }

  protected final static int[] toIntArray(Object[] intergerArray) {
    int size = intergerArray.length;
    int[] intArray = new int[size];
    for (int x = 0; x < size; x++) {
      intArray[x] = ( (Integer) intergerArray[x]).intValue();
    }
    return intArray;
  }

  /**
   * �qtargetArray,�C�X�Ҧ��i�઺���X
   * @param targetArray DCTarget[]
   * @return DCTarget[][]
   */
  public final static DCTarget[][] selectTarget(DCTarget[] targetArray) {
    int setSize = targetArray.length;
    int[][] possibleSet = possibleSet(setSize);
    int possibleSetSize = possibleSet.length;

    DCTarget[][] selectTargetArray = new DCTarget[possibleSetSize][];

    for (int x = 0; x < possibleSetSize; x++) {
      int[] set = possibleSet[x];
      int size = set.length;
      DCTarget[] trainingTargetArray = new DCTarget[size];
      for (int y = 0; y < size; y++) {
        trainingTargetArray[y] = targetArray[set[y]];
      }
      selectTargetArray[x] = trainingTargetArray;
    }

    return selectTargetArray;
  }

  public static void main(String[] args) {
    LightSource.i1Pro[] lightSource = new LightSource.i1Pro[] {
        LightSource.i1Pro.D50, LightSource.i1Pro.D65,
        LightSource.i1Pro.F2,
        LightSource.i1Pro.F8, LightSource.i1Pro.F10};
    Spectra[] lightSourceSpectra = DCUtils.getSpectra(lightSource);
    double[] factor = DCUtils.produceNormalFactorToEqualLuminance(
        lightSourceSpectra);

    DCTarget D50Target = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                               lightSource[0],
                                               factor[0], DCTarget.Chart.CCSG);
    DCTarget D65Target = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                               lightSource[1],
                                               factor[1], DCTarget.Chart.CCSG);
    DCTarget F2Target = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                              lightSource[2],
                                              (.75) * factor[2],
                                              DCTarget.Chart.CCSG);
    DCTarget F8Target = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                              lightSource[3],
                                              factor[3], DCTarget.Chart.CCSG);
    DCTarget F10Target = DCTarget.Instance.get(DCTarget.Camera.D200Raw,
                                               lightSource[4],
                                               (0.6000000000000002) * factor[4],
                                               DCTarget.Chart.CCSG);

    DCTarget[] trainingTargetSet = new DCTarget[] {
        D50Target, D65Target, F2Target, F8Target, F10Target};
    int setSize = trainingTargetSet.length;
    int[][] possibleSet = possibleSet(setSize);
    int possibleSetSize = possibleSet.length;

    for (int x = 0; x < possibleSetSize; x++) {
      int[] set = possibleSet[x];
      int size = set.length;
      DCTarget[] trainingTargetArray = new DCTarget[size];
      for (int y = 0; y < size; y++) {
        trainingTargetArray[y] = trainingTargetSet[set[y]];
        System.out.print(trainingTargetArray[y].getDescription() + "/");
      }
      System.out.println("");
    }
  }

}
