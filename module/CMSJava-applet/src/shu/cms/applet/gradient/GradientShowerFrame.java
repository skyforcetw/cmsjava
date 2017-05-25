package shu.cms.applet.gradient;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

import javax.swing.*;

import shu.cms.ui.*;
import shu.ui.*;
import shu.image.GradientImage;
import shu.cms.image.*;
import java.awt.Dimension;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class GradientShowerFrame extends JFrame {

    private PixelSelectedListener pixelSelectedListener;

    public void setPixelSelectedListener(PixelSelectedListener listener) {
        this.pixelSelectedListener = listener;
    }

    private JPanel contentPane;
    private BorderLayout borderLayout1 = new BorderLayout();
    private JToolBar jToolBar = new JToolBar();
    private JTextField jTextField_Start = new JTextField();
    private JLabel jLabel1 = new JLabel();
    private JTextField jTextField_End = new JTextField();
    private JToggleButton jButton_256 = new JToggleButton("256");
    private JToggleButton jButton_128 = new JToggleButton();
    private JToggleButton jButton_64 = new JToggleButton();
    private JToggleButton jButton_32 = new JToggleButton();
    private JToggleButton jButton_16 = new JToggleButton();
    private JToggleButton jButton_8 = new JToggleButton();
    private JToggleButton jButton_4 = new JToggleButton();
    private JToggleButton jButton_R = new JToggleButton();
    private JToggleButton jButton_G = new JToggleButton();
    private JToggleButton jButton_B = new JToggleButton();
    private JToggleButton jButton_Inv = new JToggleButton();
    private JPanel jPanel3 = new JPanel();
    private JToggleButton jButton_Vertical = new JToggleButton();
    private JButton jButton_Exit = new JButton();
    private DitherCanvas ditherCanvas1 = new DitherCanvas();
    private ButtonGroup stepButtonGroup = new ButtonGroup();
    private ButtonGroup hsbButtonGroup = new ButtonGroup();
    private JButton jButton_Reset = new JButton();
    private JTextField jTextField_CodeR = new JTextField();
    private JTextField jTextField_CodeG = new JTextField();
    private JTextField jTextField_CodeB = new JTextField();
//    private BufferedImage bufferedImage = null;
    private BufferedImage HSBImage = null;
    private BufferedImage HSB2Image = null;
    private BufferedImage HSB3Image = null;
    private JToggleButton jButton_Grid = new JToggleButton();
    private JToggleButton jToggleButton_HSB1 = new JToggleButton();
    private JToggleButton jToggleButton_HSB2 = new JToggleButton();
    private JToggleButton jToggleButton_HSB3 = new JToggleButton();
    private JToggleButton[] hsbButtons = new JToggleButton[] {
                                         jToggleButton_HSB1, jToggleButton_HSB2,
                                         jToggleButton_HSB3};
    private JButton jButton_About = new JButton();
    private JToggleButton jToggleButton_Fill = new JToggleButton();


    public GradientShowerFrame() {
        this(false, true);
    }

    public GradientShowerFrame(boolean UIControl) {
        this(false, UIControl);
    }

    public void setUIEnable(boolean enable) {
        this.jToolBar.setEnabled(enable);
    }

    private boolean UIControl = true;

    private GradientShowerFrame(boolean getImageOnly, boolean UIControl) {
        this.UIControl = UIControl;
        if (!getImageOnly) {
            try {
                setDefaultCloseOperation(EXIT_ON_CLOSE);
                jbInit();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    }

    public static void main(String[] args) throws IOException {
        GradientShowerFrame frame = new GradientShowerFrame(true);
        frame.setVisible(true);

    }

    /**
     * Component initialization.
     *
     * @throws java.lang.Exception
     */
    private void jbInit() throws Exception {
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); //最大化
        this.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                this_keyTyped(e);
            }
        });
        this.setResizable(false); //不能改變大小
        this.setUndecorated(true); //不要邊框
        contentPane = (JPanel) getContentPane();
        contentPane.setLayout(borderLayout1);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize);
        setTitle("GradientShower");

        jTextField_Start.setMaximumSize(new Dimension(40, 22));
        jTextField_Start.setPreferredSize(new Dimension(30, 22));
        jTextField_Start.setToolTipText("起始階調");
        jTextField_Start.setText("0");
