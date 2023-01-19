package org.camunda.community.template.generator.objectmodel;

/** Represents one property of an activity for a Camunda Modeler Template file */
public class Property {

  private String label;

  private String type;

  private String value;

  private String description;

  private String propertyType;

  private boolean editable;

  private Choice[] choices;

  private Constraint constraints;

  private Binding binding;

  /**
   * @return label
   */
  public String getLabel() {
    return label;
  }

  /**
   * @param label
   */
  public void setLabel(String label) {
    this.label = label;
  }

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
   * @return description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return propertyType
   */
  public String getPropertyType() {
    return propertyType;
  }

  /**
   * @param propertyType
   */
  public void setPropertyType(String propertyType) {
    this.propertyType = propertyType;
  }

  /**
   * @return editable
   */
  public boolean isEditable() {
    return editable;
  }

  /**
   * @param editable
   */
  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  /**
   * @return choices
   */
  public Choice[] getChoices() {
    return choices;
  }

  /**
   * @param choices
   */
  public void setChoices(Choice[] choices) {
    this.choices = choices;
  }

  /**
   * @return constraint
   */
  public Constraint getConstraint() {
    return constraints;
  }

  /**
   * @param constraints
   */
  public void setConstraint(Constraint constraints) {
    this.constraints = constraints;
  }

  /**
   * @return binding
   */
  public Binding getBinding() {
    return binding;
  }

  /**
   * @param binding
   */
  public void setBinding(Binding binding) {
    this.binding = binding;
  }
}
