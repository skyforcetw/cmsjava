package shu.cms.colorformat.cxf;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.bind.*;

import shu.cms.*;
import shu.cms.colorformat.cxf.attr.*;
import shu.cms.colorformat.trans.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.reference.cie.*;
import shu.io.files.*;
import shu.util.log.*;

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
public class CXFOperator {
  static {
    try {
      jc = JAXBContext.newInstance("shu.cms.colorformat.cxf");
    }
    catch (JAXBException ex) {
      Logger.log.error("", ex);
    }
  }

  protected static JAXBContext jc;
  protected CXF cxf;
  public CXFOperator(CXF cxf) {
    this.cxf = cxf;
    init();
  }

  public static void main(String[] args) {
    CXFOperator cxf = new CXFOperator(CIE.class.getResource("Illuminant.cxf"));
    List<Spectra> spectraList = cxf.getSpectraList();
    for (Spectra s : spectraList) {
      System.out.println(s);
    }

  }

  public Map<String, Spectrum> getSpectrumMap(int sampleSetIndex) {

    List<Sample> samples = cxf.getSampleSet().get(sampleSetIndex).getSample();
    int size = samples.size();
    Map<String, Spectrum>
        spectrumMap = new LinkedHashMap<String, Spectrum> (size);

    for (Sample sample : samples) {
      String key = sample.getName();
      Spectrum spectrum = sample.getSampleAttribute().get(0).getSpectrum();
      spectrumMap.put(key, spectrum);
    }
    return spectrumMap;
  }

  protected Map<String, Conditions> conditionsMap;
  protected String[] colorSpaceDescriptions;
  protected Map<String, ColorVector>[] colorVectorMapArray;

  protected void init() {
    initConditionsMap();
    initColorVectorMapArray();
  }

  public List<String> getSampleNameList(int sampleSetIndex) {
    List<SampleSet> setList = cxf.getSampleSet();
    List<Sample> samples = setList.get(sampleSetIndex).getSample();
    int size = samples.size();
    List<String> nameList = new ArrayList<String> (size);
    for (Sample sample : samples) {
      nameList.add(sample.getName());
    }
    return nameList;
  }

  public List<String> getSampleNameList() {
    return getSampleNameList(0);
  }

  public List<CIEXYZ> getCIEXYZList() {
    Map<String, ColorVector>
        colorVectorMap = getColorVectorMapByDescriptions(ColorSpaceDescription.
        Type.CIEXYZ);

    if (colorVectorMap == null) {
      return null;
    }
    List<CIEXYZ> CIEXYZList = new ArrayList<CIEXYZ> (colorVectorMap.size());

    for (Map.Entry<String, ColorVector> entry : colorVectorMap.entrySet()) {
      ColorVector colorVector = entry.getValue();
      CIEXYZ XYZ = colorVector2CIEXYZ(colorVector);
      CIEXYZList.add(XYZ);
    }
    return CIEXYZList;
  }

  public List<CIELab> getCIELabList() {
    Map<String, ColorVector>
        colorVectorMap = getColorVectorMapByDescriptions(ColorSpaceDescription.
        Type.CIELab);

    if (colorVectorMap == null) {
      return null;
    }
    List<CIELab> CIELabList = new ArrayList<CIELab> (colorVectorMap.size());

    for (Map.Entry<String, ColorVector> entry : colorVectorMap.entrySet()) {
      ColorVector colorVector = entry.getValue();
      CIELab Lab = colorVector2CIELab(colorVector);
      CIELabList.add(Lab);
    }
    return CIELabList;
  }

  public List<RGB> getRGBList() {
//    Map<String, ColorVector>
//        colorVectorMap = getColorVectorMap(ColorSpaceDescription.Type.RGB);
    Map<String, ColorVector>
        colorVectorMap = getColorVectorMapByDescriptions(ColorSpaceDescription.
        Type.RGB);
    if (colorVectorMap == null) {
      return null;
    }
    List<RGB> RGBList = new ArrayList<RGB> (colorVectorMap.size());

    for (Map.Entry<String, ColorVector> entry : colorVectorMap.entrySet()) {
      ColorVector colorVector = entry.getValue();
      RGB rgb = colorVector2RGB(colorVector);
      RGBList.add(rgb);
    }
    return RGBList;
  }

