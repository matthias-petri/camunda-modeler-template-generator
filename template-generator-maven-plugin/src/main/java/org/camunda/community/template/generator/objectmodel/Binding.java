package org.camunda.community.template.generator.objectmodel;

/** Binding object for a property */
public class Binding {

  private String type;

  private String name;

  private String source;

  private String scriptFormat;

  /**
   * @return type
   */
  public String getType() {
    return type;
  }

  /**
   * @param type
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return source
   */
  public String getSource() {
    return source;
  }

  /**
   * @param source
   */
  public void setSource(String source) {
    this.source = source;
  }

  /**
   * @return scriptFormat
   */
  public String getScriptFormat() {
    return scriptFormat;
  }

  /**
   * @param scriptFormat
   */
  public void setScriptFormat(String scriptFormat) {
    this.scriptFormat = scriptFormat;
  }

  /**
   * @param type
   * @param name
   */
  public Binding(String type, String name) {
    super();
    this.type = type;
    this.name = name;
  }

  /** */
  public Binding() {
    super();
  }
}
