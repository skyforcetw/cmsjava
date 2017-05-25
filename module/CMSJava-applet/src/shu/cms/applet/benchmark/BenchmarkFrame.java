package shu.cms.applet.benchmark;

import java.io.*;

import java.awt.*;
import java.awt.datatransfer.*;
import javax.swing.*;

import org.apache.commons.io.*;
import shu.ui.*;
import shu.util.log.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class BenchmarkFrame
    extends JFrame {
  protected JPanel contentPane;
  protected BorderLayout borderLayout1 = new BorderLayout();

  public BenchmarkFrame() {
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
    setSize(new Dimension(250, 100));
    setTitle("Benchmark");
    setTransferHandler(handler);
    jTextArea1.setEditable(false);
    jTextArea1.setText("Put Benchmark Excel File(s) in this window.");
    jTextArea1.setLineWrap(true);
    jTextArea1.setTransferHandler(null);
    jRadioButton_Window.setSelected(true);
    jRadioButton_Window.setText("Window");
    jRadioButton_Excel.setText("Excel");
    jLabel1.setText("Output:");
    contentPane.add(jTextArea1, java.awt.BorderLayout.CENTER);
    jPanel1.add(jLabel1);
    jPanel1.add(jRadioButton_Window);
    jPanel1.add(jRadioButton_Excel);
    jPanel1.setTransferHandler(null);
    buttonGroup1.add(jRadioButton_Window);
    contentPane.add(jPanel1, java.awt.BorderLayout.NORTH);
    buttonGroup1.add(jRadioButton_Excel);
  }

  private final static java.util.List<File> getFileList(TransferHandler.
      TransferSupport support) {
    Transferable t = support.getTransferable();

    try {
      java.util.List<File> l =
          (java.util.List<File>) t.getTransferData(DataFlavor.
          javaFileListFlavor);
      return l;
    }
    catch (IOException ex) {
      Logger.log.error("", ex);
    }
    catch (UnsupportedFlavorException ex) {
      Logger.log.error("", ex);
    }
    return null;

  }

  private TransferHandler handler = new TransferHandler() {
    public boolean canImport(TransferHandler.TransferSupport support) {
      if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
        return false;
      }
//      java.util.List<File> l = getFileList(support);
//
//      for (File f : l) {
//        String ext = FilenameUtils.getExtension(f.getName());
//        if (!ext.equals("xls")) {
//          return false;
//        }
//      }

      return true;
    }

    public boolean importData(TransferHandler.TransferSupport support) {
      if (!canImport(support)) {
        return false;
      }

      java.util.List<File> l = getFileList(support);
//      try {
      for (File f : l) {
        String filename = f.getAbsolutePath();
        String ext = FilenameUtils.getExtension(filename);
        if (ext.equals("xls")) {
          String outputFilename = FilenameUtils.getBaseName(filename) +
              " (benchmark)." + ext;

          if (jRadioButton_Window.isSelected()) {

          }
          else {
            String benchmarkFilename = FilenameUtils.getFullPath(filename) +
                "\\" + outputFilename;
            System.out.println(benchmarkFilename);
//              ExcelFile excel = new ExcelFile(filename, benchmarkFilename);
//              excel.close();
          }

        }
      }
//      }
//      catch (IOException ex) {
//        Logger.log.error("", ex);
//        return false;
//      }
//      catch (WriteException ex) {
//        Logger.log.error("", ex);
//        return false;
//      }

      return true;
    }

  };
  protected JTextArea jTextArea1 = new JTextArea();
  protected JRadioButton jRadioButton_Window = new JRadioButton();
  protected JRadioButton jRadioButton_Excel = new JRadioButton();
  protected JPanel jPanel1 = new JPanel();
  protected ButtonGroup buttonGroup1 = new ButtonGroup();
  protected JLabel jLabel1 = new JLabel();
  public static void main(String[] args) {
    GUIUtils.runAsApplication(new BenchmarkFrame(), false);
  }
}