  protected Map<String, ColorVector>
      getColorVectorMapByDescriptions(ColorSpaceDescription.Type type) {
    for (int x = 0; x < colorSpaceDescriptions.length; x++) {
      String descriptions = colorSpaceDescriptions[x];
      if (type.name().equals(descriptions)) {
        return colorVectorMapArray[x];
      }
    }
    return null;
  }

  protected void initColorVectorMapArray() {
    List<SampleSet> setList = cxf.getSampleSet();
    int size = setList.size();
    colorSpaceDescriptions = new String[size];
    colorVectorMapArray = new Map[size];
    for (int x = 0; x < size; x++) {
      List<Sample> samples = setList.get(x).getSample();
      Map<String, ColorVector>
          colorVectorMap = new LinkedHashMap<String, ColorVector> (samples.size());
      String descriptions = null;

      for (Sample sample : samples) {
        String key = sample.getName();
        ColorVector colorVector = sample.getSampleAttribute().get(0).
            getColorVector();
        colorVectorMap.put(key, colorVector);

        if (descriptions == null && colorVector != null) {
          String conditionsID = colorVector.getConditions();
          Conditions conditions = conditionsMap.get(conditionsID);
          ColorSpaceDescription colorSpaceDescription = ColorSpaceDescription.
              getInstance(conditions.getAttribute());
          colorSpaceDescriptions[x] = colorSpaceDescription.type;
        }

      }
      colorVectorMapArray[x] = colorVectorMap;

    }
  }

  protected void initConditionsMap() {
    List<Conditions> conditionsList = cxf.getConditions();
    conditionsMap = new HashMap<String, Conditions> (conditionsList.size());
    for (Conditions conditions : conditionsList) {
      conditionsMap.put(conditions.getID(), conditions);
    }

  }

  public CXFOperator(String filename) {
    try {
      Unmarshaller unmarshaller = jc.createUnmarshaller();
      cxf = (CXF) unmarshaller.unmarshal(new File(filename));
    }
    catch (JAXBException ex) {
      Logger.log.error("", ex);
    }
    init();
  }

  public CXFOperator(URL url) {
    try {
      Unmarshaller unmarshaller = jc.createUnmarshaller();
      cxf = (CXF) unmarshaller.unmarshal(url);
    }
    catch (JAXBException ex) {
      Logger.log.error("", ex);
    }
    init();
  }

  public CXFOperator(InputStream is) {
    try {
      Unmarshaller unmarshaller = jc.createUnmarshaller();
      cxf = (CXF) unmarshaller.unmarshal(is);
    }
    catch (JAXBException ex) {
      Logger.log.error("", ex);
    }
    init();
  }

  public static void saveCXF(CXF cxf, String filename) {
    CXFOperator operator = new CXFOperator(cxf);
    operator.saveCXF(filename);
  }

  public void saveCXF(String filename) {
    try {
      Marshaller marshaller = jc.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                             Boolean.valueOf(true));
      FileOutputStream fos = new FileOutputStream(filename);
      marshaller.marshal(cxf, fos);
      fos.close();
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    catch (JAXBException ex) {
      Logger.log.error("", ex);
    }
  }

  public int getLgorowLength() {
    if (cxf.getAdditionalData().getValue().size() != 0) {
      Value v = (Value) cxf.getAdditionalData().getValue().get(0);
      if (v.getName().equals("LGOROWLENGTH")) {
        return Integer.parseInt(v.getvalue());
      }
    }
    return -1;
  }

  protected static RGB colorVector2RGB(ColorVector colorVector) {
    List<Value> valueList = colorVector.getValue();
    double[] rgb = new double[3];
    for (Value val : valueList) {
      if (val.getName().equals("R")) {
        rgb[0] = Double.parseDouble(val.getvalue());
      }
      else if (val.getName().equals("G")) {
        rgb[1] = Double.parseDouble(val.getvalue());
      }
      else if (val.getName().equals("B")) {
        rgb[2] = Double.parseDouble(val.getvalue());
      }
    }
    return new RGB(RGB.ColorSpace.unknowRGB, rgb, RGB.MaxValue.Double255);
  }

