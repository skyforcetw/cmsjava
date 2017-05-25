package shu.util.log;

import java.io.*;
import java.util.logging.*;

import java.awt.*;
import java.awt.event.*;

import org.apache.commons.logging.*;
import shu.util.log.frame.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2001</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class Logger {

  public static void main(String[] args) {

//�qLogFactory���h��l�Ƥ@��Log �� ��@(�pSimplelog),�ڭ̪��Ҧ��T��
//    �N�|�x�s�b�o��Log��

    Log log = LogFactory.getLog("CMSJava");
//    Log log = LogFactory.getLog("x");

//�H�U���O�ϥ�Commons-Logging��6�Ӽh�Ũӳ]�wLog��T
    log.trace("�o�O trace �h�Ÿ�T");
    log.debug("�o�O debug �h�Ÿ�T");
    log.info("�o�O info �h�Ÿ�T");
    log.error("�o�O error �h�Ÿ�T");
    log.warn("�o�O warn �h�Ÿ�T");
    log.fatal("�o�O fatal �h�Ÿ�T");

  }

  private final static boolean initLogDir = initLogDir();
  private final static String LogDirname = "log";
  private static java.util.logging.Logger jdk14log = getDefaultFileLogger(
      "CMSJava", LogDirname + File.separator + "cmsjava.log");
  public static Log log = LogFactory.getLog("CMSJava");

  public final static Level LogLeve = Level.ALL;
  public final static Level ConsoleLeve = Level.ALL;

  private static Tray tray;

  private static class Tray
      implements ActionListener {
    private TrayIcon trayIcon;
    private LoggerFrame frame;
    private Image icon;

    private void addHandler(LoggerFrameHandler handler, String logName) {
      frame.addHandler(handler, logName);
    }

    private Tray() {
      if (SystemTray.isSupported()) {
        SystemTray tray = SystemTray.getSystemTray();
        icon = Toolkit.getDefaultToolkit()
            .getImage(LoggerFrame.class.getResource(
                "help.png"));
        trayIcon =
            new TrayIcon(icon, "Logger");
        trayIcon.addActionListener(this);
        try {
          tray.add(trayIcon);
        }
        catch (AWTException e) {
          e.printStackTrace();
        }
      }
      else {
        Logger.log.error("�L�k���o�t�Τu��C");
      } //�u��C�ϥ�
      frame = new LoggerFrame();
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
      frame.setVisible(true);
    }

  }

  private final static boolean ShowTryIcon = false;

  public final static java.util.logging.Logger getDefaultLogger(String
      loggerName) {
    java.util.logging.Logger jdk14log = java.util.logging.Logger.
        getLogger(loggerName);
    jdk14log.setUseParentHandlers(false);

    if (ShowTryIcon) {
      if (tray == null) {
        tray = new Tray();
      }
      LoggerFrameHandler handler = new LoggerFrameHandler();
      jdk14log.addHandler(handler);
      tray.addHandler(handler, loggerName);
    }

    return jdk14log;
  }

  public final static java.util.logging.Logger getDefaultFileLogger(String
      loggerName, String filename) {
    java.util.logging.Logger jdk14log = getDefaultLogger(loggerName);
    if (UseFileHandler) {
      setDefaultFileHandler(jdk14log, filename);
    }
    return jdk14log;
  }

  private final static boolean UseFileHandler = true;
  private final static boolean UseConsoleHandler = true;

  public final static void setDefaultFileHandler(java.util.logging.Logger
                                                 jdk14log, String filename) {
    try {
      //�s���ɮ׸�
      FileHandler fileHandler = new FileHandler(filename, true);
      fileHandler.setFormatter(new SimpleFormatter());
      //buffer�Ŷ�
      MemoryHandler memoryHandler = new MemoryHandler(fileHandler, 10,
          Level.ALL);
      jdk14log.addHandler(memoryHandler);
    }
    catch (SecurityException ex) {
      ex.printStackTrace();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  static {
    initLogDir();

    //�T��global��handlers
    jdk14log.setUseParentHandlers(false);
    jdk14log.setLevel(LogLeve);

//    setDefaultFileHandler(jdk14log, "log" + File.separator + "cmsjava.log");

    if (UseConsoleHandler) {
      //�@��T����console���
      ConsoleHandler consoleHandler = new ConsoleHandler();
      consoleHandler.setLevel(ConsoleLeve);
      consoleHandler.setFormatter(new SimplerFormatter());
      jdk14log.addHandler(consoleHandler);
    }
    useSwingHandler();
//    setUsePlatformHandler();
  }

  private final static boolean initLogDir() {
    File dir = new File(LogDirname);
    if (!dir.exists()) {
      return dir.mkdir();
    }
    else {
      return false;
    }
  }

  public final static void useSwingHandler() {
    //���j���~��swing���
    SwingHandler swingHandler = new SwingHandler();
    swingHandler.setLevel(Level.SEVERE);
    jdk14log.addHandler(swingHandler);
  }

//  public final static void usePlatformHandler() {
//    //���j���~
//    PlatformHandler plotformHandler = new PlatformHandler();
//    plotformHandler.setLevel(Level.SEVERE);
//    jdk14log.addHandler(plotformHandler);
//  }

  private Logger() {
  }

}
