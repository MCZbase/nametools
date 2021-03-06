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
/**
 * Vernacular
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-03-28T23:57:28.350Z[GMT]")
public class Vernacular {
  @JsonProperty("vernacular")
  private String vernacular = null;

  @JsonProperty("language_code")
  private String languageCode = null;

  @JsonProperty("language")
  private String language = null;

  public Vernacular vernacular(String vernacular) {
    this.vernacular = vernacular;
    return this;
  }

   /**
   * Get vernacular
   * @return vernacular
  **/
  @Schema(description = "")
  public String getVernacular() {
    return vernacular;
  }

  public void setVernacular(String vernacular) {
    this.vernacular = vernacular;
  }

  public Vernacular languageCode(String languageCode) {
    this.languageCode = languageCode;
    return this;
  }

   /**
   * Get languageCode
   * @return languageCode
  **/
  @Schema(description = "")
  public String getLanguageCode() {
    return languageCode;
  }

  public void setLanguageCode(String languageCode) {
    this.languageCode = languageCode;
  }

  public Vernacular language(String language) {
    this.language = language;
    return this;
  }

   /**
   * Get language
   * @return language
  **/
  @Schema(description = "")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Vernacular vernacular = (Vernacular) o;
    return Objects.equals(this.vernacular, vernacular.vernacular) &&
        Objects.equals(this.languageCode, vernacular.languageCode) &&
        Objects.equals(this.language, vernacular.language);
  }

  @Override
  public int hashCode() {
    return Objects.hash(vernacular, languageCode, language);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Vernacular {\n");
    
    sb.append("    vernacular: ").append(toIndentedString(vernacular)).append("\n");
    sb.append("    languageCode: ").append(toIndentedString(languageCode)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
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
