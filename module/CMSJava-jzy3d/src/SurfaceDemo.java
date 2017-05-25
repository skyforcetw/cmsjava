import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.ChartMouseController;
import org.jzy3d.chart.controllers.thread.ChartThreadController;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapHotCold;
import org.jzy3d.global.Settings;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axes.layout.providers.SmartTickProvider;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.ITickRenderer;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.Renderer2d;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

public class SurfaceDemo
    extends JFrame {
  private float[] aValue;
  private String[] aLabel;
  private float[] bValue;
  private String[] bLabel;
  private float[][] sData;
  private Chart chart1, chart2;
  private Shape surface1, surface2;
  private JSlider alphaSld;
  private JCheckBox showWireframeChk, showFacesChk, orthChk;
  private List<Polygon> polygons;

  public SurfaceDemo() {
    super("Surface Demo");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Settings.getInstance().setHardwareAccelerated(true);

    buildData();

    chart1 = new Chart(Quality.Nicest, "swing");
    chart1.addRenderer(new TitleRenderer("Chart1", chart1));
    chart1.getView().setViewPositionMode(ViewPositionMode.FREE);
    chart1.getAxeLayout().setXTickRenderer(new ARenderer(aValue, aLabel));
    chart1.getAxeLayout().setXTickProvider(new ATickProvider(aValue));
    chart1.getAxeLayout().setYTickRenderer(new BRenderer(bValue, bLabel));
    chart1.getAxeLayout().setYTickProvider(new BTickProvider(bValue));
    chart1.getAxeLayout().setZTickRenderer(new SRenderer());

    chart2 = new Chart(Quality.Nicest, "swing");
    chart2.addRenderer(new TitleRenderer("Chart2", chart2));
    chart2.getView().setViewPositionMode(ViewPositionMode.FREE);
    chart2.getAxeLayout().setXTickRenderer(new ARenderer(aValue, aLabel));
    chart2.getAxeLayout().setXTickProvider(new ATickProvider(aValue));
    chart2.getAxeLayout().setYTickRenderer(new BRenderer(bValue, bLabel));
    chart2.getAxeLayout().setYTickProvider(new BTickProvider(bValue));
    chart2.getAxeLayout().setZTickRenderer(new SRenderer());

    ChartMouseController mouse = new ChartMouseController();
    chart1.addController(mouse);

    ChartThreadController thread = new ChartThreadController();
    mouse.addSlaveThreadController(thread);
    chart1.addController(thread);

    mouse = new ChartMouseController();
    chart2.addController(mouse);
    thread = new ChartThreadController();
    mouse.addSlaveThreadController(thread);
    chart2.addController(thread);
    //thread.start();

    JPanel cp = (JPanel) getContentPane();

    JPanel southPanel = new JPanel(new BorderLayout());
    cp.add(southPanel, BorderLayout.SOUTH);

    JPanel panel = new JPanel(new GridLayout(1, 0, 0, 0));
    southPanel.add(panel, BorderLayout.WEST);

    ActionHandler ah = new ActionHandler();
    panel.add(showWireframeChk = new JCheckBox("Wireframe", true));
    showWireframeChk.addActionListener(ah);
    panel.add(showFacesChk = new JCheckBox("Faces", true));
    showFacesChk.addActionListener(ah);
    panel.add(orthChk = new JCheckBox("Ortho"));
    orthChk.addActionListener(ah);

    southPanel.add(alphaSld = new JSlider(0, 100), BorderLayout.CENTER);
    alphaSld.addChangeListener(new ChangeHandler());

    chart();

    setSize(800, 800);
    setVisible(true);
  }

  private void buildData() {
    aValue = new float[] {
        -15, -10, -5, 0, 5, 10, 15};
    aLabel = new String[] {
        "A-15", "A-10", "A-5", "A-Base", "A+5", "A+10", "A+15"};
    bValue = new float[] {
        -10, -5, -3, 0, 3, 5, 10};
    bLabel = new String[] {
        "B-10", "B-5", "B-3", "B-Base", "B+3", "B+5", "B+10"};

    sData = new float[][] {
        {
        -74323560F, -64700478F, -36468490F, -6336259F, 38365868F, 84427406F,
        211319644}, {
        -68280838F, -59355475F, -32888594F, -3615625F, 42208781F, 90263703F,
        218894640}, {
        -65257262F, -56604354F, -30822641F, -1917163F, 44444060F, 93381195F,
        222773719}, {
        -62237213F, -53796957F, -28583800F, 0F, 46862986F, 96602286F,
        226729272}, {
        -59220932F, -50928664F, -26180580F, 2125525F, 49452987F, 99916231F,
        230768255}, {
        -56205255F, -47993425F, -23619943F, 4447846F, 52199770F, 103318587F,
        234893907}, {
        -50149930F, -41902429F, -18062040F, 9635538F, 58099637F, 110391217F,
        243401887}
    };

    polygons = new ArrayList<Polygon> ();
    for (int y = 0; y < sData.length - 1; y++) {
      for (int x = 0; x < sData[y].length - 1; x++) {
        Polygon polygon = new Polygon();
        polygon.add(new Point(new Coord3d(aValue[x], bValue[y], sData[x][y])));
        polygon.add(new Point(new Coord3d(aValue[x], bValue[y + 1],
                                          sData[x][y + 1])));
        polygon.add(new Point(new Coord3d(aValue[x + 1], bValue[y + 1],
                                          sData[x + 1][y + 1])));
        polygon.add(new Point(new Coord3d(aValue[x + 1], bValue[y],
                                          sData[x + 1][y])));
        polygons.add(polygon);
      }
    }
  }

  private void chart() {
    chart1.getAxeLayout().setXAxeLabel("A1");
    chart1.getAxeLayout().setYAxeLabel("B1");
    chart1.getAxeLayout().setZAxeLabel("S1");

    chart2.getAxeLayout().setXAxeLabel("A2");
    chart2.getAxeLayout().setYAxeLabel("B2");
    chart2.getAxeLayout().setZAxeLabel("S2");

    surface1 = new Shape(polygons);
    ColorMapper colorMapper = new ColorMapper(new ColorMapHotCold(),
                                              surface1.getBounds().getZmin(),
                                              surface1.getBounds().getZmax(),
                                              new Color(1f, 1f, 1f, .7f));

    surface1.setColorMapper(colorMapper);
    surface1.setWireframeDisplayed(true);
    surface1.setWireframeColor(Color.BLACK);
    chart1.getScene().getGraph().add(surface1);

    surface2 = new Shape(polygons);
    colorMapper = new ColorMapper(new ColorMapHotCold(),
                                  surface2.getBounds().getZmin(),
                                  surface2.getBounds().getZmax(),
                                  new Color(1f, 1f, 1f, .7f));
    surface2.setColorMapper(colorMapper);
    surface2.setWireframeDisplayed(true);
    surface2.setWireframeColor(Color.BLACK);
    chart2.getScene().getGraph().add(surface2);

    JPanel cp = (JPanel) getContentPane();

    JPanel centerPanel = new JPanel(new GridLayout(1, 2, 0, 0));
    cp.add(centerPanel, BorderLayout.CENTER);

    centerPanel.add( (javax.swing.JComponent) chart1.getCanvas());
    centerPanel.add( (javax.swing.JComponent) chart2.getCanvas());
  }

  protected static String getLabel(float val,
                                   float[] value,
                                   String[] label) {
    for (int i = 0; i < label.length; i++) {
      if (val == value[i]) {
        return label[i];
      }
    }
    return "";
  }

  private static class TitleRenderer
      implements Renderer2d {
    private String title;
    private Chart chart;

    private TitleRenderer(String title,
                          Chart chart) {
      this.title = title;
      this.chart = chart;
    }

    public void paint(Graphics g) {
      g.setColor(java.awt.Color.BLACK);
      g.setFont(new Font("Elephant", Font.BOLD, 26));
      g.drawString(title,
                   (int) (15 + 0.05d * chart.getCanvas().getRendererWidth()),
                   (int) (15 + 0.05d * chart.getCanvas().getRendererHeight()));
    }
  };

  private static class ARenderer
      implements ITickRenderer {
    private float[] value;
    private String[] label;

    public ARenderer(float[] value,
                     String[] label) {
      this.value = value;
      this.label = label;
    }

    public String format(float val) {
      return getLabel(val, value, label);
    }
  }

  private static class ATickProvider
      extends SmartTickProvider {
    private float[] value;

    public ATickProvider(float[] value) {
      this.value = value;
    }

    public float[] generateTicks(float min, float max) {
      return generateTicks(min, max, getSteps(min, max));
    }

    public float[] generateTicks(float min, float max, int steps) {
      return value;
    }

    public int getSteps(float min, float max) {
      return value.length;
    }
  }

  private static class BRenderer
      implements ITickRenderer {
    private float[] value;
    private String[] label;

    public BRenderer(float[] value,
                     String[] label) {
      this.value = value;
      this.label = label;
    }

    public String format(float val) {
      return getLabel(val, value, label);
    }
  }

  private static class BTickProvider
      extends SmartTickProvider {
    private float[] value;

    public BTickProvider(float[] value) {
      this.value = value;
    }

    public float[] generateTicks(float min, float max) {
      return generateTicks(min, max, getSteps(min, max));
    }

    public float[] generateTicks(float min, float max, int steps) {
      return value;
    }

    public int getSteps(float min, float max) {
      return value.length;
    }
  }

  private class SRenderer
      implements ITickRenderer {
    public String format(float value) {
      return Integer.toString( (int) (value / 1000000)) + "M";
    }
  }

  private class ActionHandler
      implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
      if (evt.getSource() == showFacesChk) {
        surface1.setFaceDisplayed(showFacesChk.isSelected());
      }
      else if (evt.getSource() == showWireframeChk) {
        surface1.setWireframeDisplayed(showWireframeChk.isSelected());
      }
      else if (evt.getSource() == orthChk) {
        chart1.getView().setCameraMode(orthChk.isSelected() ?
                                       CameraMode.ORTHOGONAL :
                                       CameraMode.PERSPECTIVE);
      }
      chart1.render();
    }
  }

  private class ChangeHandler
      implements ChangeListener {
    public void stateChanged(ChangeEvent e) {
      float alpha = alphaSld.getValue() / 100.0f;
      ColorMapper colorMapper = new ColorMapper(new ColorMapHotCold(),
                                                surface1.getBounds().getZmin(),
                                                surface1.getBounds().getZmax(),
                                                new Color(1f, 1f, 1f, alpha));
      surface1.setColorMapper(colorMapper);
      chart1.render();
    }
  }

  public static void main(String[] args) {
    new SurfaceDemo();
  }
}
