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
import org.marinespecies.aphia.v1_0.model.AttributeKey;
/**
 * AttributeKey
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-03-28T23:57:28.350Z[GMT]")
public class AttributeKey {
  @JsonProperty("measurementTypeID")
  private Integer measurementTypeID = null;

  @JsonProperty("measurementType")
  private String measurementType = null;

  @JsonProperty("input_id")
  private Integer inputId = null;

  @JsonProperty("CategoryID")
  private Integer categoryID = null;

  @JsonProperty("children")
  private List<AttributeKey> children = null;

  public AttributeKey measurementTypeID(Integer measurementTypeID) {
    this.measurementTypeID = measurementTypeID;
    return this;
  }

   /**
   * An internal identifier for the measurementType
   * @return measurementTypeID
  **/
  @Schema(example = "4", description = "An internal identifier for the measurementType")
  public Integer getMeasurementTypeID() {
    return measurementTypeID;
  }

  public void setMeasurementTypeID(Integer measurementTypeID) {
    this.measurementTypeID = measurementTypeID;
  }

  public AttributeKey measurementType(String measurementType) {
    this.measurementType = measurementType;
    return this;
  }

   /**
   * The nature of the measurement, fact, characteristic, or assertion &lt;a href&#x3D;&#x27;https://www.marinespecies.org/traits/wiki&#x27; target&#x3D;&#x27;_blank&#x27;&gt;https://www.marinespecies.org/traits/wiki&lt;/a&gt;
   * @return measurementType
  **/
  @Schema(example = "Functional group", description = "The nature of the measurement, fact, characteristic, or assertion <a href='https://www.marinespecies.org/traits/wiki' target='_blank'>https://www.marinespecies.org/traits/wiki</a>")
  public String getMeasurementType() {
    return measurementType;
  }

  public void setMeasurementType(String measurementType) {
    this.measurementType = measurementType;
  }

  public AttributeKey inputId(Integer inputId) {
    this.inputId = inputId;
    return this;
  }

   /**
   * The data type that is expected as value for this attribute definition
   * @return inputId
  **/
  @Schema(example = "1", description = "The data type that is expected as value for this attribute definition")
  public Integer getInputId() {
    return inputId;
  }

  public void setInputId(Integer inputId) {
    this.inputId = inputId;
  }

  public AttributeKey categoryID(Integer categoryID) {
    this.categoryID = categoryID;
    return this;
  }

   /**
   * The category identifier to list possible attribute values for this attribute definition
   * @return categoryID
  **/
  @Schema(example = "7", description = "The category identifier to list possible attribute values for this attribute definition")
  public Integer getCategoryID() {
    return categoryID;
  }

  public void setCategoryID(Integer categoryID) {
    this.categoryID = categoryID;
  }

  public AttributeKey children(List<AttributeKey> children) {
    this.children = children;
    return this;
  }

  public AttributeKey addChildrenItem(AttributeKey childrenItem) {
    if (this.children == null) {
      this.children = new ArrayList<AttributeKey>();
    }
    this.children.add(childrenItem);
    return this;
  }

   /**
   * The possible child attribute keys that help to describe to current attribute
   * @return children
  **/
  @Schema(description = "The possible child attribute keys that help to describe to current attribute")
  public List<AttributeKey> getChildren() {
    return children;
  }

  public void setChildren(List<AttributeKey> children) {
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
    AttributeKey attributeKey = (AttributeKey) o;
    return Objects.equals(this.measurementTypeID, attributeKey.measurementTypeID) &&
        Objects.equals(this.measurementType, attributeKey.measurementType) &&
        Objects.equals(this.inputId, attributeKey.inputId) &&
        Objects.equals(this.categoryID, attributeKey.categoryID) &&
        Objects.equals(this.children, attributeKey.children);
  }

  @Override
  public int hashCode() {
    return Objects.hash(measurementTypeID, measurementType, inputId, categoryID, children);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AttributeKey {\n");
    
    sb.append("    measurementTypeID: ").append(toIndentedString(measurementTypeID)).append("\n");
    sb.append("    measurementType: ").append(toIndentedString(measurementType)).append("\n");
    sb.append("    inputId: ").append(toIndentedString(inputId)).append("\n");
    sb.append("    categoryID: ").append(toIndentedString(categoryID)).append("\n");
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
