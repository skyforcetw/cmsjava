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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Action;
import javax.swing.table.*;

//import org.loon.framework.dll.NativeLoader;

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
public class MyPanel
    extends JPanel {
  /** */
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private static final Color evenColor = new Color(250, 250, 250);

  private final MyTableModel model = new MyTableModel();

  private final TableRowSorter<MyTableModel> sorter = new TableRowSorter<
      MyTableModel> (
          model);

  private final JTable table;

  public MyPanel() {
    super(new BorderLayout());
    table = new JTable(model) {
      /** */
      /**
       *
       */
      private static final long serialVersionUID = 1L;

      public Component prepareRenderer(
          TableCellRenderer tableCellRenderer, int row, int column) {
        Component component = super.prepareRenderer(tableCellRenderer, row,
            column);
        //背景色及字體設置
        if (isRowSelected(row)) {
          component.setForeground(getSelectionForeground());
          component.setBackground(getSelectionBackground());
        }
        else {
          component.setForeground(getForeground());
          component.setBackground( (row % 2 == 0) ? evenColor : table
                                  .getBackground());
        }
        return component;
      }

      public JPopupMenu getComponentPopupMenu() {
        return makePopup();
      }
    };
    table.setRowSorter(sorter);
    model.addTest(new Test("進度條測試", 100), null);

    // 滾動條
    JScrollPane scrollPane = new JScrollPane(table);
    // 背景色
    scrollPane.getViewport().setBackground(Color.black);
    // 彈出菜單
    table.setComponentPopupMenu(new JPopupMenu());
    // 是否始終大到足以填充封閉視口的高度
    table.setFillsViewportHeight(true);
    // 將單元格間距的高度和寬度設置為指定的Dimension
    table.setIntercellSpacing(new Dimension());
    // 是否繪製單元格間的水平線
    table.setShowHorizontalLines(true);
    // 是否繪製單元格間的垂直線
    table.setShowVerticalLines(false);
    // 停止編輯時重新定義焦點，避免TableCellEditor丟失數據
    table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    // 表示JTable中列的所有屬性，如寬度、大小可調整性、最小和最大寬度等。
    TableColumn column = table.getColumnModel().getColumn(0);
    column.setMaxWidth(60);
    column.setMinWidth(60);
    column.setResizable(false);
    column = table.getColumnModel().getColumn(2);
    // 繪製此列各值的TableCellRenderer
    column.setCellRenderer(new ProgressRenderer());

    // 添加按鈕
    add(new JButton(new CreateNewAction("添加", null)), BorderLayout.SOUTH);
    add(scrollPane, BorderLayout.CENTER);
    setPreferredSize(new Dimension(320, 180));
  }

  class CreateNewAction
      extends AbstractAction {
    /** */
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CreateNewAction(String label, Icon icon) {
      super(label, icon);
    }

    public void actionPerformed(ActionEvent evt) {
      createNewActionPerformed(evt);
    }
  }

  /** */
  /**
   * 創建事件
   * @param evt
   */
  private void createNewActionPerformed(ActionEvent evt) {
    final int key = model.getRowCount();
    //在jdk1.6後，當一個Swing程序需要執行一個多線程任務時，可以通過javax.swing.SwingWorker實例進行實現。
    //SwingWorker的process可以定義約束屬性。更改這些屬性將觸發事件，並從事件調度線程上引起事件處理方法的調用。
    //SwingWorker的done方法，在後台任務完成時自動的在事件調度線程上被調用。
    SwingWorker<Integer, Integer> worker = new SwingWorker<Integer, Integer> () {
      // 隨機sleep
      private int sleepDummy = new Random().nextInt(100) + 1;

      // 最大任務數量
      private int taskSize = 200;

      protected Integer doInBackground() {
        int current = 0;
        while (current < taskSize && !isCancelled()) {
          current++;
          try {
            Thread.sleep(sleepDummy);
          }
          catch (InterruptedException ie) {
            publish( -1);
            break;
          }
          publish(100 * current / taskSize);
        }
        return sleepDummy * taskSize;
      }

      /** */
      /**
       * 進行中處理
       */
      protected void process(java.util.List<Integer> data) {
        for (Integer value : data) {
          // 把數據填入對應的行列
          model.setValueAt(value, key, 2);
        }
        // 傳送變更事件給指定行列
        model.fireTableCellUpdated(key, 2);
      }

      /** */
      /**
       * 完成後處理
       */
      protected void done() {
      }
    };
    model.addTest(new Test("進度條測試", 0), worker);
    worker.execute();
  }

  class CancelAction
      extends AbstractAction {
    /** */
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CancelAction(String label, Icon icon) {
      super(label, icon);
    }

    public void actionPerformed(ActionEvent evt) {
      cancelActionPerformed(evt);
    }
  }

  /** */
  /**
   * 取消進度
   * @param evt
   */
  public synchronized void cancelActionPerformed(ActionEvent evt) {
    int[] selection = table.getSelectedRows();
    if (selection == null || selection.length <= 0) {
      return;
    }
    for (int i = 0; i < selection.length; i++) {
      int midx = table.convertRowIndexToModel(selection[i]);
      SwingWorker worker = model.getSwingWorker(midx);
      if (worker != null && !worker.isDone()) {
        worker.cancel(true);
      }
      worker = null;
    }
    table.repaint();
  }

  /** */
  /**
   * 取消下載進程
   *
   * @author chenpeng
   *
   */
  class DeleteAction
      extends AbstractAction {
    /** */
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public DeleteAction(String label, Icon icon) {
      super(label, icon);
    }

    public void actionPerformed(ActionEvent evt) {
      deleteActionPerformed(evt);
    }
  }

  private final HashSet<Integer> set = new HashSet<Integer> ();

  public synchronized void deleteActionPerformed(ActionEvent evt) {
    int[] selection = table.getSelectedRows();
    if (selection == null || selection.length <= 0) {
      return;
    }
    for (int i = 0; i < selection.length; i++) {
      int midx = table.convertRowIndexToModel(selection[i]);
      set.add(midx);
      SwingWorker worker = model.getSwingWorker(midx);
      if (worker != null && !worker.isDone()) {
        worker.cancel(true);
      }
      worker = null;
    }
    // JTable過濾器
    final RowFilter<MyTableModel, Integer> filter = new RowFilter<MyTableModel,
        Integer> () {

      public boolean include(
          Entry<? extends MyTableModel, ? extends Integer> entry) {
        Integer midx = entry.getIdentifier();
        return!set.contains(midx);
      }
    };
    sorter.setRowFilter(filter);
    table.repaint();
  }

  private JPopupMenu makePopup() {
    JPopupMenu pop = new JPopupMenu();
    Action act = new CreateNewAction("添加", null);
    pop.add(act);
    act = new CancelAction("取消", null);
    int[] selection = table.getSelectedRows();
    if (selection == null || selection.length <= 0) {
      act.setEnabled(false);
    }
    pop.add(act);
    // 分割線
    pop.add(new JSeparator());
    act = new DeleteAction("刪除", null);
    if (selection == null || selection.length <= 0) {
      act.setEnabled(false);
    }
    pop.add(act);
    return pop;
  }

  public static void main(String[] args) {
    UIManager.put("ProgressBar.selectionBackground", Color.BLUE);
    UIManager.put("ProgressBar.selectionForeground", Color.WHITE);

    EventQueue.invokeLater(new Runnable() {
      public void run() {
        createGUI();
      }
    });
  }

  public static void createGUI() {

    JFrame frame = new JFrame("在JTable中加載進度條及進行操作");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.getContentPane().add(new MyPanel());
    frame.setSize(400, 400);
    // 透明度90%
    // NativeLoader.getInstance().setTransparence(frame, 0.9f);
    // 居中
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

  }
}
