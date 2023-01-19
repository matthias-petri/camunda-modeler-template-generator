package org.camunda.community.template.generator.objectmodel;

import java.util.ArrayList;
import java.util.List;

/** Represents one activity for a Camunda Modeler Template file */
public class Template {

  private String $schema;

  private String name;

  private String id;

  private String[] appliesTo;

  private boolean entriesVisible;

  private List<Property> properties;

  /**
   * @return schemaURL
   */
  public String getSchemaURL() {
    return $schema;
  }

  /**
   * @param schemaURL
   */
  public void setSchemaURL(String schemaURL) {
    this.$schema = schemaURL;
  }

  /**
   * @return name
   */
  public String getTemplateName() {
    return name;
  }

  /**
   * @param templateName
   */
  public void setTemplateName(String templateName) {
    this.name = templateName;
  }

  /**
   * @return ID
   */
  public String getTemplateID() {
    return id;
  }

  /**
   * @param templateID
   */
  public void setTemplateID(String templateID) {
    this.id = templateID;
  }

  /**
   * @return appliesTo
   */
  public String[] getAppliesTo() {
    return appliesTo;
  }

  /**
   * @param appliesTo
   */
  public void setAppliesTo(String[] appliesTo) {
    this.appliesTo = appliesTo;
  }

  /**
   * @return entriesVisible
   */
  public boolean isEntriesVisible() {
    return entriesVisible;
  }

  /**
   * @param entriesVisible
   */
  public void setEntriesVisible(boolean entriesVisible) {
    this.entriesVisible = entriesVisible;
  }

  /**
   * @return properties
   */
  public List<Property> getTemplateProperties() {
    return properties;
  }

  /**
   * @param templateProperty
   */
  public void addTemplateProperty(Property templateProperty) {
    properties.add(templateProperty);
  }

  /** */
  public Template() {
    super();
    this.properties = new ArrayList<>();
  }
}
