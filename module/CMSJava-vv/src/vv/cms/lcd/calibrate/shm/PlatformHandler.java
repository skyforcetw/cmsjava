package vv.cms.lcd.calibrate.shm;

import java.util.logging.*;

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
public class PlatformHandler
    extends Handler {
  /**
   * Default constructor.
   *
   */
  protected PlatformHandler() {
    super();
    this.setFormatter(new SimpleFormatter());
  }

  /**
   * Close the <tt>Handler</tt> and free all associated resources.
   *
   * @throws SecurityException if a security manager exists and if the caller
   *   does not have <tt>LoggingPermission("control")</tt>.
   */
  public void close() throws SecurityException {
  }

  /**
   * Flush any buffered output.
   *
   */
  public void flush() {
  }

  /**
   * Publish a <tt>LogRecord</tt>.
   *
   * @param record description of the log event. A null record is silently
   *   ignored and is not published
   */
  public void publish(LogRecord record) {
    if (!isLoggable(record)) {
      return;
    }

    try {
      if (connector.isInputStreamAvailable()) {
        String msg = getFormatter().format(record);
        String level = record.getLevel().getLocalizedName();
        connector.sendMessage(level + " " + msg);
      }
    }
    catch (Exception ex) {
      // We don't want to throw an exception here, but we
      // report the exception to any registered ErrorManager.
      reportError(null, ex, ErrorManager.FORMAT_FAILURE);
    }
  }

  private ShareMemoryConnector connector = ShareMemoryConnector.getInstance();

}
