package sky4s.test.ui.table.progress;

import java.awt.*;
import javax.swing.*;

public class ProgressEx {
  public static void main(String[] args) {
//    UIManager.put("ProgressBar.background", Color.WHITE);
//    UIManager.put("ProgressBar.foreground", Color.BLACK);
//    UIManager.put("ProgressBar.selectionBackground", Color.YELLOW);
//    UIManager.put("ProgressBar.selectionForeground", Color.RED);
//    UIManager.put("ProgressBar.shadow", Color.GREEN);
//    UIManager.put("ProgressBar.highlight", Color.BLUE);

    JFrame f = new JFrame("Test");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    UIManager.put("ProgressBar.foreground", Color.orange);
    JProgressBar pb1 = new JProgressBar();
    pb1.setStringPainted(true);
    pb1.setValue(50);
    UIManager.put("ProgressBar.foreground", Color.yellow);
    JProgressBar pb2 = new JProgressBar();
    pb2.setIndeterminate(true);
    Container cp = f.getContentPane();
    cp.add(pb1, BorderLayout.NORTH);
    cp.add(pb2, BorderLayout.SOUTH);
    f.pack();
    f.setVisible(true);
  }
}