//        jTextField_Start.addKeyListener(new
//                                        GradientShowerFrame_jTextField_Start_keyAdapter(this));
        jTextField_Start.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                jTextField_Start_keyReleased(e);
            }
        });
        jLabel1.setText("-");
        jTextField_End.setMaximumSize(new Dimension(40, 22));
        jTextField_End.setPreferredSize(new Dimension(30, 22));
        jTextField_End.setToolTipText("結束階調");
        jTextField_End.setText("255");
        jTextField_End.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                jTextField_End_keyReleased(e);
            }
        });

        jButton_256.setMaximumSize(new Dimension(33, 22));
        jButton_256.setMinimumSize(new Dimension(33, 22));
        jButton_256.setPreferredSize(new Dimension(33, 22));
        jButton_256.setToolTipText("256灰階展開");
        jButton_256.setMnemonic(8);
        jButton_256.setSelected(true);
        jButton_256.setText("256");
        jButton_256.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton4_actionPerformed(e);
            }
        });

        jButton_128.setMaximumSize(new Dimension(33, 22));
        jButton_128.setMinimumSize(new Dimension(33, 22));
        jButton_128.setToolTipText("128灰階展開");
        jButton_128.setMnemonic(7);
        jButton_128.setText("128");
        jButton_128.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton5_actionPerformed(e);
            }
        });

        jButton_64.setMaximumSize(new Dimension(26, 22));
        jButton_64.setMinimumSize(new Dimension(26, 22));
        jButton_64.setPreferredSize(new Dimension(26, 22));
        jButton_64.setToolTipText("64灰階展開");
        jButton_64.setMnemonic(6);
        jButton_64.setText("64");
        jButton_64.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton6_actionPerformed(e);
            }
        });

        jButton_32.setMaximumSize(new Dimension(26, 22));
        jButton_32.setMinimumSize(new Dimension(26, 22));
        jButton_32.setPreferredSize(new Dimension(26, 22));
        jButton_32.setToolTipText("32灰階展開");
        jButton_32.setMnemonic(5);
        jButton_32.setText("32");
        jButton_32.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton7_actionPerformed(e);
            }
        });

        jButton_16.setMaximumSize(new Dimension(26, 22));
        jButton_16.setMinimumSize(new Dimension(26, 22));
        jButton_16.setPreferredSize(new Dimension(26, 22));
        jButton_16.setToolTipText("16灰階展開");
        jButton_16.setMnemonic(4);
        jButton_16.setText("16");
        jButton_16.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton8_actionPerformed(e);
            }
        });

        jButton_8.setMaximumSize(new Dimension(19, 22));
        jButton_8.setMinimumSize(new Dimension(19, 22));
        jButton_8.setPreferredSize(new Dimension(19, 22));
        jButton_8.setToolTipText("8灰階展開");
        jButton_8.setMnemonic(3);
        jButton_8.setText("8");
        jButton_8.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton9_actionPerformed(e);
            }
        });

        jButton_4.setMaximumSize(new Dimension(19, 22));
        jButton_4.setMinimumSize(new Dimension(19, 22));
        jButton_4.setPreferredSize(new Dimension(19, 22));
        jButton_4.setToolTipText("4灰階展開");
        jButton_4.setMnemonic(2);
        jButton_4.setText("4");
        jButton_4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton10_actionPerformed(e);
            }
        });

        jPanel3.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                jPanel3_mouseEntered(e);
            }
        });

        jButton_Reset.setMaximumSize(new Dimension(45, 22));
        jButton_Reset.setMinimumSize(new Dimension(40, 22));
        jButton_Reset.setPreferredSize(new Dimension(40, 30));
        jButton_Reset.setToolTipText("重置顯示設定");
        jButton_Reset.setText("reset");
        jButton_Reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton_Reset_actionPerformed(e);
            }
        });

        jTextField_CodeR.setMaximumSize(new Dimension(40, 22));
        jTextField_CodeR.setPreferredSize(new Dimension(30, 22));
        jTextField_CodeR.setToolTipText("滑鼠點選到的階調");
        jTextField_CodeR.setEditable(false);
        jTextField_CodeG.setMaximumSize(new Dimension(40, 22));
        jTextField_CodeG.setPreferredSize(new Dimension(30, 22));
        jTextField_CodeG.setToolTipText("滑鼠點選到的階調");
        jTextField_CodeG.setEditable(false);
        jTextField_CodeB.setMaximumSize(new Dimension(40, 22));
        jTextField_CodeB.setPreferredSize(new Dimension(30, 22));
        jTextField_CodeB.setToolTipText("滑鼠點選到的階調");
        jTextField_CodeB.setEditable(false);
        jButton_Grid.setMaximumSize(new Dimension(35, 22));
        jButton_Grid.setPreferredSize(new Dimension(35, 22));
        jButton_Grid.setToolTipText("方格顯示");
        jButton_Grid.setText("grid");
        jButton_Grid.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton_Grid_actionPerformed(e);
            }
        });

        jButton_R.setToolTipText("R頻道顯示");
        jButton_G.setToolTipText("G頻道顯示");
        jButton_B.setToolTipText("B頻道顯示");
        jButton_Inv.setToolTipText("階調反轉");
        jButton_Vertical.setToolTipText("階調垂直顯示");
        jButton_Exit.setToolTipText("結束程式");
        jToolBar.setBorder(null);
        jToolBar.setDoubleBuffered(true);
        jToggleButton_HSB1.setMaximumSize(new Dimension(44, 22));
        jToggleButton_HSB1.setMinimumSize(new Dimension(44, 22));
        jToggleButton_HSB1.setPreferredSize(new Dimension(44, 22));
        jToggleButton_HSB1.setToolTipText("HSB顯示1");
        jToggleButton_HSB1.setText("HSB1");
        jToggleButton_HSB1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jToggleButton_HSB1_actionPerformed(e);
            }
        });

        jToggleButton_HSB2.setMaximumSize(new Dimension(44, 22));
        jToggleButton_HSB2.setMinimumSize(new Dimension(44, 22));
        jToggleButton_HSB2.setPreferredSize(new Dimension(44, 22));
        jToggleButton_HSB2.setToolTipText("HSB顯示2");
        jToggleButton_HSB2.setText("HSB2");
        jToggleButton_HSB2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jToggleButton_HSB2_actionPerformed(e);
            }
        });

        jToggleButton_HSB3.setMaximumSize(new Dimension(44, 22));
        jToggleButton_HSB3.setMinimumSize(new Dimension(44, 22));
        jToggleButton_HSB3.setPreferredSize(new Dimension(44, 22));
        jToggleButton_HSB3.setText("HSB3");
        jToggleButton_HSB3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jToggleButton_HSB3_actionPerformed(e);
            }
        });

        jToggleButton_Fill.setMaximumSize(new Dimension(27, 22));
        jToggleButton_Fill.setMinimumSize(new Dimension(27, 22));
        jToggleButton_Fill.setPreferredSize(new Dimension(27, 22));
        jToggleButton_Fill.setToolTipText("填滿漸層");
        jToggleButton_Fill.setMnemonic('0');
        jToggleButton_Fill.setText("Fill");
        jToggleButton_Fill.setSelected(true);
        jToggleButton_Fill.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jToggleButton_Fill_actionPerformed(e);
            }
        });

        jToggleButton_HSB.setMaximumSize(new Dimension(44, 22));
        jToggleButton_HSB.setMinimumSize(new Dimension(44, 22));
        jToggleButton_HSB.setPreferredSize(new Dimension(44, 22));
        jToggleButton_HSB.setToolTipText("HSB顯示");
        jToggleButton_HSB.setText("HSB");
        jToggleButton_HSB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jToggleButton_HSB_actionPerformed(e);
            }
        });

        jButton_Mono.setMaximumSize(new Dimension(48, 22));
        jButton_Mono.setMinimumSize(new Dimension(48, 22));
        jButton_Mono.setPreferredSize(new Dimension(48, 22));
        jButton_Mono.setMnemonic('0');
        jButton_Mono.setText("mono");
        jButton_Mono.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton_Mono_actionPerformed(e);
            }
        });

        jButton_About.setMaximumSize(new Dimension(45, 22));
        jButton_About.setMinimumSize(new Dimension(45, 22));
        jButton_About.setPreferredSize(new Dimension(45, 22));
        jButton_About.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton_About_actionPerformed(e);
            }
        });
        jCheckBox_Link.setMaximumSize(new Dimension(55, 22));
        jCheckBox_Link.setMinimumSize(new Dimension(45, 22));
        jCheckBox_Link.setText("Link");
        jCheckBox_Link.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jCheckBox_Link_actionPerformed(e);
            }
        });
        stepButtonGroup.add(jButton_256);
        stepButtonGroup.add(jButton_128);
        stepButtonGroup.add(jButton_64);
        stepButtonGroup.add(jButton_32);
        stepButtonGroup.add(jButton_16);
        stepButtonGroup.add(jButton_8);
        stepButtonGroup.add(jButton_4);
        jButton_R.setMaximumSize(new Dimension(20, 22));
        jButton_R.setMinimumSize(new Dimension(20, 22));
        jButton_R.setPreferredSize(new Dimension(20, 22));
        jButton_R.setSelected(true);
        jButton_R.setText("R");
        jButton_R.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton_R_actionPerformed(e);
            }
        });

        jButton_G.setMaximumSize(new Dimension(20, 22));
        jButton_G.setMinimumSize(new Dimension(20, 22));
        jButton_G.setPreferredSize(new Dimension(20, 22));
        jButton_G.setSelected(true);
        jButton_G.setText("G");
        jButton_G.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton_G_actionPerformed(e);
            }
        });

        jButton_B.setMaximumSize(new Dimension(20, 22));
        jButton_B.setMinimumSize(new Dimension(20, 22));
        jButton_B.setPreferredSize(new Dimension(20, 22));
        jButton_B.setSelected(true);
        jButton_B.setText("B");
        jButton_B.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton_B_actionPerformed(e);
            }
        });

        contentPane.setPreferredSize(new Dimension(600, 400));
        jButton_Inv.setMaximumSize(new Dimension(28, 22));
        jButton_Inv.setMinimumSize(new Dimension(28, 22));
        jButton_Inv.setPreferredSize(new Dimension(28, 22));
        jButton_Inv.setText("inv");
        jButton_Inv.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton_Inv_actionPerformed(e);
            }
        });

        jPanel3.setBackground(Color.gray);
        jPanel3.setVisible(false);
        jButton_Vertical.setMaximumSize(new Dimension(58, 22));
        jButton_Vertical.setMinimumSize(new Dimension(58, 22));
        jButton_Vertical.setPreferredSize(new Dimension(58, 22));
        jButton_Vertical.setText("vertical");
        jButton_Vertical.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton_Vertical_actionPerformed(e);
            }
        });

        jButton_Exit.setMaximumSize(new Dimension(33, 22));
        jButton_Exit.setMinimumSize(new Dimension(33, 22));
        jButton_Exit.setPreferredSize(new Dimension(33, 22));
        jButton_Exit.setText("exit");
        jButton_Exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton13_actionPerformed(e);
            }
        });

        this.jButton_About.setText("about");

        ditherCanvas1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                ditherCanvas1_mouseClicked(e);
            }

            public void mousePressed(MouseEvent e) {
                ditherCanvas1_mousePressed(e);
            }
        });

        jToolBar.setBackground(Color.black);
        jToolBar.add(jCheckBox_Link);
        jToolBar.add(jButton_About);
        jToolBar.add(jTextField_Start);
        jToolBar.add(jLabel1);
        jToolBar.add(jTextField_End);
        jToolBar.add(jButton_Reset);
        jToolBar.add(jTextField_CodeR);
        jToolBar.add(jTextField_CodeG);
        jToolBar.add(jTextField_CodeB);
        jToolBar.add(jButton_256);
        jToolBar.add(jButton_128);
        jToolBar.add(jButton_64);
        jToolBar.add(jButton_32);
        jToolBar.add(jButton_16);
        jToolBar.add(jButton_8);
        jToolBar.add(jButton_4);
        jToolBar.add(jButton_R);
        jToolBar.add(jButton_G);
        jToolBar.add(jButton_B);
        jToolBar.add(jButton_Mono);
        jToolBar.add(jButton_Inv);
        jToolBar.add(jButton_Vertical);
