//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2006.05.04 at 12:51:40 下午 CST
//


package shu.cms.colorformat.cxf;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
         "value"
})
@XmlRootElement(name = "Components")
public class Components {

  @XmlElement(name = "Value")
  protected List<Value> value;

  /**
   * Gets the value of the value property.
   *
   * <p>
   * This accessor method returns a reference to the live list,
   * not a snapshot. Therefore any modification you make to the
   * returned list will be present inside the JAXB object.
   * This is why there is not a <CODE>set</CODE> method for the value property.
   *
   * <p>
   * For example, to add a new item, do as follows:
   * <pre>
   *    getValue().add(newItem);
   * </pre>
   *
   *
   * <p>
   * Objects of the following type(s) are allowed in the list
   * {@link Value }
   *
   *
   */
  public List<Value> getValue() {
    if (value == null) {
      value = new ArrayList<Value> ();
    }
    return this.value;
  }

}
