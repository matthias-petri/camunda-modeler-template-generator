package org.camunda.community.template.generator.objectmodel;

/** A constrained that is contained by a property */
public class Constraint {

  private boolean notEmpty;

  /**
   * @return notEmpty
   */
  public boolean isNotEmpty() {
    return notEmpty;
  }

  /**
   * @param notEmpty
   */
  public void setNotEmpty(boolean notEmpty) {
    this.notEmpty = notEmpty;
  }

  /**
   * @param notEmpty
   */
  public Constraint(boolean notEmpty) {
    super();
    this.notEmpty = notEmpty;
  }
}
