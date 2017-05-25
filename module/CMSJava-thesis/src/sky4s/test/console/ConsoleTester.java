package sky4s.test.console;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Locale;
import java.io.*;

/**
 * <p>Title: Colour Management System - thesis</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ConsoleTester {

  public static void main(String[] args) {

    Console c = System.console();

    try {
      System.out.println(System.in.read());
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }

  }
}
