package shu.util;

import java.lang.reflect.*;
import java.util.*;

import shu.util.log.*;

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
public class ApplicationLoader
    extends ThreadGroup {

  private ApplicationLoader() {
    super("ApplicationLoader");
  }

  public static void loadMain(final String[] args) {
    Runnable appStarter = new Runnable() {
      public void run() {
        try {
          Class<?> c = Class.forName(args[0]);
          Class[] argTypes = new Class[] {
              String[].class};
          Method main = c.getDeclaredMethod("main", argTypes);
          String[] mainArgs = Arrays.copyOfRange(args, 1, args.length);
          main.invoke(null, (Object) mainArgs);

        }
        catch (Exception ex) {
          Logger.log.error("", ex);
        }

      }
    };
    new Thread(new ApplicationLoader(), appStarter).start();
  }

  public static void loadMain(final Class loadedClass, final String[] args) {
    Runnable appStarter = new Runnable() {
      public void run() {
        try {
          Class[] argTypes = new Class[] {
              String[].class};
          Method main = loadedClass.getDeclaredMethod("main", argTypes);
          main.invoke(null, (Object) args);
        }
        catch (Exception ex) {
          Logger.log.error("", ex);
        }

      }
    };
    new Thread(new ApplicationLoader(), appStarter).start();
  }

  public static void main(String[] args) {
    ApplicationLoader.loadMain(args);
  }

  /**
   * We overload this method from our parent ThreadGroup , which will make sure
   *  that it gets called when it needs to be.
   * This is where the magic occurs.
   * @param thread Thread
   * @param ex Throwable
   */
  public void uncaughtException(Thread thread, Throwable ex) {
//    Logger.jdk14log.log(Level.SEVERE, "", exception);
    Logger.log.error("", ex);
  }
}
