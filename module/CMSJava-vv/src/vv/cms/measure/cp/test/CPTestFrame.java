package vv.cms.measure.cp.test;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;
import shu.cms.util.*;
import shu.ui.*;
import vv.cms.lcd.calibrate.shm.*;

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
public class CPTestFrame
    extends JFrame {
  protected JPanel contentPane;
  protected BorderLayout borderLayout1 = new BorderLayout();
  protected ButtonGroup buttonGroup1 = new ButtonGroup();
  protected JRadioButton jRadioButton1 = new JRadioButton();
  protected JRadioButton jRadioButton2 = new JRadioButton();
  protected JPanel jPanel1 = new JPanel();
  protected JButton jButton1 = new JButton();
  protected ShareMemoryConnector command;
  protected JScrollPane jScrollPane1 = new JScrollPane();
  protected JTextArea jTextArea1 = new JTextArea();

  public CPTestFrame() {
    try {
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
    command = ShareMemoryConnector.getInstance();
  }

  /**
   * Component initialization.
   *
   * @throws java.lang.Exception
   */
  private void jbInit() throws Exception {
    contentPane = (JPanel) getContentPane();
    contentPane.setLayout(borderLayout1);
    setSize(new Dimension(400, 300));
    setTitle("CP Test");
    jRadioButton1.setMnemonic('L');
    jRadioButton1.setText("load");
    jRadioButton2.setMnemonic('M');
    jRadioButton2.setText("measure");
    jButton1.setText("excute");
    jButton1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jTextArea1.setToolTipText("");
    contentPane.add(jPanel1, java.awt.BorderLayout.NORTH);
    jPanel1.add(jButton1);
    jPanel1.add(jRadioButton1);
    jPanel1.add(jRadioButton2);
    contentPane.add(jScrollPane1, java.awt.BorderLayout.CENTER);
    buttonGroup1.add(jRadioButton1);
    buttonGroup1.add(jRadioButton2);
    jScrollPane1.getViewport().add(jTextArea1);
  }

  public static void main(String[] args) {
    GUIUtils.runAsApplication(new CPTestFrame(), false);
  }

  public void jButton1_actionPerformed(ActionEvent e) {
    RGB[] rgbArray = RGBArray.getOriginalRGBArray();
    switch (buttonGroup1.getSelection().getMnemonic()) {
      case 'M': {
        CIEXYZ[] result = command.measure(rgbArray);
        StringBuilder buf = new StringBuilder();
        for (CIEXYZ XYZ : result) {
          buf.append(XYZ.toString() + "\n");
        }
        jTextArea1.setText(jTextArea1.getText() + "\n" + buf.toString());
      }
      break;
      case 'L': {
        boolean result = command.loadCode(rgbArray, RGB.MaxValue.Int10Bit);
        String original = jTextArea1.getText();
        String output = (original.length() == 0) ? (Boolean.toString(result)) :
            original + "\n" + result;
        jTextArea1.setText(output);
      }
      break;
      default:
    }
  }
}
