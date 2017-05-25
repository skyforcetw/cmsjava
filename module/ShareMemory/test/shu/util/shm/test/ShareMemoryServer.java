package shu.util.shm.test;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import shu.util.shm.*;

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
public class ShareMemoryServer
    extends JFrame {
  private JButton btnBroadCast;
  private JTextArea textArea;
  private ShareMemory shm = new ShareMemory();

  //Construct the frame
  public ShareMemoryServer() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      init();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  //Component initialization
  private void init() throws Exception {
    JPanel contentPane = (JPanel) getContentPane();
    contentPane.setLayout(new BorderLayout());

    textArea = new JTextArea();
    contentPane.add(textArea, BorderLayout.CENTER);

    btnBroadCast = new JButton("Write and BroadCast");
    btnBroadCast.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {

        String text = textArea.getText();
        byte[] byteArray = text.getBytes();
        shm.writeToMem(byteArray);
      }
    });
    JPanel panelBtn = new JPanel();
    panelBtn.add(btnBroadCast);
    contentPane.add(panelBtn, BorderLayout.SOUTH);

    setSize(new Dimension(400, 300));
    setTitle("Memory Mapped File - Java Server");
  }

  private void destroy() {
    shm.destroy();
  }

  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      destroy();
      System.exit(0);
    }
  }

  public static void main(String[] args) {
    ShareMemoryServer frame = new ShareMemoryServer();
    frame.validate();
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    frame.setLocation( (screenSize.width - frameSize.width) / 2,
                      (screenSize.height - frameSize.height) / 2);
    frame.setVisible(true);
  }

}
