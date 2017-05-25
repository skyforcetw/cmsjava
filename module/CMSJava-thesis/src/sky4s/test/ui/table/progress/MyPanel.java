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
 * @email�Gceponline@yahoo.com.cn
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
        //�I����Φr��]�m
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
    model.addTest(new Test("�i�ױ�����", 100), null);

    // �u�ʱ�
    JScrollPane scrollPane = new JScrollPane(table);
    // �I����
    scrollPane.getViewport().setBackground(Color.black);
    // �u�X���
    table.setComponentPopupMenu(new JPopupMenu());
    // �O�_�l�פj�쨬�H��R�ʳ����f������
    table.setFillsViewportHeight(true);
    // �N�椸�涡�Z�����שM�e�׳]�m�����w��Dimension
    table.setIntercellSpacing(new Dimension());
    // �O�_ø�s�椸�涡�������u
    table.setShowHorizontalLines(true);
    // �O�_ø�s�椸�涡�������u
    table.setShowVerticalLines(false);
    // ����s��ɭ��s�w�q�J�I�A�קKTableCellEditor�ᥢ�ƾ�
    table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    // ���JTable���C���Ҧ��ݩʡA�p�e�סB�j�p�i�վ�ʡB�̤p�M�̤j�e�׵��C
    TableColumn column = table.getColumnModel().getColumn(0);
    column.setMaxWidth(60);
    column.setMinWidth(60);
    column.setResizable(false);
    column = table.getColumnModel().getColumn(2);
    // ø�s���C�U�Ȫ�TableCellRenderer
    column.setCellRenderer(new ProgressRenderer());

    // �K�[���s
    add(new JButton(new CreateNewAction("�K�[", null)), BorderLayout.SOUTH);
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
   * �Ыبƥ�
   * @param evt
   */
  private void createNewActionPerformed(ActionEvent evt) {
    final int key = model.getRowCount();
    //�bjdk1.6��A��@��Swing�{�ǻݭn����@�Ӧh�u�{���ȮɡA�i�H�q�Ljavax.swing.SwingWorker��Ҷi���{�C
    //SwingWorker��process�i�H�w�q�����ݩʡC���o���ݩʱNĲ�o�ƥ�A�ñq�ƥ�ի׽u�{�W�ް_�ƥ�B�z��k���եΡC
    //SwingWorker��done��k�A�b��x���ȧ����ɦ۰ʪ��b�ƥ�ի׽u�{�W�Q�եΡC
    SwingWorker<Integer, Integer> worker = new SwingWorker<Integer, Integer> () {
      // �H��sleep
      private int sleepDummy = new Random().nextInt(100) + 1;

      // �̤j���ȼƶq
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
       * �i�椤�B�z
       */
      protected void process(java.util.List<Integer> data) {
        for (Integer value : data) {
          // ��ƾڶ�J��������C
          model.setValueAt(value, key, 2);
        }
        // �ǰe�ܧ�ƥ󵹫��w��C
        model.fireTableCellUpdated(key, 2);
      }

      /** */
      /**
       * ������B�z
       */
      protected void done() {
      }
    };
    model.addTest(new Test("�i�ױ�����", 0), worker);
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
   * �����i��
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
   * �����U���i�{
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
    // JTable�L�o��
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
    Action act = new CreateNewAction("�K�[", null);
    pop.add(act);
    act = new CancelAction("����", null);
    int[] selection = table.getSelectedRows();
    if (selection == null || selection.length <= 0) {
      act.setEnabled(false);
    }
    pop.add(act);
    // ���νu
    pop.add(new JSeparator());
    act = new DeleteAction("�R��", null);
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

    JFrame frame = new JFrame("�bJTable���[���i�ױ��ζi��ާ@");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.getContentPane().add(new MyPanel());
    frame.setSize(400, 400);
    // �z����90%
    // NativeLoader.getInstance().setTransparence(frame, 0.9f);
    // �~��
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

  }
}