  protected static CIELab colorVector2CIELab(ColorVector colorVector) {
    List<Value> valueList = colorVector.getValue();
    double[] Lab = new double[3];
    for (Value val : valueList) {
      if (val.getName().equals("L*")) {
        Lab[0] = Double.parseDouble(val.getvalue());
      }
      else if (val.getName().equals("a*")) {
        Lab[1] = Double.parseDouble(val.getvalue());
      }
      else if (val.getName().equals("b*")) {
        Lab[2] = Double.parseDouble(val.getvalue());
      }
    }
    return new CIELab(Lab);
  }

  protected static CIEXYZ colorVector2CIEXYZ(ColorVector colorVector) {
    List<Value> valueList = colorVector.getValue();
    double[] XYZ = new double[3];
    for (Value val : valueList) {
      if (val.getName().equals("X")) {
        XYZ[0] = Double.parseDouble(val.getvalue());
      }
      else if (val.getName().equals("Y")) {
        XYZ[1] = Double.parseDouble(val.getvalue());
      }
      else if (val.getName().equals("Z")) {
        XYZ[2] = Double.parseDouble(val.getvalue());
      }
    }
    return new CIEXYZ(XYZ);
  }

  public List<Spectra> getSpectraList() {
    return getSpectraList(0);
  }

  public List<Spectra> getSpectraList(int sampleSetIndex) {
    Map<String, Spectrum> spectrumMap = this.getSpectrumMap(sampleSetIndex);
    List<Spectra> spectras = new ArrayList<Spectra> (spectrumMap.size());

    String description = cxf.getDescription();
    boolean absolute = false;
    if (description != null) {
      absolute = cxf.getDescription().equals(SpectraWinAsciiFile2CxF.
                                             FROM_SPECTRAWIN);
    }

    for (Map.Entry<String, Spectrum> entry : spectrumMap.entrySet()) {
      String name = entry.getKey();
      Spectrum spectrum = entry.getValue();
      if (spectrum == null) {
        return null;
      }
      Conditions conditions = conditionsMap.get(spectrum.getConditions());
      Spectra spectra = new Spectra(name, spectrum, conditions);
      if (absolute) {
        spectra.setUnitType(Spectra.UnitType.ABSOLUTE);
      }
      spectras.add(spectra);
    }
    return spectras;
  }

  public Map<String, Conditions> getConditionsMap() {
    return conditionsMap;
  }

  public static CXF openCXF(URL url) {
    try {
      Unmarshaller unmarshaller = jc.createUnmarshaller();
      CXF cxf =
          (CXF) unmarshaller.unmarshal(url);
      return cxf;
    }
    catch (JAXBException ex) {
      Logger.log.error("", ex);
      return null;
    }
  }

  public static CXF openCXF(String filename) {
    try {
      return openCXF(new File(filename).toURI().toURL());
    }
    catch (MalformedURLException ex) {
      Logger.log.error("", ex);
      return null;
    }
  }

  public static final List<Spectra> getAllSpectraList(File directory) {
    List<Spectra> allSpectra = new LinkedList<Spectra> ();
    return getAllSpectraList(directory, allSpectra);
  }

  protected static final List<Spectra> getAllSpectraList(File directory,
      List<Spectra> allSpectra) {
    File[] files = directory.listFiles();

    try {

      for (int x = 0; x < files.length; x++) {
        if (files[x].isDirectory()) {
          getAllSpectraList(files[x], allSpectra);
        }
        else if (files[x].isFile() && files[x].getName().endsWith(".cxf")) {
          CXFOperator cxf = new CXFOperator(files[x].getCanonicalPath());
          List<Spectra> spectraList = cxf.getSpectraList();
          allSpectra.addAll(spectraList);
        }
      }
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    return allSpectra;

  }

  public final static void produceDoubleArrayData(String cxfDir,
                                                  String dataFilename) {
    List<Spectra> db = getAllSpectraList(new File(cxfDir));
    int size = db.size();
    int width = db.get(0).getData().length;

    double[][] socsData = new double[size][width];

    for (int x = 0; x < size; x++) {
      Spectra s = db.get(x);
      double[] d = s.getData();
      socsData[x] = d;
    }

    BinaryFile.writeDoubleArray(dataFilename, socsData);
  }
}
