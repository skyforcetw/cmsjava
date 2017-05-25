package shu.cms.devicemodel.dc.dcam;

import java.util.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import shu.cms.colorformat.adapter.*;
import shu.cms.colorspace.depend.*;
import shu.cms.dc.*;
import shu.cms.gma.gbd.*;
import shu.cms.plot.*;
import shu.math.lut.*;
import shu.util.log.*;
import shu.cms.devicemodel.dc.*;


///import shu.plot.*;

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
public class DCAMVisualization {
  protected DCAppearanceModel dcam;

  public DCAMVisualization(DCAppearanceModel dcam) {
    this.dcam = dcam;
  }

  public final Plot2D plotLightnessCorrection() {
    Plot2D plot = Plot2D.getInstance("Lightness Correction");

    Interpolation1DLUT lut = dcam.getLightnessCorrector();
    double[] keys = lut.getKeyArray();
    double[] vals = lut.getValueArray();
    plot.addLinePlot("L* correct", Color.blue, new double[][] {keys, vals});
    plot.setAxeLabel(0, "input L*");
    plot.setAxeLabel(1, "output L*");

    plot.setVisible();
    return plot;
  }

  public final Plot2D plotGrayBalanceCorrection() {
    Plot2D plot = Plot2D.getInstance("Gray balance correction");

    Interpolation1DLUT[] luts = dcam.getGrayBalancer().getRGBLutArray();

    double[] rkeys = luts[0].getKeyArray();
    double[] rvals = luts[0].getValueArray();
    double[] bkeys = luts[1].getKeyArray();
    double[] bvals = luts[1].getValueArray();

    plot.addLinePlot("red correct", Color.red, new double[][] {rkeys, rvals});
    plot.addLinePlot("blue correct", Color.blue, new double[][] {bkeys, bvals});
    plot.setFixedBounds(0, 0, 255);
    plot.setAxeLabel(0, "input code");
    plot.setAxeLabel(1, "ratio");
    plot.addLegend();

    plot.setVisible();
    return plot;

  }

  protected JTable hueplaneTable;
  protected Plot2D hueplaneLPlot;
  protected Plot2D hueplaneCPlot;

  protected void updateHuePlanePlot(int hueIndex) {
    DCAppearanceModel.HuePlane huePlane = dcam.getHuePlane();
    int index = hueIndex + 1;
    double hue = huePlane.getCameraHue()[index];
    DCAppearanceModel.LChPair[] plane = huePlane.get(index);
    int size = plane.length;
    double[] inL = new double[size + 2];
    double[] outL = new double[size + 2];
    double[] inC = new double[size + 1];
    double[] outC = new double[size + 1];

    for (int x = 0; x < size; x++) {
      DCAppearanceModel.LChPair pair = plane[x];
      inL[x + 1] = pair.targetLCh.L;
      outL[x + 1] = pair.cameraLCh.L;
      inC[x + 1] = pair.targetLCh.C;
      outC[x + 1] = pair.cameraLCh.C;
    }
    inL[size + 1] = 100;
    outL[size + 1] = 100;
    Arrays.sort(inL);
    Arrays.sort(outL);

//    DCAppearanceModel.LChPair[] pairArray = huePlane.get(hueIndex + 1);
//    RGB rgb = pairArray[0].patch.getRGB();
//    Color c = rgb.getColor();

    hueplaneLPlot.setTitle("Lightness Hue Plane: " + hue);
    hueplaneLPlot.removeAllPlots();
    hueplaneLPlot.addLinePlot("map", Color.red, new double[][] {inL, outL});
    hueplaneLPlot.addLinePlot("linear", Color.lightGray, 0, 100,
                              new double[] {0, 100});

    hueplaneCPlot.setTitle("Lightness Hue Plane: " + hue);
    hueplaneCPlot.removeAllPlots();
    hueplaneCPlot.addLinePlot("map", Color.red, new double[][] {inC, outC});
    hueplaneCPlot.addLinePlot("linear", Color.lightGray, 0, inC[size],
                              new double[] {0, inC[size]});
  }

  public final void plotHuePlane() {
    HuePlaneTableModel model = new HuePlaneTableModel(dcam.getHuePlane());
    HuePlaneTablePanel panel = new HuePlaneTablePanel(model);
    this.hueplaneTable = panel.table;
    // Handle the listener
    ListSelectionModel selectionModel = hueplaneTable.getSelectionModel();
    selectionModel.addListSelectionListener(tableEventProcessor);

    //Create and set up the window.
    JFrame frame = new JFrame("Hue");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setContentPane(panel);
    //Display the window.
    frame.pack();
    frame.setVisible(true);
    int width = frame.getWidth();

    hueplaneLPlot = Plot2D.getInstance("Lightness Hue Plane");
    hueplaneLPlot.addLinePlot("linear", Color.lightGray, 0, 100,
                              new double[] {0, 100});
    hueplaneLPlot.addLegend();
    hueplaneLPlot.setAxeLabel(0, "input L");
    hueplaneLPlot.setAxeLabel(1, "output L");
    hueplaneLPlot.setFixedBounds(0, 0, 100);
    hueplaneLPlot.setFixedBounds(1, 0, 100);
    hueplaneLPlot.setLocation(width, 0);
    hueplaneLPlot.setVisible();

    hueplaneCPlot = Plot2D.getInstance("Chroma Hue Plane");
    hueplaneCPlot.addLegend();
    hueplaneCPlot.setAxeLabel(0, "input C");
    hueplaneCPlot.setAxeLabel(1, "output C");
    hueplaneCPlot.setLocation(hueplaneLPlot.getSize().width + width, 0);
    hueplaneCPlot.setVisible();
  }

