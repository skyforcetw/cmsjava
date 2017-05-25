package shu.cms.applet.benchmark.test;

import shu.cms.applet.benchmark.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class ParameterTester {

  public static void main(String[] args) {
    A a = new A();
    ParameterBlock block = new ParameterBlock();
    block.set("Score", 1.1);
    block.set("String", "1234");
    block.set("Integer", 1233333);

    ParameterBlock.setParameterBlock(a, block);
    System.out.println(a.score);
    System.out.println(a.s);
    System.out.println(a.i);
  }
}
