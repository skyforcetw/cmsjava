/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.bigatti.it/projects/jacobgen)
 */
package shu.util.dll.test;

import com.jacob.com.*;

public class ALU
    extends _ALU {

  public static final String componentName = "ALUDemo.ALU";

  public ALU() {
    super(componentName);
  }

  public ALU(Dispatch d) {
    super(d);
  }

  public static void main(String[] args) {
    ALU alu = new ALU();
    Variant v = alu.add( (short) 3, (short) 4);
    System.out.println(v);
    alu.hello("world");
    System.out.println(alu.getHelloWorld());
  }
}