  public final Plot2D plotHueCorrection() {
    Plot2D plot = Plot2D.getInstance("Hue correction");

    Interpolation1DLUT lut = dcam.getHueCorrector();
    double[] keys = lut.getKeyArray();
    double[] vals = lut.getValueArray();
    plot.addLinePlot("hue correct", Color.blue, new double[][] {keys, vals});
    plot.setAxeLabel(0, "input hue");
    plot.setAxeLabel(1, "output hue");

    plot.setVisible();
    return plot;
  }

  public static void main(String[] args) {
    //==========================================================================
    // camera
    //==========================================================================
//    DCChartAdapter profile = new DCChartAdapter(CMSDir.Reference.Camera +
//                                                "/IT8 E3199808.cxf",
//                                                LightSource.CIE.D65,
//                                                RGB.RGBColorSpace.sRGB);
    GMBICCProfileAdapter profile = new GMBICCProfileAdapter(
        "Measurement Files/Camera/S5Pro/s5p-std.icc");
    //==========================================================================

    LightSource.Source lightsource = LightSource.CIE.D65;

    //==========================================================================
    // target
    //==========================================================================
//    DCChartAdapter chart = new DCChartAdapter(CMSDir.Reference.Camera +
//                                              "/IT8 Ideal.cxf",
//                                              LightSource.CIE.D65);
    DCChartAdapter chart = new DCChartAdapter(DCTarget.Chart.CC24, lightsource);
    //==========================================================================

    DCTarget target = DCTarget.Instance.get(profile, chart, lightsource,
                                            DCTarget.Chart.CC24);

    RGB.ColorSpace rgbColorSpace = RGB.ColorSpace.sRGB;
    DCAppearanceModel.Style style = DCAppearanceModel.Style.IPT;

    GamutBoundaryRGBDescriptor gbd = DCAppearanceModel.getGBDDescriptorInstance(
        rgbColorSpace, style);

    DCAppearanceModel model = new DCAppearanceModel(target, style,
        RGB.ColorSpace.sRGB, gbd);
    DCModel.Factor factor = model.produceFactor();

    //==========================================================================
    // µøÄ±¤Æ
    //==========================================================================
    DCAMVisualization visual = new DCAMVisualization(model);
//    visual.plotLightnessCorrection();
//    visual.plotHueCorrection();
//    visual.plotGrayBalanceCorrection();
    visual.plotHuePlane();
    //==========================================================================
  }

  protected JTableEventProcessor tableEventProcessor = new JTableEventProcessor(this);
  protected class JTableEventProcessor
      implements ListSelectionListener {

    protected int row;
    protected DCAMVisualization visualization;
    protected JTableEventProcessor(DCAMVisualization visualization) {
      this.visualization = visualization;
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged(ListSelectionEvent e) {
      // See if this is a valid table selection
      if (e.getSource() == hueplaneTable.getSelectionModel()
          && e.getFirstIndex() >= 0) {
        // Get the data model for this table
//        TableModel model = (TableModel) hueplaneTable.getModel();

        row = hueplaneTable.getSelectedRow();
        visualization.updateHuePlanePlot(row);
      }
    }

  }

  protected static class HuePlaneTablePanel
      extends JPanel {
    protected HuePlaneTableModel model;
    protected JTable table;

    protected HuePlaneTablePanel(HuePlaneTableModel model) {
      super(new GridLayout(1, 0));
      this.model = model;

      try {
        jbInit();
      }
      catch (Exception ex) {
        Logger.log.error("", ex);
      }

    }

    private void jbInit() throws Exception {
      table = new JTable(model);
      table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      table.setPreferredScrollableViewportSize(new Dimension(180, 300));
      table.getColumnModel().getColumn(0).setCellRenderer(model.getHueRenderer());

      JScrollPane scrollPane = new JScrollPane(table);
      //Add the scroll pane to this panel.
      add(scrollPane);
    }
  }

  protected static class HuePlaneTableModel
      extends AbstractTableModel {

    protected HueRenderer hueRenderer = new HueRenderer();

    protected HueRenderer getHueRenderer() {
      return hueRenderer;
    }

    protected class HueRenderer
        extends DefaultTableCellRenderer {
      public Component getTableCellRendererComponent(JTable table, Object value,
          boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel)
            super.getTableCellRendererComponent(
                table, value, isSelected,
                hasFocus, row, column);
        DCAppearanceModel.LChPair[] pairArray = huePlane.get(row + 1);
        RGB rgb = pairArray[0].patch.getRGB();
        Color c = rgb.getColor();
        c = c.darker();
        this.setBackground(c);
        this.setForeground(Color.lightGray);
        return label;
      }

    }

    protected DCAppearanceModel.HuePlane huePlane;
    protected double[] cameraHue;

    public String getColumnName(int col) {
      return "Hue";
    }

    /**
     *
     * @param huePlane HuePlane
     */
    public HuePlaneTableModel(DCAppearanceModel.HuePlane huePlane) {
      this.huePlane = huePlane;
      cameraHue = huePlane.getCameraHue();
    }

    /**
     * Returns the number of columns in the model.
     *
     * @return the number of columns in the model
     */
    public int getColumnCount() {
      return 1;
    }

    /**
     * Returns the number of rows in the model.
     *
     * @return the number of rows in the model
     */
    public int getRowCount() {
      return cameraHue.length - 2;
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     *
     * @param rowIndex the row whose value is to be queried
     * @param columnIndex the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
      return cameraHue[rowIndex + 1];
    }
  }

}
