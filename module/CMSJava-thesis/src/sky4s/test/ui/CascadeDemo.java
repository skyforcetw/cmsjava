package sky4s.test.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CascadeDemo
    extends JFrame implements ActionListener {

  public static final int INI_WIDTH = 200;
  public static final int INI_HEIGHT = 200;

  private ImageIcon m_earth;
  private int m_count;
  private int m_tencount;
  private JButton m_newFrame;
  private JButton m_cascadeFrames;
  private JDesktopPane m_desktop;
  private JComboBox m_UIBox;
  private UIManager.LookAndFeelInfo[] m_infos;

  public CascadeDemo() {
    super("Cascade Demo");
    setSize(570, 400);

    m_earth = new ImageIcon(CascadeDemo.class.getResource("earth.jpg"));
    m_count = m_tencount = 0;

    m_desktop = new JDesktopPane();
    m_desktop.putClientProperty("JDesktopPane.dragMode", "outline");

    m_newFrame = new JButton("New Frame");
    m_newFrame.addActionListener(this);

    m_cascadeFrames = new JButton("Cascade Frames");
    m_cascadeFrames.addActionListener(this);

    m_infos = UIManager.getInstalledLookAndFeels();
    String[] LAFNames = new String[m_infos.length];
    for (int i = 0; i < m_infos.length; i++) {
      LAFNames[i] = m_infos[i].getName();
    }
    m_UIBox = new JComboBox(LAFNames);
    m_UIBox.addActionListener(this);

    JPanel topPanel = new JPanel(true);
    topPanel.add(m_newFrame);
    topPanel.add(m_cascadeFrames);
    topPanel.add(new JLabel("Look & Feel:", SwingConstants.RIGHT));
    topPanel.add(m_UIBox);

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(topPanel, BorderLayout.NORTH);
    getContentPane().add(m_desktop, BorderLayout.CENTER);

    Dimension dim = getToolkit().getScreenSize();
    setLocation(dim.width / 2 - getWidth() / 2,
                dim.height / 2 - getHeight() / 2);
  }

  public void newFrame() {
    JInternalFrame jif = new JInternalFrame("Frame " + m_count,
                                            true, true, true, true);
    jif.setBounds(20 * (m_count % 10) + m_tencount * 80,
                  20 * (m_count % 10), INI_WIDTH, INI_HEIGHT);

    JLabel label = new JLabel(m_earth);
    jif.getContentPane().add(new JScrollPane(label));

    m_desktop.add(jif);
    //m_desktop.setSelectedFrame(jif);	- don't need to set selected
    jif.show(); // 1.3

    m_count++;
    if (m_count % 10 == 0) {
      if (m_tencount < 3) {
        m_tencount++;
      }
      else {
        m_tencount = 0;
      }
    }
  }

  public void cascadeFrames() {
    try {
      JInternalFrame[] frames = m_desktop.getAllFrames();
      JInternalFrame selectedFrame = m_desktop.getSelectedFrame();
      int x = 0;
      int y = 0;
      for (int k = frames.length - 1; k >= 0; k--) {
        frames[k].setMaximum(false);
        frames[k].setIcon(false);
        frames[k].setBounds(x, y, INI_WIDTH, INI_HEIGHT);
        x += 20;
        y += 20;
      }
      if (selectedFrame != null) {
        m_desktop.setSelectedFrame(selectedFrame);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == m_newFrame) {
      newFrame();
    }
    else if (e.getSource() == m_cascadeFrames) {
      cascadeFrames();
    }
    else if (e.getSource() == m_UIBox) {
      int index = m_UIBox.getSelectedIndex();
      if (index < 0) {
        return;
      }
      String lfClass = m_infos[index].getClassName();
      m_UIBox.hidePopup(); // BUG WORKAROUND
      try {
        UIManager.setLookAndFeel(lfClass);
        SwingUtilities.updateComponentTreeUI(this);
      }
      catch (Exception ex) {
        System.out.println("Could not load " + lfClass);
        ex.printStackTrace();
      }
      m_UIBox.setSelectedIndex(index);
    }
  }

  public static void main(String[] args) {
    CascadeDemo frame = new CascadeDemo();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}