//        jToolBar.add(jButton_Grid);
        jToolBar.add(jToggleButton_HSB);
        hsbButtonGroup.add(jToggleButton_HSB1);
        hsbButtonGroup.add(jToggleButton_HSB2);
        hsbButtonGroup.add(jToggleButton_HSB3);
        jToolBar.add(jToggleButton_Fill);
        jToolBar.add(this.jButton_About);
        jToolBar.add(jButton_Exit);

        contentPane.add(ditherCanvas1, java.awt.BorderLayout.CENTER);
        if (this.UIControl) {
            contentPane.add(jPanel3, java.awt.BorderLayout.SOUTH);
            contentPane.add(jToolBar, java.awt.BorderLayout.NORTH);
        } else {
            TinyDialog.Dialog d = TinyDialog.getDialogInstance(this, "x",
                    new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            d.setLocation(this.getWidth() - d.getWidth(), 0);
            d.setVisible(true);

        }
        this.setVisible(true);

//        bufferedImage = new BufferedImage(ditherCanvas1.getWidth(),
//                                          ditherCanvas1.getHeight(),
//                                          BufferedImage.TYPE_INT_RGB);
        ditherCanvas1.setBufferedImage(calculateImage());
        ditherCanvas1.setBackground(Color.black);
    }

//    private BufferedImage bufferedImage;

    void setBorderColor(int r, int g, int b) {
        Color bg = new Color(r, g, b);
        jPanel3.setBackground(bg);
        jToolBar.setBackground(bg);
    }

    /**
     * Calculates and returns the image.  Halts the calculation and returns
     * null if the Applet is stopped during the calculation.
     * @return Image
     */
    BufferedImage calculateImage() {
        return calculateImage(0, 255, true, true, true, false, false, false,
                              256, true);
    }

    private boolean[] hsbRGBSelected = new boolean[3];

    BufferedImage calculateHSBImage(boolean R, boolean G, boolean B) {
        Dimension size = ditherCanvas1.getSize();
        int height = size.height;
        int width = size.width;

        if (HSBImage == null || HSBImage.getWidth() != width
            || HSBImage.getHeight() != height) {
            HSBImage = new BufferedImage(width,
                                         height, BufferedImage.TYPE_INT_RGB);
        } else if (R == hsbRGBSelected[0] && G == hsbRGBSelected[1]
                   && B == hsbRGBSelected[2]) {
            return HSBImage;
        }
        hsbRGBSelected[0] = R;
        hsbRGBSelected[1] = G;
        hsbRGBSelected[2] = B;

        HSBImageUtil.fillCircleHSBImage(R, G, B, HSBImage);
        return HSBImage;
    }


    private static double[] PolarValues = new double[3];
    private static double t1, t2;
    private final static double PI180 = (180.0 / Math.PI);


    private boolean[] hsb2RGBSelected = new boolean[3];
    private boolean[] hsb3RGBSelected = new boolean[3];

    BufferedImage calculateHSB2Image(boolean saturationChange, boolean R,
                                     boolean G, boolean B) {
        Dimension size = ditherCanvas1.getSize();
        int height = size.height;
        int width = size.width;
        BufferedImage image = null;
        boolean[] hsbRGBSelected = saturationChange ? hsb2RGBSelected
                                   : hsb3RGBSelected;

        if (saturationChange) {
            if (HSB2Image == null) {
                HSB2Image = new BufferedImage(width,
                                              height,
                                              BufferedImage.TYPE_INT_RGB);
            } else if (R == hsbRGBSelected[0] && G == hsbRGBSelected[1]
                       && B == hsbRGBSelected[2]) {
                return HSB2Image;
            }
            image = HSB2Image;
        } else {
            if (HSB3Image == null) {
                HSB3Image = new BufferedImage(width,
                                              height,
                                              BufferedImage.TYPE_INT_RGB);
            } else if (R == hsbRGBSelected[0] && G == hsbRGBSelected[1]
                       && B == hsbRGBSelected[2]) {
                return HSB3Image;
            }
            image = HSB3Image;
        }

        hsbRGBSelected[0] = R;
        hsbRGBSelected[1] = G;
        hsbRGBSelected[2] = B;

        HSBImageUtil.fillRectHSBImage(R, G, B, image, saturationChange);

        return image;
    }

    BufferedImage calculateImage(int start, int end, boolean R, boolean G,
                                 boolean B,
                                 boolean inv, boolean vertical, boolean grid,
                                 int scale, boolean fill) {
//        return calculateImage3(ditherCanvas1.getSize(), start, end, R, G, B,
//                               inv, vertical, scale, fill);
        return GradientImage.getImage(ditherCanvas1.getSize(), start, end, R, G,
                                      B,
                                      inv, vertical, scale, fill, null);
    }

