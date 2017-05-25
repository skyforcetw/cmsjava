//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2006.05.04 at 12:51:40 �U�� CST
//


package shu.cms.colorformat.cxf;

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
         "assortmentName",
         "thickness",
         "substrateLinkName",
         "components"
})
@XmlRootElement(name = "Recipe")
public class Recipe {

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

  @XmlElement(name = "AssortmentName")
  protected String assortmentName;

  @XmlElement(name = "Thickness")
  protected String thickness;

  @XmlElement(name = "SubstrateLinkName")
  protected String substrateLinkName;

  @XmlElement(name = "Components")
  protected Components components;

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
   * Gets the value of the assortmentName property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getAssortmentName() {
    return assortmentName;
  }

  /**
   * Sets the value of the assortmentName property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setAssortmentName(String value) {
    this.assortmentName = value;
  }

  /**
   * Gets the value of the thickness property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getThickness() {
    return thickness;
  }

  /**
   * Sets the value of the thickness property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setThickness(String value) {
    this.thickness = value;
  }

  /**
   * Gets the value of the substrateLinkName property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getSubstrateLinkName() {
    return substrateLinkName;
  }

  /**
   * Sets the value of the substrateLinkName property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setSubstrateLinkName(String value) {
    this.substrateLinkName = value;
  }

  /**
   * Gets the value of the components property.
   *
   * @return
   *     possible object is
   *     {@link Components }
   *
   */
  public Components getComponents() {
    return components;
  }

  /**
   * Sets the value of the components property.
   *
   * @param value
   *     allowed object is
   *     {@link Components }
   *
   */
  public void setComponents(Components value) {
    this.components = value;
  }

}