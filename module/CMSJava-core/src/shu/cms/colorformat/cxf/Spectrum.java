//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2006.05.04 at 12:51:40 �U�� CST
//


package shu.cms.colorformat.cxf;

import java.util.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.*;

/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
         "name",
         "description",
         "creator",
         "created",
         "modified",
         "additionalData",
         "value"
})
@XmlRootElement(name = "Spectrum")
public class Spectrum {

  @XmlAttribute(name = "Conditions", required = true)
  @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
  protected String conditions;

  @XmlElement(name = "Name")
  protected String name;

  @XmlElement(name = "Description")
  protected String description;

  @XmlElement(name = "Creator")
  protected String creator;

  @XmlElement(name = "Created")
  protected String created;

  @XmlElement(name = "Modified")
  protected String modified;

  @XmlElement(name = "AdditionalData")
  protected AdditionalData additionalData;

  @XmlElement(name = "Value")
  protected List<Value> value;

  /**
   * Gets the value of the conditions property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getConditions() {
    return conditions;
  }

  /**
   * Sets the value of the conditions property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setConditions(String value) {
    this.conditions = value;
  }

  /**
   * Gets the value of the name property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the value of the name property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setName(String value) {
    this.name = value;
  }

  /**
   * Gets the value of the description property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the value of the description property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setDescription(String value) {
    this.description = value;
  }

  /**
   * Gets the value of the creator property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getCreator() {
    return creator;
  }

  /**
   * Sets the value of the creator property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setCreator(String value) {
    this.creator = value;
  }

  /**
   * Gets the value of the created property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getCreated() {
    return created;
  }

  /**
   * Sets the value of the created property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setCreated(String value) {
    this.created = value;
  }

  /**
   * Gets the value of the modified property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getModified() {
    return modified;
  }

  /**
   * Sets the value of the modified property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setModified(String value) {
    this.modified = value;
  }

  /**
   * Gets the value of the additionalData property.
   *
   * @return
   *     possible object is
   *     {@link AdditionalData }
   *
   */
  public AdditionalData getAdditionalData() {
    return additionalData;
  }

  /**
   * Sets the value of the additionalData property.
   *
   * @param value
   *     allowed object is
   *     {@link AdditionalData }
   *
   */
  public void setAdditionalData(AdditionalData value) {
    this.additionalData = value;
  }

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