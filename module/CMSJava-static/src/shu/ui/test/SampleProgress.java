package shu.ui.test;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SampleProgress {
  static ProgressMonitor monitor;

  static int progress;

  static Timer timer;

  public static void main(String args[]) {

    JFrame frame = new JFrame("ProgressMonitor Sample");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new GridLayout(0, 1));

    JButton startButton = new JButton("Start");
    ActionListener startActionListener = new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        Component parent = (Component) actionEvent.getSource();
        monitor = new ProgressMonitor(parent, "Loading Progress",
                                      "Getting Started...", 0, 200);
        progress = 0;
      }
    };
    startButton.addActionListener(startActionListener);
    frame.add(startButton);

    JButton increaseButton = new JButton("Manual Increase");
    ActionListener increaseActionListener = new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        if (monitor == null) {
          return;
        }
        if (monitor.isCanceled()) {
          System.out.println("Monitor canceled");
        }
        else {
          progress += 5;
          monitor.setProgress(progress);
          monitor.setNote("Loaded " + progress + " files");
        }
      }
    };
    increaseButton.addActionListener(increaseActionListener);
    frame.add(increaseButton);

    JButton autoIncreaseButton = new JButton("Automatic Increase");
    ActionListener autoIncreaseActionListener = new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        if (monitor != null) {
          if (timer == null) {
            timer = new Timer(250, new ActionListener() {

              public void actionPerformed(ActionEvent e) {
                if (monitor == null) {
                  return;
                }
                if (monitor.isCanceled()) {
                  System.out.println("Monitor canceled");
                  timer.stop();
                }
                else {
                  progress += 3;
                  monitor.setProgress(progress);
                  monitor.setNote("Loaded " + progress + " files");
                }
              }
            });
          }
          timer.start();
        }
      }
    };
    autoIncreaseButton.addActionListener(autoIncreaseActionListener);
    frame.add(autoIncreaseButton);

    frame.setSize(300, 200);
    frame.setVisible(true);
  }
}
