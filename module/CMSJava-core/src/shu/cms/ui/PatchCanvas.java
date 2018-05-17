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
   * 產生容納PatchCanvas的一個JFrame
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
   * 將影像複製到剪貼簿
   */
  public void toClipboard() {
    Image image = createImage(getWidth(), getHeight());
    paint(image.getGraphics());
    Utils.setClipboard(image);
  }

  private boolean horizontalDraw = false;

  /**
   * canvas的大小
   */
  protected Dimension d;
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    d = this.getSize();
    //畫背景
    g.setColor(this.getBackground());
    g.fillRect(0, 0, d.width, d.height);

    boolean drawRef = referenceRGBList != null;
    boolean drawTar = (targetRGBList != null) ?
        (referenceRGBList.size() == targetRGBList.size()) : false;
    boolean drawTar2 = (targetRGBList2 != null) ?
        (referenceRGBList.size() == targetRGBList2.size()) : false;

    if (drawRef) {
      //========================================================================
      // 畫referenceRGBList
      //========================================================================
      int rgbSize = referenceRGBList.size();

      if (rowLength == -1) {
        //沒有限制長度的場合
        hwItems = getHeightAndWidthItems(d, rgbSize);
        patchWidth = getPatchWidth(d, hwItems);
      }
      else {
        //有限制長度的場合
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
          //畫targetRGBList
          RGB tarRGB = targetRGBList.get(i);
          g.setColor(tarRGB.getColor());
          g.fillRect(x + drawWidth / 4, y + drawWidth / 4, drawWidth / 2,
                     drawWidth / 2);
        }

        if (drawTar2) {
          //畫targetRGBList2
          RGB tarRGB = targetRGBList2.get(i);
          g.setColor(tarRGB.getColor());
          g.fillRect(x + (int) (drawWidth * (5. / 8)), y + drawWidth / 4,
                     (int) (drawWidth * (1. / 8)),
                     drawWidth / 2);
        }

      }
      //========================================================================

      //========================================================================
      // 把需要highLight的畫出來
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
      // 把已點選的標示出來
      //========================================================================
      if (selectEnable && pressed) {
        int x = memoryPressed[0] * patchWidth;
        int y = memoryPressed[1] * patchWidth;
        //按下去的
        g.setColor(selectedColor);
        //畫兩個框
        //框1
        g.drawRect(x, y, patchWidth - 1,
                   patchWidth - 1);
        //框2
        g.drawRect(x + 1, y + 1, patchWidth - 3,
                   patchWidth - 3);
      }

      //選取到的index
      selectIndex = selectedCol * hwItems[0] + selectedRow;
      if (selectEnable && selectedCol != -1 && selectIndex < rgbSize &&
          selectedRow < hwItems[0]) {

        int x = (selectedCol * patchWidth);
        int y = (selectedRow * patchWidth);

        //把正點選的畫出來
        Color c = mousePress ? selectedColor : selectColor;
        g.setColor(c);
        g.drawRect(x, y, patchWidth - 1, patchWidth - 1);

        //======================================================================
        // 紀錄按下去的位置點
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
   * 將目前選取到的index回傳
   * @return int
   */
  public int getSelectIndex() {
    return selectedCol != -1 ? selectIndex : -1;
  }

  /**
   * 將click點選的index回傳
   * @return int
   */
  public int getSelectedIndex() {
    return pressed ? selectedIndex : -1;
  }

  /**
   * 目前選取的index
   */
  protected int selectIndex = 0;
  /**
   * 已選取到的index
   */
  protected int selectedIndex = 0;
  /**
   * 是否按下去了?
   */
  protected boolean pressed = false;
  /**
   * 記憶按下去的位置
   */
  protected int[] memoryPressed = new int[2];
  /**
   * patch的寬度
   */
  protected int patchWidth;
  /**
   * 實際要畫的寬度
   */
  protected int drawWidth;
  /**
   * 長及寬的數量
   */
  protected int[] hwItems;
  /**
   * 參考的RGB
   */
  protected List<RGB> referenceRGBList = null;
  /**
   * 目標的RGB
   */
  protected List<RGB> targetRGBList = null;
  /**
   * 目標第二RGB
   */
  protected List<RGB> targetRGBList2 = null;
  /**
   * 每個row可放的patch數量
   */
  protected int rowLength = -1;

  /**
   * highlight的index
   */
  protected int[] highLightIndex;
  /**
   * highlight的color
   */
  protected Color highLightColor;
  /**
   * 已選取採用的顏色
   */
  protected Color selectedColor = Color.white;
  /**
   * 目前選取採用的顏色
   */
  protected Color selectColor = Color.white;
  /**
   * 已選取的column (以patch為單位)
   */
  protected int selectedCol = -1;
  /**
   * 已選取的row (以patch為單位)
   */
  protected int selectedRow = -1;
  /**
   * 是否啟動選取功能
   */
  protected boolean selectEnable = false;
  /**
   * 色塊的gap間距(in pixel)
   */
  protected int gap = 0;

  /**
   * 計算patch的寬度
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
    //先用所有 面積/方塊數 算每一個方塊的平均大小
    //floor無條件捨去
    int width = (int) Math.floor(Math.sqrt( ( (double) (d.width * d.height)) /
                                           patchItems));
    //ceil無條件進位
    //超出一個, 為了是填滿
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
   * 計算長和寬的patch數量
   * @param d Dimension
   * @param patchItems int
   * @return int[]
   */
  private final static int[] getHeightAndWidthItems(Dimension d,
      int patchItems) {
    //先用所有 面積/方塊數 算每一個方塊的平均大小
    //floor無條件捨去
    int width = (int) Math.floor(Math.sqrt( ( (double) (d.width * d.height)) /
                                           patchItems));
    //ceil無條件進位
    //超出一個, 為了是填滿
    int hItems = (int) Math.ceil( ( (double) d.height) / width);
    int wItems = (int) Math.ceil( ( (double) patchItems) / hItems);
    int patchWidth = Math.min( (int) Math.floor(d.height / hItems),
                              (int) Math.floor(d.width / wItems));
    hItems = (wItems * patchWidth > d.width) ? hItems + 1 : hItems;

    return new int[] {
        hItems, wItems};
  }

  /**
   * 設定參考的RGB
   * @param referenceRGBList List
   */
  public void setReferenceRGBList(List<RGB> referenceRGBList) {
    this.referenceRGBList = referenceRGBList;
  }

  /**
   * 設定目標的RGB
   * @param targetRGBList List
   */
  public void setTargetRGBList(List<RGB> targetRGBList) {
    this.targetRGBList = targetRGBList;
  }

  /**
   * 設定每一個row可以放的patch數量
   * @param rowLength int
   */
  public void setRowLength(int rowLength) {
    this.rowLength = rowLength;
  }

  /**
   * 設定第二個目標RGB
   * @param targetRGBList2 List
   */
  public void setTargetRGBList2(List<RGB> targetRGBList2) {
    this.targetRGBList2 = targetRGBList2;
  }

  /**
   * 設定選取功能是否啟動
   * @param selectEnable boolean
   */
  public void setSelectEnable(boolean selectEnable) {
    this.selectEnable = selectEnable;
  }

  /**
   * 設定色塊之間的間距
   * @param gap int
   */
  public void setGap(int gap) {
    this.gap = gap;
  }

  /**
   * 被定要被highlight出來的色塊索引值及highlight的色彩
   * @param highLightIndex int[] highlight的索引值
   * @param highLightColor Color highlight的色彩
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
