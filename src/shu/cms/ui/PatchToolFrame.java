package shu.cms.ui;

import java.util.*;
import java.util.List;

import java.awt.*;
import javax.swing.*;

import shu.cms.colorspace.depend.*;
import shu.cms.lcd.*;

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
 * @deprecated
 */
public class PatchToolFrame
    extends JFrame {
  BorderLayout borderLayout1 = new BorderLayout();

  public PatchToolFrame() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  protected PatchCanvas patchCanvas = new PatchCanvas();
  protected List<RGB> referenceRGBList = null;
  protected List<RGB> targetRGBList = null;

  protected class PatchCanvas
      extends JComponent {

    protected Dimension d;
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      d = this.getSize();
      boolean drawRef = referenceRGBList != null;
      boolean drawTar = (targetRGBList != null) ?
          (referenceRGBList.size() == targetRGBList.size()) : false;

      if (drawRef) {
        int rgbSize = referenceRGBList.size();

        if (rowLength == 0) {
          hwItems = getHeightAndWidthItems(d, rgbSize);
          patchWidth = getPatchWidth(d, hwItems);
        }
        else {
          patchWidth = d.height / rowLength;
          hwItems = new int[] {
              rowLength, (int) Math.ceil(rgbSize / rowLength)};
        }
        drawWidth = patchWidth - 4;

        for (int i = 0; i < rgbSize; i++) {
          int row = i % hwItems[0];
          int col = i / hwItems[0];
          RGB refRGB = referenceRGBList.get(i);

          int x = (col * patchWidth) + 2;
          int y = (row * patchWidth) + 2;

          if (drawTar) {

            g.setColor(refRGB.getColor());
            g.fillRect(x, y, drawWidth, drawWidth);

            RGB tarRGB = targetRGBList.get(i);
            g.setColor(tarRGB.getColor());
            g.fillRect(x + drawWidth / 4, y + drawWidth / 4, drawWidth / 2,
                       drawWidth / 2);
          }
          else {
            g.setColor(refRGB.getColor());
            g.fillRect(x, y, drawWidth, drawWidth);
          }

        }
      }
    }

    int patchWidth;
    int drawWidth;
    int[] hwItems;

  }

  protected final static int getPatchWidth(Dimension d, int[] hwItems) {
    return d.height / hwItems[0];
  }

  protected final static int[] getHeightAndWidthItems(Dimension d,
      int patchItems) {
    int width = (int) Math.floor(Math.sqrt( ( (double) (d.width * d.height)) /
                                           patchItems));

    int hItems = (int) Math.ceil( ( (double) d.height) / width);
    int wItems = (int) Math.ceil( ( (double) patchItems) / hItems);
    int patchWidth = Math.min( (int) Math.floor(d.height / hItems),
                              (int) Math.floor(d.width / wItems));

    hItems = (wItems * patchWidth > d.width) ? hItems + 1 : hItems;

    return new int[] {
        hItems, wItems};
  }

  protected int rowLength = 0;

  private void jbInit() throws Exception {
    getContentPane().setLayout(borderLayout1);
    this.getContentPane().setBackground(Color.gray);
//    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setSize(500, 500);
    this.getContentPane().add(patchCanvas, java.awt.BorderLayout.CENTER);

    this.setVisible(true);

  }

  public static void main(String[] args) {
    LCDTarget.setRGBNormalize(false);
    PatchToolFrame patchtoolframe = new PatchToolFrame();
    List<RGB>
        rgbList = LCDTarget.Instance.getRGBList(LCDTargetBase.Number.WHQL);
    patchtoolframe.setReferenceRGBList(rgbList);

    List<RGB> tarRGBList = new ArrayList<RGB> ();
    for (RGB rgb : rgbList) {
      RGB tarRGB = (RGB) rgb.clone();
      tarRGB.G += 20;
      tarRGB.G = tarRGB.G > 255 ? 255 : tarRGB.G;
      tarRGBList.add(tarRGB);
    }
//    patchtoolframe.setTargetRGBList(tarRGBList);

    patchtoolframe.repaint();
  }

  public int getRowLength() {
    return rowLength;
  }

  public shu.cms.ui.PatchToolFrame.PatchCanvas getPatchCanvas() {
    return patchCanvas;
  }

  public void setRowLength(int rowLength) {
    this.rowLength = rowLength;
  }

  public void setReferenceRGBList(List referenceRGBList) {
    this.referenceRGBList = referenceRGBList;
  }

  public void setTargetRGBList(List targetRGBList) {
    this.targetRGBList = targetRGBList;
  }
}
