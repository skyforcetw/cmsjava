package shu.util.log.frame;

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
public class LoggerFrameHandler
    extends Handler {
  public LoggerFrameHandler() {
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
   * @todo Implement this java.util.logging.Handler method
   */
  public void publish(LogRecord record) {
    if (loggerInterface == null) {
      return;
    }
    if (!isLoggable(record)) {
      return;
    }

    try {
      String msg = record.getMessage();
//      mi.addMessage(msg + '\n');
      loggerInterface.log(msg + '\n');
    }
    catch (Exception ex) {
      // We don't want to throw an exception here, but we
      // report the exception to any registered ErrorManager.
      reportError(null, ex, ErrorManager.FORMAT_FAILURE);
      return;
    }
  }

  private LoggerInterface loggerInterface;
  void setLoggerInterface(LoggerInterface li) {
    this.loggerInterface = li;
  }
}