//    private final static int STUFF = 0;
//    private final static boolean CONDITION = true;
    private JToggleButton jToggleButton_HSB = new JToggleButton();

//    public final static BufferedImage getImage(Dimension size, int start,
//                                               int end, boolean R,
//                                               boolean G, boolean B,
//                                               boolean inv,
//                                               boolean vertical, int scale,
//                                               boolean fill,
//                                               BufferedImage bufferedImage) {
//        int width = size.width;
//        int height = size.height;
//        if (bufferedImage == null || bufferedImage.getWidth() != width
//            || bufferedImage.getHeight() != height) {
//            bufferedImage = new BufferedImage(width,
//                                              height,
//                                              BufferedImage.TYPE_INT_RGB);
//        }
//
//        //==========================================================================
//        // 參數初始化
//        //==========================================================================
//        //每加一次code,改變的量
//        int codeScale = 255 / (scale - 1);
//        //幾階,畫面被切成幾段
//        int level = ((end - start + 1) / codeScale) + STUFF;
//        if (level == 0) {
//            return getImage(size, 0, 255, true, true, true, false, false,
//                            256, true, bufferedImage);
//        }
//        //每一階的pixel數
//        int normalStep = (width / level);
//        int gripStep = (height / level);
//        //每一次code的增加量
//        int additive = inv ? -codeScale : codeScale;
//        //code數值的結束值
//        int limit = inv ? start : end;
//        //code的初始值
//        int codeStart = inv ? end : start;
//        //幾個pixel變換一次code
//        int step = vertical ? gripStep : normalStep;
//        boolean condition = (step == 1) ? false : CONDITION;
//        //==========================================================================
//
//        //==========================================================================
//        // fill參數初始化
//        //==========================================================================
//        int remainder = width - (normalStep * level);
//        int fillStartLevel = level - remainder + 1;
//        int fillStartCode = codeStart + fillStartLevel * additive;
//        //==========================================================================
//
//        short[][] pixels = new short[level][3];
//        short[][] coordinates = new short[level][2];
//        short[][] sizes = new short[level][2];
//        short code = (short) codeStart;
//        for (int x = 0; x < level; x++) {
//            pixels[x][0] = R ? code : 0;
//            pixels[x][1] = G ? code : 0;
//            pixels[x][2] = B ? code : 0;
//            code += additive;
//            coordinates[x][0] = (short) (vertical ? 0 : step * x);
//            coordinates[x][1] = (short) (vertical ? step * x : 0);
//            sizes[x][0] = (short) (vertical ? width : step);
//            sizes[x][1] = (short) (vertical ? step : height);
//        }
//
//        Graphics g = bufferedImage.getGraphics();
//        g.setColor(Color.black);
//        g.fillRect(0, 0, width, height);
//
//        for (int x = 0; x < level; x++) {
//            short[] pixel = pixels[x];
//            Color color = new Color(pixel[0], pixel[1], pixel[2]);
//            g.setColor(color);
//            short[] coordinate = coordinates[x];
//            short[] xysize = sizes[x];
//            g.fillRect(coordinate[0], coordinate[1], xysize[0], xysize[1]);
//        }
//
//        return bufferedImage;
//    }


