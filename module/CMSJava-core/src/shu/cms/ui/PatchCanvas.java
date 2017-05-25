package shu.cms.ui;

import java.util.*;
import java.util.List;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.math.plot.*;
import shu.cms.colorspace.depend.*;
import shu.cms.lcd.*;
import shu.util.*;

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
public class PatchCanvas
    extends JComponent {

  protected final static class CanvasToolBar
      extends JToolBar {
    public CanvasToolBar(PatchCanvas canvas) {
      buttonCopyToClipboard = new JButton(new ImageIcon(PlotPanel.class.
          getResource("icons/toclipboard.png")));
      buttonCopyToClipboard.setToolTipText("Copy graphics to clipboard");
      buttonCopyToClipboard.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          copyToClipboard();
        }
      });

      add(buttonCopyToClipboard, null);
      this.patchCanvas = canvas;
    }

    void copyToClipboard() {
      patchCanvas.toClipboard();
    }

    protected JButton buttonCopyToClipboard;
    protected PatchCanvas patchCanvas;
  }

  /**
   * ���ͮe��PatchCanvas���@��JFrame
   * @param canvas PatchCanvas
   * @return JFrame
   */
  public final static JFrame getJFrameInstance(PatchCanvas canvas) {
    JFrame frame = new JFrame();
    canvas.setBackground(Color.gray);
    frame.add(canvas);
    CanvasToolBar toolbar = new CanvasToolBar(canvas);
    frame.add(toolbar, BorderLayout.NORTH);
    frame.pack();
    return frame;
  }

  public final static JFrame getJFrameInstance(int width, int height, List<RGB>
      referenceRGBList, List<RGB> targetRGBList) {
    PatchCanvas canvas = new PatchCanvas();
    canvas.setPreferredSize(new Dimension(width, height));
    canvas.setReferenceRGBList(referenceRGBList);
    canvas.setTargetRGBList(targetRGBList);
    JFrame frame = PatchCanvas.getJFrameInstance(canvas);
    return frame;
  }

  public PatchCanvas() {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void main(String[] args) {
    LCDTarget.setRGBNormalize(false);
    List<RGB>
        rgbList = LCDTarget.Instance.getRGBList(LCDTargetBase.Number.FourColor);
    PatchCanvas canvas = new PatchCanvas();
//    canvas.gap = false;
    canvas.setReferenceRGBList(rgbList);
    canvas.setSelectEnable(true);
    canvas.setRowLength(3);

    List<RGB> tarRGBList = new ArrayList<RGB> ();
    List<RGB> tarRGBList2 = new ArrayList<RGB> ();
    for (RGB rgb : rgbList) {
      RGB tarRGB = (RGB) rgb.clone();
      tarRGB.G += 20;
      tarRGB.G = tarRGB.G > 255 ? 255 : tarRGB.G;
      tarRGBList.add(tarRGB);

      RGB tarRGB2 = (RGB) rgb.clone();
      tarRGB2.G -= 20;
      tarRGB2.G = tarRGB2.G > 255 ? 255 : tarRGB2.G;
      tarRGB2.G = tarRGB2.G < 0 ? 0 : tarRGB2.G;
      tarRGBList2.add(tarRGB2);

    }

//    canvas.setTargetRGBList(tarRGBList);
//    canvas.setTargetRGBList2(tarRGBList2);
//    canvas.setHighLightIndex(new int[] {44, 45, 46}, Color.green);

    JFrame frame = getJFrameInstance(canvas);
    frame.setVisible(true);
    frame.setSize(800, 400);
//
//    JFrame frame = new JFrame();
//    frame.getContentPane().setBackground(Color.gray);
//    frame.add(canvas);
//    frame.pack();
//    frame.setVisible(true);
  }

  /**
   * �N�v���ƻs��ŶKï
   */
  public void toClipboard() {
    Image image = createImage(getWidth(), getHeight());
    paint(image.getGraphics());
    Utils.setClipboard(image);
  }

  private boolean horizontalDraw = false;

  /**
   * canvas���j�p
   */
  protected Dimension d;
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    d = this.getSize();
    //�e�I��
    g.setColor(this.getBackground());
    g.fillRect(0, 0, d.width, d.height);

    boolean drawRef = referenceRGBList != null;
    boolean drawTar = (targetRGBList != null) ?
        (referenceRGBList.size() == targetRGBList.size()) : false;
    boolean drawTar2 = (targetRGBList2 != null) ?
        (referenceRGBList.size() == targetRGBList2.size()) : false;

    if (drawRef) {
      //========================================================================
      // �ereferenceRGBList
      //========================================================================
      int rgbSize = referenceRGBList.size();

      if (rowLength == -1) {
        //�S��������ת����X
        hwItems = getHeightAndWidthItems(d, rgbSize);
        patchWidth = getPatchWidth(d, hwItems);
      }
      else {
        //��������ת����X
        patchWidth = d.height / rowLength;
        hwItems = new int[] {
            rowLength, (int) Math.ceil(rgbSize / rowLength)};
      }
      drawWidth = patchWidth - gap * 2;

      for (int i = 0; i < rgbSize; i++) {
        int row = i % (hwItems[0] + 0);
        int col = i / (hwItems[0] + 0);
        RGB refRGB = referenceRGBList.get(i);

        int x = (col * patchWidth) + gap;
        int y = (row * patchWidth) + gap;
        if (horizontalDraw) {
          x = x - y;
          y = x + y;
          x = -x + y;
        }

        g.setColor(refRGB.getColor());
        g.fillRect(x, y, drawWidth, drawWidth);

        if (drawTar) {
          //�etargetRGBList
          RGB tarRGB = targetRGBList.get(i);
          g.setColor(tarRGB.getColor());
          g.fillRect(x + drawWidth / 4, y + drawWidth / 4, drawWidth / 2,
                     drawWidth / 2);
        }

        if (drawTar2) {
          //�etargetRGBList2
          RGB tarRGB = targetRGBList2.get(i);
          g.setColor(tarRGB.getColor());
          g.fillRect(x + (int) (drawWidth * (5. / 8)), y + drawWidth / 4,
                     (int) (drawWidth * (1. / 8)),
                     drawWidth / 2);
        }

      }
      //========================================================================

      //========================================================================
      // ��ݭnhighLight���e�X��
      //========================================================================
      if (highLightIndex != null) {
        int size = highLightIndex.length;
        for (int m = 0; m < size; m++) {
          int i = highLightIndex[m];
          if (i >= rgbSize) {
            continue;
          }
          int row = i % hwItems[0];
          int col = i / hwItems[0];

          int x = (col * patchWidth) + 2;
          int y = (row * patchWidth) + 2;

          g.setColor(highLightColor);
          g.drawRect(x + 1, y + 1, drawWidth - 3, drawWidth - 3);
          g.drawRect(x + 2, y + 2, drawWidth - 5, drawWidth - 5);
        }
      }
      //========================================================================



      //========================================================================
      // ��w�I�諸�ХܥX��
      //========================================================================
      if (selectEnable && pressed) {
        int x = memoryPressed[0] * patchWidth;
        int y = memoryPressed[1] * patchWidth;
        //���U�h��
        g.setColor(selectedColor);
        //�e��Ӯ�
        //��1
        g.drawRect(x, y, patchWidth - 1,
                   patchWidth - 1);
        //��2
        g.drawRect(x + 1, y + 1, patchWidth - 3,
                   patchWidth - 3);
      }

      //����쪺index
      selectIndex = selectedCol * hwItems[0] + selectedRow;
      if (selectEnable && selectedCol != -1 && selectIndex < rgbSize &&
          selectedRow < hwItems[0]) {

        int x = (selectedCol * patchWidth);
        int y = (selectedRow * patchWidth);

        //�⥿�I�諸�e�X��
        Color c = mousePress ? selectedColor : selectColor;
        g.setColor(c);
        g.drawRect(x, y, patchWidth - 1, patchWidth - 1);

        //======================================================================
        // �������U�h����m�I
        //======================================================================
        if (mousePress) {
          memoryPressed[0] = selectedCol;
          memoryPressed[1] = selectedRow;
          pressed = true;
          selectedIndex = selectIndex;
        }
        //======================================================================

      }
      //========================================================================
    }

  }

  /**
   * �N�ثe����쪺index�^��
   * @return int
   */
  public int getSelectIndex() {
    return selectedCol != -1 ? selectIndex : -1;
  }

  /**
   * �Nclick�I�諸index�^��
   * @return int
   */
  public int getSelectedIndex() {
    return pressed ? selectedIndex : -1;
  }

  /**
   * �ثe�����index
   */
  protected int selectIndex = 0;
  /**
   * �w����쪺index
   */
  protected int selectedIndex = 0;
  /**
   * �O�_���U�h�F?
   */
  protected boolean pressed = false;
  /**
   * �O�Ы��U�h����m
   */
  protected int[] memoryPressed = new int[2];
  /**
   * patch���e��
   */
  protected int patchWidth;
  /**
   * ��ڭn�e���e��
   */
  protected int drawWidth;
  /**
   * ���μe���ƶq
   */
  protected int[] hwItems;
  /**
   * �ѦҪ�RGB
   */
  protected List<RGB> referenceRGBList = null;
  /**
   * �ؼЪ�RGB
   */
  protected List<RGB> targetRGBList = null;
  /**
   * �ؼвĤGRGB
   */
  protected List<RGB> targetRGBList2 = null;
  /**
   * �C��row�i��patch�ƶq
   */
  protected int rowLength = -1;

  /**
   * highlight��index
   */
  protected int[] highLightIndex;
  /**
   * highlight��color
   */
  protected Color highLightColor;
  /**
   * �w����ĥΪ��C��
   */
  protected Color selectedColor = Color.white;
  /**
   * �ثe����ĥΪ��C��
   */
  protected Color selectColor = Color.white;
  /**
   * �w�����column (�Hpatch�����)
   */
  protected int selectedCol = -1;
  /**
   * �w�����row (�Hpatch�����)
   */
  protected int selectedRow = -1;
  /**
   * �O�_�Ұʿ���\��
   */
  protected boolean selectEnable = false;
  /**
   * �����gap���Z(in pixel)
   */
  protected int gap = 0;

  /**
   * �p��patch���e��
   * @param d Dimension
   * @param hwItems int[]
   * @return int
   */
  private final static int getPatchWidth(Dimension d, int[] hwItems) {
    return d.height / hwItems[0];
  }

  /**
   *
   * @param d Dimension
   * @param patchItems int
   * @return int[]
   * @deprecated
   */
  private final static int[] getItemsAndWidth(Dimension d,
                                              int patchItems) {
    //���ΩҦ� ���n/����� ��C�@�Ӥ���������j�p
    //floor�L����˥h
    int width = (int) Math.floor(Math.sqrt( ( (double) (d.width * d.height)) /
                                           patchItems));
    //ceil�L����i��
    //�W�X�@��, ���F�O��
    int hItems = (int) Math.floor( ( (double) d.height) / width);
    int wItems = (int) Math.floor( ( (double) patchItems) / hItems);
    int patchWidth = Math.min( (int) Math.floor(d.height / hItems),
                              (int) Math.floor(d.width / wItems));
    hItems = (wItems * patchWidth > d.width) ? hItems + 1 : hItems;

    int[] result = new int[] {
        hItems, wItems, patchWidth};
    return result;
  }

  /**
   * �p����M�e��patch�ƶq
   * @param d Dimension
   * @param patchItems int
   * @return int[]
   */
  private final static int[] getHeightAndWidthItems(Dimension d,
      int patchItems) {
    //���ΩҦ� ���n/����� ��C�@�Ӥ���������j�p
    //floor�L����˥h
    int width = (int) Math.floor(Math.sqrt( ( (double) (d.width * d.height)) /
                                           patchItems));
    //ceil�L����i��
    //�W�X�@��, ���F�O��
    int hItems = (int) Math.ceil( ( (double) d.height) / width);
    int wItems = (int) Math.ceil( ( (double) patchItems) / hItems);
    int patchWidth = Math.min( (int) Math.floor(d.height / hItems),
                              (int) Math.floor(d.width / wItems));
    hItems = (wItems * patchWidth > d.width) ? hItems + 1 : hItems;

    return new int[] {
        hItems, wItems};
  }

  /**
   * �]�w�ѦҪ�RGB
   * @param referenceRGBList List
   */
  public void setReferenceRGBList(List<RGB> referenceRGBList) {
    this.referenceRGBList = referenceRGBList;
  }

  /**
   * �]�w�ؼЪ�RGB
   * @param targetRGBList List
   */
  public void setTargetRGBList(List<RGB> targetRGBList) {
    this.targetRGBList = targetRGBList;
  }

  /**
   * �]�w�C�@��row�i�H��patch�ƶq
   * @param rowLength int
   */
  public void setRowLength(int rowLength) {
    this.rowLength = rowLength;
  }

  /**
   * �]�w�ĤG�ӥؼ�RGB
   * @param targetRGBList2 List
   */
  public void setTargetRGBList2(List<RGB> targetRGBList2) {
    this.targetRGBList2 = targetRGBList2;
  }

  /**
   * �]�w����\��O�_�Ұ�
   * @param selectEnable boolean
   */
  public void setSelectEnable(boolean selectEnable) {
    this.selectEnable = selectEnable;
  }

  /**
   * �]�w������������Z
   * @param gap int
   */
  public void setGap(int gap) {
    this.gap = gap;
  }

  /**
   * �Q�w�n�Qhighlight�X�Ӫ�������ޭȤ�highlight����m
   * @param highLightIndex int[] highlight�����ޭ�
   * @param highLightColor Color highlight����m
   */
  public void setHighLightIndex(int[] highLightIndex, Color highLightColor) {
    this.highLightIndex = highLightIndex;
    this.highLightColor = highLightColor;
  }

  private void jbInit() throws Exception {
    this.setPreferredSize(new Dimension(500, 300));
    this.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        this_mousePressed(e);
      }

      public void mouseReleased(MouseEvent e) {
        this_mouseReleased(e);
      }
    });
    this.addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {
        this_mouseMoved(e);
      }
    });
  }

  public void this_mouseMoved(MouseEvent e) {
    if (selectEnable) {
      selectedCol = e.getX() / patchWidth;
      selectedRow = e.getY() / patchWidth;
      this.repaint();
    }
  }

  protected boolean mousePress = false;

  public void this_mousePressed(MouseEvent e) {
    if (selectEnable) {
      mousePress = true;
      this.repaint();
    }

  }

  public void this_mouseReleased(MouseEvent e) {
    if (selectEnable) {
      mousePress = false;
      this.repaint();
    }
  }

}
