package shu.plot;

import static java.lang.Math.*;
import java.util.*;

import java.awt.*;
import javax.swing.*;

import static org.math.array.StatisticSample.*;
import shu.math.array.DoubleArray;

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
public abstract class PlotBase
    extends PlotWindow implements PlotInterface {
  protected PlotBase(String title, int width, int height) {
    super(title, width, height);
  }

  protected PlotBase(JFrame frame) {
    super(frame);
  }

  protected final static String getKeyName(Class c) {
    return "%" + c.getName() + "%";
  }

  public int getCachePlotIndex(String name) {
    return cache.cachePlotIndexMap.get(name);
  }

  protected final static void checkParameter(String name, double ...values) {
    if (name == null) {
      throw new IllegalArgumentException("name cannot be null.");
    }
  }

  public final void addCacheScatterPlot(String name, Color c,
                                        double ...values) {
    cache.getCachePlotInstance(ScatterCachePlot.class, name, c, values);
  }

  public final void addCacheScatterLinePlot(String name, Color c,
                                            double ...values) {
    cache.getCachePlotInstance(ScatterLineCachePlot.class, name, c, values);
  }

  public class Cache {
    private final void putCachePlot(String key, CachePlot plot) {
      cachePlotMap.put(key, plot);
    }

    private Map<String, Integer> cachePlotIndexMap = new HashMap<String,
        Integer> ();

    public final int getCachePlotSize() {
      return cachePlotMap.size();
    }

    private final CachePlot getCachePlot(String key) {
      return cachePlotMap.get(key);
    }

    protected final CachePlot getCachePlotInstance(Class c, String name,
        Color color, double[] values) {
      checkParameter(name, values);

      CachePlot cachePlot = cache.getCachePlot(c, name);
      if (cachePlot == null) {
        if (c.equals(ScatterCachePlot.class)) {
          cachePlot = new ScatterCachePlot(name, color, values);
        }
        else if (c.equals(ScatterLineCachePlot.class)) {
          cachePlot = new ScatterLineCachePlot(name, color, values);
        }
      }
      else {
        cachePlot.addPlotValues(values);
      }

      return cachePlot;
    }

    public final CachePlot getCachePlot(Class c, String name) {
      String key = getKeyName(c) + name;
      return getCachePlot(key);
    }

    private Map<String, CachePlot> cachePlotMap = new HashMap<String, CachePlot> ();

  }

  public final void drawCachePlot() {
    for (CachePlot plot : cache.cachePlotMap.values()) {
      int index = plot.draw();
      cache.cachePlotIndexMap.put(plot.name, index);
    }
    cache.cachePlotMap.clear();
  }

  public Cache cache = new Cache();

  protected abstract class CachePlot {

    /**
     *
     * @return int index of the plot in the panel
     */
    public abstract int draw();

    protected String name;
    protected Color c;
    protected CachePlot(String name, Color c) {
      this.name = name;
      this.c = c;
      init();
    }

    protected CachePlot(String name, Color c, double[] values) {
      this(name, c);
      this.addPlotValues(values);
    }

    /**
     * ªì©l¤Æ
     */
    private void init() {
      if (name != null) {
        String key = getKeyName(this.getClass()) + name;
        cache.putCachePlot(key, this);
      }
    }

    public final void addPlotValues(double[] values) {
      valuesList.add(values);
    }

    protected ArrayList<double[]> valuesList = new ArrayList<double[]> ();

    protected double[] get1DArrayFromValuesList() {
      int size = valuesList.size();
      double[] array = new double[size];
      for (int x = 0; x < size; x++) {
        double[] d = valuesList.get(x);
        array[x] = d[0];
      }
      return array;
    }

    protected double[][] get2DArrayFromValuesList() {
      int size = valuesList.size();
      double[][] array = new double[size][];
      for (int x = 0; x < size; x++) {
        double[] d = valuesList.get(x);
        array[x] = d;
      }
      return array;
    }
  }

  public void setAxisLabels(String ...labels) {
    int index = 0;
    for (String label : labels) {
      setAxeLabel(index, label);
      index++;
    }
  }

  protected class ScatterLineCachePlot
      extends ScatterCachePlot {
    /**
     * XYLineCachePlot
     *
     * @param name String
     * @param c Color
     */
    protected ScatterLineCachePlot(String name, Color c) {
      super(name, c);
    }

    protected ScatterLineCachePlot(String name, Color c, double[] values) {
      super(name, c, values);
    }

    /**
     *
     * @return int
     */
    public int draw() {
      double[][] xydata = produceData();
      if (xydata.length == 2) {
        xydata = DoubleArray.transpose(xydata);
      }
      return addLinePlot(name, c, xydata);
    }

  }

  protected class ScatterCachePlot
      extends CachePlot {
    /**
     * ScatterCachePlot
     *
     * @param name String
     * @param c Color
     */
    protected ScatterCachePlot(String name, Color c) {
      super(name, c);
    }

    protected ScatterCachePlot(String name, Color c, double[] values) {
      super(name, c, values);
    }

    /**
     *
     * @return int
     */
    public int draw() {
      double[][] datas = produceData();
      return addScatterPlot(name, c, datas);
    }

    protected double[][] produceData() {
      double[][] datas = get2DArrayFromValuesList();
      return datas;
    }
  }

  public void setVisible() {
    if (cache.getCachePlotSize() != 0) {
      drawCachePlot();
    }
    super.setVisible();
    if (null != Skin) {
      Skin.setSkin(this);
    }
  }

  protected Color getNewColor() {
    return getNewColor(getPlotSize());
  }

//  public final static Color[] COLORLIST = {
//      Color.black, Color.blue, Color.cyan, Color.darkGray, Color.gray,
//      Color.green, Color.lightGray, Color.magenta, Color.orange, Color.pink,
//      Color.red, Color.yellow};

//  public final static Color[] COLORLIST = {
//      Color.black, Color.red, Color.darkGray, Color.orange, Color.gray,
//      Color.yellow, Color.lightGray, Color.green, Color.blue, Color.cyan,
//      Color.magenta, Color.pink,
//
//  };

  public final static Color[] COLORLIST = {
      Color.red, Color.orange,
      Color.yellow, Color.green, Color.blue, Color.cyan,
      Color.magenta, Color.pink, Color.black, Color.lightGray

  };

  public final static Color getNewColor(int index) {
    return COLORLIST[index % COLORLIST.length];
  }

//  private SkinInterface skin;
  public static void setSkin(SkinInterface skin) {
    Skin = skin;
  }

  private static SkinInterface Skin;

  public final void setAxisVisible(boolean v) {
    for (int x = 0; x < this.getAxisCount(); x++) {
      this.setAxisVisible(x, v);
    }
  }

  public final void setGridVisible(boolean v) {
    for (int x = 0; x < this.getAxisCount(); x++) {
      this.setGridVisible(x, v);
    }

  }
}
