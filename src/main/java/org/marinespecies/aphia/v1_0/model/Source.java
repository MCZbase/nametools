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
 * Source
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-03-28T23:57:28.350Z[GMT]")
public class Source {
  @JsonProperty("source_id")
  private Integer sourceId = 1;

  @JsonProperty("use")
  private String use = null;

  @JsonProperty("reference")
  private String reference = null;

  @JsonProperty("page")
  private String page = null;

  @JsonProperty("url")
  private String url = null;

  @JsonProperty("link")
  private String link = null;

  @JsonProperty("fulltext")
  private String fulltext = null;

  @JsonProperty("doi")
  private String doi = null;

  public Source sourceId(Integer sourceId) {
    this.sourceId = sourceId;
    return this;
  }

   /**
   * Unique identifier for the source within WoRMS -- 
   * @return sourceId
  **/
  @Schema(description = "Unique identifier for the source within WoRMS -- ")
  public Integer getSourceId() {
    return sourceId;
  }

  public void setSourceId(Integer sourceId) {
    this.sourceId = sourceId;
  }

  public Source use(String use) {
    this.use = use;
    return this;
  }

   /**
   * Usage of the source for this taxon. See &lt;a href&#x3D;&#x27;https://www.marinespecies.org/aphia.php?p&#x3D;manual#topic6&#x27; target&#x3D;&#x27;_blank&#x27;&gt;here&lt;/a&gt; for all values
   * @return use
  **/
  @Schema(description = "Usage of the source for this taxon. See <a href='https://www.marinespecies.org/aphia.php?p=manual#topic6' target='_blank'>here</a> for all values")
  public String getUse() {
    return use;
  }

  public void setUse(String use) {
    this.use = use;
  }

  public Source reference(String reference) {
    this.reference = reference;
    return this;
  }

   /**
   * Full citation string
   * @return reference
  **/
  @Schema(description = "Full citation string")
  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public Source page(String page) {
    this.page = page;
    return this;
  }

   /**
   * Page(s) where the taxon is mentioned
   * @return page
  **/
  @Schema(description = "Page(s) where the taxon is mentioned")
  public String getPage() {
    return page;
  }

  public void setPage(String page) {
    this.page = page;
  }

  public Source url(String url) {
    this.url = url;
    return this;
  }

   /**
   * Direct link to the source record
   * @return url
  **/
  @Schema(description = "Direct link to the source record")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Source link(String link) {
    this.link = link;
    return this;
  }

   /**
   * External link (i.e. journal, data system, etc..)
   * @return link
  **/
  @Schema(description = "External link (i.e. journal, data system, etc..)")
  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public Source fulltext(String fulltext) {
    this.fulltext = fulltext;
    return this;
  }

   /**
   * Full text link (PDF)
   * @return fulltext
  **/
  @Schema(description = "Full text link (PDF)")
  public String getFulltext() {
    return fulltext;
  }

  public void setFulltext(String fulltext) {
    this.fulltext = fulltext;
  }

  public Source doi(String doi) {
    this.doi = doi;
    return this;
  }

   /**
   * Digital Object Identifier
   * @return doi
  **/
  @Schema(description = "Digital Object Identifier")
  public String getDoi() {
    return doi;
  }

  public void setDoi(String doi) {
    this.doi = doi;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Source source = (Source) o;
    return Objects.equals(this.sourceId, source.sourceId) &&
        Objects.equals(this.use, source.use) &&
        Objects.equals(this.reference, source.reference) &&
        Objects.equals(this.page, source.page) &&
        Objects.equals(this.url, source.url) &&
        Objects.equals(this.link, source.link) &&
        Objects.equals(this.fulltext, source.fulltext) &&
        Objects.equals(this.doi, source.doi);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceId, use, reference, page, url, link, fulltext, doi);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Source {\n");
    
    sb.append("    sourceId: ").append(toIndentedString(sourceId)).append("\n");
    sb.append("    use: ").append(toIndentedString(use)).append("\n");
    sb.append("    reference: ").append(toIndentedString(reference)).append("\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    link: ").append(toIndentedString(link)).append("\n");
    sb.append("    fulltext: ").append(toIndentedString(fulltext)).append("\n");
    sb.append("    doi: ").append(toIndentedString(doi)).append("\n");
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
