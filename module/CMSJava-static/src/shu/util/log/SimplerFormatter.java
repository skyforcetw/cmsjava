package shu.util.log;

import java.io.*;
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
public class SimplerFormatter
    extends Formatter {

  // Line separator string.  This is the value of the line.separator
// property at the moment that the SimpleFormatter was created.
  private String lineSeparator = (String) java.security.AccessController.
      doPrivileged(
          new sun.security.action.GetPropertyAction("line.separator"));

  private String preClassName;
  private String preMethodName;

  /**
   * Format the given log record and return the formatted string.
   *
   * @param record the log record to be formatted.
   * @return the formatted log record
   */
  public synchronized String format(LogRecord record) {
    StringBuffer sb = new StringBuffer();
    String message = formatMessage(record);

    String className = record.getSourceClassName();
    String methodName = record.getSourceMethodName();
    if (! (className != null && methodName != null &&
           className.equals(preClassName) && methodName.equals(preMethodName))) {
      sb.append(record.getLevel().getLocalizedName());
      sb.append(" ");

      if (className != null) {
        sb.append(className);
      }
      else {
        sb.append(record.getLoggerName());
      }
      if (methodName != null) {
        sb.append("::");
        sb.append(methodName + "()");
      }
      sb.append(":\n");
    }
    preClassName = className;
    preMethodName = methodName;

    sb.append(message);
    sb.append(lineSeparator);
    if (record.getThrown() != null) {
      try {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        record.getThrown().printStackTrace(pw);
        pw.close();
        sb.append(sw.toString());
      }
      catch (Exception ex) {
      }
    }
    String result = sb.toString();
//    System.out.println(result);
    return result;
  }

}
