package shu.cms.ui;

import java.util.List;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.lcd.*;
import shu.ui.*;
import shu.util.log.*;

/**
 * TableDemo is just like SimpleTableDemo, except that it
 * uses a custom TableModel.
 */
public class PatchListTablePanel
    extends JPanel {
  protected PatchListTableModel model;
  protected JTable table;
  protected List<Patch> patchList;

  public PatchListTablePanel(List<Patch> patchList) {
    super(new GridLayout(1, 0));
//    setPatchList(patchList);
    this.patchList = patchList;
    this.model = new PatchListTableModel(patchList);
    try {
      jbInit();
    }
    catch (Exception ex) {
      Logger.log.error("", ex);
    }

  }

  public static void main(String[] args) {
    //==========================================================================
    // 設定區
    //==========================================================================
    LCDTarget.setRGBNormalize(false);
//    LCDTarget.setXYZNormalize(false);
    LCDTarget lcdTarget = LCDTarget.Instance.get("cpt_17inch No.3",
                                                 LCDTarget.Source.CA210,
                                                 LCDTarget.Room.Dark,
                                                 LCDTarget.TargetIlluminant.
                                                 Native,
                                                 LCDTargetBase.Number.Ramp1792,
                                                 LCDTarget.FileType.VastView,
                                                 null, null);
    //==========================================================================


    //Create and set up the window.
    JFrame frame = new JFrame("TableDemo");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //Create and set up the content pane.
    PatchListTablePanel newContentPane = new PatchListTablePanel(lcdTarget.
        getLabPatchList());
    newContentPane.setOpaque(true); //content panes must be opaque
    frame.setContentPane(newContentPane);
    GUIUtils.defaultScreen(frame);

    //Display the window.
//    frame.pack();
    frame.setVisible(true);
  }

  protected JPopupMenu jPopupMenu1 = new JPopupMenu();

  private void jbInit() throws Exception {
    //==========================================================================

    table = new JTable(model);

    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    table.setPreferredScrollableViewportSize(new Dimension(500, 70));
    table.setFillsViewportHeight(true);
    table.setDefaultRenderer(Color.class,
                             new PatchListTableModel.ColorRenderer(true));
    table.setDefaultRenderer(RGB.class,
                             new PatchListTableModel.sRGBRenderer());
    table.getColumnModel().getColumn(7).setCellRenderer(model.getCIEXYZRenderer());
    table.getColumnModel().getColumn(8).setCellRenderer(model.getCIEXYZRenderer());
    table.getColumnModel().getColumn(9).setCellRenderer(model.getCIEXYZRenderer());

    PopupListener popupListener = new PopupListener();
    table.addMouseListener(popupListener);
    table.getTableHeader().addMouseListener(popupListener);

    int col = table.getColumnCount();
    sizeArray = new SizeRequirements[col];

    //Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(table);
    jMenuItem3_Color.addActionListener(new
                                       PatchListTablePanel_jMenuItem3_Color_actionAdapter(this));

    //Add the scroll pane to this panel.
    add(scrollPane);

    jMenuItem1_ID.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem1_actionPerformed(e);
      }
    });
    jMenuItem2_Name.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem1_actionPerformed(e);
      }
    });

    resetMenuItem();
    jMenuItem8_ShowAll.setText("Show All");
    jMenuItem8_ShowAll.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem8_ShowAll_actionPerformed(e);
      }
    });
    jMenuItem4_RGB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem4_RGB_actionPerformed(e);
      }
    });
    jMenuItem5_XYZ.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem5_XYZ_actionPerformed(e);
      }
    });
    jMenuItem6_Lab.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem6_Lab_actionPerformed(e);
      }
    });

    jPopupMenu1.add(jMenuItem1_ID);
    jPopupMenu1.add(jMenuItem2_Name);
    jPopupMenu1.add(jMenuItem3_Color);
    jPopupMenu1.add(jMenuItem4_RGB);
    jPopupMenu1.add(jMenuItem5_XYZ);
    jPopupMenu1.add(jMenuItem6_Lab);
    jPopupMenu1.add(jMenuItem7_LCh);
    jPopupMenu1.addSeparator();
    jPopupMenu1.add(jMenuItem8_ShowAll);
  }

  protected SizeRequirements sizeArray[];

  protected JMenuItem jMenuItem1_ID = new JMenuItem();
  protected JMenuItem jMenuItem2_Name = new JMenuItem();
  protected JMenuItem jMenuItem3_Color = new JMenuItem();
  protected JMenuItem jMenuItem4_RGB = new JMenuItem();
  protected JMenuItem jMenuItem5_XYZ = new JMenuItem();
  protected JMenuItem jMenuItem6_Lab = new JMenuItem();
  protected JMenuItem jMenuItem7_LCh = new JMenuItem();
  protected JMenuItem jMenuItem8_ShowAll = new JMenuItem();

  protected void resetMenuItem() {
    jMenuItem1_ID.setText("Hide ID");
    jMenuItem2_Name.setText("Hide Name");
    jMenuItem3_Color.setText("Hide Color");
    jMenuItem4_RGB.setText("Hide RGB");
    jMenuItem5_XYZ.setText("Hide XYZ");
    jMenuItem6_Lab.setText("Hide L*a*b*");
    jMenuItem7_LCh.setText("Hide L*C*h");
  }

  protected final void hideOrShow(int index, TableColumn tc) {
    if (sizeArray[index] == null || sizeArray[index].alignment == 1) {
      sizeArray[index] = new SizeRequirements(tc.getMinWidth(),
                                              tc.getPreferredWidth(),
                                              tc.getMaxWidth(), 0);

    }
    else {
      sizeArray[index].alignment = 1 - sizeArray[index].alignment;
    }
    if (sizeArray[index].alignment == 0) {
      tc.setMaxWidth(0);
      tc.setMinWidth(0);
      tc.setPreferredWidth(0);
//      menuItem.setText("Show " + id);
    }
    else {
      tc.setMaxWidth(sizeArray[index].maximum);
      tc.setMinWidth(sizeArray[index].minimum);
      tc.setPreferredWidth(sizeArray[index].preferred);
//      menuItem.setText("Hide " + id);
    }

  }

  public void jMenuItem1_actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    int index = 0;
    if (src == jMenuItem1_ID) {
      index = 0;
    }
    else if (src == jMenuItem2_Name) {
      index = 1;
    }
    else {
      return;
    }
