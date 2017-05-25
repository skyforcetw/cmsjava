package org.math.plot.canvas;

import java.io.File;
import java.io.IOException;
import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.math.plot.components.DatasFrame;
import org.math.plot.components.LegendPanel;
import org.math.plot.components.SetScalesFrame;
import static org.math.plot.plotObjects.Base.*;
import org.math.plot.plotObjects.Base;
import org.math.plot.plotObjects.BaseDependant;
import org.math.plot.plotObjects.BasePlot;
import org.math.plot.plotObjects.Plotable;
import org.math.plot.plots.Plot;
import org.math.plot.render.AbstractDrawer;
import org.math.plot.utils.Array;
import javax.swing.KeyStroke;
import javax.swing.InputMap;
import java.awt.Event;
import javax.swing.text.DefaultEditorKit;
import shu.math.array.DoubleArray;
import org.math.plot.plotObjects.Axis;
import org.math.plot.plotObjects.BaseLine;
import shu.math.array.IntArray;

/**
 * BSD License
 *
 * @author Yann RICHET
 */
public abstract class PlotCanvas
    extends JPanel implements MouseListener, MouseMotionListener,
    ComponentListener, BaseDependant, MouseWheelListener {

  //public int[] panelSize = new int[] { 400, 400 };
  public Base base;
  protected AbstractDrawer draw;
  protected BasePlot grid;
  public LegendPanel linkedLegendPanel;
  public LinkedList<Plot> plots;
  public LinkedList<Plotable> objects;

  // ///////////////////////////////////////////
  // ////// Constructor & inits ////////////////
  // ///////////////////////////////////////////
  public PlotCanvas() {
    initPanel();
    initBasenGrid();
    initDrawer();
  }

  public PlotCanvas(Base b, BasePlot bp) {
    initPanel();
    initBasenGrid(b, bp);
    initDrawer();
  }

  public PlotCanvas(double[] min, double[] max) {
    initPanel();
    initBasenGrid(min, max);
    initDrawer();
  }

  public PlotCanvas(double[] min, double[] max, String[] axesScales,
                    String[] axesLabels) {
    initPanel();
    initBasenGrid(min, max, axesScales, axesLabels);
    initDrawer();
  }

  public void attachLegend(LegendPanel lp) {
    linkedLegendPanel = lp;
  }

  private void initPanel() {
    objects = new LinkedList<Plotable> ();
    plots = new LinkedList<Plot> ();

    setDoubleBuffered(true);

    //setSize(panelSize[0], panelSize[1]);
    //setPreferredSize(new Dimension(panelSize[0], panelSize[1]));
    setBackground(Color.white);

    addComponentListener(this);
    addMouseListener(this);
    addMouseMotionListener(this);
    addMouseWheelListener(this);
  }

  public abstract void initDrawer();

  public void initBasenGrid(double[] min, double[] max, String[] axesScales,
                            String[] axesLabels) {
    base = new Base(min, max, axesScales);
    grid = new BasePlot(base, axesLabels);
    // grid.getAxe(0).getDarkLabel().setCorner(0.5,-1);
    // grid.getAxe(1).getDarkLabel().setCorner(0,-0.5);
  }

  public abstract void initBasenGrid(double[] min, double[] max);

  public abstract void initBasenGrid();

  public void initBasenGrid(Base b, BasePlot bp) {
    base = b;
    grid = bp;

  }

  // ///////////////////////////////////////////
  // ////// set actions ////////////////////////
  // ///////////////////////////////////////////
  public void setActionMode(int am) {
    ActionMode = am;
  }

  public void setNoteCoords(boolean b) {
    allowNoteCoord = b;
  }

  public void setEditable(boolean b) {
    allowEdit = b;
  }

  public boolean getEditable() {
    return allowEdit;
  }

  public void setNotable(boolean b) {
    allowNote = b;
  }

  public boolean getNotable() {
    return allowNote;
  }

  // ///////////////////////////////////////////
  // ////// set/get elements ///////////////////
  // ///////////////////////////////////////////
  public LinkedList<Plot> getPlots() {
    return plots;
  }

  public Plot getPlot(int i) {
    return (Plot) plots.get(i);
  }

  public int getPlotIndex(Plot p) {
    for (int i = 0; i < plots.size(); i++) {
      if (getPlot(i) == p) {
        return i;
      }
    }
    return -1;
  }

  public LinkedList<Plotable> getPlotables() {
    return objects;
  }

  public Plotable getPlotable(int i) {
    return (Plotable) objects.get(i);
  }

  public BasePlot getGrid() {
    return grid;
  }

  public String[] getAxisScales() {
    return base.getAxesScales();
  }

  public void setAxisLabels(String ...labels) {
    grid.setLegend(labels);
    repaint();
  }

  public void setAxisLabel(int axe, String label) {
    grid.setLegend(axe, label);
    repaint();
  }

  public int[] getAxisPixelCount(int axe) {
    Axis axis = grid.getAxis(axe);
    BaseLine darkLine = axis.getDarkLine();
//  draw.project()
    int[] base0 = draw.projectBase(darkLine.getExtrem()[0]);
    int[] base1 = draw.projectBase(darkLine.getExtrem()[1]);
    int[] pixelCount = IntArray.minus(base1, base0);
    IntArray.abs(pixelCount);
    IntArray.plusAndNoReturn(pixelCount, 1);
//    IntArray.mi
//   this.getaxie
//    return new int[]{ base1[0]-base0[0],
    return pixelCount;
  }

  public void setAxisScales(String ...scales) {
    base.setAxesScales(scales);
    setAutoBounds();
  }

  public void setAxiScale(int axe, String scale) {
    base.setAxesScales(axe, scale);
    setAutoBounds(axe);
  }

  public void setFixedBounds(double[] min, double[] max) {
    base.setFixedBounds(min, max);
    resetBase();
    repaint();
  }

  public void setFixedBounds(int axe, double min, double max) {
    base.setFixedBounds(axe, min, max);
    resetBase();
    repaint();
  }

  public double[] getFixedBounds(int axe) {
    double[] min = base.getMinBounds();
    double[] max = base.getMaxBounds();
    double[] bounds = new double[] {
        min[axe], max[axe]};
    return bounds;
  }

  public void includeInBounds(double ...into) {
    base.includeInBounds(into);
    grid.resetBase();
    repaint();
  }

  public void includeInBounds(Plot plot) {
    base.includeInBounds(Array.min(plot.getData()));
    base.includeInBounds(Array.max(plot.getData()));
    resetBase();
    repaint();
  }

  public void setAutoBounds() {
    if (plots.size() > 0) {
      Plot plot0 = this.getPlot(0);
      base.setRoundBounds(Array.min(plot0.getData()), Array.max(plot0.getData()));
    }
    else { // build default min and max bounds
      double[] min = new double[base.dimension];
      double[] max = new double[base.dimension];
      for (int i = 0; i < base.dimension; i++) {
        if (base.getAxeScale(i).equalsIgnoreCase(LINEAR)) {
          min[i] = 0.0;
          max[i] = 1.0;
        }
        else if (base.getAxeScale(i).equalsIgnoreCase(LOGARITHM)) {
          min[i] = 1.0;
          max[i] = 10.0;
        }
      }
      base.setRoundBounds(min, max);
    }
    for (int i = 1; i < plots.size(); i++) {
      Plot ploti = this.getPlot(i);
      base.includeInBounds(Array.min(ploti.getData()));
      base.includeInBounds(Array.max(ploti.getData()));
    }
    resetBase();
    repaint();
  }

  public void setAutoBounds(int axe) {
    if (plots.size() > 0) {
      Plot plot0 = this.getPlot(0);
      base.setRoundBounds(axe, Array.min(plot0.getData())[axe],
                          Array.max(plot0.getData())[axe]);
    }
    else { // build default min and max bounds
      double min = 0.0;
      double max = 0.0;
      if (base.getAxeScale(axe).equalsIgnoreCase(LINEAR) |
          base.getAxeScale(axe).equalsIgnoreCase(STRINGS)) {
        min = 0.0;
        max = 1.0;
      }
      else if (base.getAxeScale(axe).equalsIgnoreCase(LOGARITHM)) {
        min = 1.0;
        max = 10.0;
      }
      base.setRoundBounds(axe, min, max);
    }

    for (int i = 1; i < plots.size(); i++) {
      Plot ploti = this.getPlot(i);
      base.includeInBounds(axe, Array.min(ploti.getData())[axe]);
      base.includeInBounds(axe, Array.max(ploti.getData())[axe]);
    }
    resetBase();
    repaint();
  }

  public void reset() {
    resetBase();
    setAutoBounds();
  }

  public void resetBase() {
    // System.out.println("PlotCanvas.resetBase");
    draw.resetBaseProjection();
    grid.resetBase();

    for (int i = 0; i < objects.size(); i++) {
      if (objects.get(i) instanceof BaseDependant) {
        ( (BaseDependant) (objects.get(i))).resetBase();
      }
    }
    repaint();
  }

  // ///////////////////////////////////////////
  // ////// add/remove elements ////////////////
  // ///////////////////////////////////////////
  public void addLabel(String text, Color c, double ...where) {
    addPlotable(new org.math.plot.plotObjects.Label(text, c, where));
  }

  public void addBaseLabel(String text, Color c, double ...where) {
    addPlotable(new org.math.plot.plotObjects.BaseLabel(text, c, where));
  }

  public void addPlotable(Plotable p) {
    objects.add(p);
    // resetBase();
    repaint();
  }

  public void removePlotable(Plotable p) {
    objects.remove(p);
    repaint();
  }

  public void removePlotable(int i) {
    objects.remove(i);
    repaint();
  }

  public int addPlot(Plot newPlot) {
    plots.add(newPlot);
    if (linkedLegendPanel != null) {
      linkedLegendPanel.updateLegends();
    }
    if (plots.size() == 1) {
      setAutoBounds();
    }
    else {
      includeInBounds(newPlot);
    }
    return plots.size() - 1;
  }

  public void setPlot(int I, Plot p) {
    plots.set(I, p);
    if (linkedLegendPanel != null) {
      linkedLegendPanel.updateLegends();
    }
    repaint();
  }

  public void changePlotData(int I, double[] ...XY) {
    getPlot(I).setData(XY);
    repaint();
  }

  public void changePlotName(int I, String name) {
    getPlot(I).setName(name);
    if (linkedLegendPanel != null) {
      linkedLegendPanel.updateLegends();
    }
    repaint();
  }

  public void changePlotColor(int I, Color c) {
    getPlot(I).setColor(c);
    if (linkedLegendPanel != null) {
      linkedLegendPanel.updateLegends();
    }
    repaint();
  }

  public void removePlot(int I) {
    plots.remove(I);
    if (linkedLegendPanel != null) {
      linkedLegendPanel.updateLegends();
    }
    if (plots.size() != 0) {
      setAutoBounds();
    }

  }

  public void removePlot(Plot p) {
    plots.remove(p);
    if (linkedLegendPanel != null) {
      linkedLegendPanel.updateLegends();
    }
    if (plots.size() != 0) {
      setAutoBounds();
    }

  }

  public void removeAllPlots() {
    plots.clear();
    if (linkedLegendPanel != null) {
      linkedLegendPanel.updateLegends();
    }
    clearNotes();
  }

  public void addVectortoPlot(int numPlot, double[][] v) {
    getPlot(numPlot).addVector(v);
  }

  /*public void addQuantiletoPlot(int numPlot, boolean _symetric, double[]... q) {
       getPlot(numPlot).addQuantiles(q, _symetric);
       }*/
  public void addQuantiletoPlot(int numPlot, int numAxe, double rate,
                                boolean symetric, double[] q) {
    getPlot(numPlot).addQuantile(numAxe, rate, q, symetric);
  }

  public void addQuantiletoPlot(int numPlot, int numAxe, double rate,
                                boolean symetric, double q) {
    getPlot(numPlot).addQuantile(numAxe, rate, q, symetric);
  }

  public void addQuantilestoPlot(int numPlot, int numAxe, double[][] q) {
    getPlot(numPlot).addQuantiles(numAxe, q);
  }

  public void addQuantilestoPlot(int numPlot, int numAxe, double[] q) {
    getPlot(numPlot).addQuantiles(numAxe, q);
  }

  public void addGaussQuantilestoPlot(int numPlot, int numAxe, double[] s) {
    getPlot(numPlot).addGaussQuantiles(numAxe, s);
  }

  public void addGaussQuantilestoPlot(int numPlot, int numAxe, double s) {
    getPlot(numPlot).addGaussQuantiles(numAxe, s);
  }

  // ///////////////////////////////////////////
  // ////// call for toolbar actions ///////////
  // ///////////////////////////////////////////
  public void toGraphicFile(File file) throws IOException {

    Image image = createImage(getWidth(), getHeight());
    paint(image.getGraphics());
    image = new ImageIcon(image).getImage();

    BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
        image.getHeight(null), BufferedImage.TYPE_INT_RGB);
    Graphics g = bufferedImage.createGraphics();
    g.drawImage(image, 0, 0, Color.WHITE, null);
    g.dispose();

    try {
      ImageIO.write( (RenderedImage) bufferedImage, "PNG", file);
    }
    catch (IllegalArgumentException ex) {
    }
  }

  public void displaySetScalesFrame() {
    new SetScalesFrame(this);
  }

  public void displayDatasFrame(int i) {
    DatasFrame df = new DatasFrame(this, linkedLegendPanel);
    if (df.panels.getTabCount() != 0) {
      df.panels.setSelectedIndex(i);
    }
  }

  public void displayDatasFrame() {
    displayDatasFrame(0);
  }

  boolean mapset = false;

  public void resetMapData() {
    for (int i = 0; i < grid.getAxis().length; i++) {
      grid.getAxis()[i].setStringMap(null);
      setAxiScale(i, Base.LINEAR);
    }
    mapset = false;
  }

  public double[][] mapData(Object[][] data) {
    //System.out.println("mapData:" + Array.cat(data));

    double[][] mapeddata = new double[data.length][data[0].length];

    if (!mapset) {
      for (int j = 0; j < data[0].length; j++) {
        if (!Array.isDouble(data[0][j].toString())) {
          //System.out.println(data[0][j].toString() + " is not a double");
          setAxiScale(j, Base.STRINGS);

          ArrayList<String> string_array_j = new ArrayList<String> (data.length);
          for (int i = 0; i < data.length; i++) {
            string_array_j.add(data[i][j].toString());
          }

          grid.getAxis(j).setStringMap(Array.mapStringArray(string_array_j));
          grid.getAxis(j).init();

          for (int i = 0; i < data.length; i++) {
            mapeddata[i][j] = grid.getAxis(j).getStringMap().get(data[i][j].
                toString());
          }

          //System.out.println("Axe " + j + ":" + Array.toString(grid.getAxe(j).getStringMap()));
          initReverseMap(j);
        }
        else {
          //System.out.println(data[0][j].toString() + " is a double");
          //System.out.println("Axe " + j + ": double[]");
          for (int i = 0; i < data.length; i++) {
            mapeddata[i][j] = Double.valueOf(data[i][j].toString());
          }
        }
      }
      mapset = true;
    }
    else {
      for (int j = 0; j < data[0].length; j++) {
        if (!Array.isDouble(data[0][j].toString())) {
          //System.out.println(data[0][j].toString() + " is not a double");
          if (base.getAxeScale(j).equals(Base.STRINGS)) {
            for (int i = 0; i < data.length; i++) {
              if (!grid.getAxis(j).getStringMap().containsKey(data[i][j].
                  toString())) {
                Set<String> s = grid.getAxis(j).getStringMap().keySet();
                ArrayList<String>
                    string_array_j = new ArrayList<String> (s.size() + 1);
                string_array_j.addAll(s);
                string_array_j.add(data[i][j].toString());
                grid.getAxis(j).setStringMap(Array.mapStringArray(
                    string_array_j));

                //System.out.println("Axe " + j + ":" + Array.toString(grid.getAxe(j).getStringMap()));
                initReverseMap(j);
              }
              mapeddata[i][j] = grid.getAxis(j).getStringMap().get(data[i][j].
                  toString());
            }
          }
          else {
            throw new IllegalArgumentException(
                "The mapping of this PlotPanel was not set on axis " + j);
          }
        }
        else {
          //System.out.println(data[0][j].toString() + " is a double");
          //System.out.println("Axe " + j + ": double[]");
          for (int i = 0; i < data.length; i++) {
            mapeddata[i][j] = Double.valueOf(data[i][j].toString());
          }
        }
      }
    }
    return mapeddata;
  }

  public Object[][] reverseMapedData(double[][] mapeddata) {
    Object[][] stringdata = new Object[mapeddata.length][mapeddata[0].length];

    for (int i = 0; i < mapeddata.length; i++) {
      stringdata[i] = reverseMapedData(mapeddata[i]);
    }

    return stringdata;
  }

  public Object[] reverseMapedData(double[] mapeddata) {
    Object[] stringdata = new Object[mapeddata.length];

    if (reversedMaps == null) {
      reversedMaps = new HashMap[grid.getAxis().length];
    }

    for (int j = 0; j < mapeddata.length; j++) {
      if (reversedMaps[j] != null) {
        stringdata[j] = reversedMaps[j].get( (Double) (mapeddata[j]));
      }
      else {
        stringdata[j] = (Double) (mapeddata[j]);
      }
    }

    return stringdata;
  }

  HashMap<Double, String>[] reversedMaps;

  private void initReverseMap(int j) {
    if (reversedMaps == null) {
      reversedMaps = new HashMap[grid.getAxis().length];
    }

    if (grid.getAxis(j) != null) {
      reversedMaps[j] = Array.reverseStringMap(grid.getAxis(j).getStringMap());
    }
  }

  // ///////////////////////////////////////////
  // ////// Paint method ///////////////////////
  // ///////////////////////////////////////////

  // anti-aliasing constant
  final protected static RenderingHints AALIAS = new RenderingHints(
      RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  public static Color NOTE_COLOR = Color.BLACK;
  public static Color EDIT_COLOR = Color.BLACK;
  public boolean allowEdit = true;
  public boolean allowNote = true;
  public boolean allowNoteCoord = true;
  protected double[] coordNoted;
  protected String coordNotedName;
  public boolean plotOnTop = false;

  public void paint(Graphics gcomp) {
    // System.out.println("PlotCanvas.paint");

    Graphics2D gcomp2D = (Graphics2D) gcomp;

    // anti-aliasing methods
    gcomp2D.addRenderingHints(AALIAS);
    gcomp2D.setRenderingHint(RenderingHints.KEY_RENDERING,
                             RenderingHints.VALUE_RENDER_QUALITY);

    gcomp2D.setColor(getBackground());
    gcomp2D.fillRect(0, 0, getSize().width, getSize().height);

    draw.initGraphics(gcomp2D);

    // draw plot
    grid.plot(draw);

    if (plotOnTop) {
      for (int i = 0; i < objects.size(); i++) {
        getPlotable(i).plot(draw);
      }

    }

    for (int i = 0; i < plots.size(); i++) {
      getPlot(i).plot(draw);
      if (linkedLegendPanel != null) {
        linkedLegendPanel.nonote(i);
      }
    }

    if (!plotOnTop) {
      for (int i = 0; i < objects.size(); i++) {
        getPlotable(i).plot(draw);
      }
    }

    // draw note
    if (allowNote) {
      /*if (allowNoteCoord && coordNoted != null) {
                   draw.setColor(NOTE_COLOR);
                   draw.drawCoordinate(coordNoted);
       draw.drawText(Array.cat(reverseMapedData(coordNoted)), coordNoted);
                   }*/
      if (allowNoteCoord && coordNoted != null) {
        draw.setColor(NOTE_COLOR);
        draw.drawCoordinate(coordNoted);
        String text = this.coordNotedName != null &&
            this.coordNotedName.length() != 0 ? "[" +
            coordNotedName + "] " + Array.cat(reverseMapedData(coordNoted)) :
            Array.cat(reverseMapedData(coordNoted));
        draw.drawText(text, coordNoted);
      }
      for (int i = 0; i < plots.size(); i++) {
        if (getPlot(i).noted) {
          if (linkedLegendPanel != null) {
            linkedLegendPanel.note(i);
          }
          getPlot(i).note(draw);
          if (allowNoteCoord && coordNoted != null) {
            getPlot(i).noteCoord(draw, coordNoted);
          }
          //return;
        }
      }
    }
  }

  // ///////////////////////////////////////////
  // ////// Listeners //////////////////////////
  // ///////////////////////////////////////////
  public final static int ZOOM = 0;
  public final static int TRANSLATION = 1;
  public final static int RESCALES = 2;
  public final static int CENTER = 3;
  public int ActionMode;
  protected boolean dragging = false;
  protected int[] mouseCurent = new int[2];
  protected int[] mouseClick = new int[2];

  public void clearNotes() {
    coordNoted = null;
    repaint();
  }

  public void mousePressed(MouseEvent e) {
    //System.out.println("PlotCanvas.mousePressed");
    /*
     * System.out.println("PlotCanvas.mousePressed"); System.out.println("
     * mouseClick = [" + mouseClick[0] + " " + mouseClick[1] + "]");
     * System.out.println(" mouseCurent = [" + mouseCurent[0] + " " +
     * mouseCurent[1] + "]");
     */
    mouseCurent[0] = e.getX();
    mouseCurent[1] = e.getY();
    e.consume();
    mouseClick[0] = mouseCurent[0];
    mouseClick[1] = mouseCurent[1];
  }

  public void mouseDragged(MouseEvent e) {
    //System.out.println("PlotCanvas.mouseDragged");
    dragging = true;
    /*
     * System.out.println("PlotCanvas.mouseDragged"); System.out.println("
     * mouseClick = [" + mouseClick[0] + " " + mouseClick[1] + "]");
     * System.out.println(" mouseCurent = [" + mouseCurent[0] + " " +
     * mouseCurent[1] + "]");
     */
    mouseCurent[0] = e.getX();
    mouseCurent[1] = e.getY();
    e.consume();
    switch (ActionMode) {
      case TRANSLATION:
        draw.translate(mouseCurent[0] - mouseClick[0],
                       mouseCurent[1] - mouseClick[1]);
        mouseClick[0] = mouseCurent[0];
        mouseClick[1] = mouseCurent[1];
        repaint();
        break;
      case ZOOM:

//        repaint();
//        Graphics gcomp = getGraphics();
//        gcomp.setColor(Color.black);
//        gcomp.drawRect(min(mouseClick[0], mouseCurent[0]),
//                       min(mouseClick[1], mouseCurent[1]),
//                       abs(mouseCurent[0] - mouseClick[0]),
//                       abs(mouseCurent[1] - mouseClick[1]));
        drawZoomRect(e);
        break;
      case CENTER:
        center();
        break;
      case RESCALES:
        drawZoomRect(e);
        break;
    }
    //repaint();
  }

  protected void center() {
    double[] units = draw.getUnits();
    double[] t = new double[] {
        mouseCurent[0] - mouseClick[0],
        mouseCurent[1] - mouseClick[1]};

    int dimension = draw.getDimension();

    double[] move = new double[] {
        -t[0] * units[0], t[1] * units[1]};

    for (int x = 0; x < dimension; x++) {
      if (t[x] == 0) {
        continue;
      }
      double[] bounds = this.getFixedBounds(x);
      double[] result = DoubleArray.plus(bounds, move[x]);
      this.setFixedBounds(x, result[0], result[1]);
    }

//    draw.center(mouseCurent[0] - mouseClick[0],
//                mouseCurent[1] - mouseClick[1]);
    mouseClick[0] = mouseCurent[0];
    mouseClick[1] = mouseCurent[1];
    repaint();

  }

  private int x, y, width, height;

  private void calculateRect() {
    x = min(mouseClick[0], mouseCurent[0]);
    y = min(mouseClick[1], mouseCurent[1]);
    width = abs(mouseCurent[0] - mouseClick[0]);
    height = abs(mouseCurent[1] - mouseClick[1]);
  }

  protected void drawZoomRect(MouseEvent e) {
    this.calculateRect();
    repaint();
    Graphics gcomp = getGraphics();
    gcomp.setColor(Color.black);

    if ( (e.getModifiers() & MouseEvent.SHIFT_MASK) != 0) {
      //正方形
      int length = min(width, height);
      width = height = length;
    }
    if ( (e.getModifiers() & MouseEvent.ALT_MASK) != 0) {
      //起點為中心
      x -= width;
      y -= height;
      width *= 2;
      height *= 2;
    }

    gcomp.drawRect(x, y, width, height);
  }

  public void mouseReleased(MouseEvent e) {
    //System.out.println("PlotCanvas.mouseReleased");

    /*
     * System.out.println("PlotCanvas.mouseReleased"); System.out.println("
     * mouseClick = [" + mouseClick[0] + " " + mouseClick[1] + "]");
     * System.out.println(" mouseCurent = [" + mouseCurent[0] + " " +
     * mouseCurent[1] + "]");
     */
    mouseCurent[0] = e.getX();
    mouseCurent[1] = e.getY();
    e.consume();
    switch (ActionMode) {
      case ZOOM:

//        if (abs(mouseCurent[0] - mouseClick[0]) > 10 &&
//            abs(mouseCurent[1] - mouseClick[1]) > 10) {
//          int[] origin = {
//              min(mouseClick[0], mouseCurent[0]),
//              min(mouseClick[1], mouseCurent[1])};
//          double[] ratio = {
//              abs( (double) (mouseCurent[0] - mouseClick[0]) /
//                  (double) getWidth()),
//              abs( (double) (mouseCurent[1] - mouseClick[1]) /
//                  (double) getHeight())
//          };
//          draw.dilate(origin, ratio);
//          repaint();
//        }
        zoom();
        break;
      case RESCALES:
        rescales();
        break;
    }
    //repaint();
  }

  private void rescales() {
    this.calculateRect();
    if (width > 10 && height > 10) {
      double[] units = draw.getUnits();
      int[][] boundsSC = draw.getBoundsSc();

      double minx = getFixedBounds(0)[0] + (x - boundsSC[0][0]) * units[0];
      double maxy = getFixedBounds(1)[1] - (y - boundsSC[1][1]) * units[1];
      double maxx = minx + units[0] * width;
      double miny = maxy - units[1] * height;
      double[] min = new double[] {
          minx, miny};
      double[] max = new double[] {
          maxx, maxy};
      this.setFixedBounds(min, max);
      repaint();
    }
  }

  public void zoom(double wPercent, double hPercent) {
    this.ActionMode = PlotCanvas.ZOOM;
    int w = getWidth();
    int h = getHeight();
    int[] origin = new int[] {
        90, 90};
    double[] ratio = new double[] {
        100. / wPercent, 100. / hPercent
    };
    draw.dilate(origin, ratio);
    repaint();
  }

  private void zoom() {
    this.calculateRect();
    int[] origin = null;
    double[] ratio = null;
//    if (this instanceof Plot3DCanvas) {
//      if (abs(mouseCurent[0] - mouseClick[0]) > 10 &&
//          abs(mouseCurent[1] - mouseClick[1]) > 10) {
//        origin = new int[] {
//            min(mouseClick[0], mouseCurent[0]),
//            min(mouseClick[1], mouseCurent[1])};
//        ratio = new double[] {
//            abs( (double) (mouseCurent[0] - mouseClick[0]) /
//                (double) getWidth()),
//            abs( (double) (mouseCurent[1] - mouseClick[1]) /
//                (double) getHeight())
//        };
//
//      }
//    }
//    else {
    if (width > 10 && height > 10) {
      origin = new int[] {
          x, y};
      ratio = new double[] {
          abs( (double) (width) /
              (double) getWidth()),
          abs( (double) (height) /
              (double) getHeight())
      };
      draw.dilate(origin, ratio);
      repaint();
    }
//    }
  }

  public void mouseClicked(MouseEvent e) {
    //System.out.println("PlotCanvas.mouseClicked");

    /*
     * System.out.println("PlotCanvas.mouseClicked"); System.out.println("
     * mouseClick = [" + mouseClick[0] + " " + mouseClick[1] + "]");
     * System.out.println(" mouseCurent = [" + mouseCurent[0] + " " +
     * mouseCurent[1] + "]");
     */
    mouseCurent[0] = e.getX();
    mouseCurent[1] = e.getY();
    e.consume();
    mouseClick[0] = mouseCurent[0];
    mouseClick[1] = mouseCurent[1];

    if (e.getButton() == MouseEvent.BUTTON2) {
      this.setAutoBounds();
      return;
    }

    if (allowEdit) {
      if (e.getModifiers() == MouseEvent.BUTTON1_MASK && e.getClickCount() > 1) {
        for (int i = 0; i < grid.getAxis().length; i++) {
          if (grid.getAxis(i).isSelected(mouseClick, draw) != null) {
            grid.getAxis(i).edit(this);
            return;
          }
        }

        for (int i = 0; i < plots.size(); i++) {
          if (getPlot(i).isSelected(mouseClick, draw) != null) {
            getPlot(i).edit(this);
            return;
          }
        }
      }
    }

    if (!dragging && allowNote) {
      for (int i = 0; i < plots.size(); i++) {
        double[] _coordNoted = getPlot(i).isSelected(mouseClick, draw);
        String _coordNotedName = getPlot(i).getName();
        if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
          //左鍵選整筆
          if (_coordNoted != null) {
            getPlot(i).noted = !getPlot(i).noted;
          }
          else {
            getPlot(i).noted = false;
          }
        }
        else if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
          //右鍵選單點
          if (_coordNoted != null) {
            if (coordNoted != null) {
              boolean alreadyNoted = true;
              for (int j = 0; j < _coordNoted.length; j++) {
                alreadyNoted = alreadyNoted && _coordNoted[j] == coordNoted[j];
              }
              if (alreadyNoted) {
                coordNoted = null;
                coordNotedName = null;
              }
              else {
                coordNoted = _coordNoted;
                coordNotedName = _coordNotedName;
              }
            }
            else {
              coordNoted = _coordNoted;
              coordNotedName = _coordNotedName;
            }
          }
        }
      }
      repaint();
    }
    else {
      dragging = false;
    }
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mouseMoved(MouseEvent e) {
    //System.out.println("PlotCanvas.mouseMoved");
    /*
     * System.out.println("PlotCanvas.mouseClicked"); System.out.println("
     * mouseClick = [" + mouseClick[0] + " " + mouseClick[1] + "]");
     * System.out.println(" mouseCurent = [" + mouseCurent[0] + " " +
     * mouseCurent[1] + "]");*/
    mouseCurent[0] = e.getX();
    mouseCurent[1] = e.getY();
    e.consume();
    if (allowNote) {
      for (int i = 0; i < plots.size(); i++) {
        double[] _coordNoted = getPlot(i).isSelected(mouseCurent, draw);
        if (_coordNoted != null) {
          getPlot(i).noted = !getPlot(i).noted;
        }
        else {
          getPlot(i).noted = false;
        }
      }
      repaint();
    }
  }

  public void mouseWheelMoved(MouseWheelEvent e) {
    //System.out.println("PlotCanvas.mouseWheelMoved");
    /*
     * System.out.println("PlotCanvas.mouseWheelMoved");
     * System.out.println(" mouseClick = [" + mouseClick[0] + " " +
     * mouseClick[1] + "]"); System.out.println(" mouseCurent = [" +
     * mouseCurent[0] + " " + mouseCurent[1] + "]");
     */
    mouseCurent[0] = e.getX();
    mouseCurent[1] = e.getY();
    e.consume();
    int[] origin;
    double[] ratio;
    // double factor = 1.5;
//    switch (ActionMode) {
//      case ZOOM:
    if (e.getWheelRotation() == -1) {
      origin = new int[] {
          (int) (mouseCurent[0] - getWidth() / 3 /* (2*factor) */),
          (int) (mouseCurent[1] - getHeight() / 3 /* (2*factor) */)};
      ratio = new double[] {
          0.666
          /* 1/factor, 1/factor */, 0.666};
    }
    else {
      origin = new int[] {
          (int) (mouseCurent[0] - getWidth() / 1.333 /* (2/factor) */),
          (int) (mouseCurent[1] - getHeight() / 1.333 /* (2/factor) */)
      };
      ratio = new double[] {
          1.5, 1.5 /* factor, factor */};
    }
    draw.dilate(origin, ratio);
    repaint();
//        break;
//    }
  }

  public void componentHidden(ComponentEvent e) {
  }

  public void componentMoved(ComponentEvent e) {
  }

  public void componentResized(ComponentEvent e) {
    //System.out.println("PlotCanvas.componentResized");
    //panelSize = new int[] { (int) (getSize().getWidth()), (int) (getSize().getHeight()) };
    if (draw != null) {
      draw.resetBaseProjection();
    }
    //System.out.println("PlotCanvas : "+panelSize[0]+" x "+panelSize[1]);
    repaint();
    if (linkedLegendPanel != null) {
      linkedLegendPanel.componentResized(e);
    }
  }

  public void componentShown(ComponentEvent e) {
  }

}
