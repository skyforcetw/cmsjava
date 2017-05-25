package sky4s.test.ui.table.progress;

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
import java.util.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import shu.ui.*;

/** */
/**
 * <p>
 * Title: LoonFramework
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: LoonFramework
 * </p>
 *
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class MyTableModel
    extends DefaultTableModel {
  /** */
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private static final ColumnContext[] columnArray = {
      new ColumnContext("ID", Integer.class, false),
      new ColumnContext("名稱", String.class, false),
      new ColumnContext("進度", Integer.class, false)};

  private final Map<Integer, SwingWorker> swmap = new HashMap<Integer,
      SwingWorker> ();

  private int number = 0;

  public void addTest(Test t, SwingWorker worker) {
    Object[] obj = {
        new Integer(number), t.getName(), t.getProgress()};
    super.addRow(obj);
    swmap.put(number, worker);
    number++;
  }

  public synchronized SwingWorker getSwingWorker(int identifier) {
    Integer key = (Integer) getValueAt(identifier, 0);
    return swmap.get(key);
  }

  public Test getTest(int identifier) {
    return new Test( (String) getValueAt(identifier, 1),
                    (Integer) getValueAt(identifier, 2));
  }

  public boolean isCellEditable(int row, int col) {
    return columnArray[col].isEditable;
  }

  public Class<?> getColumnClass(int modelIndex) {
    return columnArray[modelIndex].columnClass;
  }

  public int getColumnCount() {
    return columnArray.length;
  }

  public String getColumnName(int modelIndex) {
    return columnArray[modelIndex].columnName;
  }

  private static class ColumnContext {
    public final String columnName;

    public final Class columnClass;

    public final boolean isEditable;

    public ColumnContext(String columnName, Class columnClass,
                         boolean isEditable) {
      this.columnName = columnName;
      this.columnClass = columnClass;
      this.isEditable = isEditable;
    }
  }
}

class Test {
  private String name;

  private Integer progress;

  public Test(String name, Integer progress) {
    this.name = name;
    this.progress = progress;
  }

  public void setName(String str) {
    name = str;
  }

  public void setProgress(Integer str) {
    progress = str;
  }

  public String getName() {
    return name;
  }

  public Integer getProgress() {
    return progress;
  }
}

class ProgressRenderer
    extends DefaultTableCellRenderer {
  /** */
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private final JProgressBar b = new JackProgressBar(0, 100, Color.red);

  public ProgressRenderer() {
    super();
    setOpaque(true);
    b.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus, int row,
                                                 int column) {
    Integer i = (Integer) value;
    String text = "完成";
    if (i < 0) {
      //刪除
      text = "取消完畢";
    }
    else if (i < 100) {
      b.setValue(i);
      return b;
    }
    super.getTableCellRendererComponent(table, text, isSelected, hasFocus,
                                        row, column);
    return this;
  }
}
