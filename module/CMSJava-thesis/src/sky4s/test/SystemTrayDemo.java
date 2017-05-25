package sky4s.test;

import java.awt.*;

public class SystemTrayDemo {
  public static void main(String[] args) {
    if (SystemTray.isSupported()) {
      SystemTray tray = SystemTray.getSystemTray();
      Image image = Toolkit.getDefaultToolkit()
          .getImage("color_wheel.png");
      PopupMenu popup = new PopupMenu();
      MenuItem item = new MenuItem("�}��JNotePad 1.0");
      popup.add(item);
      TrayIcon trayIcon =
          new TrayIcon(image, "JNotePad 1.0", popup);
      try {
        tray.add(trayIcon);
      }
      catch (AWTException e) {
        System.err.println("�L�k�[�J�t�Τu��C�ϥ�");
        e.printStackTrace();
      }
    }
    else {
      System.err.println("�L�k���o�t�Τu��C");
    } //�u��C�ϥ�
  }
}
