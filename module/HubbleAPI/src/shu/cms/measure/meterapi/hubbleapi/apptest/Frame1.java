package shu.cms.measure.meterapi.hubbleapi.apptest;

import java.awt.*;
import javax.swing.*;
import java.awt.BorderLayout;
import org.xvolks.jnative.util.Callback;
import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.JNative;
import shu.cms.measure.meterapi.hubbleapi.HubbleAPI;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Frame1
    extends JFrame implements Callback {
  protected JPanel contentPane;
  protected BorderLayout borderLayout1 = new BorderLayout();
  protected JCheckBox jCheckBox1 = new JCheckBox();
  public Frame1() {

    HubbleAPI hubble = new HubbleAPI();
    int n = hubble.getNumberOfDevices();
    System.out.println(n);
    hubble.setDevice(0);
    hubble.registerButtonCallback(this);

    try {
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
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
    setTitle("Frame Title");
    jCheckBox1.setText("jCheckBox1");
    contentPane.add(jCheckBox1, java.awt.BorderLayout.NORTH);
  }

  public int callback(long[] values) {

    return -1;
  }

  /**
   * This method should call JNative.createCallback() AND MUST allow multiple calls
   * <p>
   * Something like :
   * <pre>
   * abstract class MyCallback implements Callback {
   * 		private int myAddress = -1;
   * 		public int getCallbackAddress() throws NativeException {
   * 			if(myAddress == -1) {
   *				myAddress = JNative.createCallback(numParam, this);
   *			}
   *			return myAddress;
   * 		}
   * }
   * </pre>
   * </p>
   * @return the address of the callback function
   */
  public int getCallbackAddress() throws NativeException {
    return JNative.createCallback(2, this);
  }

}