//    BufferedImage calculateImage3(Dimension size, int start, int end, boolean R,
//                                  boolean G, boolean B, boolean inv,
//                                  boolean vertical, int scale, boolean fill) {
//        int width = size.width;
//        int height = size.height;
//        if (bufferedImage == null || bufferedImage.getWidth() != width
//            || bufferedImage.getHeight() != height) {
//            bufferedImage = new BufferedImage(width,
//                                              height,
//                                              BufferedImage.TYPE_INT_RGB);
//        }
//
//        //==========================================================================
//        // 參數初始化
//        //==========================================================================
//        //每加一次code,改變的量
//        int codeScale = 255 / (scale - 1);
//        //幾階,畫面被切成幾段
//        int level = ((end - start + 1) / codeScale) + STUFF;
//        if (level == 0) {
//            BufferedImage img = this.calculateImage();
//            return img;
//        }
//        //每一階的pixel數
//        int normalStep = (width / level);
//        int gripStep = (height / level);
//        //每一次code的增加量
//        int additive = inv ? -codeScale : codeScale;
//        //code數值的結束值
//        int limit = inv ? start : end;
//        //code的初始值
//        int codeStart = inv ? end : start;
//        //幾個pixel變換一次code
//        int step = vertical ? gripStep : normalStep;
//        boolean condition = (step == 1) ? false : CONDITION;
//        //==========================================================================
//
//        //==========================================================================
//        // fill參數初始化
//        //==========================================================================
//        int remainder = width - (normalStep * level);
//        int fillStartLevel = level - remainder + 1;
//        int fillStartCode = codeStart + fillStartLevel * additive;
//        //==========================================================================
//
//        short[][] pixels = new short[level][3];
//        short[][] coordinates = new short[level][2];
//        short[][] sizes = new short[level][2];
//        short code = (short) codeStart;
//        for (int x = 0; x < level; x++) {
//            pixels[x][0] = R ? code : 0;
//            pixels[x][1] = G ? code : 0;
//            pixels[x][2] = B ? code : 0;
//            code += additive;
//            coordinates[x][0] = (short) (vertical ? 0 : step * x);
//            coordinates[x][1] = (short) (vertical ? step * x : 0);
//            sizes[x][0] = (short) (vertical ? width : step);
//            sizes[x][1] = (short) (vertical ? step : height);
//        }
//
//        Graphics g = bufferedImage.getGraphics();
//        g.setColor(Color.black);
//        g.fillRect(0, 0, width, height);
//
//        for (int x = 0; x < level; x++) {
//            short[] pixel = pixels[x];
//            Color color = new Color(pixel[0], pixel[1], pixel[2]);
//            g.setColor(color);
//            short[] coordinate = coordinates[x];
//            short[] xysize = sizes[x];
//            g.fillRect(coordinate[0], coordinate[1], xysize[0], xysize[1]);
//        }
//
//        return bufferedImage;
//    }


    public void jButton13_actionPerformed(ActionEvent e) {
        this.dispose();
    }

    public void setToolBarVisible(boolean visible) {
        this.jToolBar.setVisible(visible);
    }

    public void ditherCanvas1_mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            this.jToolBar.setVisible(!jToolBar.isVisible());
        }
    }

    public void jButton_R_actionPerformed(ActionEvent e) {
        updateImage();
    }

    public void jButton_G_actionPerformed(ActionEvent e) {
        updateImage();
    }

    public void setChannel(String name) {
        if (name.equals("R")) {
            setChannel(true, false, false);
        } else if (name.equals("G")) {
            setChannel(false, true, false);
        } else if (name.equals("B")) {
            setChannel(false, false, true);
        } else if (name.equals("Y")) {
            setChannel(true, true, false);
        } else if (name.equals("M")) {
            setChannel(true, false, true);
        } else if (name.equals("C")) {
            setChannel(false, true, true);
        } else if (name.equals("W")) {
            setChannel(true, true, true);
        }
    }


    public void setChannel(boolean R, boolean G, boolean B) {
        this.jButton_R.setSelected(R);
        this.jButton_G.setSelected(G);
        this.jButton_B.setSelected(B);
        this.updateImage();
    }

    public void jButton_B_actionPerformed(ActionEvent e) {
        updateImage();
    }

    protected void setChannelButtons(boolean enable) {
        this.jButton_R.setEnabled(enable);
        this.jButton_G.setEnabled(enable);
        this.jButton_B.setEnabled(enable);
    }

    protected void setStepButtons(boolean enable) {
        this.jButton_256.setEnabled(enable);
        this.jButton_128.setEnabled(enable);
        this.jButton_64.setEnabled(enable);
        this.jButton_32.setEnabled(enable);
        this.jButton_16.setEnabled(enable);
        this.jButton_8.setEnabled(enable);
        this.jButton_4.setEnabled(enable);

    }

    protected void setDirectionButtons(boolean enable) {
        this.jButton_Grid.setEnabled(enable);
        this.jButton_Inv.setEnabled(enable);
        this.jButton_Vertical.setEnabled(enable);
        this.jToggleButton_Fill.setEnabled(enable);
    }

