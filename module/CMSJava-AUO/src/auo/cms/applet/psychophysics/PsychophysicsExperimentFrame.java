package auo.cms.applet.psychophysics;

import java.beans.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import com.sun.media.jai.widget.*;
import shu.math.array.*;
import auo.cms.prefercolor.MemoryColorPatches;
import auo.cms.prefercolor.MemoryColorInterface;
import org.math.io.files.ASCIIFile;
import shu.ui.TinyDialog;
import com.borland.jbcl.layout.VerticalFlowLayout;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class PsychophysicsExperimentFrame
    extends JFrame implements PropertyChangeListener, ChangeListener {
  protected ImagePanel imagePanel = new ImagePanel();
  protected JPanel contentPane;
  protected JMenuBar jMenuBar1 = new JMenuBar();
  protected JMenu jMenuFile = new JMenu();
  protected JMenuItem jMenuFileExit = new JMenuItem();
  protected HSVPanel hsvPanel = new HSVPanel();
  protected JButton jButton1 = new JButton();
  protected JPanel jPanel1 = new JPanel();
  protected DisplayJAI displayJAI = new DisplayJAI();
  protected BorderLayout borderLayout1 = new BorderLayout();
  protected BorderLayout borderLayout2 = new BorderLayout();
  protected FlowLayout flowLayout1 = new FlowLayout();

  public PsychophysicsExperimentFrame() {
    try {
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
    imageCount = filenames.length;
    images = new Image[imageCount];
    for (int x = 0; x < imageCount; x++) {
      String filename = "psychophysics/" + filenames[x];
      images[x] = Toolkit.getDefaultToolkit().createImage(filename);
    }
    imagePanel.setImage(images[index % imageCount]);
    HSV hsv = getMemoryColorHSV(index);
    hsvPanel.setColor( (int) hsv.H, (int) hsv.S, (int) hsv.V);
  }

  long startTime = System.currentTimeMillis();
  File file = new File(Long.toString(startTime) + ".txt");

  HSV getMemoryColorHSV(int index) {
    CIELab Lab = CIELabArray[index % imageCount];
    CIEXYZ XYZ = Lab.toXYZ();
    RGB rgb = new RGB(RGB.ColorSpace.sRGB_gamma22, XYZ);
    HSV hsv = new HSV(rgb);
    return hsv;
  }

  protected int imageCount;
  protected String[] filenames = new String[] {
      "skin.jpg", "sky.jpg", "grass.jpg", "foliage.jpg", "banana.jpg",
      "orange.jpg"};
  protected int[][] coordinators = new int[][] {
      {
      423, 330}, {
      108, 65}, {
      130, 460}, {
      1048, 136}, {
      87, 504}, {
      863, 456}
  };
  protected MemoryColorInterface memoryColors = MemoryColorPatches.Korean.
      getInstance();
  protected CIELab[] CIELabArray = new CIELab[] {
      memoryColors.getSkin(), memoryColors.getSky(), memoryColors.getGrass(),
      memoryColors.getFoliage(),
      new CIELab(75.555852, 7.716508, 78.187896,
                 new CIEXYZ(0.950471, 1, 1.088830)),
      memoryColors.getOrange()};
  protected Image[] images;
  protected int index = 0;

  /**
   * Component initialization.
   *
   * @throws java.lang.Exception
   */
  private void jbInit() throws Exception {
    contentPane = (JPanel) getContentPane();
    contentPane.setLayout(borderLayout2);
    setSize(new Dimension(1680, 1050));
    setTitle("Psychophysics Experiment");
    jMenuFile.setText("File");
    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        jMenuFileExit_actionPerformed(actionEvent);
      }
    });
    jButton1.setText("OK");
    jButton1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    imagePanel.setLayout(borderLayout1);
    jPanel1.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.RIGHT);
    jLabel1.setText("1");
    jPanel2.setLayout(verticalFlowLayout1);
    jButton2.setText("Color");
    jButton2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton2_actionPerformed(e);
      }
    });
    jMenuBar1.add(jMenuFile);
    jMenuFile.add(jMenuFileExit);
    jPanel1.add(hsvPanel);
    jPanel1.add(jPanel2);
    jPanel2.add(jLabel1);
    jPanel2.add(jButton2);
    jPanel2.add(jButton1);
    contentPane.add(imagePanel, java.awt.BorderLayout.CENTER);
    contentPane.add(jPanel1, java.awt.BorderLayout.SOUTH);
    hsvPanel.addPropertyChangeListener(this);

    AbstractColorChooserPanel[] chooserPanels = jColorChooser1.getChooserPanels();
    jColorChooser1.removeChooserPanel(chooserPanels[0]);
    jColorChooser1.removeChooserPanel(chooserPanels[2]);
    jColorChooser1.getSelectionModel().addChangeListener(this);

  }

  /**
   * File | Exit action performed.
   *
   * @param actionEvent ActionEvent
   */
  void jMenuFileExit_actionPerformed(ActionEvent actionEvent) {
    System.exit(0);
  }

  /**
   * This method gets called when a bound property is changed.
   *
   * @param evt A PropertyChangeEvent object describing the event source and
   *   the property that has changed.
   */
  public void propertyChange(PropertyChangeEvent evt) {
    Color c = getHSVPanelColor();
    int[] coordinator = coordinators[index % imageCount];
    imagePanel.setClip(c, coordinator[0], coordinator[1], clipWidth, clipHeight);
  }

  int clipWidth = 65;
  int clipHeight = 65;
  protected JLabel jLabel1 = new JLabel();
  protected JColorChooser jColorChooser1 = new JColorChooser();
  protected JPanel jPanel2 = new JPanel();
  protected VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  protected JButton jButton2 = new JButton();
  protected Color getHSVPanelColor() {
    int[] hsvValues = hsvPanel.getHSVValue();
    HSV hsv = new HSV(RGB.ColorSpace.unknowRGB,
                      IntArray.toDoubleArray(hsvValues));
    Color c = hsv.toRGB().getColor();
    return c;
  }

  public void jButton1_actionPerformed(ActionEvent e) {
    int[] hsvValues = hsvPanel.getHSVValue();
    ASCIIFile.append(file, IntArray.toString(hsvValues) + "\n");

    if (index == 59) {
      long seconds = (System.currentTimeMillis() - startTime) / 1000;
      JOptionPane.showMessageDialog(this, "實驗結束! 用了" + seconds + "秒");
      System.exit(0);
    }

    index++;
    this.jLabel1.setText(Integer.toString(index + 1));
    imagePanel.setImage(images[index % imageCount]);
    int[] coordinator = coordinators[index % imageCount];
    HSV hsv = getMemoryColorHSV(index);
    hsvPanel.setColor( (int) hsv.H, (int) hsv.S, (int) hsv.V);
    Color c = getHSVPanelColor();
    jColorChooser1.setColor(c);
    imagePanel.setClip(c, coordinator[0], coordinator[1], clipWidth, clipHeight);
  }

  public void jButton2_actionPerformed(ActionEvent e) {
    jColorChooser1.setColor(this.getHSVPanelColor());
    JDialog dialog = JColorChooser.createDialog(this, "", false, jColorChooser1, null, null);
    dialog.setVisible(true);
  }

  /**
   * Invoked when the target of the listener has changed its state.
   *
   * @param e a ChangeEvent object
   * @todo Implement this javax.swing.event.ChangeListener method
   */
  public void stateChanged(ChangeEvent e) {
    Color newColor = jColorChooser1.getColor();
    RGB rgb = new RGB(RGB.ColorSpace.unknowRGB, newColor);
    HSV hsv = new HSV(rgb);
    hsvPanel.setColor( (int) hsv.H, (int) hsv.S, (int) hsv.V);
  }

}

class ImagePanel
    extends JPanel {
  Image image;

  public void setImage(Image image) {
    this.image = image;
    this.repaint();
  }

  Color color = Color.black;
  int x = 0, y = 0, width = 0, height = 0;
  public void setClip(Color color, int x, int y, int width, int height) {
    this.color = color;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.repaint();
  }

  public void paint(Graphics g) {
    g.clearRect(0, 0, this.getWidth(), this.getHeight());
    g.drawImage(image, 0, 0, this);
    g.setColor(color);
    g.fillRect(x, y, width, height);
  }
}