//    else if (src == jMenuItem3_Color) {
//      index = 2;
//    }

    JMenuItem menuItem = (JMenuItem) src;
    String id = model.getColumnName(index);
    TableColumn tc = table.getColumn(id);
    hideOrShow(index, tc);

    if (sizeArray[index].alignment == 0) {
      menuItem.setText("Show " + id);
    }
    else {
      menuItem.setText("Hide " + id);
    }

  }

  public void jMenuItem8_ShowAll_actionPerformed(ActionEvent e) {
    TableColumnModel tcModel = table.getColumnModel();
    int size = tcModel.getColumnCount();
    for (int x = 0; x < size; x++) {
      SizeRequirements sr = sizeArray[x];
      if (sr == null) {
        continue;
      }
      sr.alignment = 1;
      TableColumn col = tcModel.getColumn(x);
      col.setMinWidth(sr.minimum);
      col.setMaxWidth(sr.maximum);
      col.setPreferredWidth(sr.preferred);
    }
    resetMenuItem();
  }

  public void jMenuItem4_RGB_actionPerformed(ActionEvent e) {
    hideOrShowGroup(new int[] {4, 5, 6});
    if (jMenuItem4_RGB.getText().indexOf("Hide RGB") != -1) {
      jMenuItem4_RGB.setText("Show RGB");
    }
    else {
      jMenuItem4_RGB.setText("Hide RGB");
    }
  }

  public void jMenuItem5_XYZ_actionPerformed(ActionEvent e) {
    hideOrShowGroup(new int[] {7, 8, 9});
    if (jMenuItem5_XYZ.getText().indexOf("Hide XYZ") != -1) {
      jMenuItem5_XYZ.setText("Show XYZ");
    }
    else {
      jMenuItem5_XYZ.setText("Hide XYZ");
    }
  }

  protected final void hideOrShowGroup(int[] index) {
    for (int i : index) {
      String idL = model.getColumnName(i);
      TableColumn tcL = table.getColumn(idL);
      hideOrShow(i, tcL);
    }
  }

  public void jMenuItem6_Lab_actionPerformed(ActionEvent e) {
    hideOrShowGroup(new int[] {10, 11, 12});
    if (jMenuItem6_Lab.getText().indexOf("Hide L*a*b*") != -1) {
      jMenuItem6_Lab.setText("Show L*a*b*");
    }
    else {
      jMenuItem6_Lab.setText("Hide L*a*b*");
    }
  }

  protected class PopupListener
      extends MouseAdapter {
    public void mousePressed(MouseEvent e) {
      showPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
      showPopup(e);
    }

    private void showPopup(MouseEvent e) {
      if (e.isPopupTrigger()) {
        jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }

  public List getPatchList() {
    return patchList;
  }

  public void jMenuItem3_Color_actionPerformed(ActionEvent e) {
    hideOrShowGroup(new int[] {2, 3});
    if (jMenuItem3_Color.getText().indexOf("Hide Color") != -1) {
      jMenuItem3_Color.setText("Show Color");
    }
    else {
      jMenuItem3_Color.setText("Hide Color");
    }
  }

  public void setPatchList(List patchList) {
    this.patchList = patchList;
    model.setPatchList(patchList);
//    if (table != null) {
//      table.setModel(model);
//    }
  }

}

class PatchListTablePanel_jMenuItem3_Color_actionAdapter
    implements ActionListener {
  private PatchListTablePanel adaptee;
  PatchListTablePanel_jMenuItem3_Color_actionAdapter(PatchListTablePanel
      adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem3_Color_actionPerformed(e);
  }
}