//  protected boolean stepButtonEnable = true;
    public void setupImage(boolean R, boolean G, boolean B) {
        this.jButton_R.setSelected(R);
        this.jButton_G.setSelected(G);
        this.jButton_B.setSelected(B);
        this.updateImage();
    }

    public void setupImage(boolean R, boolean G, boolean B, boolean inverse) {
        this.jButton_Inv.setSelected(inverse);
        setupImage(R, G, B);
    }

    void updateImage() {
        BufferedImage img = null;
        if (this.jToggleButton_HSB1.isSelected()) {
            img = calculateHSBImage(this.jButton_R.isSelected(),
                                    this.jButton_G.isSelected(),
                                    this.jButton_B.isSelected());

        } else if (this.jToggleButton_HSB2.isSelected()) {
            img = calculateHSB2Image(false, this.jButton_R.isSelected(),
                                     this.jButton_G.isSelected(),
                                     this.jButton_B.isSelected());
        } else if (this.jToggleButton_HSB3.isSelected()) {
            img = calculateHSB2Image(true, this.jButton_R.isSelected(),
                                     this.jButton_G.isSelected(),
                                     this.jButton_B.isSelected());
        } else {
            String startStr = jTextField_Start.getText();
            String endStr = jTextField_End.getText();
            int start = 0;
            int end = 255;
            try {
                if (startStr.length() != 0) {
                    start = Integer.parseInt(startStr);
                    start = start < 0 ? 0 : start;
                    start = start > 255 ? 255 : start;
                }
                if (endStr.length() != 0) {
                    end = Integer.parseInt(endStr);
                    end = end < 0 ? 0 : end;
                    end = end > 255 ? 255 : end;
                }
            } catch (NumberFormatException ex) {
                return;
            }
            if (start > end) {
                return;
            }
            int m = this.stepButtonGroup.getSelection().getMnemonic();
            img = calculateImage(start, end, this.jButton_R.isSelected(),
                                 this.jButton_G.isSelected(),
                                 this.jButton_B.isSelected(),
                                 this.jButton_Inv.isSelected(),
                                 this.jButton_Vertical.isSelected(),
                                 this.jButton_Grid.isSelected(),
                                 (int) Math.pow(2, m),
                                 this.jToggleButton_Fill.isSelected());
        }
        ditherCanvas1.setBufferedImage(img);
    }

    public void jTextField_Start_keyReleased(KeyEvent e) {
        boolean link = jCheckBox_Link.isSelected();
        if (link) {
            String start = jTextField_Start.getText();
            jTextField_End.setText(start);
        }
        this.updateImage();
    }

    public void jTextField_End_keyReleased(KeyEvent e) {
        this.updateImage();
    }

    public void jButton_Inv_actionPerformed(ActionEvent e) {
        this.updateImage();
    }

    public void setInverse(boolean inverse) {
        this.jButton_Inv.setSelected(inverse);
        this.updateImage();
    }

    public void jButton_Vertical_actionPerformed(ActionEvent e) {
        this.updateImage();
    }

    public void jButton4_actionPerformed(ActionEvent e) {
        this.updateImage();
    }

    public void jButton5_actionPerformed(ActionEvent e) {
        this.updateImage();
    }

    public void jButton6_actionPerformed(ActionEvent e) {
        this.updateImage();
    }

    public void jButton7_actionPerformed(ActionEvent e) {
        this.updateImage();
    }

    public void jButton8_actionPerformed(ActionEvent e) {
        this.updateImage();
    }

    public void jButton9_actionPerformed(ActionEvent e) {
        this.updateImage();
    }

    public void jButton10_actionPerformed(ActionEvent e) {
        this.updateImage();
    }

    public void jPanel3_mouseEntered(MouseEvent e) {
        this.jPanel3.setVisible(false);
    }

    public void jButton_Reset_actionPerformed(ActionEvent e) {
        this.jTextField_Start.setText("0");
        this.jTextField_End.setText("255");
        this.jButton_256.setSelected(true);
        this.jButton_R.setSelected(true);
        this.jButton_G.setSelected(true);
        this.jButton_B.setSelected(true);
        this.jButton_Vertical.setSelected(false);
        this.jButton_Grid.setSelected(false);
        this.jButton_Inv.setSelected(false);
        this.jButton_Mono.setText("mono");
//    hsbButtonGroup.clearSelection();
//    this.jToggleButton_HSB.setSelected(false);
//    this.jToggleButton_HSB2.setSelected(false);
//    this.stepButtonEnable=false;
//    switchStepButton();
        resetHSBButtons();
        this.setStepButtons(true);
        setDirectionButtons(true);
        setChannelButtons(true);
        this.updateImage();
    }

    public void jButton_Grid_actionPerformed(ActionEvent e) {
        this.updateImage();
    }

    protected void resetHSBButtons() {
        this.jToggleButton_HSB.setEnabled(true);
        this.jToggleButton_HSB.setSelected(false);
        this.jToggleButton_HSB.setText("HSB");
        this.jToggleButton_HSB1.setSelected(false);
        this.jToggleButton_HSB2.setSelected(false);
        this.jToggleButton_HSB3.setSelected(false);
        hsbButtonGroup.clearSelection();
    }

    public void jToggleButton_HSB1_actionPerformed(ActionEvent e) {
        if (this.jToggleButton_HSB1.isSelected()) {
            setStepButtons(false);
            setDirectionButtons(false);
            this.updateImage();
        } else {
            this.jButton_Reset.doClick();
        }

    }

    public void jToggleButton_HSB2_actionPerformed(ActionEvent e) {
        if (this.jToggleButton_HSB2.isSelected()) {
            setStepButtons(false);
            setDirectionButtons(false);
            this.updateImage();
        } else {
            this.jButton_Reset.doClick();
        }
    }

    public void jToggleButton_HSB3_actionPerformed(ActionEvent e) {
        if (this.jToggleButton_HSB3.isSelected()) {
            setStepButtons(false);
            setDirectionButtons(false);
            this.updateImage();
        } else {
            this.jButton_Reset.doClick();
        }
    }

    public void jToggleButton_Fill_actionPerformed(ActionEvent e) {
        this.updateImage();
    }

    public void ditherCanvas1_mousePressed(MouseEvent e) {
        if (e.getClickCount() == 1 && !this.jToggleButton_HSB1.isSelected()) {
            BufferedImage img = ditherCanvas1.getBufferedImage();
            if (e.getX() > img.getWidth() || e.getY() > img.getHeight()) {
                return;
            }
            int[] pixel = img.getRaster().getPixel(e.getX(), e.getY(),
                    new int[3]);
            this.jTextField_CodeR.setText(Integer.toString(pixel[0]));
            this.jTextField_CodeG.setText(Integer.toString(pixel[1]));
            this.jTextField_CodeB.setText(Integer.toString(pixel[2]));
        }
    }

    private int hsbToogleIndex = 0;
    protected JButton jButton_Mono = new JButton();

    public void jToggleButton_HSB_actionPerformed(ActionEvent e) {

        JToggleButton button = hsbButtons[hsbToogleIndex++ % hsbButtons.length];
        button.setSelected(true);
        this.jToggleButton_HSB.setSelected(true);
        this.jToggleButton_HSB.setText(button.getText());

        setStepButtons(false);
        setDirectionButtons(false);
        this.updateImage();
    }

    private int monoColorIndex = 0;
    private String[] channelNames = new String[] {
                                    "R", "G", "B", "Y", "M", "C", "W", "K"};
    JCheckBox jCheckBox_Link = new JCheckBox();

    public void jButton_Mono_actionPerformed(ActionEvent e) {
        String name = channelNames[monoColorIndex++ % channelNames.length];
        this.jButton_Mono.setText(name);
        this.jButton_256.setSelected(true);
        if (name.equals("K")) {
            setChannel(false, false, false);
            this.jTextField_Start.setText("0");
            this.jTextField_End.setText("0");
        } else {
//      RGBBase.Channel ch = RGBBase.Channel.valueOf(name);
            setChannel(name);
            this.jTextField_Start.setText("255");
            this.jTextField_End.setText("255");
        }

        //==========================================================================
        // UI設定
        //==========================================================================
        setStepButtons(false);
        setDirectionButtons(false);
        setChannelButtons(false);
//    this.jToggleButton_HSB.setEnabled(false);
        //==========================================================================
        this.updateImage();
    }

    public void jButton_About_actionPerformed(ActionEvent e) {
        AboutBox dlg = new AboutBox(this);
        Dimension dlgSize = dlg.getPreferredSize();
        Dimension frmSize = getSize();
        Point loc = getLocation();
        dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
                        (frmSize.height - dlgSize.height) / 2 + loc.y);
        dlg.setModal(true);
        dlg.pack();
        dlg.setVisible(true);
    }

    public void this_keyTyped(KeyEvent e) {
        char key = e.getKeyChar();
        switch (key) {
        case 'R':
        case 'r':
            jButton_R.setSelected(!jButton_R.isSelected());
            break;
        case 'G':
        case 'g':
            jButton_G.setSelected(!jButton_G.isSelected());
            break;
        case 'B':
        case 'b':
            jButton_B.setSelected(!jButton_B.isSelected());
            break;
        }
    }

    public void jCheckBox_Link_actionPerformed(ActionEvent e) {
        boolean link = jCheckBox_Link.isSelected();
        jTextField_End.setEditable(!link);
        if (link) {
            String start = jTextField_Start.getText();
            jTextField_End.setText(start);
        }
        else {
             jTextField_End.setText("255");
        }
        this.updateImage();
    }
}


