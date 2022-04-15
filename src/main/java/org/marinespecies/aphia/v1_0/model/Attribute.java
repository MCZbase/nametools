/*
 * WoRMS REST webservice
 * The definitions and operations are listed below. Click on an operation name to view it's details, and test it.
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package org.marinespecies.aphia.v1_0.model;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import org.marinespecies.aphia.v1_0.model.Attribute;
/**
 * Attribute
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-03-28T23:57:28.350Z[GMT]")
public class Attribute {
  @JsonProperty("AphiaID")
  private Integer aphiaID = null;

  @JsonProperty("measurementTypeID")
  private Integer measurementTypeID = null;

  @JsonProperty("measurementType")
  private String measurementType = null;

  @JsonProperty("measurementValue")
  private String measurementValue = null;

  @JsonProperty("source_id")
  private Integer sourceId = null;

  @JsonProperty("reference")
  private String reference = null;

  @JsonProperty("qualitystatus")
  private String qualitystatus = null;

  @JsonProperty("CategoryID")
  private Integer categoryID = null;

  @JsonProperty("AphiaID_Inherited")
  private Integer aphiaIDInherited = null;

  @JsonProperty("children")
  private List<Attribute> children = null;

  public Attribute aphiaID(Integer aphiaID) {
    this.aphiaID = aphiaID;
    return this;
  }

   /**
   * Unique and persistent identifier within WoRMS
   * @return aphiaID
  **/
  @Schema(example = "127160", description = "Unique and persistent identifier within WoRMS")
  public Integer getAphiaID() {
    return aphiaID;
  }

  public void setAphiaID(Integer aphiaID) {
    this.aphiaID = aphiaID;
  }

  public Attribute measurementTypeID(Integer measurementTypeID) {
    this.measurementTypeID = measurementTypeID;
    return this;
  }

   /**
   * The corresponding AttributeKey its measurementTypeID
   * @return measurementTypeID
  **/
  @Schema(example = "15", description = "The corresponding AttributeKey its measurementTypeID")
  public Integer getMeasurementTypeID() {
    return measurementTypeID;
  }

  public void setMeasurementTypeID(Integer measurementTypeID) {
    this.measurementTypeID = measurementTypeID;
  }

  public Attribute measurementType(String measurementType) {
    this.measurementType = measurementType;
    return this;
  }

   /**
   * The corresponding AttributeKey its measurementType
   * @return measurementType
  **/
  @Schema(example = "Body size", description = "The corresponding AttributeKey its measurementType")
  public String getMeasurementType() {
    return measurementType;
  }

  public void setMeasurementType(String measurementType) {
    this.measurementType = measurementType;
  }

  public Attribute measurementValue(String measurementValue) {
    this.measurementValue = measurementValue;
    return this;
  }

   /**
   * The value of the measurement, fact, characteristic, or assertion
   * @return measurementValue
  **/
  @Schema(example = "70", description = "The value of the measurement, fact, characteristic, or assertion")
  public String getMeasurementValue() {
    return measurementValue;
  }

  public void setMeasurementValue(String measurementValue) {
    this.measurementValue = measurementValue;
  }

  public Attribute sourceId(Integer sourceId) {
    this.sourceId = sourceId;
    return this;
  }

   /**
   * The identifier for the AphiaSource for this attribute
   * @return sourceId
  **/
  @Schema(example = "232308", description = "The identifier for the AphiaSource for this attribute")
  public Integer getSourceId() {
    return sourceId;
  }

  public void setSourceId(Integer sourceId) {
    this.sourceId = sourceId;
  }

  public Attribute reference(String reference) {
    this.reference = reference;
    return this;
  }

   /**
   * The AphiaSource reference for this attribute
   * @return reference
  **/
  @Schema(description = "The AphiaSource reference for this attribute")
  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public Attribute qualitystatus(String qualitystatus) {
    this.qualitystatus = qualitystatus;
    return this;
  }

   /**
   * Quality status of the record. Possible values: &#x27;checked&#x27;, &#x27;trusted&#x27; or &#x27;unreviewed&#x27;. See &lt;a href&#x3D;&#x27;https://www.marinespecies.org/aphia.php?p&#x3D;manual#topic22&#x27; target&#x3D;&#x27;_blank&#x27;&gt;https://www.marinespecies.org/aphia.php?p&#x3D;manual#topic22&lt;/a&gt;
   * @return qualitystatus
  **/
  @Schema(example = "checked", description = "Quality status of the record. Possible values: 'checked', 'trusted' or 'unreviewed'. See <a href='https://www.marinespecies.org/aphia.php?p=manual#topic22' target='_blank'>https://www.marinespecies.org/aphia.php?p=manual#topic22</a>")
  public String getQualitystatus() {
    return qualitystatus;
  }

  public void setQualitystatus(String qualitystatus) {
    this.qualitystatus = qualitystatus;
  }

  public Attribute categoryID(Integer categoryID) {
    this.categoryID = categoryID;
    return this;
  }

   /**
   * The category identifier to list possible attribute values for this attribute definition
   * @return categoryID
  **/
  @Schema(description = "The category identifier to list possible attribute values for this attribute definition")
  public Integer getCategoryID() {
    return categoryID;
  }

  public void setCategoryID(Integer categoryID) {
    this.categoryID = categoryID;
  }

  public Attribute aphiaIDInherited(Integer aphiaIDInherited) {
    this.aphiaIDInherited = aphiaIDInherited;
    return this;
  }

   /**
   * The AphiaID from where this attribute is inherited
   * @return aphiaIDInherited
  **/
  @Schema(example = "126132", description = "The AphiaID from where this attribute is inherited")
  public Integer getAphiaIDInherited() {
    return aphiaIDInherited;
  }

  public void setAphiaIDInherited(Integer aphiaIDInherited) {
    this.aphiaIDInherited = aphiaIDInherited;
  }

  public Attribute children(List<Attribute> children) {
    this.children = children;
    return this;
  }

  public Attribute addChildrenItem(Attribute childrenItem) {
    if (this.children == null) {
      this.children = new ArrayList<Attribute>();
    }
    this.children.add(childrenItem);
    return this;
  }

   /**
   * The possible child attributes that help to describe to current attribute
   * @return children
  **/
  @Schema(description = "The possible child attributes that help to describe to current attribute")
  public List<Attribute> getChildren() {
    return children;
  }

  public void setChildren(List<Attribute> children) {
    this.children = children;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Attribute attribute = (Attribute) o;
    return Objects.equals(this.aphiaID, attribute.aphiaID) &&
        Objects.equals(this.measurementTypeID, attribute.measurementTypeID) &&
        Objects.equals(this.measurementType, attribute.measurementType) &&
        Objects.equals(this.measurementValue, attribute.measurementValue) &&
        Objects.equals(this.sourceId, attribute.sourceId) &&
        Objects.equals(this.reference, attribute.reference) &&
        Objects.equals(this.qualitystatus, attribute.qualitystatus) &&
        Objects.equals(this.categoryID, attribute.categoryID) &&
        Objects.equals(this.aphiaIDInherited, attribute.aphiaIDInherited) &&
        Objects.equals(this.children, attribute.children);
  }

  @Override
  public int hashCode() {
    return Objects.hash(aphiaID, measurementTypeID, measurementType, measurementValue, sourceId, reference, qualitystatus, categoryID, aphiaIDInherited, children);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Attribute {\n");
    
    sb.append("    aphiaID: ").append(toIndentedString(aphiaID)).append("\n");
    sb.append("    measurementTypeID: ").append(toIndentedString(measurementTypeID)).append("\n");
    sb.append("    measurementType: ").append(toIndentedString(measurementType)).append("\n");
    sb.append("    measurementValue: ").append(toIndentedString(measurementValue)).append("\n");
    sb.append("    sourceId: ").append(toIndentedString(sourceId)).append("\n");
    sb.append("    reference: ").append(toIndentedString(reference)).append("\n");
    sb.append("    qualitystatus: ").append(toIndentedString(qualitystatus)).append("\n");
    sb.append("    categoryID: ").append(toIndentedString(categoryID)).append("\n");
    sb.append("    aphiaIDInherited: ").append(toIndentedString(aphiaIDInherited)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}