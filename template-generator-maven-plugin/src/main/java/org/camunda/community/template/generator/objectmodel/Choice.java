package org.camunda.community.template.generator.objectmodel;

/** Represents one choice of a dropdown property */
public class Choice {

  private String name;

  private String value;

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
   * @return value
   */
  public String getValue() {
    return value;
  }

  /**
   * @param value
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * @param name
   * @param value
   */
  public Choice(String name, String value) {
    super();
    this.name = name;
    this.value = value;
  }
}
