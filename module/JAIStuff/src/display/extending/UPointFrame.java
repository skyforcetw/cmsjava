package display.extending;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import shu.ui.GUIUtils;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import java.awt.image.RenderedImage;
import javax.media.jai.PlanarImage;
import javax.media.jai.JAI;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import javax.swing.JScrollBar;
import java.awt.*;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.media.jai.*;
import java.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class UPointFrame extends JFrame implements MouseMotionListener,
        MouseListener {
    BorderLayout borderLayout1 = new BorderLayout();
    JLabel jLabel1 = new JLabel();
    private DisplayJAIWithPixelInfo dj;
    RenderedImage image;
    JScrollPane jScrollPane1 = new JScrollPane();
    JPanel jPanel1 = new JPanel();
    JToggleButton jToggleButton1 = new JToggleButton();
    public UPointFrame() {
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void setImage(RenderedImage image) {
        this.image = image;
        dj = new DisplayJAIWithPixelInfo(image);
        dj.addMouseMotionListener(this);
        dj.addMouseListener(this);
//        dj.setDoubleBuffered(true);
        jScrollPane1.setViewportView(dj);

    }

    private void jbInit() throws Exception {
        getContentPane().setLayout(borderLayout1);
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jToggleButton1.setMnemonic('0');
        jToggleButton1.setText("Move");
        jLabel1.setMinimumSize(new Dimension(65, 27));
        jLabel1.setPreferredSize(new Dimension(65, 27));
        jButton1.setText("Smooth");
        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton1_actionPerformed(e);
            }
        });
        this.getContentPane().add(jLabel1, java.awt.BorderLayout.SOUTH);
        this.getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);
        jPanel1.add(jToggleButton1);
        jPanel1.add(jButton1);
        this.getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);
    }

    public static void main(String[] args) {
        UPointFrame frame = new UPointFrame();
        GUIUtils.runAsApplication(frame, false);
        PlanarImage image = JAI.create("fileload", "d200.jpg");
        frame.setImage(image);
    }

    /**
     * Invoked when a mouse button is pressed on a component and then dragged.
     *
     * @param e MouseEvent
     * @todo Implement this java.awt.event.MouseMotionListener method
     */
    public void mouseDragged(MouseEvent e) {
        if (null != preEvent &&
            (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
//            System.out.println(showUPoint);
            boolean nearOtherDot = true;
            boolean movePicture = false;

            if (nearControlDot) {
                //move u-point
                resetAllDot();
                recalculateAllDotByAdjust(e.getX(), e.getY());
                drawUPoint(e.getX(), e.getY(), uPointSize, true, g2d);
                nearOtherDot = false;
            }
            //move other dot
            else if (nearSizeDot) {
                //此時拖動size bar
                uPointSize = (e.getX() - uPointDot.x) * 2;
                if (uPointSize < 0) {
                    //小於零的upoint沒意義
                    return;
                }
                sizeDot = new Point(uPointDot.x + uPointSize / 2,
                                    sizeDot.y);
            } else if (nearHDot) {
                h = e.getX() - uPointDot.x;
                hDot = new Point(uPointDot.x + h,
                                 uPointDot.y + HSVDotDistance);
            } else if (nearSDot) {
                s = e.getX() - uPointDot.x;
                sDot = new Point(uPointDot.x + s,
                                 uPointDot.y + HSVDotDistance * 2);

            } else if (nearVDot) {
                v = e.getX() - uPointDot.x;
                vDot = new Point(uPointDot.x + v,
                                 uPointDot.y + HSVDotDistance * 3);
            } else if (nearContrastDot) {
                c = e.getX() - uPointDot.x;
                contrastDot = new Point(uPointDot.x + c,
                                        uPointDot.y +
                                        HSVDotDistance * 4);
            } else {
                nearOtherDot = false;
                movePicture = true;

                //move picture
                int dx = e.getXOnScreen() - preEvent.getXOnScreen();
                int dy = e.getYOnScreen() - preEvent.getYOnScreen();

                JScrollBar horizontal = jScrollPane1.getHorizontalScrollBar();
                horizontal.setValue(horizontal.getValue() - dx);
                JScrollBar vertical = jScrollPane1.getVerticalScrollBar();
                vertical.setValue(vertical.getValue() - dy);

                if (showUPoint) {

                }
            }

            if (nearOtherDot) {
                drawUPoint(uPointDot.x, uPointDot.y, uPointSize, true, g2d);
            }
            if (!movePicture) {
                nowPoint = new UPoint(uPointDot, uPointSize, h, s, v, c);
            }
//            if (showUPoint) {
//                if (nearControlDot) {
//                    //move u-point
//                    resetAllDot();
//                    recalculateAllDotByAdjust(e.getX(), e.getY());
//                    drawUPoint(e.getX(), e.getY(), uPointSize, true, g2d);
//                    nowPoint = new UPoint(uPointDot, uPointSize, h, s, v, c);
//                } else {
//                    //move other dot
//                    if (nearSizeDot) {
//                        //此時拖動size bar
//                        uPointSize = (e.getX() - uPointDot.x) * 2;
//                        if (uPointSize < 0) {
//                            //小於零的upoint沒意義
//                            return;
//                        }
//                        sizeDot = new Point(uPointDot.x + uPointSize / 2,
//                                            sizeDot.y);
//                    } else if (nearHDot) {
//                        h = e.getX() - uPointDot.x;
//                        hDot = new Point(uPointDot.x + h,
//                                         uPointDot.y + HSVDotDistance);
//                    } else if (nearSDot) {
//                        s = e.getX() - uPointDot.x;
//                        sDot = new Point(uPointDot.x + s,
//                                         uPointDot.y + HSVDotDistance * 2);
//
//                    } else if (nearVDot) {
//                        v = e.getX() - uPointDot.x;
//                        vDot = new Point(uPointDot.x + v,
//                                         uPointDot.y + HSVDotDistance * 3);
//                    } else if (nearContrastDot) {
//                        c = e.getX() - uPointDot.x;
//                        contrastDot = new Point(uPointDot.x + c,
//                                                uPointDot.y +
//                                                HSVDotDistance * 4);
//                    }
//
//                    drawUPoint(uPointDot.x, uPointDot.y, uPointSize, true, g2d);
//                    nowPoint = new UPoint(uPointDot, uPointSize, h, s, v, c);
//                }
//
//            } else {
//                //move picture
//                int dx = e.getXOnScreen() - preEvent.getXOnScreen();
//                int dy = e.getYOnScreen() - preEvent.getYOnScreen();
//
//                JScrollBar horizontal = jScrollPane1.getHorizontalScrollBar();
//                horizontal.setValue(horizontal.getValue() - dx);
//                JScrollBar vertical = jScrollPane1.getVerticalScrollBar();
//                vertical.setValue(vertical.getValue() - dy);
//            }
        }
        preEvent = e;
    }

    private MouseEvent preEvent;

    /**
     * Invoked when the mouse cursor has been moved onto a component but no
     * buttons have been pushed.
     *
     * @param e MouseEvent
     */
    public void mouseMoved(MouseEvent e) {
        String pos = "(X " + e.getX() + ",Y " + e.getY() + ") ";
        jLabel1.setText(pos + dj.getPixelInfo()); // Update the label with the
        if (showUPoint) {
            nearSizeDot = isNearPoint(e.getX(), e.getY(), sizeDot,
                                      UPointTolerance);
            nearControlDot = isNearPoint(e.getX(), e.getY(), uPointDot,
                                         UPointTolerance);
            nearHDot = isNearPoint(e.getX(), e.getY(), hDot,
                                   UPointTolerance);
            nearSDot = isNearPoint(e.getX(), e.getY(), sDot,
                                   UPointTolerance);
            nearVDot = isNearPoint(e.getX(), e.getY(), vDot,
                                   UPointTolerance);
            nearContrastDot = isNearPoint(e.getX(), e.getY(), contrastDot,
                                          UPointTolerance);
            //此時的upoint不會動, 只有dot的顏色會更新, 所以沒repaint也沒差
            drawUPoint(uPointDot.x, uPointDot.y, uPointSize, false, g2d);
        }
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released)
     * on a component.
     *
     * @param e MouseEvent
     */
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     * @param e MouseEvent
     * @todo Implement this java.awt.event.MouseListener method
     */
    public void mousePressed(MouseEvent e) {

//        preEvent = null;
    }

    /**
     * Invoked when a mouse button has been released on a component.
     *
     * @param e MouseEvent
     * @todo Implement this java.awt.event.MouseListener method
     */
    public void mouseReleased(MouseEvent e) {
//        System.out.println("release");
        preEvent = null;
        Graphics g = dj.getGraphics();
        g2d = g instanceof Graphics2D ? (Graphics2D) g : null;
        if (MouseEvent.BUTTON1 == e.getButton()) {
            //左鍵
            if (showUPoint) {
                if (isNearUPoint()) {
                    drawUPoint(uPointDot.x, uPointDot.y, uPointSize, false, g2d);
                } else {
//                    resetAllDot();
//                    showUPoint = false;
                }
            } else {

            }

        } else if (MouseEvent.BUTTON3 == e.getButton()) {
            //右鍵
            if (g2d != null) {
                if (null != nowPoint) {

                    if (!nowPoint.isNonAdjust()) {
//                        System.out.println(nowPoint);
                        upointList.add(nowPoint);
                    }
                }
                dj.paint(g2d);
                resetAllDot();
                resetAllAdjust();
                drawUPoint(e.getX(), e.getY(), uPointSize, false, g2d);
                showUPoint = true;
                nowPoint = new UPoint(uPointDot, uPointSize, h, s, v, c);
            }
        }
    }

    private void recalculateAllDotByAdjust(int x, int y) {
//        int x = uPointDot.x;
//        int y = uPointDot.y;
        //======================================================================
        //H point
        //======================================================================
        hDot = new Point(x + h, y + HSVDotDistance);
        //======================================================================
        //S point
        //======================================================================
        sDot = new Point(x + s, y + HSVDotDistance * 2);
        //======================================================================
        //V point
        //======================================================================
        vDot = new Point(x + v, y + HSVDotDistance * 3);
        //======================================================================
        //Contrast point
        //======================================================================
        contrastDot = new Point(x + c, y + HSVDotDistance * 4);
    }

    private void resetAllDot() {
        sizeDot = hDot = sDot = vDot = contrastDot = null;
//        showUPoint = false;
    }

    private void resetAllAdjust() {
        h = s = v = c = 0;
    }

    private Graphics2D g2d;
    private final static BasicStroke UPointStroke = new BasicStroke(3.0f);
    private final static BasicStroke LineStroke = new BasicStroke(1f);
    private final static int UPointDotSize = 9;
    private final static int UPointTolerance = UPointDotSize + 2;
    private final static int HSVDotDistance = 20;
    private final static int UPointSize = 50;
    private final static int TextOffset = 14;

    private final static Color NearColor = Color.green;
    private final static Color FarColor = Color.red;
    private boolean nearSizeDot = false;
    private boolean nearControlDot = false;
    private boolean nearHDot = false;
    private boolean nearSDot = false;
    private boolean nearVDot = false;
    private boolean nearContrastDot = false;

    private Point sizeDot;
    private Point hDot;
    private Point sDot;
    private Point vDot;
    private Point contrastDot;
    private Point uPointDot;
    private int h, s, v, c;
    java.util.List<UPoint> upointList = new ArrayList<UPoint>();
    UPoint nowPoint;


    private boolean isNearUPoint() {
        return nearSizeDot || nearControlDot || nearHDot || nearSDot ||
                nearVDot || nearContrastDot;
    }

    private boolean isNearPoint(int x, int y, Point point, int tolerance) {
        return Math.abs(x - point.x) <= tolerance &&
                Math.abs(y - point.y) <= tolerance;
    }

    private Rectangle getUPointPaintRectangle(int x, int y, int size) {
        int minHeight = HSVDotDistance * 4 + size * 4;
        int height = (size * 4) < minHeight ? minHeight : size * 4;
        Rectangle rectangle = new Rectangle(x - size * 2, y - size * 2,
                                            size * 4, height);
        return rectangle;
    }


    private void drawUPoint(int x, int y, int size, boolean drawOval,
                            Graphics2D g) {
        Rectangle rectangle = getUPointPaintRectangle(x, y, size);
        dj.paintImmediately(rectangle);

        uPointDot = new Point(x, y);
        g.setColor(Color.white);
        //圓圈
        if (drawOval) {
            g.setStroke(UPointStroke);
            g.drawOval(x - size / 2, y - size / 2, size, size);
        }
        g.setStroke(LineStroke);
        //直槓
        g.drawLine(x, y, x, y + HSVDotDistance * 4);

        //size槓
        g.setColor(Color.white);
        g.drawLine(x, y, x + size / 2, y);

        //h槓
        //s槓
        //v槓

        //======================================================================
        //size
        //======================================================================
        //size dot
        g.setColor(nearSizeDot ? NearColor : FarColor);
        sizeDot = (null == sizeDot) ?
                  new Point(x + size / 2 - UPointDotSize / 2,
                            y - UPointDotSize / 2) : sizeDot;
        g.fillOval(sizeDot.x, sizeDot.y, UPointDotSize, UPointDotSize);
        //======================================================================
        //control point
        //======================================================================
        g.setColor(nearControlDot ? NearColor : FarColor);
        g.fillOval(x - UPointDotSize / 2, y - UPointDotSize / 2, UPointDotSize,
                   UPointDotSize);
        //======================================================================
        //H point
        //======================================================================
        g.setColor(nearHDot ? NearColor : FarColor);
        hDot = (null == hDot) ?
               new Point(x, y + HSVDotDistance) : hDot;
        g.fillOval(hDot.x - UPointDotSize / 2, hDot.y - UPointDotSize / 2,
                   UPointDotSize, UPointDotSize);
        g.drawString("H", hDot.x - TextOffset, hDot.y);
        //======================================================================
        //S point
        //======================================================================
        g.setColor(nearSDot ? NearColor : FarColor);
        sDot = (null == sDot) ?
               new Point(x, y + HSVDotDistance * 2) : sDot;
        g.fillOval(sDot.x - UPointDotSize / 2, sDot.y - UPointDotSize / 2,
                   UPointDotSize, UPointDotSize);
        g.drawString("S", sDot.x - TextOffset, sDot.y);
        //======================================================================
        //V point
        //======================================================================
        g.setColor(nearVDot ? NearColor : FarColor);
        vDot = (null == vDot) ?
               new Point(x, y + HSVDotDistance * 3) : vDot;
        g.fillOval(vDot.x - UPointDotSize / 2, vDot.y - UPointDotSize / 2,
                   UPointDotSize, UPointDotSize);
        g.drawString("V", vDot.x - TextOffset, vDot.y);
        //======================================================================
        //Contrast point
        //======================================================================
        g.setColor(nearContrastDot ? NearColor : FarColor);
        contrastDot = (null == contrastDot) ?
                      new Point(x, y + HSVDotDistance * 4) : contrastDot;
        g.fillOval(contrastDot.x - UPointDotSize / 2,
                   contrastDot.y - UPointDotSize / 2,
                   UPointDotSize, UPointDotSize);
        g.drawString("C", contrastDot.x - TextOffset, contrastDot.y);
    }

    private boolean showUPoint = false;
    private int uPointSize = UPointSize;
    JButton jButton1 = new JButton();


    /**
     * Invoked when the mouse enters a component.
     *
     * @param e MouseEvent
     * @todo Implement this java.awt.event.MouseListener method
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e MouseEvent
     * @todo Implement this java.awt.event.MouseListener method
     */
    public void mouseExited(MouseEvent e) {
    }

    public void jButton1_actionPerformed(ActionEvent e) {
        int kernelSize = 10;
        float[] kernelMatrix = new float[kernelSize * kernelSize];
        for (int k = 0; k < kernelMatrix.length; k++) {
            kernelMatrix[k] = 1.0f / (kernelSize * kernelSize);
        }
        KernelJAI kernel = new KernelJAI(kernelSize, kernelSize, kernelMatrix);
        // Run the convolve operator, creating the output image.
        PlanarImage output = JAI.create("convolve", image, kernel);
        this.setImage(output);
    }
}


class UPoint {
    int size;
    int h, s, v, c;
    Point dot;
    boolean isNonAdjust() {
        return h == 0 && s == 0 && v == 0 && c == 0;
    }

    UPoint(Point dot, int size, int h, int s, int v, int c) {
        this.dot = dot;
        this.size = size;
        this.h = h;
        this.s = s;
        this.v = v;
        this.c = c;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return dot + " " + size + " " + h + " " + s + " " + v + " " + c;
    }
}
