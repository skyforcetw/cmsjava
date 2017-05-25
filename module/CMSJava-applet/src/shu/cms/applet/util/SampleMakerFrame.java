package shu.cms.applet.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 用來產生測色色塊
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class SampleMakerFrame
    extends JFrame {
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextPane jTextPane1 = new JTextPane();
  JComboBox jComboBox_step = new JComboBox();
  JComboBox jComboBox_channel = new JComboBox();
  JButton jButton_ok = new JButton();
  JLabel jLabel_channel = new JLabel();
  JLabel jLabel_step = new JLabel();
  JButton jButton_clear = new JButton();

  public SampleMakerFrame() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    getContentPane().setLayout(borderLayout1);
    this.setSize(800, 600);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setTitle("Sample Maker");
    jLabel_channel.setText("channel:");
    jLabel_step.setText("step:");
    jButton_ok.addActionListener(new SampleMakerFrame_jButton_ok_actionAdapter(this));
    jButton_clear.setText("clear");
    jButton_clear.addActionListener(new
                                    SampleMakerFrame_jButton_clear_actionAdapter(this));
    jLabel_start.setText("startIndex:");
    jTextField_startIndex.setPreferredSize(new Dimension(40, 23));
    jTextField_startIndex.setText("1");
    jCheckBox_nonZero.setText("non-Zero");
    this.getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);
    jScrollPane1.getViewport().add(jTextPane1);
    jTextPane1.setText("");
    jButton_ok.setText("ok");
    jPanel1.add(jLabel_start);
    jPanel1.add(jTextField_startIndex);
    jPanel1.add(jLabel_channel);
    jPanel1.add(jComboBox_channel);
    jPanel1.add(jLabel_step);
    jPanel1.add(jComboBox_step);
    jPanel1.add(jButton_ok);
    jPanel1.add(jButton_clear);
    this.getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);
    jPanel1.add(jCheckBox_nonZero);
    jComboBox_channel.addItem("RGB");
    jComboBox_channel.addItem("Grey");
    jComboBox_channel.addItem("R");
    jComboBox_channel.addItem("G");
    jComboBox_channel.addItem("B");
    jComboBox_channel.addItem("C");
    jComboBox_channel.addItem("M");
    jComboBox_channel.addItem("Y");
    jComboBox_channel.addItem("R+G");
    jComboBox_channel.addItem("G+B");
    jComboBox_channel.addItem("R+B");

    jComboBox_step.addItem("1");
    jComboBox_step.addItem("2");
    jComboBox_step.addItem("4");
    jComboBox_step.addItem("5");
    jComboBox_step.addItem("6");
    jComboBox_step.addItem("7");
    jComboBox_step.addItem("8");
    jComboBox_step.addItem("12");
    jComboBox_step.addItem("13");
    jComboBox_step.addItem("16");
    jComboBox_step.addItem("17");
    jComboBox_step.addItem("24");
    jComboBox_step.addItem("32");
    jComboBox_step.addItem("37");
    jComboBox_step.addItem("64");

    this.jTextField_startIndex.setText(Integer.toString(index));
  }

  protected String produceGreySampler(int step) {
    StringBuilder sbuf = new StringBuilder();
    int max = 256;
    boolean stop = false;

    for (int g = 0; !stop; g += step) {
      if (g >= (max - 1)) {
        g = max - 1;
        stop = true;
      }
      sbuf.append("A" + index++ +'\t');
      sbuf.append(g + "\t" + g + "\t" + g);
      sbuf.append('\n');
    }
    return sbuf.toString();
  }

  protected String produceCMYSampler(int channel, int step) {
    int cMax = 256;
    int mMax = 256;
    int yMax = 256;

    switch (channel) {
      case 5:
        mMax = 1;
        yMax = 1;
        break;
      case 6:
        cMax = 1;
        yMax = 1;
        break;
      case 7:
        cMax = 1;
        mMax = 1;
        break;
    }

    StringBuilder sbuf = new StringBuilder();

    if (255 % step == 0) {
      for (int c = 0; c < cMax; c += step) {
        for (int m = 0; m < mMax; m += step) {
          for (int y = 0; y < yMax; y += step) {
            sbuf.append("A" + index++ +'\t');
            int r = (c + m + y) - m;
            int g = (c + m + y) - c;
            int b = (c + m + y) - y;
            sbuf.append(r + "\t" + g + "\t" + b);
            sbuf.append('\n');
          }
        }
      }
    }
    else {
      boolean cStop = false;
      for (int c = 0; !cStop; c += step) {
        if (c >= cMax) {
          if (cMax == 1) {
            break;
          }
          c = cMax - 1;
          cStop = true;
        }

        boolean mStop = false;
        for (int m = 0; !mStop; m += step) {
          if (m >= mMax) {
            if (mMax == 1) {
              break;
            }
            m = mMax - 1;
            mStop = true;
          }

          boolean yStop = false;
          for (int y = 0; !yStop; y += step) {
            if (y >= yMax) {
              if (yMax == 1) {
                break;
              }
              y = yMax - 1;
              yStop = true;
            }

            sbuf.append("A" + index++ +'\t');
            int r = (c + m + y) - m;
            int g = (c + m + y) - c;
            int b = (c + m + y) - y;
            sbuf.append(r + "\t" + g + "\t" + b);
            sbuf.append('\n');
          }
        }
      }

    }

    return sbuf.toString();
  }

  protected String produceColorSampler(int channel, int step, boolean nonZero) {
    int rMax = 256;
    int gMax = 256;
    int bMax = 256;

    switch (channel) {
      case 2:
        gMax = 1;
        bMax = 1;
        break;
      case 3:
        rMax = 1;
        bMax = 1;
        break;
      case 4:
        rMax = 1;
        gMax = 1;
        break;
      case 8:
        bMax = 1;
        break;
      case 9:
        rMax = 1;
        break;
      case 10:
        gMax = 1;
        break;
    }

    StringBuilder sbuf = new StringBuilder();

    if (255 % step == 0) {
      for (int r = 0; r < rMax; r += step) {
        for (int g = 0; g < gMax; g += step) {
          for (int b = 0; b < bMax; b += step) {
            if (nonZero) {
              int zeroCount = 0;
              zeroCount += (r == 0) ? 1 : 0;
              zeroCount += (g == 0) ? 1 : 0;
              zeroCount += (b == 0) ? 1 : 0;
              if (zeroCount >= 2) {
                continue;
              }
            }
            sbuf.append("A" + index++ +'\t');
            sbuf.append(r + "\t" + g + "\t" + b);
            sbuf.append('\n');
          }
        }
      }
    }
    else {
      boolean rStop = false;
      for (int r = 0; !rStop; r += step) {
        if (r >= rMax) {
          if (rMax == 1) {
            break;
          }
          r = rMax - 1;
          rStop = true;
        }

        boolean gStop = false;
        for (int g = 0; !gStop; g += step) {
          if (g >= gMax) {
            if (gMax == 1) {
              break;
            }
            g = gMax - 1;
            gStop = true;
          }

          boolean bStop = false;
          for (int b = 0; !bStop; b += step) {
            if (b >= bMax) {
              if (bMax == 1) {
                break;
              }
              b = bMax - 1;
              bStop = true;
            }

            if (nonZero) {
              int zeroCount = 0;
              zeroCount += (r == 0) ? 1 : 0;
              zeroCount += (g == 0) ? 1 : 0;
              zeroCount += (b == 0) ? 1 : 0;
              if (zeroCount >= 2) {
                continue;
              }
            }
            sbuf.append("A" + index++ +'\t');
            sbuf.append(r + "\t" + g + "\t" + b);
            sbuf.append('\n');
          }
        }
      }

    }

    return sbuf.toString();
  }

  public static void main(String[] args) {
    SampleMakerFrame samplemakerframe = new SampleMakerFrame();
    samplemakerframe.setVisible(true);
  }

  protected int index = 1;
  JLabel jLabel_start = new JLabel();
  JTextField jTextField_startIndex = new JTextField();
  protected JCheckBox jCheckBox_nonZero = new JCheckBox();

  public void jButton_ok_actionPerformed(ActionEvent e) {
    this.jButton_ok.setEnabled(false);
    index = Integer.parseInt(this.jTextField_startIndex.getText());
    int channel = jComboBox_channel.getSelectedIndex();
    int step = Integer.parseInt( (String) jComboBox_step.getSelectedItem());

    String str = null;
    if (channel == 1) {
      str = produceGreySampler(step);
    }
    else if (channel == 5 || channel == 6 || channel == 7) {
      str = produceCMYSampler(channel, step);
    }
    else {
      str = produceColorSampler(channel, step,
                                this.jCheckBox_nonZero.isSelected());
    }
    StringBuilder builder = new StringBuilder(jTextPane1.getText());
    builder.append(str);
    jTextPane1.setText(builder.toString());
    jButton_ok.setEnabled(true);
    jTextField_startIndex.setText(Integer.toString(index));
  }

  public void jButton_clear_actionPerformed(ActionEvent e) {
    index = 1;
    jTextPane1.setText("");
    jTextField_startIndex.setText(Integer.toString(index));
  }
}

class SampleMakerFrame_jButton_clear_actionAdapter
    implements ActionListener {
  private SampleMakerFrame adaptee;
  SampleMakerFrame_jButton_clear_actionAdapter(SampleMakerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_clear_actionPerformed(e);
  }
}

class SampleMakerFrame_jButton_ok_actionAdapter
    implements ActionListener {
  private SampleMakerFrame adaptee;
  SampleMakerFrame_jButton_ok_actionAdapter(SampleMakerFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton_ok_actionPerformed(e);
  }
}
