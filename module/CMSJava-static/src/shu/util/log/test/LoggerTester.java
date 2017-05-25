package shu.util.log.test;

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
public class LoggerTester {
  public static void main(String[] args) {
    Logger.log.error("error");
    Logger.log.info("info");
    Logger.log.trace("trace");
  }
}