class AboutBox extends JDialog implements ActionListener {

    JPanel panel1 = new JPanel();
    JPanel panel2 = new JPanel();
    JPanel insetsPanel1 = new JPanel();
    JPanel insetsPanel2 = new JPanel();
    JPanel insetsPanel3 = new JPanel();
    JButton button1 = new JButton();
    JLabel imageLabel = new JLabel();
    JLabel label1 = new JLabel();
    JLabel label2 = new JLabel();
    JLabel label3 = new JLabel();
    JLabel label4 = new JLabel();
    ImageIcon image1 = new ImageIcon();
    BorderLayout borderLayout1 = new BorderLayout();
    BorderLayout borderLayout2 = new BorderLayout();
    FlowLayout flowLayout1 = new FlowLayout();
    GridLayout gridLayout1 = new GridLayout();
    String product = "Gradient Shower";
    String version = "1.0 (20100903)";
    String copyright = "skyforce (c) 2010";
    String comments = "a Colour Management System by Java";

    public AboutBox(Frame parent) {
        super(parent);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public AboutBox() {
        this(null);
    }

    /**
     * Component initialization.
     *
     * @throws java.lang.Exception
     */
    private void jbInit() throws Exception {
        image1 = new ImageIcon(GradientShowerFrame.class.getResource(
                "about.png"));
        imageLabel.setIcon(image1);
        setTitle("About");
        panel1.setLayout(borderLayout1);
        panel2.setLayout(borderLayout2);
        insetsPanel1.setLayout(flowLayout1);
        insetsPanel2.setLayout(flowLayout1);
        insetsPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridLayout1.setRows(4);
        gridLayout1.setColumns(1);
        label1.setText(product);
        label2.setText(version);
        label3.setText(copyright);
        label4.setText(comments);
        insetsPanel3.setLayout(gridLayout1);
        insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 10));
        button1.setText("OK");
        button1.addActionListener(this);
        insetsPanel2.add(imageLabel, null);
        panel2.add(insetsPanel2, BorderLayout.WEST);
        getContentPane().add(panel1, null);
        insetsPanel3.add(label1, null);
        insetsPanel3.add(label2, null);
        insetsPanel3.add(label3, null);
        insetsPanel3.add(label4, null);
        panel2.add(insetsPanel3, BorderLayout.CENTER);
        insetsPanel1.add(button1, null);
        panel1.add(insetsPanel1, BorderLayout.SOUTH);
        panel1.add(panel2, BorderLayout.NORTH);
        setResizable(true);
    }

    /**
     * Close the dialog on a button event.
     *
     * @param actionEvent ActionEvent
     */
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == button1) {
            dispose();
        }
    }
}
